// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { useFormik } from 'formik';
import * as yup from 'yup';

import { Box, Button, Stack } from '@mui/material';

import CustomTextField from '../theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';

const validationSchema = yup.object({
  emailInstant: yup.string().email('请输入有效的邮箱').required('邮箱是必填项'),
  passwordInstant: yup
    .string()
    .min(8, '密码长度至少为8个字符')
    .required('密码是必填项'),
});

const FVOnLeave = () => {
  const formik = useFormik({
    initialValues: {
      emailInstant: '',
      passwordInstant: '',
    },
    validationSchema,
    onSubmit: (values) => {
      alert(values.emailInstant);
    },
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <Stack>
        <Box mt="-10px">
          <CustomFormLabel>邮箱地址</CustomFormLabel>
          <CustomTextField
            fullWidth
            id="emailInstant"
            name="emailInstant"
            value={formik.values.emailInstant}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.emailInstant && Boolean(formik.errors.emailInstant)}
            helperText={formik.touched.emailInstant && formik.errors.emailInstant}
          />
        </Box>
        <Box mb={3}>
          <CustomFormLabel>密码</CustomFormLabel>
          <CustomTextField
            fullWidth
            id="passwordInstant"
            name="passwordInstant"
            type="password"
            value={formik.values.passwordInstant}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.passwordInstant && Boolean(formik.errors.passwordInstant)}
            helperText={formik.touched.passwordInstant && formik.errors.passwordInstant}
          />
        </Box>
        <Stack direction="row" justifyContent="flex-end">
          <Button variant="contained" type="submit">
            提交
          </Button>
        </Stack>
      </Stack>
    </form>
  );
};

export default FVOnLeave;
