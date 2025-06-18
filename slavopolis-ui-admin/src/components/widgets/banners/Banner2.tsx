// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { CardContent, Typography, Button, Box } from '@mui/material';
import starBg from 'src/assets/images/backgrounds/gold.png';
import BlankCard from '../../shared/BlankCard.tsx';

const Banner2 = () => {
  return (
    <BlankCard>
      <CardContent sx={{ p: '30px' }}>
        <Typography variant="subtitle1" textAlign="center" mb={2} textTransform="uppercase" color="textSecondary">
          升级
        </Typography>
        <Box textAlign="center">
          <img src={starBg} alt="star" width={150} />

          <Typography variant="h5">您已查看所有通知</Typography>
          <Typography variant="subtitle1" color="textSecondary" mt={1} mb={2}>恭喜您，<br/> 点击继续下一个任务。</Typography>

          <Button color="primary" variant="contained" size="large">
            是的，明白了！
          </Button>
        </Box>
      </CardContent>
    </BlankCard>
  );
};

export default Banner2;
