// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Grid, Typography } from '@mui/material';
import AnimationFadeIn from '../animation/Animation.tsx';

const TestimonialTitle = () => {
  return (
    <Grid container spacing={3} justifyContent="center">
      <Grid item xs={12} sm={10} lg={8}>
        <AnimationFadeIn>
          <Typography
            variant="h2"
            fontWeight={700}
            textAlign="center"
            sx={{
              fontSize: {
                lg: '36px',
                xs: '25px',
              },
              lineHeight: {
                lg: '43px',
                xs: '30px',
              },
            }}
          >
            不要只听我们说，看看像您这样的开发者都在说什么
          </Typography>
        </AnimationFadeIn>
      </Grid>
    </Grid>
  );
};

export default TestimonialTitle;
