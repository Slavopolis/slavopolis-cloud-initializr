// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const TextColorButtons = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
    <Button color="primary">主要</Button>
    <Button color="secondary">次要</Button>
    <Button color="error">错误</Button>
    <Button color="warning">警告</Button>
    <Button color="success">成功</Button>
  </Stack>
);

export default TextColorButtons;
