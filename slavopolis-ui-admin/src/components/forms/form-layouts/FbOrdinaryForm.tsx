// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';
import { FormControlLabel, Button } from '@mui/material';
import CustomTextField from '../theme-elements/CustomTextField.tsx';
import CustomCheckbox from '../theme-elements/CustomCheckbox.tsx';
import CustomFormLabel from '../theme-elements/CustomFormLabel.tsx';
import ParentCard from '../../shared/ParentCard.tsx';

const FbOrdinaryForm = () => {
  const [state, setState] = React.useState({
    checkedB: false,
  });

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setState({ ...state, [event.target.name]: event.target.checked });
  };

  return (
    <ParentCard title="Ordrinary Form">
      <form>
        <CustomFormLabel
          sx={{
            mt: 0,
          }}
          htmlFor="email-address"
        >
          Email
        </CustomFormLabel>
        <CustomTextField
          id="email-address"
          helperText="We'll never share your email with anyone else."
          variant="outlined"
          fullWidth
        />
        <CustomFormLabel htmlFor="ordinary-outlined-password-input">Password</CustomFormLabel>

        <CustomTextField
          id="ordinary-outlined-password-input"
          type="password"
          autoComplete="current-password"
          variant="outlined"
          fullWidth
          sx={{
            mb: '10px',
          }}
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
          label="Check Me Out!"
          sx={{
            mb: 1,
          }}
        />
        <div>
          <Button color="primary" variant="contained">
            Submit
          </Button>
        </div>
      </form>
    </ParentCard>
  );
};

export default FbOrdinaryForm;
