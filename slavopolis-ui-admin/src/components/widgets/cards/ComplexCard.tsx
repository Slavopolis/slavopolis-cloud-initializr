// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import {
    Avatar,
    Box,
    CardContent,
    CardMedia,
    Chip,
    Grid,
    Skeleton,
    Stack,
    Tooltip,
    Typography
} from '@mui/material';
import { IconEye, IconMessage2, IconPoint } from '@tabler/icons-react';
import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import img1 from 'src/assets/images/blog/blog-img1.jpg';
import img2 from 'src/assets/images/blog/blog-img2.jpg';
import img3 from 'src/assets/images/blog/blog-img3.jpg';
import user1 from 'src/assets/images/profile/user-1.jpg';
import user2 from 'src/assets/images/profile/user-2.jpg';
import user3 from 'src/assets/images/profile/user-3.jpg';

import BlankCard from '../../shared/BlankCard.tsx';

interface cardType {
  avatar: string;
  coveravatar: string;
  title: string;
  category: string;
  name: string;
  view: string;
  comments: string;
  time: string;
}

const complexCard: cardType[] = [
  {
    avatar: user1,
    coveravatar: img1,
    title: '随着日元贬值，热爱电子产品的日本转向二手iPhone',
    category: '社交',
    name: 'Georgeanna Ramero',
    view: '9,125',
    comments: '3',
    time: '周一, 12月19日',
  },
  {
    avatar: user2,
    coveravatar: img2,
    title: '英特尔失去恢复针对专利对手Fortress的反垄断案的机会',
    category: '电子产品',
    name: 'Georgeanna Ramero',
    view: '4,150',
    comments: '38',
    time: '周日, 12月18日',
  },
  {
    avatar: user3,
    coveravatar: img3,
    title: '随着中国更多地区面临封锁，新冠疫情加剧',
    category: '健康',
    name: 'Georgeanna Ramero',
    view: '9,480',
    comments: '12',
    time: '周六, 12月17日',
  },
];

const ComplexCard = () => {
  const [isLoading, setLoading] = React.useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setLoading(false);
    }, 700);

    return () => clearTimeout(timer);
  }, []);

  return (
    <Grid container spacing={3}>
      {complexCard.map((author, index) => (
        <Grid item xs={12} sm={4} key={index}>
          <BlankCard className="hoverCard">
            <>
              <Typography component={Link} to="/">
                {isLoading ? (
                  <Skeleton variant="rectangular" animation="wave" width="100%" height={240}></Skeleton>
                ) : (
                  <CardMedia
                    component="img"
                    height="240"
                    image={author.coveravatar}
                    alt="green iguana"
                  />
                )}
              </Typography>
              <CardContent>
                <Stack direction="row" sx={{ marginTop: '-45px' }}>
                  <Tooltip title={author.name} placement="top">
                    <Avatar aria-label="recipe" src={author.avatar}></Avatar>
                  </Tooltip>
                  <Chip
                    sx={{ marginLeft: 'auto', marginTop: '-21px', backgroundColor: 'white' }}
                    label="阅读时间2分钟"
                    size="small"
                  ></Chip>
                </Stack>
                <Chip label={author.category} size="small" sx={{ marginTop: 2 }}></Chip>
                <Box my={3}>
                  <Typography
                    gutterBottom
                    variant="h5"
                    color="inherit"
                    sx={{ textDecoration: 'none' }}
                    component={Link}
                    to="/"
                  >
                    {author.title}
                  </Typography>
                </Box>
                <Stack direction="row" gap={3} alignItems="center">
                  <Stack direction="row" gap={1} alignItems="center">
                    <IconEye size="18" /> {author.view}
                  </Stack>
                  <Stack direction="row" gap={1} alignItems="center">
                    <IconMessage2 size="18" /> {author.comments}
                  </Stack>

                  <Stack direction="row" ml="auto" alignItems="center">
                    <IconPoint size="16" />
                    <small>{author.time}</small>
                  </Stack>
                </Stack>
              </CardContent>
            </>
          </BlankCard>
        </Grid>
      ))}
    </Grid>
  );
};

export default ComplexCard;
