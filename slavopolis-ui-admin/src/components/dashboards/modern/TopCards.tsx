import { Box, CardContent, Grid, Typography, styled } from '@mui/material';

import icon3 from '../../../assets/images/svgs/icon-briefcase.svg';
import icon1 from '../../../assets/images/svgs/icon-connect.svg';
import icon5 from '../../../assets/images/svgs/icon-favorites.svg';
import icon4 from '../../../assets/images/svgs/icon-mailbox.svg';
import icon6 from '../../../assets/images/svgs/icon-speech-bubble.svg';
import icon2 from '../../../assets/images/svgs/icon-user-male.svg';

// 创建带有hover效果的StyledBox组件
const StyledBox = styled(Box)(({ theme }) => ({
  transition: 'all 0.3s ease-in-out',
  cursor: 'pointer',
  borderRadius: '16px',
  overflow: 'hidden',
  height: '80px',
  position: 'relative',
  background: theme.palette.mode === 'dark' ? 
    'linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, rgba(255, 255, 255, 0.03) 100%)' : 
    'inherit',
  backdropFilter: theme.palette.mode === 'dark' ? 'blur(20px)' : 'none',
  border: theme.palette.mode === 'dark' ? 
    '1px solid rgba(255, 255, 255, 0.08)' : 
    '1px solid rgba(0, 0, 0, 0.08)',
  boxShadow: theme.palette.mode === 'dark' ? 
    '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)' : 
    '0 2px 4px -1px rgba(0, 0, 0, 0.06), 0 1px 2px -1px rgba(0, 0, 0, 0.1)',
  '&:hover': {
    transform: 'translateY(-2px)',
    boxShadow: theme.palette.mode === 'dark' ? 
      '0 25px 50px -12px rgba(0, 0, 0, 0.25), 0 0 0 1px rgba(255, 255, 255, 0.1)' :
      '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    border: theme.palette.mode === 'dark' ? 
      '1px solid rgba(255, 255, 255, 0.15)' :
      '1px solid rgba(0, 0, 0, 0.12)',
    '& .card-content': {
      background: theme.palette.mode === 'dark' ? 
        'rgba(255, 255, 255, 0.12)' : 
        'rgba(255, 255, 255, 0.7)',
    },
    '& .icon-container': {
      transform: 'scale(1.05) rotate(5deg)',
      boxShadow: theme.palette.mode === 'dark' ? 
        '0 8px 25px rgba(0, 0, 0, 0.3)' :
        '0 8px 25px rgba(0, 0, 0, 0.15)',
    }
  },
}));

const StyledCardContent = styled(CardContent)({
  transition: 'all 0.3s ease-in-out',
  padding: '16px !important',
  height: '100%',
  display: 'flex',
  alignItems: 'center',
  '& .icon-container': {
    transition: 'all 0.3s ease-in-out',
    width: '48px',
    height: '48px',
    borderRadius: '12px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: '16px',
    flexShrink: 0,
  },
  '& .content-container': {
    flex: 1,
    textAlign: 'left',
  }
});

interface cardType {
  icon: string;
  title: string;
  digits: string;
  bgcolor: string;
}

const topcards: cardType[] = [
  {
    icon: icon2,
    title: '员工',
    digits: '96',
    bgcolor: 'primary',
  },
  {
    icon: icon3,
    title: '用户',
    digits: '3,650',
    bgcolor: 'warning',
  },
  {
    icon: icon4,
    title: '项目',
    digits: '356',
    bgcolor: 'secondary',
  },
  {
    icon: icon5,
    title: '事件',
    digits: '696',
    bgcolor: 'error',
  },
  {
    icon: icon6,
    title: '工资',
    digits: '$96k',
    bgcolor: 'success',
  },
  {
    icon: icon1,
    title: '报告',
    digits: '59',
    bgcolor: 'info',
  },
];

const TopCards = () => {
  return (
    <Grid container spacing={3} mt={2}>
      {topcards.map((topcard, i) => (
        <Grid item xs={12} sm={4} lg={2} key={i}>
          <StyledBox bgcolor={topcard.bgcolor + '.light'}>
            <StyledCardContent className="card-content">
              <Box 
                className="icon-container" 
                sx={{ 
                  bgcolor: topcard.bgcolor + '.main',
                  '& img': {
                    filter: 'brightness(0) invert(1)'
                  }
                }}
              >
                <img 
                  src={topcard.icon} 
                  alt={topcard.icon} 
                  width="24" 
                  height="24"
                />
              </Box>
              <Box className="content-container">
                <Typography
                  color={topcard.bgcolor + '.main'}
                  variant="body2"
                  fontWeight={500}
                  sx={{ lineHeight: 1.2, marginBottom: '4px' }}
                >
                  {topcard.title}
                </Typography>
                <Typography 
                  color={topcard.bgcolor + '.main'} 
                  variant="h5" 
                  fontWeight={700}
                  sx={{ lineHeight: 1.2 }}
                >
                  {topcard.digits}
                </Typography>
              </Box>
            </StyledCardContent>
          </StyledBox>
        </Grid>
      ))}
    </Grid>
  );
};

export default TopCards;
