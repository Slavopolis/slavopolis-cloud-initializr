// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { CardContent, Typography, Grid, Button, Box, AvatarGroup, Avatar, Stack } from '@mui/material';
import img1 from 'src/assets/images/profile/user-1.jpg';
import img2 from 'src/assets/images/profile/user-2.jpg';
import img3 from 'src/assets/images/profile/user-3.jpg';
import img4 from 'src/assets/images/profile/user-4.jpg';
import BlankCard from '../../shared/BlankCard.tsx';

interface Follower {
  title: string;
  location: string;
  avatar: string;
}

const followerCard: Follower[] = [
  {
    title: 'Andrew Grant',
    location: '萨尔瓦多',
    avatar: img1,
  },
  {
    title: 'Leo Pratt',
    location: '保加利亚',
    avatar: img2,
  },
  {
    title: 'Charles Nunez',
    location: '尼泊尔',
    avatar: img3,
  },
  {
    title: 'Lora Powers',
    location: '尼泊尔',
    avatar: img4,
  },
];

const FriendCard = () => {
  return (
    <Grid container spacing={3}>
      {followerCard.map((card, index) => (
        <Grid item xs={12} sm={6} lg={3} key={index}>
          <BlankCard>
            <CardContent>
              <Avatar src={card.avatar} sx={{ height: 80, width: 80 }}></Avatar>
              <Stack direction="row" spacing={2} mt={3}>
                <Box>
                  <Typography variant="h6" mb={1}>
                    {card.title}
                  </Typography>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <AvatarGroup>
                      <Avatar sx={{ height: 28, width: 28 }} alt="Remy Sharp" src={img1} />
                      <Avatar sx={{ height: 28, width: 28 }} alt="Travis Howard" src={img2} />
                      <Avatar sx={{ height: 28, width: 28 }} alt="Cindy Baker" src={img3} />
                    </AvatarGroup>
                    <Typography variant="subtitle2" color="textSecondary">
                      3个共同好友
                    </Typography>
                  </Stack>
                </Box>
              </Stack>
              <Stack spacing={2} mt={3}>
                <Button size="large" variant="text" color="primary">
                  添加好友
                </Button>
                <Button size="large" variant="text" color="secondary">
                  移除
                </Button>
              </Stack>
            </CardContent>
          </BlankCard>
        </Grid>
      ))}
    </Grid>
  );
};

export default FriendCard;
