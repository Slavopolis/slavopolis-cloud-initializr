import { Grid, InputAdornment, Button } from '@mui/material';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import CustomTextField from '../theme-elements/CustomTextField.tsx';
import CustomOutlinedInput from '../theme-elements/CustomOutlinedInput.tsx';

const BasicLayout = () => {
  return (
    <div>
      {/* ------------------------------------------------------------------------------------------------ */}
      {/* Basic Layout */}
      {/* ------------------------------------------------------------------------------------------------ */}
      <Grid container>
        {/* 1 */}
        <Grid item xs={12} display="flex" alignItems="center">
          <CustomFormLabel htmlFor="bl-name" sx={{ mt: 0 }}>
            姓名
          </CustomFormLabel>
        </Grid>
        <Grid item xs={12}>
          <CustomTextField id="bl-name" placeholder="张三" fullWidth />
        </Grid>
        {/* 2 */}
        <Grid item xs={12} display="flex" alignItems="center">
          <CustomFormLabel htmlFor="bl-company">
            公司名称
          </CustomFormLabel>
        </Grid>
        <Grid item xs={12}>
          <CustomTextField id="bl-company" placeholder="公司名称" fullWidth />
        </Grid>
        {/* 3 */}
        <Grid item xs={12} display="flex" alignItems="center">
          <CustomFormLabel htmlFor="bl-email">
            邮箱
          </CustomFormLabel>
        </Grid>
        <Grid item xs={12}>
          <CustomOutlinedInput
            endAdornment={<InputAdornment position="end">@example.com</InputAdornment>}
            id="bl-email"
            placeholder="zhangsan"
            fullWidth
          />
        </Grid>
        {/* 4 */}
        <Grid item xs={12} display="flex" alignItems="center">
          <CustomFormLabel htmlFor="bl-phone">
            电话号码
          </CustomFormLabel>
        </Grid>
        <Grid item xs={12}>
          <CustomTextField id="bl-phone" placeholder="138 1234 5678" fullWidth />
        </Grid>
        {/* 5 */}
        <Grid item xs={12} display="flex" alignItems="center">
          <CustomFormLabel htmlFor="bl-message">
            消息
          </CustomFormLabel>
        </Grid>
        <Grid item xs={12}>
          <CustomTextField
            id="bl-message"
            placeholder="嗨，你有时间聊聊吗？"
            multiline
            fullWidth
          />
        </Grid>
        <Grid item xs={12} mt={3}>
            <Button variant="contained" color="primary">发送</Button>
        </Grid>
      </Grid>
    </div>
  );
};

export default BasicLayout;
