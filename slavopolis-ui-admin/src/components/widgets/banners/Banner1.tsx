// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import trackBg from 'src/assets/images/backgrounds/login-bg.svg';

const Banner1 = () => {
  return (
    <Card
      elevation={0}
      sx={{
        backgroundColor: (theme) => theme.palette.secondary.light,
        py: 0,
        overflow: 'hidden',
        position: 'relative',
      }}
    >
      <CardContent sx={{ p: '30px' }}>
        <Grid container spacing={3} justifyContent="space-between">
          <Grid item sm={6} display="flex" alignItems="center">
            <Box
              sx={{
                textAlign: {
                  xs: 'center',
                  sm: 'left',
                },
              }}
            >
              <Typography variant="h5">轻松追踪您的每一笔交易</Typography>
              <Typography variant="subtitle1" color="textSecondary" my={2}>
                轻松追踪并记录您的每一笔收入和经历，掌控您的余额
              </Typography>
              <Button variant="contained" color="secondary">
                下载
              </Button>
            </Box>
          </Grid>
          <Grid item sm={4}>
            <Box mb="-90px">
              <img src={trackBg} alt={trackBg} />
            </Box>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default Banner1;
