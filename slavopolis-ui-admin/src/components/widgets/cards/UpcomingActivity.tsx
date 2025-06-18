// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { useTheme } from '@mui/material/styles';
import { Stack, Typography, Avatar, Box } from '@mui/material';
import DashboardCard from '../../shared/DashboardCard.tsx';
import { IconDatabase, IconMail, IconMapPin, IconPhone, IconScreenShare } from '@tabler/icons-react';

const UpcomingActivity = () => {
  // chart color
  const theme = useTheme();
  const primary = theme.palette.primary.main;
  const primarylight = theme.palette.primary.light;
  const error = theme.palette.error.main;
  const errorlight = theme.palette.error.light;
  const warning = theme.palette.warning.main;
  const warninglight = theme.palette.warning.light;
  const secondary = theme.palette.secondary.main;
  const secondarylight = theme.palette.secondary.light;
  const success = theme.palette.success.main;
  const successlight = theme.palette.success.light;

  interface statType {
    title: string;
    subtitle: string;
    time: number;
    color: string;
    lightcolor: string;
    icon: JSX.Element;
  }

  const stats: statType[] = [
    {
      title: '新加坡之旅',
      subtitle: '进行中',
      time: 5,
      color: primary,
      lightcolor: primarylight,
      icon: <IconMapPin width={20} />,
    },
    {
      title: '归档数据',
      subtitle: '进行中',
      time: 10,
      color: secondary,
      lightcolor: secondarylight,
      icon: <IconDatabase width={20} />,
    },
    {
      title: '与客户会面',
      subtitle: '待处理',
      time: 15,
      color: warning,
      lightcolor: warninglight,
      icon: <IconPhone width={20} />,
    },
    {
      title: '筛选任务团队',
      subtitle: '进行中',
      time: 20,
      color: error,
      lightcolor: errorlight,
      icon: <IconScreenShare width={20} />,
    },
    {
      title: '发送信封给John',
      subtitle: '已完成',
      time: 20,
      color: success,
      lightcolor: successlight,
      icon: <IconMail width={20} />,
    },
  ];

  return (
    <DashboardCard title="即将进行的活动" subtitle='在新年'>
      <>
        <Stack spacing={3} mt={5}>
          {stats.map((stat, i) => (
            <Stack
              direction="row"
              spacing={3}
              justifyContent="space-between"
              alignItems="center"
              key={i}
            >
              <Stack direction="row" alignItems="center" spacing={2}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: stat.lightcolor, color: stat.color, width: 40, height: 40 }}
                >
                  {stat.icon}
                </Avatar>
                <Box>
                  <Typography variant="h6" mb="4px">
                    {stat.title}
                  </Typography>
                  <Typography variant="subtitle2" color="textSecondary">
                    {stat.subtitle}
                  </Typography>
                </Box>
              </Stack>

              <Typography variant="subtitle2" color="textSecondary">
                {stat.time} 分钟
              </Typography>
            </Stack>
          ))}
        </Stack>
      </>
    </DashboardCard>
  );
};

export default UpcomingActivity;
