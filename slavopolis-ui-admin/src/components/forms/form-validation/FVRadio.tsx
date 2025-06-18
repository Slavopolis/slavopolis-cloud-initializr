// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { useFormik } from 'formik';
import * as yup from 'yup';

import {
  Box,
  Button,
  Stack,
  FormControlLabel,
  FormControl,
  RadioGroup,
  Radio,
  FormHelperText,
} from '@mui/material';

const validationSchema = yup.object({
  color: yup.string().required('颜色选择是必填项'),
});

const FVRadio = () => {
  const formik = useFormik({
    initialValues: {
      color: '',
    },
    validationSchema,
    onSubmit: (values) => {
      alert(values.color);
    },
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <Stack>
        <Box mt="-10px" mb={3}>
          <FormControl>
            <RadioGroup
              row
              aria-label="color"
              value={formik.values.color}
              onChange={formik.handleChange}
              name="color"
              id="color"
            >
              <FormControlLabel
                value="primary"
                control={
                  <Radio
                    sx={{
                      color: 'primary.main',
                      '&.Mui-checked': { color: 'primary.main' },
                    }}
                  />
                }
                label="主要"
              />
              <FormControlLabel
                value="error"
                control={
                  <Radio
                    sx={{
                      color: 'error.main',
                      '&.Mui-checked': { color: 'error.main' },
                    }}
                  />
                }
                label="错误"
              />
              <FormControlLabel
                value="secondary"
                control={
                  <Radio
                    sx={{
                      color: 'secondary.main',
                      '&.Mui-checked': { color: 'secondary.main' },
                    }}
                  />
                }
                label="次要"
              />
            </RadioGroup>
          </FormControl>
          {formik.errors.color && (
            <FormHelperText error id="standard-weight-helper-text-email-login">
              {' '}
              {formik.errors.color}{' '}
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

export default FVRadio;
