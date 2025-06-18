import { Box, Container, Button, styled, Typography, Grid, Avatar, Chip } from '@mui/material';

// images
import mainDemo from 'src/assets/images/landingpage/demos/demo-main.jpg';
import darkDemo from 'src/assets/images/landingpage/demos/demo-dark.jpg';
import horizontalDemo from 'src/assets/images/landingpage/demos/demo-horizontal.jpg';
import minisidebarDemo from 'src/assets/images/landingpage/demos/demo-firebase.jpg';
import rtlDemo from 'src/assets/images/landingpage/demos/demo-rtl.jpg';

import app1 from 'src/assets/images/landingpage/apps/app-calendar.jpg';
import app2 from 'src/assets/images/landingpage/apps/app-chat.jpg';
import app3 from 'src/assets/images/landingpage/apps/app-contact.jpg';
import app4 from 'src/assets/images/landingpage/apps/app-email.jpg';
import app5 from 'src/assets/images/landingpage/apps/app-note.jpg';
import app6 from 'src/assets/images/landingpage/apps/app-user-profile.jpg';
import app7 from 'src/assets/images/landingpage/apps/app-blog.jpg';
import app8 from 'src/assets/images/landingpage/apps/app-ticket.jpg';
import app9 from 'src/assets/images/landingpage/apps/app-ecommerce-shop.jpg';
import app10 from 'src/assets/images/landingpage/apps/app-ecommerce-detail.jpg';
import app11 from 'src/assets/images/landingpage/apps/app-ecommerce-checkout.jpg';
import app12 from 'src/assets/images/landingpage/apps/app-ecommerce-list.jpg';
import app13 from 'src/assets/images/landingpage/apps/app-blog-detail.jpg';

import DemoTitle from './DemoTitle.tsx';

const StyledBox = styled(Box)(() => ({
  overflow: 'auto',
  position: 'relative',
  '.MuiButton-root': {
    display: 'none',
  },
  '&:hover': {
    '.MuiButton-root': {
      display: 'block',
      transform: 'translate(-50%,-50%)',
      position: 'absolute',
      left: '50%',
      right: '50%',
      top: '50%',
      minWidth: '100px',
      zIndex: '9',
    },
    '&:before': {
      content: '""',
      position: 'absolute',
      top: '0',
      left: ' 0',
      width: '100%',
      height: '100%',
      zIndex: '8',
      backgroundColor: 'rgba(55,114,255,.2)',
    },
  },
}));

interface DemoTypes {
  link: string;
  img: string;
  title: string;
}

const demos: DemoTypes[] = [
  {
    link: 'https://modernize-react-main.netlify.app/dashboards/modern',
    img: mainDemo,
    title: '主页',
  },
  {
    link: 'https://modernize-react-dark.netlify.app/dashboards/ecommerce',
    img: darkDemo,
    title: '暗色',
  },
  {
    link: 'https://modernize-react-horizontal.netlify.app/dashboards/modern',
    img: horizontalDemo,
    title: '水平布局',
  },
  {
    link: 'https://modernize-react-firebase.netlify.app/auth/login',
    img: minisidebarDemo,
    title: 'Firebase认证',
  },
  {
    link: 'https://modernize-react-rtl.netlify.app/dashboards/modern',
    img: rtlDemo,
    title: '从右到左布局'
  },
];

const apps: DemoTypes[] = [
  {
    link: '/apps/calendar',
    img: app1,
    title: '日历应用',
  },
  {
    link: '/apps/chats',
    img: app2,
    title: '聊天应用',
  },
  {
    link: 'apps/contacts',
    img: app3,
    title: '联系人应用',
  },
  {
    link: 'apps/email',
    img: app4,
    title: '邮件应用',
  },
  {
    link: '/apps/notes',
    img: app5,
    title: '笔记应用',
  },
  {
    link: '/apps/user-profile',
    img: app6,
    title: '用户档案应用',
  },
  {
    link: '/apps/blog/posts',
    img: app7,
    title: '博客应用',
  },
  {
    link: '/apps/blog/detail/streaming-video-way-before-it-was-cool-go-dark-tomorrow',
    img: app13,
    title: '博客详情应用',
  },
  {
    link: '/apps/tickets',
    img: app8,
    title: '工单应用',
  },
  {
    link: '/apps/ecommerce/shop',
    img: app9,
    title: '电商商城应用',
  },
  {
    link: '/apps/ecommerce/detail/1',
    img: app10,
    title: '电商详情应用',
  },
  {
    link: '/apps/ecommerce/eco-checkout',
    img: app11,
    title: '电商结账应用'
  },
  {
    link: '/apps/ecommerce/eco-product-list',
    img: app12,
    title: '电商列表应用'
  },
];

const DemoSlider = () => {
  return (
    <Box
      pb="140px"
      overflow="hidden"
      sx={{
        pt: {
          sm: '60px',
          lg: '0',
        },
      }}
    >
      <Container maxWidth="lg">
        {/* Title */}
        <DemoTitle />

        {/* slider */}
        <Box mt={9}>
          <Grid container spacing={3} justifyContent="center">
            {demos.map((demo, index) => (
              <Grid item xs={12} lg={3} key={index}>
                <Box>
                  {/* <Link href={demo.link}> */}
                  <StyledBox>
                    <Avatar
                      src={demo.img}
                      sx={{
                        borderRadius: '8px',
                        width: '100%',
                        height: '100%',
                      }}
                    />
                    <Button
                      variant="contained"
                      color="primary"
                      size="small"
                      href={demo.link}
                      target="_blank"
                    >
                      在线预览
                    </Button>
                  </StyledBox>
                  {/* </Link> */}
                  <Typography
                    variant="body1"
                    color="textPrimary"
                    textAlign="center"
                    fontWeight={500}
                    mt={2}
                  >
                    {demo.title}
                  </Typography>
                </Box>
              </Grid>
            ))}
          </Grid>
          <Box mb={2} mt={5} textAlign="center">
            <Chip label="应用" color="primary" />
          </Box>
          <Grid container spacing={3} justifyContent="center">
            {apps.map((demo, index) => (
              <Grid item xs={12} lg={3} key={index}>
                <Box>
                  {/* <Link href={demo.link}> */}
                  <StyledBox>
                    <Avatar
                      src={demo.img}
                      sx={{
                        borderRadius: '8px',
                        width: '100%',
                        height: '100%',
                      }}
                    />
                    <Button
                      variant="contained"
                      color="primary"
                      size="small"
                      href={demo.link}
                      target="_blank"
                    >
                      在线预览
                    </Button>
                  </StyledBox>
                  {/* </Link> */}
                  <Typography
                    variant="body1"
                    color="textPrimary"
                    textAlign="center"
                    fontWeight={500}
                    mt={2}
                  >
                    {demo.title}
                  </Typography>
                </Box>
              </Grid>
            ))}
          </Grid>
        </Box>
      </Container>
    </Box>
  );
};

export default DemoSlider;
