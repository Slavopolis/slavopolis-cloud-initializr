import { Box, Button, Divider, Grid, Menu, Typography } from '@mui/material';
import { IconChevronDown, IconHelp } from '@tabler/icons-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import AppLinks from './AppLinks.tsx';
import QuickLinks from './QuickLinks.tsx';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore

const AppDD = () => {
  const [anchorEl2, setAnchorEl2] = useState(null);

  const handleClick2 = (event: any) => {
    setAnchorEl2(event.currentTarget);
  };

  const handleClose2 = () => {
    setAnchorEl2(null);
  };

  return (
    <>
      <Box>
        <Button
          aria-label="show 11 new notifications"
          color="inherit"
          variant="text"
          aria-controls="msgs-menu"
          aria-haspopup="true"
          sx={{
            bgcolor: anchorEl2 ? 'primary.light' : '',
            color: anchorEl2 ? 'primary.main' : (theme) => theme.palette.text.secondary,
          }}
          onClick={handleClick2}
          endIcon={<IconChevronDown size="15" style={{ marginLeft: '-5px', marginTop: '2px' }} />}
        >
          应用程序
        </Button>
        {/* ------------------------------------------- */}
        {/* Message Dropdown */}
        {/* ------------------------------------------- */}
        <Menu
          id="msgs-menu"
          anchorEl={anchorEl2}
          keepMounted
          open={Boolean(anchorEl2)}
          onClose={handleClose2}
          anchorOrigin={{ horizontal: 'left', vertical: 'bottom' }}
          transformOrigin={{ horizontal: 'left', vertical: 'top' }}
          sx={{
            '& .MuiMenu-paper': {
              width: '850px',
            },
            '& .MuiMenu-paper ul': {
              p: 0,
            },
          }}
        >
          <Grid container>
            <Grid item sm={8} display="flex">
              <Box p={4} pr={0} pb={3}>
                <AppLinks />
                <Divider />
                <Box
                  sx={{
                    display: {
                      xs: 'none',
                      sm: 'flex',
                    },
                  }}
                  alignItems="center"
                  justifyContent="space-between"
                  pt={2}
                  pr={4}
                >
                  <Link to="/faq">
                    <Typography
                      variant="subtitle2"
                      fontWeight="600"
                      color="textPrimary"
                      display="flex"
                      alignItems="center"
                      gap="4px"
                    >
                      <IconHelp width={24} />
                      常见问题
                    </Typography>
                  </Link>
                  <Button variant="contained" color="primary">
                    查看
                  </Button>
                </Box>
              </Box>
              <Divider orientation="vertical" />
            </Grid>
            <Grid item sm={4}>
              <Box p={4}>
                <QuickLinks />
              </Box>
            </Grid>
          </Grid>
        </Menu>
      </Box>
      <Button color="inherit" sx={{color: (theme) => theme.palette.text.secondary}} variant="text" to="/apps/chats" component={Link}>
        聊天
      </Button>
      <Button color="inherit" sx={{color: (theme) => theme.palette.text.secondary}} variant="text" to="/apps/calendar" component={Link}>
        日历
      </Button>
      <Button color="inherit" sx={{color: (theme) => theme.palette.text.secondary}} variant="text" to="/apps/email" component={Link}>
        电子邮件    
      </Button>
    </>
  );
};

export default AppDD;
