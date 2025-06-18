// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Avatar, Box, CardContent, Grid, IconButton, Typography, Tooltip, Button, Stack } from '@mui/material';

// components
import BlankCard from '../../shared/BlankCard.tsx';
import CustomTextField from '../../forms/theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../../forms/theme-elements/CustomFormLabel.tsx';
import CustomSwitch from '../../forms/theme-elements/CustomSwitch.tsx';
import {
  IconArticle,
  IconCheckbox,
  IconClock,
  IconDownload,
  IconMail,
  IconPlayerPause,
  IconTruckDelivery,
} from '@tabler/icons-react';

const NotificationTab = () => {
  return (
    <>
      <Grid container spacing={3} justifyContent="center">
        <Grid item xs={12} lg={9}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" mb={2}>
                通知偏好设置
              </Typography>
              <Typography color="textSecondary">
                选择您希望通过电子邮件接收的通知。请注意，您无法选择不接收服务消息，如付款、安全或法律通知。
              </Typography>

              <CustomFormLabel htmlFor="text-email">电子邮箱地址*</CustomFormLabel>
              <CustomTextField id="text-email" variant="outlined" fullWidth />
              <Typography color="textSecondary">接收通知所必需。</Typography>

              {/* list 1 */}
              <Stack direction="row" spacing={2} mt={4}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconArticle size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    我们的通讯
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    我们会及时通知您重要的变更
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <CustomSwitch />
                </Box>
              </Stack>

              {/* list 2 */}
              <Stack direction="row" spacing={2} mt={3}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconCheckbox size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    订单确认
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    当客户订购任何产品时，您将收到通知
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <CustomSwitch checked />
                </Box>
              </Stack>

              {/* list 3 */}
              <Stack direction="row" spacing={2} mt={3}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconClock size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    订单状态变更
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    当客户对订单进行更改时，您将收到通知
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <CustomSwitch checked />
                </Box>
              </Stack>

              {/* list 4 */}
              <Stack direction="row" spacing={2} mt={3}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconTruckDelivery size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    订单已送达
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    订单送达后，您将收到通知
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <CustomSwitch />
                </Box>
              </Stack>

              {/* list 5 */}
              <Stack direction="row" spacing={2} mt={3}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconMail size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    邮件通知
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    开启邮件通知以通过电子邮件获取更新
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <CustomSwitch checked />
                </Box>
              </Stack>
            </CardContent>
          </BlankCard>
        </Grid>

        {/* 2 */}
        <Grid item xs={12} lg={9}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" mb={2}>
                日期和时间
              </Typography>
              <Typography color="textSecondary">
                时区和日历显示设置。
              </Typography>

              {/* list 1 */}
              <Stack direction="row" spacing={2} mt={4}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconClock size="22" />
                </Avatar>
                <Box>
                  <Typography variant="subtitle1" color="textSecondary">
                    时区
                  </Typography>
                  <Typography variant="h6" mb={1}>
                    (UTC + 02:00) 雅典，布加勒斯特
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <Tooltip title="下载">
                    <IconButton>
                      <IconDownload size="22" />
                    </IconButton>
                  </Tooltip>
                </Box>
              </Stack>
            </CardContent>
          </BlankCard>
        </Grid>

        {/* 3 */}
        <Grid item xs={12} lg={9}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" mb={2}>
                忽略追踪
              </Typography>

              {/* list 1 */}
              <Stack direction="row" spacing={2} mt={4}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconPlayerPause size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    忽略浏览器追踪
                  </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    浏览器Cookie
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <CustomSwitch />
                </Box>
              </Stack>
            </CardContent>
          </BlankCard>
        </Grid>
      </Grid>

      <Stack direction="row" spacing={2} sx={{ justifyContent: 'end' }} mt={3}>
        <Button size="large" variant="contained" color="primary">
          保存
          </Button>
          <Button size="large" variant="text" color="error">
            取消
        </Button>
      </Stack>
    </>
  );
};

export default NotificationTab;
