// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Button, Stack } from '@mui/material';
import { IconTrash, IconSend } from '@tabler/icons-react';

const OutlinedIconButtons = () => (
    <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
      <Button
        variant="outlined"
        color="error"
        startIcon={<IconTrash width={18} />}
      >
        左侧图标
      </Button>
      <Button
        variant="outlined"
        color="secondary"
        endIcon={<IconSend width={18} />}
      >
        右侧图标
      </Button>
    </Stack>
);

export default OutlinedIconButtons;
