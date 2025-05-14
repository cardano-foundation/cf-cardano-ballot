import { useMemo, useImperativeHandle, useRef } from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import MenuItem from '@mui/material/MenuItem';
import { Input } from "@/components/molecules/Field/Input";
import { Select } from "@/components/molecules/Field/Select";
import { TextArea } from "@/components/molecules/Field/TextArea";

import { useRegisterFormContext } from "@hooks";
import { geographicRepresentationList } from "@utils";
import type { Step5RefsType } from '@/types/formData.ts';

type FormStep5Props = {
  ref: React.Ref<Step5RefsType>;
}

export const FormStep5 = ({ ref }: FormStep5Props) => {
  const { candidateType, data, error, handleChange } = useRegisterFormContext();
  const options = useMemo(() => geographicRepresentationList(), []);

  const inputRefs = useRef<Record<string, HTMLInputElement | HTMLTextAreaElement | null>>({});

  useImperativeHandle(ref, () => ({
    focusField: (name: string) => {
      inputRefs.current[name]?.focus();
    },
  }));

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
        ref={(el) => { inputRefs.current['email'] = el }}
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
        errorMessage={error && error.publicContact ? 'Enter a valid e-mail address' : ''}
        helpfulText={'Email address where you would like to be contacted by the Cardano Community (Will be made public)'}
        id="publicContact"
        label={'Public Point of Contact'}
        name="publicContact"
        onChange={handleChange}
        value={data.publicContact}
        ref={(el) => { inputRefs.current['publicContact'] = el }}
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
          ref={(el) => { inputRefs.current['name'] = el }}
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
              errorMessage={error && error.socialX ? 'Enter a valid X URL: https://x.com/Your_Username' : ''}
              name="socialX"
              onChange={handleChange}
              placeholder={'X (Twitter)'}
              value={data.socialX}
              ref={(el) => { inputRefs.current['socialX'] = el }}
            />
            <Input
              errorMessage={error && error.socialLinkedin ? 'Enter a valid LinkedIn URL: https://www.linkedin.com/in/Your_Username' : ''}
              name="socialLinkedin"
              onChange={handleChange}
              placeholder={'LinkedIn'}
              value={data.socialLinkedin}
              ref={(el) => {inputRefs.current['socialLinkedin'] = el}}
            />
            <Input
              errorMessage={error && error.socialDiscord ? 'Enter a valid Discord URL: https://discordapp.com/users/Your_User_ID' : ''}
              name="socialDiscord"
              onChange={handleChange}
              placeholder={'Discord'}
              value={data.socialDiscord}
              ref={(el) => {inputRefs.current['socialDiscord'] = el}}
            />
            <Input
              errorMessage={error && error.socialTelegram ? 'Enter a valid Telegram URL: https://t.me/Your_Username' : ''}
              name="socialTelegram"
              onChange={handleChange}
              placeholder={'Telegram'}
              value={data.socialTelegram}
              ref={(el) => {inputRefs.current['socialTelegram'] = el}}
            />
            <Input
              errorMessage={error && error.socialWebsite ? 'Enter a valid URL' : ''}
              name="socialWebsite"
              onChange={handleChange}
              placeholder={'Website'}
              value={data.socialWebsite}
              ref={(el) => {inputRefs.current['socialWebsite'] = el}}
            />
            <Input
              errorMessage={error && error.socialOther ? 'Enter a valid URL' : ''}
              name="socialOther"
              onChange={handleChange}
              placeholder={'Other'}
              value={data.socialOther}
              ref={(el) => {inputRefs.current['socialOther'] = el}}
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
          errorMessage={error && error.about ? 'This field is required.' : ''}
          helpfulText={'Short introduction about yourself and your candidacy'}
          id="about"
          label={aboutLabel()}
          name="about"
          onChange={handleChange}
          value={data.about}
          ref={(el) => { inputRefs.current['about'] = el }}
        />
        <TextArea
          errorMessage={error && error.bio ? 'This field is required.' : ''}
          helpfulText={'Extended information about yourself, your relevant experience, technical and governance background etc'}
          id="bio"
          label={'Bio*'}
          name="bio"
          onChange={handleChange}
          value={data.bio}
          ref={(el) => { inputRefs.current['bio'] = el }}
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
          errorMessage={error && error.videoPresentationLink ? 'Enter a valid YouTube or X post URL' : ''}
          helpfulText={'(Optional) To help voters get a better sense of you and your candidacy, feel free to include a video (as a YouTube link or X post) introducing yourself. This also helps voters confirm active participation.'}
          id="videoPresentationLink"
          label={'Video link'}
          name="videoPresentationLink"
          onChange={handleChange}
          value={data.videoPresentationLink}
          ref={(el) => {inputRefs.current['videoPresentationLink'] = el}}
        />
      </Box>
    </Box>
  );
};
