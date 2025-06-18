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
  AvatarGroup,
  Box, Stack
} from '@mui/material';
import BlankCard from '../shared/BlankCard.tsx';
import img1 from 'src/assets/images/profile/user-1.jpg';
import img2 from 'src/assets/images/profile/user-2.jpg';
import img3 from 'src/assets/images/profile/user-3.jpg';
import img4 from 'src/assets/images/profile/user-4.jpg';
import img5 from 'src/assets/images/profile/user-5.jpg';
import img6 from 'src/assets/images/profile/user-6.jpg';
import { IconDotsVertical, IconEdit, IconPlus, IconTrash } from '@tabler/icons-react';

interface rowsType {
  status: string;
  avatar: string;
  name: string;
  project: string;
  percent: number;
  users: Array<{ img: string }>;
}

const rows:rowsType[] = [
  {
    status: '活跃',
    avatar: img1,
    name: 'Olivia Rhye',
    project: 'Xtreme admin',
    percent: 60,
    users: [{ img: img1 }, { img: img2 }],
  },
  {
    status: '已取消',
    avatar: img2,
    name: 'Barbara Steele',
    project: 'Adminpro admin',
    percent: 30,
    users: [{ img: img1 }, { img: img2 }, { img: img3 }],
  },
  {
    status: 'active',
    avatar: img3,
    name: 'Leonard Gordon',
    project: 'Monster admin',
    percent: 45,
    users: [{ img: img3 }, { img: img2 }],
  },
  {
    status: '待处理',
    avatar: img4,
    name: 'Evelyn Pope',
    project: 'Materialpro admin',
    percent: 37,
    users: [{ img: img1 }, { img: img2 }, { img: img5 }],
  },
  {
    status: 'cancel',
    avatar: img5,
    name: 'Tommy Garza',
    project: 'Elegant admin',
    percent: 87,
    users: [{ img: img5 }, { img: img6 }],
  },
  {
    status: 'pending',
    avatar: img6,
    name: 'Isabel Vasquez',
    project: 'Modernize admin',
    percent: 32,
    users: [{ img: img2 }, { img: img4 }],
  },
];

const Table2 = () => {
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
                <Typography variant="h6">用户</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">项目名称</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">用户</Typography>
              </TableCell>
              <TableCell>
                <Typography variant="h6">状态</Typography>
              </TableCell>
              <TableCell></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.name} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                <TableCell>
                  <Stack direction="row" alignItems="center" spacing={2}>
                    <Avatar src={row.avatar} alt={row.avatar} sx={{ width: 42, height: 42 }} />
                    <Box>
                      <Typography variant="h6">{row.name}</Typography>
                    </Box>
                  </Stack>
                </TableCell>
                <TableCell scope="row">
                  <Typography variant="subtitle1" color="textSecondary">
                    {row.project}
                  </Typography>
                </TableCell>
                <TableCell>
                  <AvatarGroup sx={{ justifyContent: 'start' }}>
                    {row.users.map((user, i) => (
                      <Avatar
                        src={user.img}
                        alt={user.img}
                        key={i}
                        sx={{ width: 35, height: 35 }}
                      />
                    ))}
                  </AvatarGroup>
                </TableCell>
                <TableCell>
                  <Chip
                    label={row.status}
                    sx={{
                      backgroundColor:
                        row.status == '活跃'
                          ? (theme) => theme.palette.primary.light
                          : row.status == '已取消'
                            ? (theme) => theme.palette.error.light
                            : (theme) => theme.palette.success.light,
                      color:
                        row.status == 'active'
                          ? (theme) => theme.palette.primary.main
                          : row.status == 'cancel'
                            ? (theme) => theme.palette.error.main
                            : (theme) => theme.palette.success.main,
                    }}
                  />
                </TableCell>

                <TableCell>
                  <IconButton
                    id="basic-button"
                    aria-controls={open ? 'basic-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClick}
                  >
                    <IconDotsVertical width={18} />
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

export default Table2;
