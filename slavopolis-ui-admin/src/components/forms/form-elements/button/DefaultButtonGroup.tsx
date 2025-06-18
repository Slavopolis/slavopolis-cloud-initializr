// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import {  Button, ButtonGroup, Stack } from '@mui/material';

const DefaultButtonGroup = () => (
    <Stack spacing={1} >
       <ButtonGroup variant="outlined" aria-label="outlined button group">
        <Button>一</Button>
        <Button>二</Button>
        <Button>三</Button>
      </ButtonGroup>
      <ButtonGroup variant="contained" aria-label="outlined primary button group">
        <Button>一</Button>
        <Button>二</Button>
        <Button>三</Button>
      </ButtonGroup>
     
      <ButtonGroup variant="text" aria-label="text button group">
        <Button>一</Button>
        <Button>二</Button>
        <Button>三</Button>
      </ButtonGroup>
    </Stack>
);

export default DefaultButtonGroup;
