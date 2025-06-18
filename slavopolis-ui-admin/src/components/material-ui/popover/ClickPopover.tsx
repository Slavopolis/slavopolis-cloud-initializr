// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Popover, Typography, Button, Box } from '@mui/material';

const ClickPopover = () => {
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? 'simple-popover' : undefined;

  return (
    <>
      <Button aria-describedby={id} variant="contained" onClick={handleClick}>
        打开弹出框
      </Button>
      <Popover
        id={id}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
      >
        <Box p={2}>
          <Typography variant="h6" mb={1}>
            基础弹出框
          </Typography>
          <Typography color="textSecondary">
            此组件基于Modal组件构建。
          </Typography>
        </Box>
      </Popover>
    </>
  );
};
export default ClickPopover;
