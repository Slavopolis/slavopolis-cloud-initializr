// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import {
  TableContainer,
  Table,
  TableRow,
  TableCell,
  TableBody,
  Avatar,
  Typography,
  TableHead,
  Chip,
  Box,
  AvatarGroup,
  Stack
} from '@mui/material';
import BlankCard from '../shared/BlankCard.tsx';
import { basicsTableData, TableType } from './tableData.ts';

const basics: TableType[] = basicsTableData;

const Table5 = () => {
  return (
    <BlankCard>
      <TableContainer>
        <Table
          aria-label="simple table"
          sx={{
            whiteSpace: 'nowrap',
          }}
        >
          <TableHead>
            <TableRow>
              <TableCell>
                <Typography variant="h6">用户</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">项目名称</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">团队</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">状态</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">预算</Typography>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {basics.map((basic) => (
              <TableRow key={basic.id}>
                <TableCell>
                  <Stack direction="row" spacing={2}>
                    <Avatar src={basic.imgsrc} alt={basic.imgsrc} sx={{ width: 35 }} />
                    <Box>
                      <Typography variant="h6" fontWeight="600">
                        {basic.name}
                      </Typography>
                      <Typography color="textSecondary" variant="subtitle2">
                        {basic.post}
                      </Typography>
                    </Box>
                  </Stack>
                </TableCell>
                <TableCell>
                  <Typography color="textSecondary" variant="h6" fontWeight={400}>
                    {basic.pname}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Stack direction="row">
                    <AvatarGroup max={4}>
                      {basic.teams?.map((team) => (
                        <Avatar
                          key={team.id}
                          sx={{
                            bgcolor: team.color,
                            width: 35,
                            height: 35,
                          }}
                        >
                          {team.text}
                        </Avatar>
                      ))}
                    </AvatarGroup>
                  </Stack>
                </TableCell>
                <TableCell>
                  {/* <Chip chipcolor={basic.status == 'Active' ? 'success' : basic.status == 'Pending' ? 'warning' : basic.status == 'Completed' ? 'primary' : basic.status == 'Cancel' ? 'error' : 'secondary'} */}
                  <Chip
                    sx={{
                      bgcolor:
                        basic.status === '活跃'
                          ? (theme) => theme.palette.success.light
                          : basic.status === '待处理'
                          ? (theme) => theme.palette.warning.light
                          : basic.status === '已完成'
                          ? (theme) => theme.palette.primary.light
                          : basic.status === '取消'
                          ? (theme) => theme.palette.error.light
                          : (theme) => theme.palette.secondary.light,
                      color:
                        basic.status === '活跃'
                          ? (theme) => theme.palette.success.main
                          : basic.status === '待处理'
                          ? (theme) => theme.palette.warning.main
                          : basic.status === '已完成'
                          ? (theme) => theme.palette.primary.main
                          : basic.status === '取消'
                          ? (theme) => theme.palette.error.main
                          : (theme) => theme.palette.secondary.main,
                      borderRadius: '8px',
                    }}
                    size="small"
                    label={basic.status}
                  />
                </TableCell>
                <TableCell>
                  <Typography variant="h6">${basic.budget}k</Typography>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </BlankCard>
  );
};

export default Table5;
