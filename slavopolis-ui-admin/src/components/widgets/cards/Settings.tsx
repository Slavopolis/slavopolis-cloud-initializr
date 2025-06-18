// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Avatar, Box, Button, CardContent, Divider, Stack, Typography } from '@mui/material';
import { IconMessage, IconVolume } from '@tabler/icons-react';
import React from 'react';
import BlankCard from '../../shared/BlankCard.tsx';

import CustomSlider from '../../forms/theme-elements/CustomSlider.tsx';
import CustomSwitch from '../../forms/theme-elements/CustomSwitch.tsx';

const Settings = () => {
  const [value3, setValue3] = React.useState(45);
  const handleChange6 = (event: React.ChangeEvent<unknown>, newValue: number) => {
    setValue3(newValue);
  };

  return (
    <BlankCard>
      <CardContent sx={{ p: "30px" }}>
        <Typography variant="h5">设置</Typography>
        <Stack spacing={2} mt={3}>
          <Stack direction="row" spacing={2}>
            <Avatar variant="rounded" sx={{ bgcolor: 'primary.main', width: 48, height: 48 }}>
              <IconVolume width={22} />
            </Avatar>
            <Box width="100%">
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Typography variant="h6">声音</Typography>
                <Typography variant="subtitle2" color="textSecondary">
                  45%
                </Typography>
              </Box>
              <CustomSlider aria-label="Volume" value={value3} onChange={handleChange6} />
            </Box>
          </Stack>
          <Divider />
          <Stack direction="row" spacing={2}>
            <Avatar variant="rounded" sx={{ bgcolor: 'secondary.main', width: 48, height: 48 }}>
              <IconMessage width={22} />
            </Avatar>
            <Box display="flex" alignItems="center" justifyContent="space-between" width="100%">
              <Box>
                <Typography variant="h6" mb={1}>聊天</Typography>
                <Typography variant="subtitle2" color="textSecondary">
                  通话期间开启聊天
                </Typography>
              </Box>
              <Box>
                <CustomSwitch />
              </Box>
            </Box>
          </Stack>
          <Divider />
        </Stack>
        <Stack direction="row" justifyContent="end" spacing={2} mt={2}>
          <Button variant="outlined" color="error">取消</Button>
          <Button variant="contained" color="primary">保存</Button>
        </Stack>
      </CardContent>
    </BlankCard>
  );
};

export default Settings;
