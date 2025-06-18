// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const SizeButton = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} alignItems="center" justifyContent="center">
    <Button variant="contained" size="small">
      小
    </Button>
    <Button variant="contained" size="medium">
      中
    </Button>
    <Button variant="contained" size="large">
      大
    </Button>
  </Stack>
);

export default SizeButton;
