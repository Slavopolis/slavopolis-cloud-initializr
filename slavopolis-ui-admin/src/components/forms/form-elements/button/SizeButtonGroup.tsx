// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, ButtonGroup, Stack } from '@mui/material';

const SizeButtonGroup = () => (
  <Stack spacing={1} justifyContent="center">
        <ButtonGroup size="small" variant="outlined" aria-label="outlined primary button group">
          <Button>一</Button>
          <Button>二</Button>
          <Button>三</Button>
        </ButtonGroup>
        <ButtonGroup variant="outlined" aria-label="outlined button group">
          <Button>一</Button>
          <Button>二</Button>
          <Button>三</Button>
        </ButtonGroup>
        <ButtonGroup size="large" variant="outlined" aria-label="text button group">
          <Button>一</Button>
          <Button>二</Button>
          <Button>三</Button>
        </ButtonGroup>
    </Stack>
);

export default SizeButtonGroup;
