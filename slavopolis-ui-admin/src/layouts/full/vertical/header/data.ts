import img1 from 'src/assets/images/profile/user-1.jpg';
import img2 from 'src/assets/images/profile/user-2.jpg';
import img3 from 'src/assets/images/profile/user-3.jpg';
import img4 from 'src/assets/images/profile/user-4.jpg';

import icon1 from 'src/assets/images/svgs/icon-account.svg';
import icon2 from 'src/assets/images/svgs/icon-inbox.svg';
import icon3 from 'src/assets/images/svgs/icon-tasks.svg';

import ddIcon8 from 'src/assets/images/svgs/icon-dd-application.svg';
import ddIcon2 from 'src/assets/images/svgs/icon-dd-cart.svg';
import ddIcon1 from 'src/assets/images/svgs/icon-dd-chat.svg';
import ddIcon4 from 'src/assets/images/svgs/icon-dd-date.svg';
import ddIcon3 from 'src/assets/images/svgs/icon-dd-invoice.svg';
import ddIcon6 from 'src/assets/images/svgs/icon-dd-lifebuoy.svg';
import ddIcon7 from 'src/assets/images/svgs/icon-dd-message-box.svg';
import ddIcon5 from 'src/assets/images/svgs/icon-dd-mobile.svg';

// Notifications dropdown

interface notificationType {
    avatar: string;
    title: string;
    subtitle: string;
}

const notifications: notificationType[] = [
    {
        avatar: img1,
        title: '罗马加入团队！',
        subtitle: '恭喜他',
    },
    {
        avatar: img2,
        title: '新消息已接收',
        subtitle: '沙拉玛发送了新消息',
    },
    {
        avatar: img3,
        title: '新支付已接收',
        subtitle: '检查您的收益',
    },
    {
        avatar: img4,
        title: '约丽完成任务',
        subtitle: '指派她新任务',
    },
    {
        avatar: img1,
        title: '罗马加入团队！',
        subtitle: '恭喜他',
    },
    {
        avatar: img2,
        title: '新消息已接收',
        subtitle: '沙拉玛发送了新消息',
    },
    {
        avatar: img3,
        title: '新支付已接收',
        subtitle: '检查您的收益',
    },
    {
        avatar: img4,
        title: '约丽完成任务',
        subtitle: '指派她新任务',
    },
];

//
// Messages dropdown
//
interface messageType {
    avatar: string;
    title: string;
    subtitle: string;
    time: string;
    status: string;
}
const messages: messageType[] = [
    {
        avatar: img1,
        title: '罗马加入团队！',
        subtitle: '恭喜他',
        time: '1 个小时前',
        status: 'success',
    },
    {
        avatar: img2,
        title: '新消息已接收',
        subtitle: '沙拉玛发送了新消息',
        time: '1 天前',
        status: 'warning',
    },
    {
        avatar: img3,
        title: '新支付已接收',
        subtitle: '检查您的收益',
        time: '2 天前',
        status: 'success',
    },
    {
        avatar: img4,
        title: '约丽完成任务',
        subtitle: '指派她新任务',
        time: '1 周前',
        status: 'danger',
    },
];

//
// Profile dropdown
//
interface ProfileType {
    href: string;
    title: string;
    subtitle: string;
    icon: any;
}
const profile: ProfileType[] = [
    {
        href: '/user-profile',
        title: '我的档案',
        subtitle: '账户设置',
        icon: icon1,
    },
    {
        href: '/apps/email',
        title: '我的邮箱',
        subtitle: '消息和电子邮件',
        icon: icon2,
    },
    {
        href: '/apps/notes',
        title: '我的任务',
        subtitle: '待做和日常任务',
        icon: icon3,
    },
];

// apps dropdown

interface appsLinkType {
    href: string;
    title: string;
    subtext: string;
    avatar: string;
}

const appsLink: appsLinkType[] = [
    {
        href: '/apps/chats',
        title: '聊天应用',
        subtext: '新消息到达',
        avatar: ddIcon1
    },
    {
        href: '/apps/ecommerce/shop',
        title: '电子商务应用',
        subtext: '新库存可用',
        avatar: ddIcon2
    },
    {
        href: '/apps/notes',
        title: '笔记应用',
        subtext: '待做和日常任务',
        avatar: ddIcon3
    },
    {
        href: '/apps/calendar',
        title: '日历应用程序',
        subtext: '获取日期',
        avatar: ddIcon4
    },
    {
        href: '/apps/contacts',
        title: '联系人应用程序',
        subtext: '2 个未保存的联系人',
        avatar: ddIcon5
    },
    {
        href: '/apps/tickets',
        title: '工单应用程序',
        subtext: '提交工单',
        avatar: ddIcon6
    },
    {
        href: '/apps/email',
        title: '电子邮件应用程序',
        subtext: '获取新电子邮件',
        avatar: ddIcon7
    },
    {
        href: '/apps/blog/posts',
        title: '博客应用程序',
        subtext: '添加新博客',
        avatar: ddIcon8
    },
]


interface LinkType {
    href: string;
    title: string;
}

const pageLinks: LinkType[] = [
    {
        href: '/pricing',
        title: '定价页面'
    },
    {
        href: '/auth/login',
        title: '认证设计'
    },
    {
        href: '/auth/register',
        title: '立即注册'
    },
    {
        href: '/404',
        title: '404 错误页面'
    },
    {
        href: '/auth/login',
        title: '登录页面'
    },
    {
        href: '/user-profile',
        title: '用户应用程序'
    },
    {
        href: '/apps/blog/posts',
        title: '博客设计'
    },
    {
        href: '/apps/ecommerce/eco-checkout',
        title: '购物车'
    },
]

export { appsLink, messages, notifications, pageLinks, profile };

