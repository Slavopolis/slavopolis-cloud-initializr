// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Grid, Typography, AvatarGroup, Avatar, Stack, Button, Box } from '@mui/material';

import user1 from 'src/assets/images/profile/user-1.jpg';
import user2 from 'src/assets/images/profile/user-2.jpg';
import user3 from 'src/assets/images/profile/user-3.jpg';

const StillQuestions = () => {
  return (
    <Grid container spacing={3} justifyContent="center">
      <Grid item xs={12} lg={10}>
        <Box bgcolor="primary.light" p={5} mt={7}>
          <Stack>
            <AvatarGroup sx={{ flexDirection: 'row', justifyContent: 'center' }}>
              <Avatar alt="Remy Sharp" src={user1} />
              <Avatar alt="Travis Howard" src={user2} />
              <Avatar alt="Cindy Baker" src={user3} />
            </AvatarGroup>
          </Stack>

          <Typography variant="h3" textAlign="center" mt={3} mb={1}>
            还有问题？
          </Typography>
          <Typography variant="h6" fontWeight={400} lineHeight="23px" color="textSecondary" textAlign="center">
            找不到您要找的答案？请与我们友好的团队聊天。
          </Typography>
          <Box textAlign="center" mt={3}>
            <Button variant="contained" color="primary">
              与我们聊天
            </Button>
          </Box>
        </Box>
      </Grid>
    </Grid>
  );
};

export default StillQuestions;
