// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Box, Button, CardContent, Typography } from '@mui/material';
import shopBg from 'src/assets/images/products/empty-shopping-cart.svg';
import BlankCard from '../../shared/BlankCard.tsx';

const Banner5 = () => {
  return (
    <BlankCard>
      <CardContent sx={{ p: '30px' }}>
        <Box textAlign="center">
          <img src={shopBg} alt="star" width={200} />

          <Typography variant="h5" mt={3}>
            哎呀，您的购物车是空的！
          </Typography>
          <Typography variant="subtitle1" color="textSecondary" mt={1} mb={2}>
            回到购物中并从中获得奖励。
          </Typography>

          <Button color="primary" variant="contained" size="large">
            去购物吧！
          </Button>
        </Box>
      </CardContent>
    </BlankCard>
  );
};

export default Banner5;
