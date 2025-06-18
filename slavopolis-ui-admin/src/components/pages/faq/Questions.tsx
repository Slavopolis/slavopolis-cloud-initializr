// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Grid, Typography, Accordion, AccordionSummary, AccordionDetails, Divider, Box } from '@mui/material';
import { IconChevronDown } from '@tabler/icons-react';

const Questions = () => {
  return (
    <Box>
      <Grid container spacing={3} justifyContent="center">
        <Grid item xs={12} lg={8}>
          <Typography variant="h3" textAlign="center" mb={1}>常见问题</Typography>
          <Typography variant="h6" fontWeight={400} color="textSecondary" textAlign="center" mb={4}>了解更多关于即用型管理后台模板</Typography>
          <Accordion elevation={9}>
            <AccordionSummary
              expandIcon={<IconChevronDown />}
              aria-controls="panel1a-content"
              id="panel1a-header"
            >
              <Typography variant="h6" px={2} py={1}>什么是管理后台？</Typography>
            </AccordionSummary>
            <Divider />
            <AccordionDetails>
              <Typography variant="subtitle1" pt={1} px={2} color="textSecondary">
                管理后台是网站或应用程序的后端界面，用于管理网站的整体内容和设置。
                网站所有者广泛使用它来跟踪他们的网站、更改内容等。
              </Typography>
            </AccordionDetails>
          </Accordion>
          <Accordion elevation={9}>
            <AccordionSummary
              expandIcon={<IconChevronDown />}
              aria-controls="panel2a-content"
              id="panel2a-header"
            >
              <Typography variant="h6" px={2} py={1}>管理后台模板应该包含什么？</Typography>
            </AccordionSummary>
            <Divider />
            <AccordionDetails>
              <Typography variant="subtitle1" pt={1} px={2} color="textSecondary">
                管理后台模板应该包含用户友好和SEO友好的设计，以及各种组件和应用设计，
                以帮助您轻松创建自己的Web应用。这包括自定义选项、技术支持和约6个月的
                后续更新。
              </Typography>
            </AccordionDetails>
          </Accordion>
          <Accordion elevation={9}>
            <AccordionSummary
              expandIcon={<IconChevronDown />}
              aria-controls="panel3a-content"
              id="panel3a-header"
            >
              <Typography variant="h6" px={2} py={1}>为什么要从AdminMart购买管理后台模板？</Typography>
            </AccordionSummary>
            <Divider />
            <AccordionDetails>
              <Typography variant="subtitle1" pt={1} px={2} color="textSecondary">
                Adminmart提供易于使用且完全可定制的高质量模板。拥有超过101,801位满意的
                客户，并受到10,000多家公司的信任。AdminMart被认为是购买管理后台模板的
                领先在线来源。
              </Typography>
            </AccordionDetails>
          </Accordion>
          <Accordion elevation={9}>
            <AccordionSummary
              expandIcon={<IconChevronDown />}
              aria-controls="panel4a-content"
              id="panel4a-header"
            >
              <Typography variant="h6" px={2} py={1}>Adminmart是否提供退款保证？</Typography>
            </AccordionSummary>
            <Divider />
            <AccordionDetails>
              <Typography variant="subtitle1" pt={1} px={2} color="textSecondary">
                大多数公司都不提供退款保证，但如果您对我们的产品不满意，Adminmart
                会为您提供100%退款保证。
              </Typography>
            </AccordionDetails>
          </Accordion>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Questions;
