// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { CardContent, Grid, Typography, MenuItem, Box, Avatar, Button, Stack } from '@mui/material';

// components
import BlankCard from '../../shared/BlankCard.tsx';
import CustomTextField from '../../forms/theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../../forms/theme-elements/CustomFormLabel.tsx';
import CustomSelect from '../../forms/theme-elements/CustomSelect.tsx';

// images
import user1 from 'src/assets/images/profile/user-1.jpg';

interface locationType {
  value: string;
  label: string;
}

// locations
const locations: locationType[] = [
  {
    value: 'us',
    label: '美国',
  },
  {
    value: 'uk',
    label: '英国',
  },
  {
    value: 'india',
    label: '印度',
  },
  {
    value: 'russia',
    label: '俄罗斯',
  },
];

// currency
const currencies: locationType[] = [
  {
    value: 'us',
    label: '美元 ($)',
  },
  {
    value: 'uk',
    label: '英镑',
  },
  {
    value: 'india',
    label: '印度卢比 (INR)',
  },
  {
    value: 'russia',
    label: '俄罗斯卢布',
  },
];

const AccountTab = () => {
  const [location, setLocation] = React.useState('india');

  const handleChange1 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLocation(event.target.value);
  };

  //   currency
  const [currency, setCurrency] = React.useState('india');

  const handleChange2 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCurrency(event.target.value);
  };

  return (
    <Grid container spacing={3}>
      {/* Change Profile */}
      <Grid item xs={12} lg={6}>
        <BlankCard>
          <CardContent>
            <Typography variant="h5" mb={1}>
              更改个人资料
            </Typography>
            <Typography color="textSecondary" mb={3}>在此更改您的个人资料照片</Typography>
            <Box textAlign="center" display="flex" justifyContent="center">
              <Box>
                <Avatar
                  src={user1}
                  alt={user1}
                  sx={{ width: 120, height: 120, margin: '0 auto' }}
                />
                <Stack direction="row" justifyContent="center" spacing={2} my={3}>
                  <Button variant="contained" color="primary" component="label">
                    上传
                    <input hidden accept="image/*" multiple type="file" />
                  </Button>
                  <Button variant="outlined" color="error">
                    重置
                  </Button>
                </Stack>
                <Typography variant="subtitle1" color="textSecondary" mb={4}>
                  允许JPG、GIF或PNG格式。最大文件大小800K
                </Typography>
              </Box>
            </Box>
          </CardContent>
        </BlankCard>
      </Grid>
      {/*  Change Password */}
      <Grid item xs={12} lg={6}>
        <BlankCard>
          <CardContent>
            <Typography variant="h5" mb={1}>
              更改密码
            </Typography>
            <Typography color="textSecondary" mb={3}>请在此确认更改您的密码</Typography>
            <form>
              <CustomFormLabel
                sx={{
                  mt: 0,
                }}
                htmlFor="text-cpwd"
              >
                当前密码
              </CustomFormLabel>
              <CustomTextField
                id="text-cpwd"
                value="MathewAnderson"
                variant="outlined"
                fullWidth
                type="password"
              />
              {/* 2 */}
              <CustomFormLabel htmlFor="text-npwd">新密码</CustomFormLabel>
              <CustomTextField
                id="text-npwd"
                value="MathewAnderson"
                variant="outlined"
                fullWidth
                type="password"
              />
              {/* 3 */}
              <CustomFormLabel htmlFor="text-conpwd">确认密码</CustomFormLabel>
              <CustomTextField
                id="text-conpwd"
                value="MathewAnderson"
                variant="outlined"
                fullWidth
                type="password"
              />
            </form>
          </CardContent>
        </BlankCard>
      </Grid>
      {/* Edit Details */}
      <Grid item xs={12}>
        <BlankCard>
          <CardContent>
            <Typography variant="h5" mb={1}>
              个人详细信息
            </Typography>
            <Typography color="textSecondary" mb={3}>在此编辑和保存您的个人详细信息</Typography>
            <form>
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-name"
                  >
                    您的姓名
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-name"
                    value="Mathew Anderson"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  {/* 2 */}
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-store-name"
                  >
                    商店名称
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-store-name"
                    value="Maxima Studio"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  {/* 3 */}
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-location"
                  >
                    位置
                  </CustomFormLabel>
                  <CustomSelect
                    fullWidth
                    id="text-location"
                    variant="outlined"
                    value={location}
                    onChange={handleChange1}
                  >
                    {locations.map((option) => (
                      <MenuItem key={option.value} value={option.value}>
                        {option.label}
                      </MenuItem>
                    ))}
                  </CustomSelect>
                </Grid>
                <Grid item xs={12} sm={6}>
                  {/* 4 */}
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-currency"
                  >
                    货币
                  </CustomFormLabel>
                  <CustomSelect
                    fullWidth
                    id="text-currency"
                    variant="outlined"
                    value={currency}
                    onChange={handleChange2}
                  >
                    {currencies.map((option) => (
                      <MenuItem key={option.value} value={option.value}>
                        {option.label}
                      </MenuItem>
                    ))}
                  </CustomSelect>
                </Grid>
                <Grid item xs={12} sm={6}>
                  {/* 5 */}
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-email"
                  >
                    Email
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-email"
                    value="info@modernize.com"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  {/* 6 */}
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-phone"
                  >
                    电话
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-phone"
                    value="+91 12345 65478"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12}>
                  {/* 7 */}
                  <CustomFormLabel
                    sx={{
                      mt: 0,
                    }}
                    htmlFor="text-address"
                  >
                    地址
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-address"
                    value="814 Howard Street, 120065, India"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
              </Grid>
            </form>
          </CardContent>
        </BlankCard>
        <Stack direction="row" spacing={2} sx={{ justifyContent: 'end' }} mt={3}>
          <Button size="large" variant="contained" color="primary">
            保存
          </Button>
          <Button size="large" variant="text" color="error">
            取消
          </Button>
        </Stack>
      </Grid>
    </Grid>
  );
};

export default AccountTab;
