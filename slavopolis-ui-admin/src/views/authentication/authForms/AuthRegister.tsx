// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Box, Typography, Button, Divider, Stack } from '@mui/material';
import { Link } from 'react-router-dom';

import CustomTextField from '../../../components/forms/theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../../../components/forms/theme-elements/CustomFormLabel.tsx';
import { registerType } from 'src/types/auth/auth';
import AuthSocialButtons from './AuthSocialButtons.tsx';


const AuthRegister = ({ title, subtitle, subtext }: registerType) => (
  <>
    {title ? (
      <Typography fontWeight="700" variant="h3" mb={1}>
        {title}
      </Typography>
    ) : null}

    {subtext}
    <AuthSocialButtons title="Sign up with" />

    <Box mt={3}>
      <Divider>
        <Typography
          component="span"
          color="textSecondary"
          variant="h6"
          fontWeight="400"
          position="relative"
          px={2}
        >
          or sign up with
        </Typography>
      </Divider>
    </Box>

    <Box>
      <Stack mb={3}>
        <CustomFormLabel htmlFor="name">姓名</CustomFormLabel>
        <CustomTextField id="name" variant="outlined" fullWidth />
        <CustomFormLabel htmlFor="email">邮箱地址</CustomFormLabel>
        <CustomTextField id="email" variant="outlined" fullWidth />
        <CustomFormLabel htmlFor="password">密码</CustomFormLabel>
        <CustomTextField id="password" variant="outlined" fullWidth />
      </Stack>
      <Button color="primary" variant="contained" size="large" fullWidth component={Link} to="/auth/login">
        注册
      </Button>
    </Box>
    {subtitle}
  </>
);

export default AuthRegister;
