// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';
import { Link } from 'react-router-dom';

import CustomTextField from '../../../components/forms/theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../../../components/forms/theme-elements/CustomFormLabel.tsx';

const AuthForgotPassword = () => (
  <>
    <Stack mt={4} spacing={2}>
      <CustomFormLabel htmlFor="reset-email">邮箱地址</CustomFormLabel>
      <CustomTextField id="reset-email" variant="outlined" fullWidth />

      <Button color="primary" variant="contained" size="large" fullWidth component={Link} to="/">
        忘记密码
      </Button>
      <Button color="primary" size="large" fullWidth component={Link} to="/auth/login">
        返回登录
      </Button>
    </Stack>
  </>
);

export default AuthForgotPassword;
