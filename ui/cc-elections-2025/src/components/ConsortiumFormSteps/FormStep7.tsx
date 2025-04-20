import Box from "@mui/material/Box";
import { Input } from "../molecules/Field/Input.tsx";
import { TextArea } from "../molecules/Field/TextArea.tsx";
import { useConsortiumFormContext } from "@hooks";


export const FormStep7 = () => {
  const { data, handleChange } = useConsortiumFormContext();
  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <TextArea
        helpfulText={'Short introduction about yourself and your candidacy'}
        id="about"
        label={'Introduce your consortium'}
        name="about"
        onChange={handleChange}
        value={data.about}
      />
      <TextArea
        helpfulText={'Extended information about your consortium'}
        id="bio"
        label={'Bio'}
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
        label={'Video presentation link'}
        name="videoPresentationLink"
        onChange={handleChange}
        value={data.videoPresentationLink}
      />
    </Box>
  );
}
