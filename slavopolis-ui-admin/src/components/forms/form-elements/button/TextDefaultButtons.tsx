// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const TextDefaultButtons = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
    <Button color="primary">主要</Button>
    <Button color="secondary">次要</Button>
    <Button disabled>禁用</Button>
    <Button href="#text-buttons" color="primary">
      链接
    </Button>
  </Stack>
);

export default TextDefaultButtons;
