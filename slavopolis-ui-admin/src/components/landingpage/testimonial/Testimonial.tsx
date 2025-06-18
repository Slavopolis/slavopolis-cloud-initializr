// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Avatar, Box, CardContent, Container, Typography, Rating, Stack } from '@mui/material';
import TestimonialTitle from './TestimonialTitle.tsx';
import BlankCard from '../../shared/BlankCard.tsx';
import img1 from 'src/assets/images/profile/user-1.jpg';
import img2 from 'src/assets/images/profile/user-2.jpg';
import img3 from 'src/assets/images/profile/user-3.jpg';
import AnimationFadeIn from '../animation/Animation.tsx';

//Carousel slider for product
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import './testimonial.css';

interface SliderType {
  title: string;
  subtitle: string;
  avatar: string;
  subtext: string;
}

const SliderData: SliderType[] = [
  {
    title: 'Jenny Wilson',
    subtitle: '功能可用性',
    avatar: img1,
    subtext:
      'adminmart的仪表板模板帮助我为我的仪表板提供了一个干净流畅的外观，使其完全符合我的期望。'
  },
  {
    title: '崔民山',
    subtitle: '功能可用性',
    avatar: img2,
    subtext:
      '设计质量非常出色，定制性和灵活性都比市场上其他产品要好得多。我强烈推荐AdminMart给其他人。'
  },
  {
    title: 'Eminson Mendoza',
    subtitle: '功能可用性',
    avatar: img3,
    subtext:
      '这个模板很棒，UI丰富且与时俱进。虽然已经相当完整，但我建议改进一下文档。感谢！强烈推荐！'
  },
  {
    title: 'Jenny Wilson',
    subtitle: '功能可用性',
    avatar: img1,
    subtext:
      'adminmart的仪表板模板帮助我为我的仪表板提供了一个干净流畅的外观，使其完全符合我的期望。',
  },
  {
    title: '崔民山',
    subtitle: '功能可用性',
    avatar: img2,
    subtext:
      '设计质量非常出色，定制性和灵活性都比市场上其他产品要好得多。我强烈推荐AdminMart给其他人。',
  },
  {
    title: 'Eminson Mendoza',
    subtitle: '功能可用性',
    avatar: img3,
    subtext:
      '这个模板很棒，UI丰富且与时俱进。虽然已经相当完整，但我建议改进一下文档。感谢！强烈推荐！'
  },
];

const Testimonial = () => {
  const [value, setValue] = React.useState<number | null>(5);

  const settings = {
    className: 'testimonial-slider',
    dots: true,
    arrows: false,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 1,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 3,
        },
      },
      {
        breakpoint: 768,
        settings: {
          slidesToShow: 2,
        },
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 2,
        },
      },
      {
        breakpoint: 480,
        settings: {
          slidesToShow: 1,
        },
      },
    ],
  };

  return (
    <Box pt={14} pb={11}>
      <Container maxWidth="lg">
        <TestimonialTitle />
        <Box mt={5}>
          <AnimationFadeIn>
            <Slider {...settings}>
              {SliderData.map((slider, index) => (
                <Box p="15px" key={index}>
                  <BlankCard>
                    <CardContent>
                      <Stack direction="row">
                        <Avatar src={slider.avatar} alt="user" sx={{ width: 40, height: 40 }} />
                        <Box ml={2}>
                          <Typography variant="h6">{slider.title}</Typography>
                          <Typography color="textSecondary" variant="subtitle1">
                            {slider.subtitle}
                          </Typography>
                        </Box>
                        <Box ml="auto">
                          <Rating
                            size="small"
                            name="simple-controlled"
                            value={value}
                            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                            // @ts-ignore
                            onChange={(event, newValue) => {
                              setValue(newValue);
                            }}
                          />
                        </Box>
                      </Stack>
                      <Typography fontSize="15px" color="textSecondary" mt={3}>
                        {slider.subtext}
                      </Typography>
                    </CardContent>
                  </BlankCard>
                </Box>
              ))}
            </Slider>
          </AnimationFadeIn>
        </Box>
      </Container>
    </Box>
  );
};

export default Testimonial;
