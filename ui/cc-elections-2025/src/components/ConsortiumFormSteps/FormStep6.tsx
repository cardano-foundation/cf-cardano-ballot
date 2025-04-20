import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Input } from "../molecules/Field/Input.tsx";
import { useConsortiumFormContext } from "@hooks";


export const FormStep6 = () => {
  const { data, error, handleChange } = useConsortiumFormContext();


  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Input
        errorMessage={error && error.name ? 'This field is required.' : ''}
        id="name"
        label={'Name of Consortium*'}
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
      <Input
        errorMessage={error && error.country ? 'This field is required.' : ''}
        id="country"
        label={'Country of Registration*'}
        name="country"
        onChange={handleChange}
        value={data.country}
      />
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
