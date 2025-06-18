// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { CardContent, Typography, Button, Box } from '@mui/material';
import oopsBg from 'src/assets/images/backgrounds/maintenance.svg';
import BlankCard from '../../shared/BlankCard.tsx';

const Banner4 = () => {
  return (
    <BlankCard>
      <CardContent sx={{ p: '30px' }}>
        <Box textAlign="center">
          <img src={oopsBg} alt="star" width={200} />

          <Typography variant="h5" mt={3}>
            哎呀，出了点问题！
          </Typography>
          <Typography variant="subtitle1" color="textSecondary" mt={1} mb={2}>
            正在重试以绕过这些
            <br /> 临时错误。
          </Typography>

          <Button color="error" variant="contained" size="large">
            重试
          </Button>
        </Box>
      </CardContent>
    </BlankCard>
  );
};

export default Banner4;
