import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Input } from "../molecules/Field/Input.tsx";
import useFormContext from "../../hooks/useCompanyFormContext";


export const FormStep4 = () => {
  const { data, error, handleChange } = useFormContext();
  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Input
        id="name"
        label={'Company Name'}
        name="name"
        onChange={handleChange}
        value={data.name}
      />
      <Input
        id="registrationNumber"
        label={'Registration Number'}
        name="registrationNumber"
        onChange={handleChange}
        value={data.registrationNumber}
      />
      <Input
        id="keyContactPerson"
        label={'Key Contact Person'}
        name="keyContactPerson"
        onChange={handleChange}
        value={data.keyContactPerson}
      />
      <Input
        id="xtwitter"
        label={'X (Twitter)'}
        name="xtwitter"
        onChange={handleChange}
        value={data.xtwitter}
      />
      <Input
        id="linkedin"
        label={'LinkedIn'}
        name="linkedin"
        onChange={handleChange}
        value={data.linkedin}
      />
      <Input
        id="discord"
        label={'Discord'}
        name="discord"
        onChange={handleChange}
        value={data.discord}
      />
      <Input
        id="telegram"
        label={'Telegram'}
        name="telegram"
        onChange={handleChange}
        value={data.telegram}
      />
      <Input
        id="website"
        label={'Website'}
        name="website"
        onChange={handleChange}
        value={data.website}
      />
      <Input
        id="other"
        label={'Other'}
        name="other"
        onChange={handleChange}
        value={data.other}
      />
      <Input
        errorMessage={error && error.email ? 'Enter a valid e-mail address' : ''}
        helpfulText={'Will not be made public'}
        id="email'"
        label={'Email*'}
        name="email"
        onChange={handleChange}
        value={data.email}
      />
      <Input id="country" label={'Country of Registration'} name="country" onChange={handleChange} value={data.country} />
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
        helpfulText={'Social media handles or email address where you would like to be contacted by the Cardano Community (Will be made public)'}
        id="publicContact"
        label={'Public Point of Contact'}
        name="publicContact"
        onChange={handleChange}
        value={data.publicContact}
      />
    </Box>
  );
}
