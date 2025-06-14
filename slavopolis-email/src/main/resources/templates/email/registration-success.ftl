<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册成功 - 欢迎加入Slavopolis</title>
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
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            color: white;
            padding: 50px 30px;
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
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="celebration" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="5" cy="5" r="1" fill="white" opacity="0.2"/><circle cx="15" cy="15" r="1" fill="white" opacity="0.2"/><circle cx="10" cy="2" r="0.5" fill="white" opacity="0.3"/><circle cx="2" cy="12" r="0.5" fill="white" opacity="0.3"/><circle cx="18" cy="8" r="0.5" fill="white" opacity="0.3"/></pattern></defs><rect width="100" height="100" fill="url(%23celebration)"/></svg>');
        }
        .header .celebration-icon {
            font-size: 64px;
            margin-bottom: 15px;
            display: block;
            animation: bounce 2s infinite;
        }
        .header h1 {
            margin: 0;
            font-size: 32px;
            font-weight: 700;
            position: relative;
            z-index: 1;
        }
        .header .subtitle {
            font-size: 18px;
            margin-top: 10px;
            opacity: 0.9;
            font-weight: 300;
        }
        @keyframes bounce {
            0%, 20%, 50%, 80%, 100% {
                transform: translateY(0);
            }
            40% {
                transform: translateY(-10px);
            }
            60% {
                transform: translateY(-5px);
            }
        }
        .content {
            padding: 50px 40px;
        }
        .welcome-message {
            text-align: center;
            margin-bottom: 40px;
        }
        .welcome-message h2 {
            color: #059669;
            font-size: 24px;
            margin-bottom: 15px;
        }
        .welcome-message p {
            font-size: 18px;
            color: #374151;
            margin: 10px 0;
        }
        .user-info {
            background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
            border: 2px solid #a7f3d0;
            border-radius: 16px;
            padding: 30px;
            margin: 30px 0;
            text-align: center;
        }
        .user-info .avatar {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            border-radius: 50%;
            margin: 0 auto 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            color: white;
            font-weight: bold;
        }
        .user-info h3 {
            margin: 0 0 10px 0;
            color: #065f46;
            font-size: 20px;
        }
        .user-info .email {
            color: #047857;
            font-size: 16px;
            font-weight: 500;
        }
        .user-info .join-date {
            color: #6b7280;
            font-size: 14px;
            margin-top: 10px;
        }
        .action-section {
            text-align: center;
            margin: 40px 0;
        }
        .action-button {
            display: inline-block;
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            color: white;
            padding: 16px 32px;
            text-decoration: none;
            border-radius: 50px;
            font-weight: 600;
            font-size: 16px;
            margin: 10px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
        }
        .action-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
        }
        .action-button.secondary {
            background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
            box-shadow: 0 4px 15px rgba(99, 102, 241, 0.3);
        }
        .action-button.secondary:hover {
            box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
        }
        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 40px 0;
        }
        .feature-card {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 25px;
            text-align: center;
            transition: transform 0.3s ease;
        }
        .feature-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.1);
        }
        .feature-card .icon {
            font-size: 40px;
            margin-bottom: 15px;
            display: block;
        }
        .feature-card h4 {
            color: #1f2937;
            margin: 0 0 10px 0;
            font-size: 18px;
        }
        .feature-card p {
            color: #6b7280;
            margin: 0;
            font-size: 14px;
        }
        .next-steps {
            background: #fef7ff;
            border: 2px solid #e879f9;
            border-radius: 16px;
            padding: 30px;
            margin: 40px 0;
        }
        .next-steps h3 {
            color: #a21caf;
            margin: 0 0 20px 0;
            font-size: 20px;
            text-align: center;
        }
        .steps-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .steps-list li {
            padding: 12px 0;
            border-bottom: 1px solid #f3e8ff;
            display: flex;
            align-items: center;
        }
        .steps-list li:last-child {
            border-bottom: none;
        }
        .steps-list .step-number {
            background: linear-gradient(135deg, #d946ef 0%, #a21caf 100%);
            color: white;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-right: 15px;
            font-size: 14px;
        }
        .steps-list .step-text {
            color: #7c2d12;
            font-weight: 500;
        }
        .support-section {
            background: #f0f9ff;
            border-left: 4px solid #0ea5e9;
            padding: 25px;
            margin: 40px 0;
            border-radius: 0 12px 12px 0;
        }
        .support-section h3 {
            color: #0c4a6e;
            margin: 0 0 15px 0;
            font-size: 18px;
        }
        .support-section p {
            color: #0c4a6e;
            margin: 10px 0;
        }
        .support-links {
            margin-top: 20px;
        }
        .support-links a {
            display: inline-block;
            background: #0ea5e9;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 20px;
            margin: 5px 10px 5px 0;
            font-size: 14px;
            font-weight: 500;
            transition: background 0.3s ease;
        }
        .support-links a:hover {
            background: #0284c7;
        }
        .footer {
            background-color: #1f2937;
            color: #d1d5db;
            padding: 40px 30px;
            text-align: center;
        }
        .footer .social-links {
            margin: 25px 0;
        }
        .footer .social-links a {
            display: inline-block;
            margin: 0 15px;
            color: #60a5fa;
            text-decoration: none;
            font-size: 16px;
            transition: color 0.3s ease;
        }
        .footer .social-links a:hover {
            color: #93c5fd;
        }
        .footer .links {
            margin: 20px 0;
            font-size: 14px;
        }
        .footer .links a {
            color: #9ca3af;
            text-decoration: none;
            margin: 0 10px;
        }
        .footer .links a:hover {
            color: #d1d5db;
        }
        .footer .brand-info {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #374151;
            font-size: 12px;
            color: #6b7280;
        }
        @media (max-width: 600px) {
            .container {
                margin: 10px;
                border-radius: 8px;
            }
            .content {
                padding: 30px 20px;
            }
            .header {
                padding: 40px 20px;
            }
            .features-grid {
                grid-template-columns: 1fr;
            }
            .action-button {
                display: block;
                margin: 10px 0;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <span class="celebration-icon">🎉</span>
            <h1>注册成功！</h1>
            <p class="subtitle">欢迎加入 Slavopolis 大家庭</p>
        </div>
        
        <div class="content">
            <div class="welcome-message">
                <h2>恭喜您成功注册！</h2>
                <p>感谢您选择 Slavopolis，您的账户已经创建完成。</p>
                <p>现在您可以开始探索我们为您准备的精彩功能了！</p>
            </div>
            
            <div class="user-info">
                <div class="avatar">${username?substring(0,1)?upper_case!'U'}</div>
                <h3>${username!'新用户'}</h3>
                <div class="email">${email!'your-email@example.com'}</div>
                <div class="join-date">加入时间：${.now?string("yyyy年MM月dd日 HH:mm")}</div>
            </div>
            
            <div class="action-section">
                <a href="${loginUrl!'#'}" class="action-button">
                    🚀 立即登录
                </a>
                <a href="${profileUrl!'#'}" class="action-button secondary">
                    👤 完善资料
                </a>
            </div>
            
            <div class="features-grid">
                <div class="feature-card">
                    <span class="icon">🔒</span>
                    <h4>安全保障</h4>
                    <p>多重安全验证，保护您的账户和数据安全</p>
                </div>
                <div class="feature-card">
                    <span class="icon">⚡</span>
                    <h4>高效体验</h4>
                    <p>流畅的用户界面，让您的操作更加便捷高效</p>
                </div>
                <div class="feature-card">
                    <span class="icon">🎯</span>
                    <h4>个性化定制</h4>
                    <p>根据您的喜好定制专属的使用体验</p>
                </div>
                <div class="feature-card">
                    <span class="icon">📱</span>
                    <h4>多端同步</h4>
                    <p>支持多设备访问，数据实时同步更新</p>
                </div>
            </div>
            
            <div class="next-steps">
                <h3>🎯 接下来您可以：</h3>
                <ul class="steps-list">
                    <li>
                        <span class="step-number">1</span>
                        <span class="step-text">完善您的个人资料和头像</span>
                    </li>
                    <li>
                        <span class="step-number">2</span>
                        <span class="step-text">设置您的偏好和通知选项</span>
                    </li>
                    <li>
                        <span class="step-number">3</span>
                        <span class="step-text">探索平台的各项功能和服务</span>
                    </li>
                    <li>
                        <span class="step-number">4</span>
                        <span class="step-text">加入社区，与其他用户互动交流</span>
                    </li>
                </ul>
            </div>
            
            <div class="support-section">
                <h3>💬 需要帮助？</h3>
                <p>如果您在使用过程中遇到任何问题，我们的客服团队随时为您提供支持。</p>
                <p>您也可以查看我们的帮助文档或参加新手引导教程。</p>
                <div class="support-links">
                    <a href="${helpUrl!'#'}">帮助中心</a>
                    <a href="${tutorialUrl!'#'}">新手教程</a>
                    <a href="${contactUrl!'#'}">联系客服</a>
                    <a href="${faqUrl!'#'}">常见问题</a>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 50px; padding-top: 30px; border-top: 1px solid #e5e7eb;">
                <p style="color: #6b7280; font-size: 16px;">
                    再次感谢您选择 Slavopolis！<br>
                    我们期待为您提供优质的服务体验。
                </p>
                <p style="color: #9ca3af; font-size: 14px; margin-top: 20px;">
                    <strong>Slavopolis 团队</strong><br>
                    ${.now?string("yyyy年MM月dd日")}
                </p>
            </div>
        </div>
        
        <div class="footer">
            <div class="social-links">
                <a href="${twitterUrl!'#'}">Twitter</a>
                <a href="${facebookUrl!'#'}">Facebook</a>
                <a href="${linkedinUrl!'#'}">LinkedIn</a>
                <a href="${wechatUrl!'#'}">微信</a>
            </div>
            
            <div class="links">
                <a href="${privacyUrl!'#'}">隐私政策</a>
                <a href="${termsUrl!'#'}">服务条款</a>
                <a href="${unsubscribeUrl!'#'}">取消订阅</a>
                <a href="${supportUrl!'#'}">技术支持</a>
            </div>
            
            <div class="brand-info">
                <p>&copy; ${.now?string("yyyy")} Slavopolis. 保留所有权利。</p>
                <p>此邮件由系统自动发送，请勿直接回复。</p>
            </div>
        </div>
    </div>
</body>
</html> 