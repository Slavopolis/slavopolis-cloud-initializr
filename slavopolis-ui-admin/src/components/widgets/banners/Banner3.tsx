// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { CardContent, Typography, Button, Avatar, Badge, Box, Stack } from '@mui/material';
import userBg from 'src/assets/images/profile/user-1.jpg';
import BlankCard from '../../shared/BlankCard.tsx';

const Banner3 = () => {
  return (
    <BlankCard>
      <CardContent sx={{ p: '30px' }}>
        <Typography variant="h5" textAlign="center" mb={3}>
          共同好友已揭晓
        </Typography>
        <Box textAlign="center">
          <Badge badgeContent={1} color="error" overlap="circular">
            <Avatar src={userBg} alt="userBg" sx={{ width: 140, height: 140 }} />
          </Badge>

          <Typography variant="h5" mt={3}>
            Tommoie Henderson
          </Typography>
          <Typography variant="subtitle1" color="textSecondary" mt={1} mb={2}>
            接受请求并<br/> 输入消息
          </Typography>

          <Stack direction="row" spacing={2} justifyContent="center">
            <Button color="primary" variant="contained" size="large">
              接受
            </Button>
            <Button color="error" variant="outlined" size="large">
              移除
            </Button>
          </Stack>
        </Box>
      </CardContent>
    </BlankCard>
  );
};

export default Banner3;
