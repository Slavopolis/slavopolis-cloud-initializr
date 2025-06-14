package club.slavopolis.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件服务状态信息
 *
 * @author slavopolis
 * @version 1.0.0
 * @package club.slavopolis.email.model
 * @since 2025/6/14
 * <p>
 * Copyright (c) 2025 slavopolis-boot
 * All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailServiceStatus {
    
    /**
     * 服务是否启用
     */
    private boolean enabled;
    
    /**
     * 邮件服务器连接是否正常
     */
    private boolean connected;
    
    /**
     * 邮件服务器地址
     */
    private String serverInfo;
    
    /**
     * 服务运行时长（毫秒）
     */
    private long uptime;
    
    /**
     * 服务版本
     */
    private String version;
} 