// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { Grid, Typography } from '@mui/material';

// components
import Breadcrumb from 'src/layouts/full/shared/breadcrumb/Breadcrumb';
import PageContainer from 'src/components/container/PageContainer';
import ParentCard from 'src/components/shared/ParentCard';
import BasicLayout from '../../components/forms/form-horizontal/BasicLayout.tsx';
import BasicIcons from '../../components/forms/form-horizontal/BasicIcons.tsx';
import FormSeparator from '../../components/forms/form-horizontal/FormSeparator.tsx';
import FormLabelAlignment from '../../components/forms/form-horizontal/FormLabelAlignment.tsx';
import CollapsibleForm from '../../components/forms/form-horizontal/CollapsibleForm.tsx';
import FormTabs from '../../components/forms/form-horizontal/FormTabs.tsx';

const BCrumb = [
  {
    to: '/',
    title: 'Home',
  },
  {
    title: 'Horizontal Form',
  },
];

const FormHorizontal = () => {
  return (
    <PageContainer title="Horizontal Form" description="this is Horizontal Form page">
      {/* breadcrumb */}
      <Breadcrumb title="Horizontal Form" items={BCrumb} />
      {/* end breadcrumb */}
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <ParentCard title="Basic Layout">
            <BasicLayout />
          </ParentCard>
        </Grid>
        <Grid item xs={12}>
          <ParentCard title="Basic with Icons">
            <BasicIcons />
          </ParentCard>
        </Grid>
        <Grid item xs={12}>
          <ParentCard title="Form Separator">
            <FormSeparator />
          </ParentCard>
        </Grid>
        <Grid item xs={12}>
          <ParentCard title="Form Label Alignment">
            <FormLabelAlignment />
          </ParentCard>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="h5" mb={3}>Collapsible Section</Typography>
          <CollapsibleForm />
        </Grid>
        <Grid item xs={12}>
          <Typography variant="h5" mb={3}>Form with Tabs</Typography>
          <FormTabs />
        </Grid>
      </Grid>
    </PageContainer>
  );
};

export default FormHorizontal;
