// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import {
  CardContent,
  Grid,
  Typography,
  Box,
  styled,
  Button,
  Container,
  Stack,
} from '@mui/material';
import BlankCard from '../../shared/BlankCard.tsx';
import AnimationFadeIn from '../animation/Animation.tsx';
import bannerbgImg from 'src/assets/images/landingpage/shape/line-bg.svg';

const ImgCard = styled(BlankCard)(() => ({
  backgroundImage: `url(${bannerbgImg})`,
  backgroundRepeat: 'no-repeat',
  backgroundPosition: 'center center',
}));

const StyledButton = styled(Button)(() => ({
  padding: '13px 48px',
  fontSize: '16px',
}));

const StyledButton2 = styled(Button)(({ theme }) => ({
  padding: '13px 48px',
  fontSize: '16px',
  background: theme.palette.background.paper,
}));

const C2a = () => {
  return (
    <Box
      pt={7}
      sx={{
        pb: {
          xs: '70px',
          lg: '120px',
        },
      }}
    >
      <Container maxWidth="lg">
        <AnimationFadeIn>
          <Grid container justifyContent="center" spacing={3}>
            <Grid item xs={12} sm={10} lg={6}>
              <ImgCard>
                <CardContent sx={{ py: 5 }}>
                  <Box textAlign="center">
                    <Typography variant="h3" fontWeight={600}>
                      还没有找到您问题的答案吗？
                    </Typography>
                    <Typography variant="subtitle1" color="textSecondary" mt={1}>
                      通过 discord 或电子邮件与我们联系
                    </Typography>
                  </Box>
                  <Stack
                    direction={{ xs: 'column', sm: 'row' }}
                    spacing={3}
                    mt={5}
                    justifyContent="center"
                    mb={3}
                  >
                    <StyledButton
                      variant="contained"
                      color="primary"
                      href="https://discord.gg/XujgB8ww4n"
                    >
                      在 Discord 上提问
                    </StyledButton>
                    <StyledButton2
                      variant="outlined"
                      color="secondary"
                      href="https://adminmart.com/support"
                    >
                      提交票据
                    </StyledButton2>
                  </Stack>
                </CardContent>
              </ImgCard>
            </Grid>
          </Grid>
        </AnimationFadeIn>
      </Container>
    </Box>
  );
};

export default C2a;
