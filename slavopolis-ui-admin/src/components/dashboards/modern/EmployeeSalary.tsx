// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { useTheme } from '@mui/material/styles';
import Chart from 'react-apexcharts';

import { Props } from 'react-apexcharts';
import DashboardWidgetCard from '../../shared/DashboardWidgetCard.tsx';

const EmployeeSalary = () => {
  // chart color
  const theme = useTheme();
  const primary = theme.palette.primary.main;
  const primarylight = theme.palette.grey[100];

  // chart
  const optionscolumnchart: Props = {
    chart: {
      type: 'bar',
      fontFamily: "'Plus Jakarta Sans', sans-serif;",
      foreColor: '#adb0bb',
      toolbar: {
        show: false,
      },
      height: 280,
    },
    colors: [primarylight, primarylight, primary, primarylight, primarylight, primarylight],
    plotOptions: {
      bar: {
        borderRadius: 4,
        columnWidth: '45%',
        distributed: true,
        endingShape: 'rounded',
      },
    },
    dataLabels: {
      enabled: false,
    },
    legend: {
      show: false,
    },
    grid: {
      yaxis: {
        lines: {
          show: false,
        },
      },
    },
    xaxis: {
      categories: [['四月'], ['五月'], ['六月'], ['七月'], ['八月'], ['九月']],
      axisBorder: {
        show: false,
      },
    },
    yaxis: {
      labels: {
        show: false,
      },
    },
    tooltip: {
      theme: theme.palette.mode === 'dark' ? 'dark' : 'light',
    },
  };
  const seriescolumnchart = [
    {
      name: '',
      data: [20, 15, 30, 25, 10, 15],
    },
  ];

  return (
    <DashboardWidgetCard
      title="员工薪资"
      subtitle="每月数据"
      dataLabel1="薪资"
      dataItem1="$36,358"
      dataLabel2="利润"
      dataItem2="$5,296"
    >
      <>
        <Chart options={optionscolumnchart} series={seriescolumnchart} type="bar" height="280px" />
      </>
    </DashboardWidgetCard>
  );
};

export default EmployeeSalary;
