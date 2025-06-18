// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import {
  TableContainer,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Avatar,
  Typography,
  Chip,
  Menu,
  MenuItem,
  IconButton,
  ListItemIcon,
  Box, Stack
} from '@mui/material';
import BlankCard from '../shared/BlankCard.tsx';
import img1 from 'src/assets/images/profile/user-1.jpg';
import img2 from 'src/assets/images/profile/user-2.jpg';
import img3 from 'src/assets/images/profile/user-3.jpg';
import img4 from 'src/assets/images/profile/user-4.jpg';
import img5 from 'src/assets/images/profile/user-5.jpg';
import img6 from 'src/assets/images/profile/user-6.jpg';
import { IconCircle, IconClock, IconDots, IconEdit, IconPlus, IconTrash } from '@tabler/icons-react';

interface rowsType {
  status: string;
  avatar: string;
  tag: string;
  cname: string;
  email: string;
  teams: Array<{ name: string; bgcolor: string }>;
}

const rows: rowsType[] = [
  {
    status: '在线',
    avatar: img1,
    tag: 'rhye',
    cname: 'Olivia Rhye',
    email: 'olivia@ui.com',
    teams: [
      { name: '设计', bgcolor: 'primary.main' },
      { name: '产品', bgcolor: 'secondary.main' },
    ],
  },
  {
    status: '离线',
    avatar: img2,
    tag: 'steele',
    cname: 'Barbara Steele',
    email: 'steele@ui.com',
    teams: [
      { name: '产品', bgcolor: 'secondary.main' },
      { name: '运营', bgcolor: 'error.main' },
    ],
  },
  {
    status: 'active',
    avatar: img3,
    tag: 'gordon',
    cname: 'Leonard Gordon',
    email: 'olivia@ui.com',
    teams: [
      { name: '财务', bgcolor: 'primary.main' },
      { name: '客户成功', bgcolor: 'success.main' },
    ],
  },
  {
    status: 'offline',
    avatar: img4,
    tag: 'pope',
    cname: 'Evelyn Pope',
    email: 'steele@ui.com',
    teams: [
      { name: '运营', bgcolor: 'error.main' },
      { name: '设计', bgcolor: 'primary.main' },
    ],
  },
  {
    status: 'active',
    avatar: img5,
    tag: 'garza',
    cname: 'Tommy Garza',
    email: 'olivia@ui.com',
    teams: [{ name: '产品', bgcolor: 'secondary.main' }],
  },
  {
    status: 'active',
    avatar: img6,
    tag: 'vasquez',
    cname: 'Isabel Vasquez',
    email: 'steele@ui.com',
    teams: [{ name: '客户成功', bgcolor: 'success.main' }],
  },
];

const Table3 = () => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <BlankCard>
      <TableContainer>
        <Table aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>
                <Typography variant="h6">客户</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">状态</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">邮箱地址</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">团队</Typography>
              </TableCell>

              <TableCell></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.cname} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                <TableCell>
                  <Stack direction="row" spacing={2}>
                    <Avatar src={row.avatar} alt={row.avatar} sx={{ width: 42, height: 42 }} />
                    <Box>
                      <Typography variant="h6">{row.cname}</Typography>
                      <Typography variant="subtitle1" color="textSecondary">
                        @{row.tag}
                      </Typography>
                    </Box>
                  </Stack>
                </TableCell>
                <TableCell>
                  <Chip
                    label={row.status}
                    size="small"
                    icon={
                      row.status == '在线' ? <IconCircle width={14} /> : <IconClock width={14} />
                    }
                    sx={{
                      backgroundColor:
                        row.status == '在线'
                          ? (theme) => theme.palette.success.light
                          : (theme) => theme.palette.grey[100],
                      color:
                        row.status == 'active'
                          ? (theme) => theme.palette.success.main
                          : (theme) => theme.palette.grey[500],
                      '.MuiChip-icon': {
                        color: 'inherit !important',
                      },
                    }}
                  />
                </TableCell>
                <TableCell>
                  <Typography variant="subtitle1" color="textSecondary">
                    {row.email}
                  </Typography>
                </TableCell>
                <TableCell scope="row">
                  <Stack direction="row" spacing={1}>
                    {row.teams.map((team, i) => (
                      <Chip
                        label={team.name}
                        sx={{ backgroundColor: team.bgcolor, color: 'white', fontSize: '11px' }}
                        key={i}
                        size="small"
                      />
                    ))}
                  </Stack>
                </TableCell>
                <TableCell>
                  <IconButton
                    id="basic-button"
                    aria-controls={open ? 'basic-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClick}
                  >
                    <IconDots width={18} />
                  </IconButton>
                  <Menu
                    id="basic-menu"
                    anchorEl={anchorEl}
                    open={open}
                    onClose={handleClose}
                    MenuListProps={{
                      'aria-labelledby': 'basic-button',
                    }}
                  >
                    <MenuItem onClick={handleClose}>
                      <ListItemIcon>
                        <IconPlus width={18} />
                      </ListItemIcon>
                      添加
                    </MenuItem>
                    <MenuItem onClick={handleClose}>
                      <ListItemIcon>
                        <IconEdit width={18} />
                      </ListItemIcon>
                      编辑
                    </MenuItem>
                    <MenuItem onClick={handleClose}>
                      <ListItemIcon>
                        <IconTrash width={18} />
                      </ListItemIcon>
                      删除
                    </MenuItem>
                  </Menu>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </BlankCard>
  );
};

export default Table3;
