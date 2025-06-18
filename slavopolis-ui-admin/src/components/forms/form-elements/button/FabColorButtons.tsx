// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Fab, Tooltip, Stack } from '@mui/material';
import { IconSend } from '@tabler/icons-react';

const FabColorButtons = () => (
  <>
    <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center" alignItems="center">
      <Tooltip title="发送">
        <Fab color="primary" aria-label="send">
          <IconSend width={20} />
        </Fab>
      </Tooltip>
      <Tooltip title="发送">
        <Fab color="secondary" aria-label="send">
          <IconSend width={20} />
        </Fab>
      </Tooltip>
      <Tooltip title="发送">
        <Fab color="warning" aria-label="send">
          <IconSend width={20} />
        </Fab>
      </Tooltip>
      <Tooltip title="发送">
        <Fab color="error" aria-label="send">
          <IconSend width={20} />
        </Fab>
      </Tooltip>
      <Tooltip title="发送">
        <Fab color="success" aria-label="send">
          <IconSend width={20} />
        </Fab>
      </Tooltip>
    </Stack>
  </>
);

export default FabColorButtons;
