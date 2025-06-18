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
  Tooltip,
  Button,
  Stack
} from '@mui/material';

// components
import BlankCard from '../../shared/BlankCard.tsx';
import CustomTextField from '../../forms/theme-elements/CustomTextField.tsx';
import CustomFormLabel from '../../forms/theme-elements/CustomFormLabel.tsx';
import { IconCirclePlus, IconCreditCard, IconPackage, IconPencilMinus } from '@tabler/icons-react';

const BillsTab = () => {
  return (
    <>
      <Grid container spacing={3} justifyContent="center">
        <Grid item xs={12} lg={9}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" mb={2}>
                账单信息
              </Typography>

              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel sx={{ mt: 0 }} htmlFor="text-bname">
                    企业名称*
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-bname"
                    value="Visitor Analytics"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel sx={{ mt: 0 }} htmlFor="text-bsector">
                    企业行业*
                  </CustomFormLabel>
                  <CustomTextField
                    id="text-bsector"
                    value="Arts, Media & Entertainment"
                    variant="outlined"
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel sx={{ mt: 0 }} htmlFor="text-baddress">
                    企业地址*
                  </CustomFormLabel>
                  <CustomTextField id="text-baddress" value="" variant="outlined" fullWidth />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel sx={{ mt: 0 }} htmlFor="text-bcy">
                    国家*
                  </CustomFormLabel>
                  <CustomTextField id="text-bcy" value="Romania" variant="outlined" fullWidth />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel sx={{ mt: 0 }} htmlFor="text-fname">
                    名字*
                  </CustomFormLabel>
                  <CustomTextField id="text-fname" value="" variant="outlined" fullWidth />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <CustomFormLabel sx={{ mt: 0 }} htmlFor="text-lname">
                    姓氏*
                  </CustomFormLabel>
                  <CustomTextField id="text-lname" value="" variant="outlined" fullWidth />
                </Grid>
              </Grid>
            </CardContent>
          </BlankCard>
        </Grid>

        {/* 2 */}
        <Grid item xs={12} lg={9}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" display="flex" mb={2}>
                当前套餐：
                <Typography variant="h4" component="div" ml="2px" color="success.main">
                  Executive
                </Typography>
              </Typography>
              <Typography color="textSecondary">
                感谢您成为高级会员并支持我们的发展。
              </Typography>

              {/* list 1 */}
              <Stack direction="row" spacing={2} mt={4} mb={2}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconPackage size="22" />
                </Avatar>
                <Box>
                  <Typography variant="subtitle1" color="textSecondary">
                    当前套餐
                  </Typography>
                  <Typography variant="h6" mb={1}>
                    每月750,000次访问
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <Tooltip title="添加">
                    <IconButton>
                      <IconCirclePlus size="22" />
                    </IconButton>
                  </Tooltip>
                </Box>
              </Stack>

              <Stack direction="row" spacing={2}>
                <Button variant="contained" color="primary">
                  更改套餐
                </Button>
                <Button variant="outlined" color="error">
                  重置套餐
                </Button>
              </Stack>
            </CardContent>
          </BlankCard>
        </Grid>

        {/* 3 */}
        <Grid item xs={12} lg={9}>
          <BlankCard>
            <CardContent>
              <Typography variant="h4" mb={2}>
                支付方式
              </Typography>
              <Typography color="textSecondary">2023年12月26日</Typography>
              {/* list 1 */}
              <Stack direction="row" spacing={2} mt={4}>
                <Avatar
                  variant="rounded"
                  sx={{ bgcolor: 'grey.100', color: 'grey.500', width: 48, height: 48 }}
                >
                  <IconCreditCard size="22" />
                </Avatar>
                <Box>
                  <Typography variant="h6" mb={1}>
                    Visa
                  </Typography>
                  <Typography variant="subtitle1" fontWeight={600}>
                    *****2102
                  </Typography>
                </Box>
                <Box sx={{ ml: 'auto !important' }}>
                  <Tooltip title="编辑">
                    <IconButton>
                      <IconPencilMinus size="22" />
                    </IconButton>
                  </Tooltip>
                </Box>
              </Stack>
              <Typography color="textSecondary" my={1}>
                如果您更新了支付方式，它将在下一个计费周期后才会显示在这里。
              </Typography>
              <Button variant="outlined" color="error">
                取消订阅
              </Button>
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

export default BillsTab;
