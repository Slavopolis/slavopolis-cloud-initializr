// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Stack } from '@mui/material';
import { IconTrash } from '@tabler/icons-react';
import LoadingButton from '@mui/lab/LoadingButton';

const IconLoadingButtons = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
    <LoadingButton loading loadingIndicator="加载中…"
      variant="contained"
      color="error"
      startIcon={<IconTrash width={18} />}
    >
      左侧图标
    </LoadingButton >
    <LoadingButton loading
      variant="contained"
      color="secondary"
      endIcon={<IconTrash width={18} />}
    >
      右侧图标
    </LoadingButton >
  </Stack>
);

export default IconLoadingButtons;
