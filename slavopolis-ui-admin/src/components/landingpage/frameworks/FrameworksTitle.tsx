// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { Grid, Typography } from '@mui/material';


const FrameworksTitle = () => {

    return (
        <Grid container spacing={3} justifyContent="center">
            <Grid item xs={12} sm={10} lg={8}>
                <Typography variant='h2' fontWeight={700} textAlign="center" sx={{
                    fontSize: {
                        lg: '36px',
                        xs: '25px'
                    },
                    lineHeight: {
                        lg: '43px',
                        xs: '30px'
                    }
                }}>使用 Slavopolis 提高开发速度并快速启动</Typography>
            </Grid>
        </Grid>
    );
};

export default FrameworksTitle;
