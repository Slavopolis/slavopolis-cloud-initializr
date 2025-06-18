// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Alert, Box, Button, FormControl, FormControlLabel, Grid, MenuItem } from '@mui/material';
import React from 'react';
import ParentCard from '../../shared/ParentCard.tsx';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import CustomRadio from '../theme-elements/CustomRadio.tsx';
import CustomSelect from '../theme-elements/CustomSelect.tsx';
import CustomTextField from '../theme-elements/CustomTextField.tsx';

interface currencyType {
  value: string;
  label: string;
}

const currencies: currencyType[] = [
  {
    value: 'female',
    label: '女',
  },
  {
    value: 'male',
    label: '男',
  },
  {
    value: 'other',
    label: '其他',
  },
];

const countries: currencyType[] = [
  {
    value: 'india',
    label: '印度',
  },
  {
    value: 'uk',
    label: '英国',
  },
  {
    value: 'srilanka',
    label: '斯里兰卡',
  },
];

const FbBasicHeaderForm = () => {
  const [currency, setCurrency] = React.useState('');

  const handleChange2 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCurrency(event.target.value);
  };

  const [selectedValue, setSelectedValue] = React.useState('');

  const handleChange3 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedValue(event.target.value);
  };

  const [country, setCountry] = React.useState('');

  const handleChange4 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCountry(event.target.value);
  };

  return (
    <div>
      {/* ------------------------------------------------------------------------------------------------ */}
      {/* Basic Checkbox */}
      {/* ------------------------------------------------------------------------------------------------ */}
      <ParentCard
        title="基本表头表单"
        footer={
          <>
            <Button
              variant="contained"
              color="error"
              sx={{
                mr: 1,
              }}
            >
              取消
            </Button>
            <Button variant="contained" color="primary">
              提交
            </Button>
          </>
        }
      >
        <>
          <Alert severity="info">个人信息</Alert>
          <form>
            <Grid container spacing={3} mb={3}>
              <Grid item lg={6} md={12} sm={12}>
                <CustomFormLabel htmlFor="fname-text">名字</CustomFormLabel>
                <CustomTextField id="fname-text" variant="outlined" fullWidth />
                <CustomFormLabel htmlFor="standard-select-currency">选择性别</CustomFormLabel>
                <CustomSelect
                  id="standard-select-currency"
                  value={currency}
                  onChange={handleChange2}
                  fullWidth
                  variant="outlined"
                >
                  {currencies.map((option) => (
                    <MenuItem key={option.value} value={option.value}>
                      {option.label}
                    </MenuItem>
                  ))}
                </CustomSelect>
                <CustomFormLabel>会员资格</CustomFormLabel>

                <FormControl
                  sx={{
                    width: '100%',
                  }}
                >
                  <Box>
                    <FormControlLabel
                      checked={selectedValue === 'a'}
                      onChange={handleChange3}
                      value="a"
                      label="免费"
                      name="radio-button-demo"
                      control={<CustomRadio />}
                     
                    />
                    <FormControlLabel
                      checked={selectedValue === 'b'}
                      onChange={handleChange3}
                      value="b"
                      label="付费"
                      control={<CustomRadio />}
                      name="radio-button-demo"
                    />
                  </Box>
                </FormControl>
              </Grid>
              <Grid item lg={6} md={12} sm={12}>
                <CustomFormLabel htmlFor="lname-text">姓氏</CustomFormLabel>

                <CustomTextField id="lname-text" variant="outlined" fullWidth />
                <CustomFormLabel htmlFor="date">出生日期</CustomFormLabel>

                <CustomTextField
                  id="date"
                  type="date"
                  variant="outlined"
                  fullWidth
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </Grid>
            </Grid>
          </form>
          <Alert severity="info">地址</Alert>
          <Grid container spacing={3} mb={3} mt={1}>
            <Grid item lg={12} md={12} sm={12} xs={12}>
              <CustomFormLabel
                sx={{
                  mt: 0,
                }}
                htmlFor="street-text"
              >
                街道
              </CustomFormLabel>

              <CustomTextField id="street-text" variant="outlined" fullWidth />
            </Grid>
            <Grid item lg={6} md={12} sm={12} xs={12}>
              <CustomFormLabel
                sx={{
                  mt: 0,
                }}
                htmlFor="city-text"
              >
                城市
              </CustomFormLabel>
              <CustomTextField id="city-text" variant="outlined" fullWidth />
            </Grid>
            <Grid item lg={6} md={12} sm={12} xs={12}>
              <CustomFormLabel
                sx={{
                  mt: 0,
                }}
                htmlFor="state-text"
              >
                州/省
              </CustomFormLabel>
              <CustomTextField id="state-text" variant="outlined" fullWidth />
            </Grid>
            <Grid item lg={6} md={12} sm={12} xs={12}>
              <CustomFormLabel
                sx={{
                  mt: 0,
                }}
                htmlFor="post-text"
              >
                邮政编码
              </CustomFormLabel>
              <CustomTextField id="post-text" variant="outlined" fullWidth />
            </Grid>
            <Grid item lg={6} md={12} sm={12} xs={12}>
              <CustomFormLabel
                sx={{
                  mt: 0,
                }}
                htmlFor="country-text"
              >
                国家
              </CustomFormLabel>
              <CustomSelect
                id="country-select"
                value={country}
                onChange={handleChange4}
                fullWidth
                variant="outlined"
              >
                {countries.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </CustomSelect>
            </Grid>
          </Grid>
        </>
      </ParentCard>
    </div>
  );
};

export default FbBasicHeaderForm;
