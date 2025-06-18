// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';

const TextSizeButton = () => (
  <Stack spacing={1} direction="row" alignItems="center" justifyContent="center">
    <Button size="small">小</Button>
    <Button size="medium">中</Button>
    <Button size="large">大</Button>
  </Stack>
);

export default TextSizeButton;
