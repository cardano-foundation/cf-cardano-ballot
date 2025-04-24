import { useMemo } from "react";
import countryList from 'react-select-country-list';
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import MenuItem from '@mui/material/MenuItem';
import { Input } from "../molecules/Field/Input.tsx";
import { Select } from "../molecules/Field/Select.tsx";
import { useFormContext } from "@hooks";


export const FormStep4 = () => {
  const { data, error, handleChange } = useFormContext();
  const options = useMemo(() => countryList().getData(), [])

  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Input
        errorMessage={error && error.name ? 'This field is required.' : ''}
        id="name"
        label={'Name or Alias*'}
        name="name"
        onChange={handleChange}
        value={data.name}
      />
      <Input
        errorMessage={error && error.email ? 'Enter a valid e-mail address' : ''}
        helpfulText={'Your email address will not be made public'}
        id="email'"
        label={'Email*'}
        name="email"
        onChange={handleChange}
        value={data.email}
      />
      <Select
        errorMessage={error && error.country ? 'This field is required.' : ''}
        id="country"
        label={'Country of Residency*'}
        name="country"
        onChange={handleChange}
        displayEmpty={true}
        value={data.country}
      >
        <MenuItem disabled value="">
          Choose from list
        </MenuItem>
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
        ))}
      </Select>
      <Box>
        <Typography variant="subtitle2">Social media (Will be made public)</Typography>
        <Box sx={{ paddingTop: '4px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <Input
            name="socialX"
            onChange={handleChange}
            value={data.socialX}
          />
          <Input
            name="socialLinkedin"
            onChange={handleChange}
            value={data.socialLinkedin}
          />
          <Input
            name="socialDiscord"
            onChange={handleChange}
            value={data.socialDiscord}
          />
          <Input
            name="socialTelegram"
            onChange={handleChange}
            value={data.socialTelegram}
          />
          <Input
            name="socialOther"
            onChange={handleChange}
            value={data.socialOther}
          />
        </Box>
      </Box>
      <Input
        errorMessage={error && error.publicContact   ? 'This field is required.' : ''}
        helpfulText={'Social media handles or email address where you would like to be contacted by the Cardano Community (Will be made public)'}
        id="publicContact"
        label={'Public Point of Contact*'}
        name="publicContact"
        onChange={handleChange}
        value={data.publicContact}
      />
    </Box>
  );
}
