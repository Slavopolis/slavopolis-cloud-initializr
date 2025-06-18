// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Box, CardContent, MenuItem, Stack, Typography } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import React from 'react';
import Chart, { Props } from 'react-apexcharts';
import CustomSelect from '../../forms/theme-elements/CustomSelect.tsx';
import BlankCard from '../../shared/BlankCard.tsx';

const MostVisited = () => {
  // for select
  const [month, setMonth] = React.useState('1');

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setMonth(event.target.value);
  };

  // chart color
  const theme = useTheme();
  const primary = theme.palette.primary.main;
  const secondary = theme.palette.secondary.main;

  // chart
  const optionscolumnchart: Props = {
    chart: {
      type: 'bar',
      fontFamily: "'Plus Jakarta Sans', sans-serif;",
      foreColor: '#adb0bb',
      toolbar: {
        show: false,
      },
      height: 250,
      stacked: true,
    },
    colors: [primary, secondary],
    plotOptions: {
      bar: {
        borderRadius: [6],
        horizontal: false,
        barHeight: '60%',
        columnWidth: '40%',
        borderRadiusApplication: 'end',
        borderRadiusWhenStacked: 'all',
      },
    },
    stroke: {
      show: false,
    },
    dataLabels: {
      enabled: false,
    },
    legend: {
      show: false,
    },
    grid: {
      show: false,
    },
    yaxis: {
      tickAmount: 4,
    },
    xaxis: {
      categories: ['01', '02', '03', '04', '05', '06'],
      axisTicks: {
        show: false,
      },
    },
    tooltip: {
      theme: theme.palette.mode === 'dark' ? 'dark' : 'light',
      fillSeriesColor: false,
    },
  };
  const seriescolumnchart = [
    {
      name: 'San Francisco',
      data: [44, 55, 41, 67, 22, 43],
    },
    {
      name: 'Diego',
      data: [13, 23, 20, 8, 13, 27],
    },
  ];

  return (
    <BlankCard>
      <CardContent sx={{ p: '30px' }}>
        <Stack direction="row" justifyContent="space-between">
          <Typography variant="h5">最常访问</Typography>
          <CustomSelect
            labelId="month-dd"
            id="month-dd"
            size="small"
            value={month}
            onChange={handleChange}
          >
            <MenuItem value={1}>2023年3月</MenuItem>
            <MenuItem value={2}>2023年4月</MenuItem>
            <MenuItem value={3}>2023年5月</MenuItem>
          </CustomSelect>
        </Stack>

        <Chart options={optionscolumnchart} series={seriescolumnchart} type="bar" height="250px" />

        <Stack direction="row" spacing={2} justifyContent="center">
          <Box display="flex" alignItems="center" gap={1}>
            <Box
              sx={{
                width: '8px',
                height: '8px',
                backgroundColor: 'primary.main',
                borderRadius: '100%',
              }}
            ></Box>
            <Typography variant="subtitle2" color="textSecondary">
              旧金山
            </Typography>
          </Box>
          <Box display="flex" alignItems="center" gap={1}>
            <Box
              sx={{
                width: '8px',
                height: '8px',
                backgroundColor: 'secondary.main',
                borderRadius: '100%',
              }}
            ></Box>
            <Typography variant="subtitle2" color="textSecondary">
              圣地亚哥
            </Typography>
          </Box>
        </Stack>
      </CardContent>
    </BlankCard>
  );
};

export default MostVisited;
