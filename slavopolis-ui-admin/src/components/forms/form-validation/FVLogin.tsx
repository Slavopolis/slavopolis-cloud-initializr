// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { Link } from 'react-router-dom';

import { Box, Button, Stack, FormGroup, FormControlLabel, Typography } from '@mui/material';

import CustomTextField from '../theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import CustomCheckbox from '../theme-elements/CustomCheckbox.tsx';

const validationSchema = yup.object({
  firstName: yup
    .string()
    .min(2, '太短！')
    .max(50, '太长！')
    .required('名字是必填项'),
  lastName: yup.string().min(2, '太短！').max(50, '太长！').required('姓氏是必填项'),
  email: yup.string().email('请输入有效的邮箱地址').required('邮箱是必填项'),
  password: yup
    .string()
    .min(8, '密码长度至少为8个字符')
    .required('密码是必填项'),
  changepassword: yup.string().when('password', {
    is: (val: string) => (val && val.length > 0 ? true : false),
    then: yup.string().oneOf([yup.ref('password')], '两次输入的密码必须相同'),
  }),
});

const FVRegister = () => {
  const formik = useFormik({
    initialValues: {
      firstName: '',
      email: '',
      password: '',
      changepassword: '',
    },
    validationSchema: validationSchema,
    onSubmit: (values) => {
      alert(JSON.stringify(values, null, 2));
    },
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <Stack>
        <Box>
          <CustomFormLabel>邮箱地址</CustomFormLabel>
          <CustomTextField
            fullWidth
            id="email"
            name="email"
            value={formik.values.email}
            onChange={formik.handleChange}
            error={formik.touched.email && Boolean(formik.errors.email)}
            helperText={formik.touched.email && formik.errors.email}
          />
        </Box>
        <Box mb={3}>
          <CustomFormLabel>密码</CustomFormLabel>
          <CustomTextField
            fullWidth
            id="password"
            name="password"
            type="password"
            value={formik.values.password}
            onChange={formik.handleChange}
            error={formik.touched.password && Boolean(formik.errors.password)}
            helperText={formik.touched.password && formik.errors.password}
          />
        </Box>
      </Stack>
      <Stack justifyContent="space-between" direction="row" alignItems="center" mb={2}>
        <FormGroup>
          <FormControlLabel
            control={<CustomCheckbox defaultChecked />}
            label="记住我"
          />
        </FormGroup>
        <Typography
          component={Link}
          to="/auth/forgot-password"
          fontWeight={600}
          sx={{
            textDecoration: 'none',
            color: 'primary.main',
          }}
        >
          忘记密码？
        </Typography>
      </Stack>
      <Button color="primary" variant="contained" type="submit">
        登录
      </Button>
    </form>
  );
};

export default FVRegister;
