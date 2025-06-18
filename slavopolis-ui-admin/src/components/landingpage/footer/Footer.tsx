// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Container, Grid, Link, Typography } from '@mui/material';
import logoIcon from 'src/assets/images/logos/logoIcon.svg';

const Footer = () => {
  return (
    <Container maxWidth="lg">
      <Grid container spacing={3} justifyContent="center" mt={4}>
        <Grid item xs={12} sm={5} lg={4} textAlign="center">
          <img src={logoIcon} alt="icon" />
          <Typography fontSize="16" color="textSecondary" mt={1} mb={4}>
            所有权利保留。由 Slavopolis 设计和开发。
            <Link target="_blank" href="https://adminmart.com/">
              <Typography color="textSecondary" component="span" display="inline">
                {' '}
                Slavopolis
              </Typography>{' '}
            </Link>
            .
          </Typography>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Footer;
