import { useMemo } from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import MenuItem from '@mui/material/MenuItem';
import { Input } from "@/components/molecules/Field/Input";
import { Select } from "@/components/molecules/Field/Select";
import { TextArea } from "@/components/molecules/Field/TextArea";

import { useRegisterFormContext } from "@hooks";
import { geographicRepresentationList } from "@utils";

export const FormStep5 = () => {
  const { candidateType, data, error, handleChange } = useRegisterFormContext();
  const options = useMemo(() => geographicRepresentationList(), []);

  const nameLabel = () => {
    switch (candidateType) {
      case 'individual':
        return 'Name or Alias*';
      case 'company':
        return 'Company Name*';
      case 'consortium':
        return 'Name of Consortium*';
      default:
        return '';
    }
  };

  const aboutLabel = () => {
    switch (candidateType) {
      case 'individual':
        return 'Introduce yourself*';
      case 'company':
        return 'Introduce your company*';
      case 'consortium':
        return 'Introduce your consortium*';
      default:
        return '';
    }
  };

  const renderEmail = () => {
    return (
      <Input
        errorMessage={error && error.email ? 'Enter a valid e-mail address' : ''}
        helpfulText={'Your email address will not be made public'}
        id="email'"
        label={'Email*'}
        name="email"
        onChange={handleChange}
        value={data.email}
      />
    );
  }

  const renderCountry = () => {
    return (
      <Select
        id="country"
        label={'Geographic Representation'}
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
    )
  }

  const renderPublicContact = () => {
    return (
      <Input
        helpfulText={'Social media handles or email address where you would like to be contacted by the Cardano Community (Will be made public)'}
        id="publicContact"
        label={'Public Point of Contact'}
        name="publicContact"
        onChange={handleChange}
        value={data.publicContact}
      />
    )
  }

  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '48px' }}>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <Input
          errorMessage={error && error.name ? 'This field is required.' : ''}
          id="name"
          label={nameLabel()}
          name="name"
          onChange={handleChange}
          value={data.name}
        />
        {candidateType === 'company' ? (
          <>
            <Input
              helpfulText="Will not be made public"
              id="keyContactPerson"
              label={'Key Contact Person'}
              name="keyContactPerson"
              onChange={handleChange}
              value={data.keyContactPerson}
            />
          </>
        ) : renderEmail()}
        {candidateType === 'individual' && renderCountry()}
        <Box>
          <Typography variant="subtitle2">Social media (Will be made public)</Typography>
          <Box sx={{ paddingTop: '4px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <Input
              name="socialX"
              onChange={handleChange}
              placeholder={'X (Twitter)'}
              value={data.socialX}
            />
            <Input
              name="socialLinkedin"
              onChange={handleChange}
              placeholder={'LinkedIn'}
              value={data.socialLinkedin}
            />
            <Input
              name="socialDiscord"
              onChange={handleChange}
              placeholder={'Discord'}
              value={data.socialDiscord}
            />
            <Input
              name="socialTelegram"
              onChange={handleChange}
              placeholder={'Telegram'}
              value={data.socialTelegram}
            />
            <Input
              name="socialOther"
              onChange={handleChange}
              placeholder={'Other'}
              value={data.socialOther}
            />
          </Box>
        </Box>
        {candidateType !== 'company' && renderPublicContact()}
      </Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {candidateType === 'company' && (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            {renderEmail()}
            {renderCountry()}
            {renderPublicContact()}
          </Box>
        )}
        <TextArea
          errorMessage={error && error.name ? 'This field is required.' : ''}
          helpfulText={'Short introduction about yourself and your candidacy'}
          id="about"
          label={aboutLabel()}
          name="about"
          onChange={handleChange}
          value={data.about}
        />
        <TextArea
          errorMessage={error && error.name ? 'This field is required.' : ''}
          helpfulText={'Extended information about yourself, your relevant experience, technical and governance background etc'}
          id="bio"
          label={'Bio*'}
          name="bio"
          onChange={handleChange}
          value={data.bio}
        />
        <TextArea
          helpfulText={'Any other relevant information that might not fit in elsewhere'}
          id="additionalInfo"
          label={'Additional information'}
          name="additionalInfo"
          onChange={handleChange}
          value={data.additionalInfo}
        />
        <Input
          helpfulText={'Must be able to embedd. Youtube is recommended'}
          id="videoPresentationLink"
          label={'Video link'}
          name="videoPresentationLink"
          onChange={handleChange}
          value={data.videoPresentationLink}
        />
      </Box>
    </Box>
  );
}
