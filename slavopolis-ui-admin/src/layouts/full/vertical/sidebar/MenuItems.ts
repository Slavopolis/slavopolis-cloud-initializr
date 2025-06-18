import {
    IconAlertCircle,
    IconAperture,
    IconApps,
    IconAppWindow,
    IconAward,
    IconBan,
    IconBasket,
    IconBorderAll,
    IconBorderHorizontal,
    IconBorderInner,
    IconBorderStyle2,
    IconBorderTop,
    IconBorderVertical,
    IconBox,
    IconBoxAlignBottom,
    IconBoxAlignLeft,
    IconBoxMultiple,
    IconCalendar,
    IconChartArcs,
    IconChartArea,
    IconChartCandle,
    IconChartDonut3,
    IconChartDots,
    IconChartLine,
    IconChartRadar,
    IconCurrencyDollar,
    IconEdit,
    IconFileDescription,
    IconFileDots,
    IconFiles,
    IconGitMerge,
    IconHelp,
    IconLayout,
    IconLogin,
    IconMail,
    IconMessage2,
    IconMoodSmile,
    IconNotes,
    IconPackage,
    IconPoint,
    IconRotate,
    IconSettings,
    IconShoppingCart,
    IconStar,
    IconTicket,
    IconUserCircle,
    IconUserPlus,
    IconZoomCode
} from '@tabler/icons-react';
import { uniqueId } from 'lodash';
import React from 'react';

interface MenuitemsType {
    [x: string]: unknown;
    id?: string;
    navlabel?: boolean;
    subheader?: string;
    title?: string;
    icon?: React.ElementType;
    href?: string;
    children?: MenuitemsType[];
    chip?: string;
    chipColor?: string;
    variant?: string;
    external?: boolean;
}

