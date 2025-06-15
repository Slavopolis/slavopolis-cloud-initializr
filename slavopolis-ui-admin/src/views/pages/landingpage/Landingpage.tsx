// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import PageContainer from 'src/components/container/PageContainer';

// components
import Banner from '../../../components/landingpage/banner/Banner.tsx';
import C2a from '../../../components/landingpage/c2a/C2a.tsx';
import C2a2 from '../../../components/landingpage/c2a/C2a2.tsx';
import DemoSlider from '../../../components/landingpage/demo-slider/DemoSlider.tsx';
import Features from '../../../components/landingpage/features/Features.tsx';
import Footer from '../../../components/landingpage/footer/Footer.tsx';
import Frameworks from '../../../components/landingpage/frameworks/Frameworks.tsx';
import LpHeader from '../../../components/landingpage/header/Header.tsx';
import Testimonial from '../../../components/landingpage/testimonial/Testimonial.tsx';

const Landingpage = () => {
  return (
    <PageContainer title="Landingpage" description="this is Landingpage">
      <LpHeader />
      <Banner />
      <DemoSlider />
      <Frameworks />
      <Testimonial />
      <Features />
      <C2a />
      <C2a2 />
      <Footer />
    </PageContainer>
  );
};

export default Landingpage;
