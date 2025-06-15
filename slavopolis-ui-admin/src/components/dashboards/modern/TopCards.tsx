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
  borderRadius: '12px',
  overflow: 'hidden',
  '&:hover': {
    transform: 'translateY(-5px)',
    boxShadow: theme.shadows[8],
    '& .card-content': {
      background: 'rgba(255, 255, 255, 0.8)',
    },
    '& img': {
      transform: 'scale(1.1)',
    }
  },
}));

const StyledCardContent = styled(CardContent)({
  transition: 'all 0.3s ease-in-out',
  '& img': {
    transition: 'all 0.3s ease-in-out',
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
    <Grid container spacing={3} mt={3}>
      {topcards.map((topcard, i) => (
        <Grid item xs={12} sm={4} lg={2} key={i}>
          <StyledBox bgcolor={topcard.bgcolor + '.light'} textAlign="center">
            <StyledCardContent className="card-content">
              <img src={topcard.icon} alt={topcard.icon} width="50" />
              <Typography
                color={topcard.bgcolor + '.main'}
                mt={1}
                variant="subtitle1"
                fontWeight={600}
              >
                {topcard.title}
              </Typography>
              <Typography color={topcard.bgcolor + '.main'} variant="h4" fontWeight={600}>
                {topcard.digits}
              </Typography>
            </StyledCardContent>
          </StyledBox>
        </Grid>
      ))}
    </Grid>
  );
};

export default TopCards;
