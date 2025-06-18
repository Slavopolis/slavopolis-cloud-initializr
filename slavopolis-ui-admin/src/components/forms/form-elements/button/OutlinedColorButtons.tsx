// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const OutlinedColorButtons = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
    <Button variant="outlined" color="primary">
      主要
    </Button>
    <Button variant="outlined" color="secondary">
      次要
    </Button>
    <Button variant="outlined" color="error">
      错误
    </Button>
    <Button variant="outlined" color="warning">
      警告
    </Button>
    <Button variant="outlined" color="success">
      成功
    </Button>
  </Stack>
);

export default OutlinedColorButtons;
