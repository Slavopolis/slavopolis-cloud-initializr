// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { useFormik } from 'formik';
import { Link } from 'react-router-dom';
import * as yup from 'yup';

import { Box, Button, FormControlLabel, FormGroup, Stack, Typography } from '@mui/material';

import CustomCheckbox from '../theme-elements/CustomCheckbox.tsx';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import CustomTextField from '../theme-elements/CustomTextField.tsx';

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
          <CustomFormLabel>姓名</CustomFormLabel>
          <CustomTextField
            fullWidth
            id="firstName"
            name="firstName"
            value={formik.values.firstName}
            onChange={formik.handleChange}
            error={formik.touched.firstName && Boolean(formik.errors.firstName)}
            helperText={formik.touched.firstName && formik.errors.firstName}
          />
        </Box>
        <Box>
          <CustomFormLabel>邮箱</CustomFormLabel>
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
        <Box>
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
        <Box mb={3}>
          <CustomFormLabel>确认密码</CustomFormLabel>
          <CustomTextField
            fullWidth
            id="changepassword"
            name="changepassword"
            type="password"
            value={formik.values.changepassword}
            onChange={formik.handleChange}
            error={formik.touched.changepassword && Boolean(formik.errors.changepassword)}
            helperText={formik.touched.changepassword && formik.errors.changepassword}
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
        注册
      </Button>
    </form>
  );
};

export default FVRegister;
