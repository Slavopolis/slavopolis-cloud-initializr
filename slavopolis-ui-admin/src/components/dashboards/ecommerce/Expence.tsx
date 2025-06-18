// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Typography } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import Chart, { Props } from 'react-apexcharts';

import DashboardCard from '../../shared/DashboardCard.tsx';

const Expence = () => {
  // chart color
  const theme = useTheme();
  const primary = theme.palette.primary.main;
  const secondary = theme.palette.secondary.main;
  const error = theme.palette.error.main;

  // chart
  const optionsexpencechart: Props = {
    chart: {
      type: 'donut',
      fontFamily: "'Plus Jakarta Sans', sans-serif;",

      toolbar: {
        show: false,
      },
      height: 120,
    },
    labels: ["利润", "收入", "支出"],
    colors: [primary, error, secondary],
    plotOptions: {
      pie: {
        
        donut: {
          size: '70%',
          background: 'transparent'
        },
      },
    },
    dataLabels: {
      enabled: false,
    },
    stroke: {
      show: false,
    },
    legend: {
      show: false,
    },
    tooltip: {
      theme: theme.palette.mode === 'dark' ? 'dark' : 'light',
      fillSeriesColor: false,
    },
  };
  const seriesexpencechart = [60, 25, 15];

  return (
    <DashboardCard>
      <>
        <Typography variant="h4">$10,230</Typography>
        <Typography variant="subtitle2" color="textSecondary" mb={2}>
          支出
        </Typography>
        <Chart
          options={optionsexpencechart}
          series={seriesexpencechart}
          type="donut"
          height="120"
        />
      </>
    </DashboardCard>
  );
};

export default Expence;