const Menuitems: MenuitemsType[] = [
    {
        navlabel: true,
        subheader: '首页',
    },

    {
        id: uniqueId(),
        title: '现代的',
        icon: IconAperture,
        href: '/dashboards/modern',
        chip: '新',
        chipColor: 'secondary',
    },
    {
        id: uniqueId(),
        title: '现代的电子商务',
        icon: IconShoppingCart,
        href: '/dashboards/ecommerce',
    },
    {
        navlabel: true,
        subheader: '应用',
    },
    {
        id: uniqueId(),
        title: '联系人',
        icon: IconPackage,
        chip: '2',
        chipColor: 'secondary',
        href: '/apps/contacts',
    },

    {
        id: uniqueId(),
        title: '博客',
        icon: IconChartDonut3,
        href: '/apps/blog/',
        children: [
            {
                id: uniqueId(),
                title: '文章',
                icon: IconPoint,
                href: '/apps/blog/posts',
            },
            {
                id: uniqueId(),
                title: '详情',
                icon: IconPoint,
                href: '/apps/blog/detail/streaming-video-way-before-it-was-cool-go-dark-tomorrow',
            },
        ],
    },
    {
        id: uniqueId(),
        title: '博客',
        icon: IconBasket,
        href: '/apps/ecommerce/',
        children: [
            {
                id: uniqueId(),
                title: '文章',
                icon: IconPoint,
                href: '/apps/ecommerce/shop',
            },
            {
                id: uniqueId(),
                title: '详情',
                icon: IconPoint,
                href: '/apps/ecommerce/detail/1',
            },
            {
                id: uniqueId(),
                title: '文章列表',
                icon: IconPoint,
                href: '/apps/ecommerce/eco-product-list',
            },
            {
                id: uniqueId(),
                title: '购物车',
                icon: IconPoint,
                href: '/apps/ecommerce/eco-checkout',
            },
        ],
    },
    {
        id: uniqueId(),
        title: '聊天',
        icon: IconMessage2,
        href: '/apps/chats',
    },
    {
        id: uniqueId(),
        title: '用户',
        icon: IconUserCircle,
        href: '/user-profile',
        children: [
            {
                id: uniqueId(),
                title: '用户',
                icon: IconPoint,
                href: '/user-profile',
            },
            {
                id: uniqueId(),
                title: '关注',
                icon: IconPoint,
                href: '/apps/followers',
            },
            {
                id: uniqueId(),
                title: '好友',
                icon: IconPoint,
                href: '/apps/friends',
            },
            {
                id: uniqueId(),
                title: '好友',
                icon: IconPoint,
                href: '/apps/gallery',
            },
        ],
    },
    {
        id: uniqueId(),
        title: '笔记',
        icon: IconNotes,
        href: '/apps/notes',
    },
    {
        id: uniqueId(),
        title: '日历',
        icon: IconCalendar,
        href: '/apps/calendar',
    },
    {
        id: uniqueId(),
        title: '邮件',
        icon: IconMail,
        href: '/apps/email',
    },
    {
        id: uniqueId(),
        title: '发票',
        icon: IconTicket,
        href: '/apps/tickets',
    },
    {
        navlabel: true,
        subheader: '页面',
    },

    {
        id: uniqueId(),
        title: '树状视图',
        icon: IconGitMerge,
        href: '/pages/treeview',
    },
    {
        id: uniqueId(),
        title: '价格',
        icon: IconCurrencyDollar,
        href: '/pages/pricing',
    },
    {
        id: uniqueId(),
        title: '账户设置',
        icon: IconUserCircle,
        href: '/pages/account-settings',
    },
    {
        id: uniqueId(),
        title: 'FAQ',
        icon: IconHelp,
        href: '/pages/faq',
    },
    {
        id: uniqueId(),
        title: '着陆页',
        icon: IconAppWindow,
        href: '/landingpage',
    },
    {
        id: uniqueId(),
        title: '小部件',
        icon: IconLayout,
        href: '/widgets/cards',
        children: [
            {
                id: uniqueId(),
                title: '卡片',
                icon: IconPoint,
                href: '/widgets/cards',
            },
            {
                id: uniqueId(),
                title: '横幅',
                icon: IconPoint,
                href: '/widgets/banners',
            },
            {
                id: uniqueId(),
                title: '图表',
                icon: IconPoint,
                href: '/widgets/charts',
            },
        ],
    },
    {
        navlabel: true,
        subheader: '表单',
    },
    {
        id: uniqueId(),
        title: '表单元素',
        icon: IconApps,
        href: '/forms/form-elements/autocomplete',
        children: [
            {
                id: uniqueId(),
                title: '自动完成',
                icon: IconPoint,
                href: '/forms/form-elements/autocomplete',
            },
            {
                id: uniqueId(),
                title: '按钮',
                icon: IconPoint,
                href: '/forms/form-elements/button',
            },
            {
                id: uniqueId(),
                title: '复选框',
                icon: IconPoint,
                href: '/forms/form-elements/checkbox',
            },
            {
                id: uniqueId(),
                title: '单选框',
                icon: IconPoint,
                href: '/forms/form-elements/radio',
            },
            {
                id: uniqueId(),
                title: '日期时间',
                icon: IconPoint,
                href: '/forms/form-elements/date-time',
            },
            {
                id: uniqueId(),
                title: '滑块',
                icon: IconPoint,
                href: '/forms/form-elements/slider',
            },
            {
                id: uniqueId(),
                title: '开关',
                icon: IconPoint,
                href: '/forms/form-elements/switch',
            },
        ],
    },
    {
        id: uniqueId(),
        title: '表单布局',
        icon: IconFileDescription,
        href: '/forms/form-layouts',
    },
    {
        id: uniqueId(),
        title: '水平表单',
        icon: IconBoxAlignBottom,
        href: '/forms/form-horizontal',
    },
    {
        id: uniqueId(),
        title: '垂直表单',
        icon: IconBoxAlignLeft,
        href: '/forms/form-vertical',
    },
    {
        id: uniqueId(),
        title: '自定义表单',
        icon: IconFileDots,
        href: '/forms/form-custom',
    },
    {
        id: uniqueId(),
        title: '表单向导',
        icon: IconFiles,
        href: '/forms/form-wizard',
    },
    {
        id: uniqueId(),
        title: '表单验证',
        icon: IconFiles,
        href: '/forms/form-validation',
    },
    {
        id: uniqueId(),
        title: 'Quill 编辑器',
        icon: IconEdit,
        href: '/forms/quill-editor',
    },
    {
        navlabel: true,
        subheader: '表格',
    },
    {
        id: uniqueId(),
        title: '基础表格',
        icon: IconBorderAll,
        href: '/tables/basic',
    },
    {
        id: uniqueId(),
        title: '可折叠表格',
        icon: IconBorderHorizontal,
        href: '/tables/collapsible',
    },
    {
        id: uniqueId(),
        title: '增强表格',
        icon: IconBorderInner,
        href: '/tables/enhanced',
    },
    {
        id: uniqueId(),
        title: '固定表头',
        icon: IconBorderVertical,
        href: '/tables/fixed-header',
    },
    {
        id: uniqueId(),
        title: '分页',
        icon: IconBorderTop,
        href: '/tables/pagination',
    },
    {
        id: uniqueId(),
        title: '搜索',
        icon: IconBorderStyle2,
        href: '/tables/search',
    },
    {
        navlabel: true,
        subheader: '用户界面',
    },
    {
        id: uniqueId(),
        title: 'UI 组件',
        icon: IconBox,
        href: '/ui-components/alert',
        children: [
            {
                id: uniqueId(),
                title: '警告',
                icon: IconPoint,
                href: '/ui-components/alert',
            },
            {
                id: uniqueId(),
                title: '手风琴',
                icon: IconPoint,
                href: '/ui-components/accordion',
            },
            {
                id: uniqueId(),
                title: '头像',
                icon: IconPoint,
                href: '/ui-components/avatar',
            },
            {
                id: uniqueId(),
                title: '标签',
                icon: IconPoint,
                href: '/ui-components/chip',
            },
            {
                id: uniqueId(),
                title: '对话框',
                icon: IconPoint,
                href: '/ui-components/dialog',
            },
            {
                id: uniqueId(),
                title: '列表',
                icon: IconPoint,
                href: '/ui-components/list',
            },
            {
                id: uniqueId(),
                title: '弹出框',
                icon: IconPoint,
                href: '/ui-components/popover',
            },
            {
                id: uniqueId(),
                title: '评分',
                icon: IconPoint,
                href: '/ui-components/rating',
            },
            {
                id: uniqueId(),
                title: '选项卡',
                icon: IconPoint,
                href: '/ui-components/tabs',
            },
            {
                id: uniqueId(),
                title: '提示框',
                icon: IconPoint,
                href: '/ui-components/tooltip',
            },
            {
                id: uniqueId(),
                title: '传输列表',
                icon: IconPoint,
                href: '/ui-components/transfer-list',
            },
            {
                id: uniqueId(),
                title: '排版',
                icon: IconPoint,
                href: '/ui-components/typography',
            },
        ],
    },

    {
        navlabel: true,
        subheader: '图表',
    },
    {
        id: uniqueId(),
        title: '线形图',
        icon: IconChartLine,
        href: '/charts/line-chart',
    },
    {
        id: uniqueId(),
        title: '渐变图',
        icon: IconChartArcs,
        href: '/charts/gredient-chart',
    },
    {
        id: uniqueId(),
        title: '区域图',
        icon: IconChartArea,
        href: '/charts/area-chart',
    },
    {
        id: uniqueId(),
        title: '蜡烛图',
        icon: IconChartCandle,
        href: '/charts/candlestick-chart',
    },
    {
        id: uniqueId(),
        title: '柱状图',
        icon: IconChartDots,
        href: '/charts/column-chart',
    },
    {
        id: uniqueId(),
        title: '环形图 & 饼图',
        icon: IconChartDonut3,
        href: '/charts/doughnut-pie-chart',
    },
    {
        id: uniqueId(),
        title: '径向条形图 & 雷达图',
        icon: IconChartRadar,
        href: '/charts/radialbar-chart',
    },
    {
        navlabel: true,
        subheader: '认证',
    },

    {
        id: uniqueId(),
        title: '登录',
        icon: IconLogin,
        href: '/auth/login',
        children: [
            {
                id: uniqueId(),
                title: '侧边登录',
                icon: IconPoint,
                href: '/auth/login',
            },
            {
                id: uniqueId(),
                title: '盒式登录',
                icon: IconPoint,
                href: '/auth/login2',
            },
        ],
    },
    {
        id: uniqueId(),
        title: '注册',
        icon: IconUserPlus,
        href: '/auth/register',
        children: [
            {
                id: uniqueId(),
                title: '侧边注册',
                icon: IconPoint,
                href: '/auth/register',
            },
            {
                id: uniqueId(),
                title: '盒式注册',
                icon: IconPoint,
                href: '/auth/register2',
            },
        ],
    },
    {
        id: uniqueId(),
        title: '忘记密码',
        icon: IconRotate,
        href: '/auth/forgot-password',
        children: [
            {
                id: uniqueId(),
                title: '侧边忘记密码',
                icon: IconPoint,
                href: '/auth/forgot-password',
            },
            {
                id: uniqueId(),
                title: '盒式忘记密码',
                icon: IconPoint,
                href: '/auth/forgot-password2',
            },
        ],
    },

    {
        id: uniqueId(),
        title: '两步验证',
        icon: IconZoomCode,
        href: '/auth/two-steps',
        children: [
            {
                id: uniqueId(),
                title: '侧边两步验证',
                icon: IconPoint,
                href: '/auth/two-steps',
            },
            {
                id: uniqueId(),
                title: '盒式两步验证',
                icon: IconPoint,
                href: '/auth/two-steps2',
            },
        ]
    },
    {
        id: uniqueId(),
        title: '错误',
        icon: IconAlertCircle,
        href: '/400',
    },
    {
        id: uniqueId(),
        title: '维护',
        icon: IconSettings,
        href: '/auth/maintenance',
    },

    {
        navlabel: true,
        subheader: '其他',
    },
    {
        id: uniqueId(),
        title: '菜单层级',
        icon: IconBoxMultiple,
        href: '/menulevel/',
        children: [
            {
                id: uniqueId(),
                title: '层级 1',
                icon: IconPoint,
                href: '/l1',
            },
            {
                id: uniqueId(),
                title: '层级 1.1',
                icon: IconPoint,
                href: '/l1.1',
                children: [
                    {
                        id: uniqueId(),
                        title: '层级 2',
                        icon: IconPoint,
                        href: '/l2',
                    },
                    {
                        id: uniqueId(),
                        title: '层级 2.1',
                        icon: IconPoint,
                        href: '/l2.1',
                        children: [
                            {
                                id: uniqueId(),
                                title: '层级 3',
                                icon: IconPoint,
                                href: '/l3',
                            },
                            {
                                id: uniqueId(),
                                title: '层级 3.1',
                                icon: IconPoint,
                                href: '/l3.1',
                            },
                        ],
                    },
                ],
            },
        ],
    },
    {
        id: uniqueId(),
        title: '禁用',
        icon: IconBan,
        href: '/',
        disabled: true,
    },
    {
        id: uniqueId(),
        title: '子标题',
        subtitle: '这是子标题',
        icon: IconStar,
        href: '/',
    },

    {
        id: uniqueId(),
        title: '标签',
        icon: IconAward,
        href: '/',
        chip: '9',
        chipColor: 'primary',
    },
    {
        id: uniqueId(),
        title: '轮廓',
        icon: IconMoodSmile,
        href: '/',
        chip: 'outline',
        variant: 'outlined',
        chipColor: 'primary',
    },
    {
        id: uniqueId(),
        title: '外部链接',
        external: true,
        icon: IconStar,
        href: 'https://google.com',
    },
];

export default Menuitems;
