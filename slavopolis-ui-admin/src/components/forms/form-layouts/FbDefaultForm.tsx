// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { FormControlLabel, Button, Grid, RadioGroup, FormControl, MenuItem } from '@mui/material';
import CustomTextField from '../theme-elements/CustomTextField.tsx';
import CustomSelect from '../theme-elements/CustomSelect.tsx';
import CustomCheckbox from '../theme-elements/CustomCheckbox.tsx';
import CustomRadio from '../theme-elements/CustomRadio.tsx';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import ParentCard from '../../shared/ParentCard.tsx';

interface numberType {
  value: string;
  label: string;
}

const numbers: numberType[] = [
  {
    value: 'one',
    label: '一',
  },
  {
    value: 'two',
    label: '二',
  },
  {
    value: 'three',
    label: '三',
  },
  {
    value: 'four',
    label: '四',
  },
];

const FbDefaultForm = () => {
  const [state, setState] = React.useState({
    checkedA: false,
    checkedB: false,
    checkedC: false,
  });

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setState({ ...state, [event.target.name]: event.target.checked });
  };

  const [value, setValue] = React.useState('');

  const handleChange2 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue(event.target.value);
  };

  const [number, setNumber] = React.useState('');

  const handleChange3 = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNumber(event.target.value);
  };

  return (
    <ParentCard title="默认表单">
      <form>
        <CustomFormLabel
          sx={{
            mt: 0,
          }}
          htmlFor="default-value"
        >
          默认文本
        </CustomFormLabel>
        <CustomTextField
          id="default-value"
          variant="outlined"
          defaultValue="张三"
          fullWidth
        />
        <CustomFormLabel htmlFor="email-text">邮箱</CustomFormLabel>
        <CustomTextField id="email-text" type="email" variant="outlined" fullWidth />
        <CustomFormLabel htmlFor="default-outlined-password-input">密码</CustomFormLabel>

        <CustomTextField
          id="default-outlined-password-input"
          type="password"
          autoComplete="current-password"
          variant="outlined"
          fullWidth
        />
        <CustomFormLabel htmlFor="outlined-multiline-static">文本区域</CustomFormLabel>

        <CustomTextField
          id="outlined-multiline-static"
          multiline
          rows={4}
          variant="outlined"
          fullWidth
        />
        <CustomFormLabel htmlFor="readonly-text">只读</CustomFormLabel>

        <CustomTextField
          id="readonly-text"
          defaultValue="你好世界"
          InputProps={{
            readOnly: true,
          }}
          variant="outlined"
          fullWidth
        />
        <Grid container spacing={0} my={2}>
          <Grid item lg={4} md={6} sm={12}>
            <FormControlLabel
              control={
                <CustomCheckbox
                  checked={state.checkedA}
                  onChange={handleChange}
                  name="checkedA"
                  color="primary"
                />
              }
              label="选中这个自定义复选框"
            />
            <FormControlLabel
              control={
                <CustomCheckbox
                  checked={state.checkedB}
                  onChange={handleChange}
                  name="checkedB"
                  color="primary"
                />
              }
              label="选中这个自定义复选框"
            />
            <FormControlLabel
              control={
                <CustomCheckbox
                  checked={state.checkedC}
                  onChange={handleChange}
                  name="checkedC"
                  color="primary"
                />
              }
              label="选中这个自定义复选框"
            />
          </Grid>
          <Grid item lg={4} md={6} sm={12}>
            <FormControl component="fieldset">
              <RadioGroup aria-label="gender" name="gender1" value={value} onChange={handleChange2}>
                <FormControlLabel
                  value="radio1"
                  control={<CustomRadio />}
                  label="切换这个自定义单选按钮"
                />
                <FormControlLabel
                  value="radio2"
                  control={<CustomRadio />}
                  label="切换这个自定义单选按钮"
                />
                <FormControlLabel
                  value="radio3"
                  control={<CustomRadio />}
                  label="切换这个自定义单选按钮"
                />
              </RadioGroup>
            </FormControl>
          </Grid>
        </Grid>
        <CustomFormLabel htmlFor="standard-select-number">选择</CustomFormLabel>
        <CustomSelect
          fullWidth
          id="standard-select-number"
          variant="outlined"
          value={number}
          onChange={handleChange3}
          sx={{
            mb: 2,
          }}
        >
          {numbers.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </CustomSelect>
        <div>
          <Button color="primary" variant="contained">
            提交
          </Button>
        </div>
      </form>
    </ParentCard>
  );
};

export default FbDefaultForm;
