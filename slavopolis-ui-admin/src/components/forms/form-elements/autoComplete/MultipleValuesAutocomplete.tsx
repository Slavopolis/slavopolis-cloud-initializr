// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import CustomTextField from '../../theme-elements/CustomTextField.tsx';
import top100Films from './data.ts';

const MultipleValuesAutocomplete = () => (
  <Autocomplete
    multiple
    fullWidth
    id="tags-outlined"
    options={top100Films}
    getOptionLabel={(option) => option.title}
    defaultValue={[top100Films[13]]}
    filterSelectedOptions
    renderInput={(params) => (
      <CustomTextField {...params} placeholder="Favorites" aria-label="Favorites" />
    )}
  />
);

export default MultipleValuesAutocomplete;
