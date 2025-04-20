import Box from "@mui/material/Box";
import { Input } from "../molecules/Field/Input.tsx";
import { useConsortiumFormContext } from "@hooks";


export const FormStep4 = () => {
  const { data, error, handleChange } = useConsortiumFormContext();
  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Input
        errorMessage={error && error.membersAmount ? 'This field is required.' : ''}
        helpfulText={'Must be 2 or more members.'}
        id="membersAmount'"
        label={'How many members are there in the consortium?*'}
        name="membersAmount"
        onChange={handleChange}
        value={data.membersAmount}
      />
    </Box>
  );
}
