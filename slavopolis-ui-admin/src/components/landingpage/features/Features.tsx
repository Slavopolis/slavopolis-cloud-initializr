// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import FeaturesTitle from './FeaturesTitle.tsx';
import { Typography, Grid, Container, Box } from '@mui/material';
import {
  IconAdjustments,
  IconArchive,
  IconArrowsShuffle,
  IconBook,
  IconBuildingCarousel,
  IconCalendar,
  IconChartPie,
  IconDatabase,
  IconDiamond,
  IconLanguageKatakana,
  IconLayersIntersect,
  IconMessages,
  IconRefresh,
  IconShieldLock,
  IconTag,
  IconWand,
} from '@tabler/icons-react';
import AnimationFadeIn from '../animation/Animation.tsx';

interface FeaturesType {
  icon: React.ReactElement;
  title: string;
  subtext: string;
}

const featuresData: FeaturesType[] = [
  {
    icon: <IconWand width={40} height={40} strokeWidth={1.5} />,
    title: '6种主题颜色',
    subtext: '我们为优雅的管理后台预设了6种主题颜色。',
  },
  {
    icon: <IconShieldLock width={40} height={40} strokeWidth={1.5} />,
    title: 'JWT + Firebase认证',
    subtext: '使用JSON对象在网络上安全传输信息。',
  },
  {
    icon: <IconArchive width={40} height={40} strokeWidth={1.5} />,
    title: '50+页面模板',
    subtext: '是的，我们提供5个演示版本，每个版本包含50+个页面，使用更加便捷。',
  },
  {
    icon: <IconAdjustments width={40} height={40} strokeWidth={1.5} />,
    title: '45+UI组件',
    subtext: 'Modernize管理后台套件提供近45+个UI组件。',
  },
  {
    icon: <IconTag width={40} height={40} strokeWidth={1.5} />,
    title: 'Material UI',
    subtext: '使用Material UI构建，支持完全响应式布局。',
  },
  {
    icon: <IconDiamond width={40} height={40} strokeWidth={1.5} />,
    title: '3400+字体图标',
    subtext: '优雅的管理后台套件中包含大量图标字体。',
  },
  {
    icon: <IconDatabase width={40} height={40} strokeWidth={1.5} />,
    title: 'Axios',
    subtext: 'Axios是一个基于Promise的Node.js和浏览器HTTP客户端。',
  },
  {
    icon: <IconLanguageKatakana width={40} height={40} strokeWidth={1.5} />,
    title: 'i18 React',
    subtext: 'react-i18是一个强大的React国际化框架。',
  },
  {
    icon: <IconBuildingCarousel width={40} height={40} strokeWidth={1.5} />,
    title: 'Slick轮播',
    subtext: '这是您需要的最后一个React轮播组件！',
  },
  {
    icon: <IconArrowsShuffle width={40} height={40} strokeWidth={1.5} />,
    title: '易于定制',
    subtext: '我们理解您的需求，定制将变得轻而易举。',
  },
  {
    icon: <IconChartPie width={40} height={40} strokeWidth={1.5} />,
    title: '丰富的图表选项',
    subtext: '您说需要什么，我们就有什么，提供多种图表变体。',
  },
  {
    icon: <IconLayersIntersect width={40} height={40} strokeWidth={1.5} />,
    title: '丰富的表格示例',
    subtext: '数据表格是基本需求，我们已经为您添加。',
  },
  {
    icon: <IconRefresh width={40} height={40} strokeWidth={1.5} />,
    title: '定期更新',
    subtext: '我们不断用新功能更新我们的套件。',
  },
  {
    icon: <IconBook width={40} height={40} strokeWidth={1.5} />,
    title: '详细文档',
    subtext: '我们提供了详细的文档，使用起来更加简单。',
  },
  {
    icon: <IconCalendar width={40} height={40} strokeWidth={1.5} />,
    title: '日历设计',
    subtext: '我们的套件提供美观的日历功能。',
  },
  {
    icon: <IconMessages width={40} height={40} strokeWidth={1.5} />,
    title: '专业支持',
    subtext: '我们相信卓越的支持是关键，我们为您提供这一切。'
  },
];

const Features = () => {
  return (
    <Box py={6}>
      <Container maxWidth="lg">
        <FeaturesTitle />
        <AnimationFadeIn>
          <Box mt={6}>
            <Grid container spacing={3}>
              {featuresData.map((feature, index) => (
                <Grid item xs={12} sm={4} lg={3} textAlign="center" key={index}>
                  <Box color="primary.main">{feature.icon}</Box>
                  <Typography variant="h5" mt={3}>
                    {feature.title}
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary" mt={1} mb={3}>
                    {feature.subtext}
                  </Typography>
                </Grid>
              ))}
            </Grid>
          </Box>
        </AnimationFadeIn>
      </Container>
    </Box>
  );
};

export default Features;
