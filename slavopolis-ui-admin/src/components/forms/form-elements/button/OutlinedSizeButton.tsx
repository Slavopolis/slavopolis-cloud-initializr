// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const OutlinedSizeButton = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} alignItems="center" justifyContent="center">
    <Button variant="outlined" size="small">
      小
    </Button>
    <Button variant="outlined" size="medium">
      中
    </Button>
    <Button variant="outlined" size="large">
      大
    </Button>
  </Stack>
);

export default OutlinedSizeButton;
