// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const ColorButtons = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
    <Button variant="contained" color="primary">
      主要
    </Button>
    <Button variant="contained" color="secondary">
      次要
    </Button>
    <Button variant="contained" color="error">
      错误
    </Button>
    <Button variant="contained" color="warning">
      警告
    </Button>
    <Button variant="contained" color="success">
      成功
    </Button>
  </Stack>
);

export default ColorButtons;
