package club.slavopolis.auth.repository;

import club.slavopolis.platform.entity.SysUser;
import club.slavopolis.platform.repository.BaseRepository;

import java.util.Optional;
import java.util.List;

/**
 * 用户数据访问接口
 * <p>定义用户相关的数据访问方法</p>
 * <p>支持多租户隔离的用户查询</p>
 * 
 * @author Slavopolis Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserRepository extends BaseRepository<SysUser, Long> {

    /**
     * 根据租户ID和用户名查找用户
     * 
     * @param tenantId 租户ID
     * @param username 用户名
     * @return 用户对象，可能为空
     */
    Optional<SysUser> findByTenantIdAndUsername(Long tenantId, String username);

    /**
     * 根据租户ID和手机号查找用户
     * 
     * @param tenantId 租户ID
     * @param phone 手机号
     * @return 用户对象，可能为空
     */
    Optional<SysUser> findByTenantIdAndPhone(Long tenantId, String phone);

    /**
     * 根据租户ID和邮箱查找用户
     * 
     * @param tenantId 租户ID
     * @param email 邮箱
     * @return 用户对象，可能为空
     */
    Optional<SysUser> findByTenantIdAndEmail(Long tenantId, String email);

    /**
     * 根据租户ID和状态查找用户列表
     * 
     * @param tenantId 租户ID
     * @param status 用户状态
     * @return 用户列表
     */
    List<SysUser> findByTenantIdAndStatus(Long tenantId, SysUser.UserStatus status);

    /**
     * 根据租户ID和用户类型查找用户列表
     * 
     * @param tenantId 租户ID
     * @param userType 用户类型
     * @return 用户列表
     */
    List<SysUser> findByTenantIdAndUserType(Long tenantId, SysUser.UserType userType);

    /**
     * 检查用户名在租户内是否已存在
     * 
     * @param tenantId 租户ID
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByTenantIdAndUsername(Long tenantId, String username);

    /**
     * 检查手机号在租户内是否已存在
     * 
     * @param tenantId 租户ID
     * @param phone 手机号
     * @return true-存在，false-不存在
     */
    boolean existsByTenantIdAndPhone(Long tenantId, String phone);

    /**
     * 检查邮箱在租户内是否已存在
     * 
     * @param tenantId 租户ID
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean existsByTenantIdAndEmail(Long tenantId, String email);

    /**
     * 更新用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码（加密后）
     * @param salt 盐值
     * @return 是否更新成功
     */
    boolean updatePassword(Long userId, String newPassword, String salt);

    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long userId, SysUser.UserStatus status);

    /**
     * 更新用户登录信息
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @return 是否更新成功
     */
    boolean updateLastLogin(Long userId, String loginIp);

    /**
     * 增加用户登录失败次数
     * 
     * @param userId 用户ID
     * @return 当前失败次数
     */
    int incrementLoginFailureCount(Long userId);

    /**
     * 重置用户登录失败次数
     * 
     * @param userId 用户ID
     * @return 是否重置成功
     */
    boolean resetLoginFailureCount(Long userId);

    /**
     * 锁定用户
     * 
     * @param userId 用户ID
     * @return 是否锁定成功
     */
    boolean lockUser(Long userId);

    /**
     * 解锁用户
     * 
     * @param userId 用户ID
     * @return 是否解锁成功
     */
    boolean unlockUser(Long userId);
} 