// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';
import { IconTrash, IconSend } from '@tabler/icons-react';

const TextIconButtons = () => (
  <Stack spacing={1} direction="row" justifyContent="center">
    <Button color="error" startIcon={<IconTrash width={18} />}>
      左侧图标
    </Button>
    <Button color="secondary" endIcon={<IconSend width={18} />}>
      右侧图标
    </Button>
  </Stack>
);

export default TextIconButtons;
