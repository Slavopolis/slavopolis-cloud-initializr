package club.slavopolis.infrastructure.cache.redis.service.impl;

import club.slavopolis.common.core.constants.CommonConstants;
import club.slavopolis.infrastructure.cache.redis.exception.CacheException;
import club.slavopolis.infrastructure.cache.redis.service.LuaScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Lua脚本执行服务实现
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.service.impl
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Slf4j
@Service
public class LuaScriptServiceImpl implements LuaScriptService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 脚本缓存，避免重复编译
     */
    private final ConcurrentMap<String, RedisScript<?>> scriptCache = new ConcurrentHashMap<>();

    public LuaScriptServiceImpl(@Autowired RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        log.info("LuaScriptService 初始化完成");
    }

    /**
     * 执行Lua脚本
     *
     * @param script Lua脚本内容
     * @param keys 键列表
     * @param args 参数列表
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(String script, List<String> keys, Object... args) {
        try {
            // 从缓存中获取或创建RedisScript
            RedisScript<T> redisScript = (RedisScript<T>) scriptCache.computeIfAbsent(script, 
                s -> {
                    DefaultRedisScript<Object> defaultScript = new DefaultRedisScript<>();
                    defaultScript.setScriptText(s);
                    defaultScript.setResultType(Object.class);
                    return defaultScript;
                });

            return redisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            log.error("执行Lua脚本失败, script: {}, keys: {}, args: {}", script, keys, args, e);
            throw new CacheException("执行Lua脚本失败", e);
        }
    }

    /**
     * 执行Lua脚本并指定返回类型
     *
     * @param script Lua脚本内容
     * @param returnType 返回类型
     * @param keys 键列表
     * @param args 参数列表
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(String script, Class<T> returnType, List<String> keys, Object... args) {
        try {
            String cacheKey = script + CommonConstants.CACHE_KEY_SEPARATOR + returnType.getName();
            RedisScript<T> redisScript = (RedisScript<T>) scriptCache.computeIfAbsent(cacheKey, 
                s -> {
                    DefaultRedisScript<T> defaultScript = new DefaultRedisScript<>();
                    defaultScript.setScriptText(script);
                    defaultScript.setResultType(returnType);
                    return defaultScript;
                });

            return redisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            log.error("执行Lua脚本失败, script: {}, returnType: {}, keys: {}, args: {}", 
                script, returnType.getName(), keys, args, e);
            throw new CacheException("执行Lua脚本失败", e);
        }
    }

    /**
     * 执行预编译的Lua脚本
     *
     * @param scriptSha 脚本SHA1值
     * @param keys 键列表
     * @param args 参数列表
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T executeByScriptSha(String scriptSha, List<String> keys, Object... args) {
        Assert.hasText(scriptSha, "Script SHA不能为空");
        Assert.notNull(keys, "Keys不能为null");
        
        try {
            return redisTemplate.execute((RedisCallback<T>) connection -> {
                // 构建keysAndArgs数组：先放keys，再放args
                java.util.List<byte[]> keysAndArgsList = new java.util.ArrayList<>();
                
                // 添加keys
                for (String key : keys) {
                    keysAndArgsList.add(key.getBytes(StandardCharsets.UTF_8));
                }
                
                // 添加args
                for (Object arg : args) {
                    keysAndArgsList.add(String.valueOf(arg).getBytes(StandardCharsets.UTF_8));
                }
                
                // 转换为数组
                byte[][] keysAndArgs = keysAndArgsList.toArray(new byte[0][]);
                
                // 使用evalSha执行预编译脚本
                return connection.scriptingCommands().evalSha(scriptSha, 
                    org.springframework.data.redis.connection.ReturnType.VALUE,
                    keys.size(), keysAndArgs);
            });
        } catch (Exception e) {
            log.error("执行预编译Lua脚本失败, scriptSha: {}, keys: {}, args: {}", scriptSha, keys, args, e);
            throw new CacheException("执行预编译Lua脚本失败", e);
        }
    }

    /**
     * 加载Lua脚本到Redis并返回SHA1值
     *
     * @param script Lua脚本内容
     * @return 脚本SHA1值
     */
    @Override
    public String loadScript(String script) {
        Assert.hasText(script, "Script不能为空");
        
        try {
            String scriptSha = redisTemplate.execute((RedisCallback<String>) connection -> 
                connection.scriptingCommands().scriptLoad(script.getBytes(StandardCharsets.UTF_8)));
            
            log.debug("Lua脚本加载成功, SHA: {}", scriptSha);
            return scriptSha;
        } catch (Exception e) {
            log.error("加载Lua脚本失败, script: {}", script, e);
            throw new CacheException("加载Lua脚本失败", e);
        }
    }

    /**
     * 检查脚本是否已存在
     *
     * @param scriptSha 脚本SHA1值
     * @return 是否存在
     */
    @Override
    public boolean existsScript(String scriptSha) {
        Assert.hasText(scriptSha, "Script SHA不能为空");
        
        try {
            List<Boolean> results = redisTemplate.execute((RedisCallback<List<Boolean>>) connection ->
                connection.scriptingCommands().scriptExists(scriptSha));
            
            boolean exists = results != null && !results.isEmpty() && Boolean.TRUE.equals(results.getFirst());
            log.debug("脚本存在性检查, SHA: {}, 存在: {}", scriptSha, exists);
            return exists;
        } catch (Exception e) {
            log.error("检查脚本是否存在失败, scriptSha: {}", scriptSha, e);
            throw new CacheException("检查脚本是否存在失败", e);
        }
    }

    /**
     * 清除所有已加载的脚本
     */
    @Override
    public void flushScripts() {
        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                connection.scriptingCommands().scriptFlush();
                return null;
            });
            
            scriptCache.clear();
            log.info("已清除所有Lua脚本缓存");
        } catch (Exception e) {
            log.error("清除Lua脚本失败", e);
            throw new CacheException("清除Lua脚本失败", e);
        }
    }
} 