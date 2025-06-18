// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, ButtonGroup, Stack } from '@mui/material';

const TextButtonGroup = () => (
  <Stack spacing={1} direction="column" justifyContent="center">
    <ButtonGroup variant="text" aria-label="text button group">
      <Button>一</Button>
      <Button>二</Button>
      <Button>三</Button>
    </ButtonGroup>
    <ButtonGroup color="secondary" variant="text" aria-label="text button group">
      <Button>一</Button>
      <Button>二</Button>
      <Button>三</Button>
    </ButtonGroup>
    <ButtonGroup color="error" variant="text" aria-label="text button group">
      <Button>一</Button>
      <Button>二</Button>
      <Button>三</Button>
    </ButtonGroup>
  </Stack>
);

export default TextButtonGroup;
