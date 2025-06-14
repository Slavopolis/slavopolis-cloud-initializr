package club.slavopolis.cache.service;

import java.util.List;

/**
 * Lua脚本执行服务接口，提供Redis Lua脚本执行能力，保证操作的原子性
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.cache.service
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
public interface LuaScriptService {

    /**
     * 执行Lua脚本
     *
     * @param script Lua脚本内容
     * @param keys 键列表
     * @param args 参数列表
     * @return 执行结果
     */
    <T> T execute(String script, List<String> keys, Object... args);

    /**
     * 执行Lua脚本并指定返回类型
     *
     * @param script Lua脚本内容
     * @param returnType 返回类型
     * @param keys 键列表
     * @param args 参数列表
     * @return 执行结果
     */
    <T> T execute(String script, Class<T> returnType, List<String> keys, Object... args);

    /**
     * 执行预编译的Lua脚本
     *
     * @param scriptSha 脚本SHA1值
     * @param keys 键列表
     * @param args 参数列表
     * @return 执行结果
     */
    <T> T executeByScriptSha(String scriptSha, List<String> keys, Object... args);

    /**
     * 加载Lua脚本到Redis并返回SHA1值
     *
     * @param script Lua脚本内容
     * @return 脚本SHA1值
     */
    String loadScript(String script);

    /**
     * 检查脚本是否已存在
     *
     * @param scriptSha 脚本SHA1值
     * @return 是否存在
     */
    boolean existsScript(String scriptSha);

    /**
     * 清除所有已加载的脚本
     */
    void flushScripts();
} 