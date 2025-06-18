// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Button, Stack } from '@mui/material';

const DefaultButtons = () => (
  <Stack spacing={1} direction={{ xs: 'column', sm: 'row' }} justifyContent="center">
    <Button variant="contained" color="primary">
      主要
    </Button>
    <Button variant="contained" color="secondary">
      次要
    </Button>
    <Button disabled>禁用</Button>
    <Button href="#text-buttons" variant="contained" color="primary">
      链接
    </Button>
  </Stack>
);

export default DefaultButtons;
