// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { useFormik } from 'formik';
import * as yup from 'yup';

import { Box, Button, Stack, FormHelperText, MenuItem } from '@mui/material';

import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import CustomSelect from '../theme-elements/CustomSelect.tsx';

const validationSchema = yup.object({
  age: yup.number().required('年龄选择是必填项。'),
});

const FVSelect = () => {
  const formik = useFormik({
    initialValues: {
      age: '',
    },
    validationSchema,
    onSubmit: (values) => {
      alert(values.age);
    },
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <Stack>
        <Box mt="-10px" mb={3}>
          <CustomFormLabel>年龄</CustomFormLabel>
          <CustomSelect
            labelId="age-select"
            id="age"
            fullWidth
            name="age"
            value={formik.values.age}
            onChange={formik.handleChange}
          >
            <MenuItem value="">
              <em>无</em>
            </MenuItem>
            <MenuItem value={10}>十</MenuItem>
            <MenuItem value={20}>二十</MenuItem>
            <MenuItem value={30}>三十</MenuItem>
          </CustomSelect>
          {formik.errors.age && (
            <FormHelperText error id="standard-weight-helper-text-email-login">
              {' '}
              {formik.errors.age}{' '}
            </FormHelperText>
          )}
        </Box>
        <Stack direction="row" justifyContent="flex-end">
          <Button variant="contained" type="submit">
            提交
          </Button>
        </Stack>
      </Stack>
    </form>
  );
};

export default FVSelect;
