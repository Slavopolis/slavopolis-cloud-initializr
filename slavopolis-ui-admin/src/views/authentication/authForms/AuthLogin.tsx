// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import {
    Box,
    Button,
    Divider,
    FormControlLabel,
    FormGroup,
    Stack,
    Typography,
} from '@mui/material';
import { Link } from 'react-router-dom';

import { loginType } from 'src/types/auth/auth';
import CustomCheckbox from '../../../components/forms/theme-elements/CustomCheckbox.tsx';
import CustomFormLabel from '../../../components/forms/theme-elements/CustomFormLabel.tsx';
import CustomTextField from '../../../components/forms/theme-elements/CustomTextField.tsx';

import AuthSocialButtons from './AuthSocialButtons.tsx';



const AuthLogin = ({ title, subtitle, subtext }: loginType) => (
  <>
    {title ? (
      <Typography fontWeight="700" variant="h3" mb={1}>
        {title}
      </Typography>
    ) : null}

    {subtext}

    <AuthSocialButtons title="Sign in with" />
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
          or sign in with
        </Typography>
      </Divider>
    </Box>

    <Stack>
      <Box>
        <CustomFormLabel htmlFor="username">用户名</CustomFormLabel>
        <CustomTextField id="username" variant="outlined" fullWidth />
      </Box>
      <Box>
        <CustomFormLabel htmlFor="password">密码</CustomFormLabel>
        <CustomTextField id="password" type="password" variant="outlined" fullWidth />
      </Box>
      <Stack justifyContent="space-between" direction="row" alignItems="center" my={2}>
        <FormGroup>
          <FormControlLabel
            control={<CustomCheckbox defaultChecked />}
            label="记住我"
          />
        </FormGroup>
        <Typography
          component={Link}
          to="/auth/forgot-password"
          fontWeight="500"
          sx={{
            textDecoration: 'none',
            color: 'primary.main',
          }}
        >
          忘记密码？
        </Typography>
      </Stack>
    </Stack>
    <Box>
      <Button
        color="primary"
        variant="contained"
        size="large"
        fullWidth
        component={Link}
        to="/"
        type="submit"
      >
        登录
      </Button>
    </Box>
    {subtitle}
  </>
);

export default AuthLogin;
