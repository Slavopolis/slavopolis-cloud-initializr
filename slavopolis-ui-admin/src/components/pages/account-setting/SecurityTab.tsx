// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import {
  Avatar,
  Box,
  CardContent,
  Grid,
  IconButton,
  Typography,
  Button,
  Divider,
  Stack
} from '@mui/material';

// components
import BlankCard from '../../shared/BlankCard.tsx';
import { IconDeviceLaptop, IconDeviceMobile, IconDotsVertical } from '@tabler/icons-react';

const SecurityTab = () => {
  return (
    <>
      <Grid container spacing={3} justifyContent="center">
        <Grid item xs={12} lg={8}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" mb={2}>
                双重认证
              </Typography>
              <Stack direction="row" justifyContent="space-between" alignItems="center" mb={4}>
                <Typography variant="subtitle1" color="textSecondary">
                  启用双重认证可以为您的账户提供额外的安全保护。
                </Typography>
                <Button variant="contained" color="primary">
                  启用
                </Button>
              </Stack>

              <Divider />

              {/* list 1 */}
              <Stack direction="row" spacing={2} py={2} alignItems="center">
                <Box>
                  <Typography variant="h6">认证应用</Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    谷歌认证器
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <Button variant="text" color="primary">
                    设置
                  </Button>
                </Box>
              </Stack>
              <Divider />
              {/* list 2 */}
              <Stack direction="row" spacing={2} py={2} alignItems="center">
                <Box>
                  <Typography variant="h6">备用邮箱</Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    用于发送验证链接的邮箱
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <Button variant="text" color="primary">
                    设置
                  </Button>
                </Box>
              </Stack>
              <Divider />
              {/* list 3 */}
              <Stack direction="row" spacing={2} py={2} alignItems="center">
                <Box>
                  <Typography variant="h6">短信恢复</Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    您的手机号码
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <Button variant="text" color="primary">
                    设置
                  </Button>
                </Box>
              </Stack>
            </CardContent>
          </BlankCard>
        </Grid>
        <Grid item xs={12} lg={4}>
          <BlankCard>
            <CardContent>
              <Avatar
                variant="rounded"
                sx={{ bgcolor: 'primary.light', color: 'primary.main', width: 48, height: 48 }}
              >
                <IconDeviceLaptop size="26" />
              </Avatar>

              <Typography variant="h5" mt={2}>
                设备
              </Typography>
              <Typography color="textSecondary" mt={1} mb={2}>
                管理您当前登录的所有设备。
              </Typography>
              <Button variant="contained" color="primary">
                从所有设备退出
              </Button>

              {/* list 1 */}
              <Stack direction="row" spacing={2} py={2} mt={3} alignItems="center">
                <IconDeviceMobile size="26" />

                <Box>
                  <Typography variant="h6">iPhone 14</Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    英国伦敦，10月23日 凌晨1:15
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <IconButton>
                    <IconDotsVertical size="22" />
                  </IconButton>
                </Box>
              </Stack>
              <Divider />
              {/* list 2 */}
              <Stack direction="row" spacing={2} py={2} alignItems="center">
                <IconDeviceLaptop size="26" />

                <Box>
                  <Typography variant="h6">Macbook Air </Typography>
                  <Typography variant="subtitle1" color="textSecondary">
                    印度古吉拉特，10月24日 凌晨3:15
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <IconButton>
                    <IconDotsVertical size="22" />
                  </IconButton>
                </Box>
              </Stack>
              <Stack>
                <Button variant="text" color="primary">
                  需要帮助？
                </Button>
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

export default SecurityTab;
