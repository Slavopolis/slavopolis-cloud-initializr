<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>验证码 - Slavopolis</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 20px auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
            color: white;
            padding: 40px 30px;
            text-align: center;
            position: relative;
        }
        .header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
        }
        .header h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 600;
            position: relative;
            z-index: 1;
        }
        .header .icon {
            font-size: 48px;
            margin-bottom: 10px;
            display: block;
        }
        .content {
            padding: 50px 40px;
            text-align: center;
        }
        .greeting {
            font-size: 18px;
            margin-bottom: 30px;
            color: #374151;
        }
        .verification-section {
            background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
            border: 2px solid #e2e8f0;
            border-radius: 16px;
            padding: 40px 30px;
            margin: 30px 0;
            position: relative;
        }
        .verification-section::before {
            content: '🔐';
            position: absolute;
            top: -20px;
            left: 50%;
            transform: translateX(-50%);
            background: white;
            padding: 10px;
            border-radius: 50%;
            font-size: 24px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .verification-code {
            font-size: 36px;
            font-weight: 700;
            color: #4f46e5;
            letter-spacing: 8px;
            margin: 20px 0;
            padding: 20px;
            background: white;
            border-radius: 12px;
            border: 3px dashed #4f46e5;
            display: inline-block;
            min-width: 200px;
            font-family: 'Courier New', monospace;
            text-shadow: 0 2px 4px rgba(79, 70, 229, 0.2);
        }
        .code-label {
            font-size: 14px;
            color: #6b7280;
            margin-bottom: 10px;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 600;
        }
        .expiry-info {
            background: #fef3c7;
            border: 1px solid #f59e0b;
            border-radius: 8px;
            padding: 15px;
            margin: 25px 0;
            color: #92400e;
            font-weight: 500;
        }
        .expiry-info .icon {
            display: inline-block;
            margin-right: 8px;
        }
        .instructions {
            background: #f0f9ff;
            border-left: 4px solid #0ea5e9;
            padding: 20px;
            margin: 25px 0;
            text-align: left;
            border-radius: 0 8px 8px 0;
        }
        .instructions h3 {
            margin: 0 0 15px 0;
            color: #0c4a6e;
            font-size: 16px;
        }
        .instructions ol {
            margin: 0;
            padding-left: 20px;
        }
        .instructions li {
            margin: 8px 0;
            color: #0c4a6e;
        }
        .security-tips {
            background: #fef2f2;
            border: 1px solid #fca5a5;
            border-radius: 8px;
            padding: 20px;
            margin: 25px 0;
            text-align: left;
        }
        .security-tips h3 {
            color: #dc2626;
            margin: 0 0 15px 0;
            font-size: 16px;
        }
        .security-tips ul {
            margin: 0;
            padding-left: 20px;
        }
        .security-tips li {
            margin: 8px 0;
            color: #7f1d1d;
        }
        .footer {
            background-color: #1f2937;
            color: #d1d5db;
            padding: 30px;
            text-align: center;
            font-size: 14px;
        }
        .footer a {
            color: #60a5fa;
            text-decoration: none;
        }
        .footer a:hover {
            text-decoration: underline;
        }
        .brand-info {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #374151;
            font-size: 12px;
            color: #9ca3af;
        }
        @media (max-width: 600px) {
            .container {
                margin: 10px;
                border-radius: 8px;
            }
            .content {
                padding: 30px 20px;
            }
            .verification-code {
                font-size: 28px;
                letter-spacing: 4px;
                min-width: auto;
            }
            .header {
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <span class="icon">🔑</span>
            <h1>验证您的身份</h1>
        </div>
        
        <div class="content">
            <div class="greeting">
                <p>您好 <strong>${username!'用户'}</strong>，</p>
                <p>感谢您选择 Slavopolis！为了确保账户安全，请使用以下验证码完成注册。</p>
            </div>
            
            <div class="verification-section">
                <div class="code-label">您的验证码</div>
                <div class="verification-code">${verificationCode}</div>
            </div>
            
            <div class="expiry-info">
                <span class="icon">⏰</span>
                <strong>重要提醒：</strong>此验证码将在 <strong>${expiryMinutes!10} 分钟</strong>后失效，请尽快使用。
            </div>
            
            <div class="instructions">
                <h3>📋 使用说明</h3>
                <ol>
                    <li>返回注册页面</li>
                    <li>在验证码输入框中输入上方的6位数字</li>
                    <li>点击"验证"按钮完成注册</li>
                    <li>验证成功后即可开始使用您的账户</li>
                </ol>
            </div>
            
            <div class="security-tips">
                <h3>🛡️ 安全提醒</h3>
                <ul>
                    <li>请勿将验证码分享给任何人</li>
                    <li>Slavopolis 工作人员不会主动索要您的验证码</li>
                    <li>如果您没有进行注册操作，请忽略此邮件</li>
                    <li>如有疑问，请立即联系我们的客服团队</li>
                </ul>
            </div>
            
            <p style="margin-top: 40px; color: #6b7280;">
                如果您在使用过程中遇到任何问题，请随时联系我们的技术支持团队。
            </p>
        </div>
        
        <div class="footer">
            <p>此邮件由 Slavopolis 系统自动发送，请勿直接回复。</p>
            <p>
                <a href="${supportUrl!'#'}">技术支持</a> | 
                <a href="${helpUrl!'#'}">帮助中心</a> | 
                <a href="${privacyUrl!'#'}">隐私政策</a>
            </p>
            
            <div class="brand-info">
                <p>&copy; ${.now?string("yyyy")} Slavopolis. 保留所有权利。</p>
                <p>发送时间：${.now?string("yyyy-MM-dd HH:mm:ss")}</p>
            </div>
        </div>
    </div>
</body>
</html> 