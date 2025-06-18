// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Typography, Box, Button, Stack, styled, useMediaQuery, Theme } from '@mui/material';
import { IconRocket } from '@tabler/icons-react';


// third party

import { motion } from 'framer-motion';

const StyledButton = styled(Button)(() => ({
  padding: '13px 48px',
  fontSize: '16px',
}));

const BannerContent = () => {

  const lgDown = useMediaQuery((theme: Theme) => theme.breakpoints.down('lg'));
  
  return (
    <Box mt={lgDown ? 8 : 0}>
      <motion.div
        initial={{ opacity: 0, translateY: 550 }}
        animate={{ opacity: 1, translateY: 0 }}
        transition={{
          type: 'spring',
          stiffness: 150,
          damping: 30,
        }}
      >
        <Typography variant="h6" display={'flex'} gap={1} mb={2}>
          <Typography color={'secondary'}>
            <IconRocket size={'21'} />
          </Typography>{' '}
          开始你的项目
        </Typography>

        <Typography
          variant="h1"
          fontWeight={900}
          sx={{
            fontSize: {
              md: '54px',
            },
            lineHeight: {
              md: '60px',
            },
          }}
        >
          最强大且{' '}
          <Typography component={'span'} variant="inherit" color={'primary'}>
            对开发者友好的
          </Typography>{' '}
          React仪表盘
        </Typography>
      </motion.div>
      <Box pt={4} pb={3}>
        <motion.div
          initial={{ opacity: 0, translateY: 550 }}
          animate={{ opacity: 1, translateY: 0 }}
          transition={{
            type: 'spring',
            stiffness: 150,
            damping: 30,
            delay: 0.2,
          }}
        >
          <Typography variant="h5" fontWeight={300}>
            Modernize提供明暗两种主题，精心设计的仪表盘、应用程序和页面。
          </Typography>
        </motion.div>
      </Box>
      <motion.div
        initial={{ opacity: 0, translateY: 550 }}
        animate={{ opacity: 1, translateY: 0 }}
        transition={{
          type: 'spring',
          stiffness: 150,
          damping: 30,
          delay: 0.4,
        }}
      >
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} mt={3}>
          <StyledButton variant="contained" color="primary" href="/auth/login">
            登录
          </StyledButton>

          <StyledButton variant="outlined" href="/dashboards/modern">
            在线预览
          </StyledButton>
        </Stack>
      </motion.div>
    </Box>
  );
};

export default BannerContent;
