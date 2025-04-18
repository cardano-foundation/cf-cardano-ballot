import Box from "@mui/material/Box";
import { Input } from "../molecules/Field/Input.tsx";
import { TextArea } from "../molecules/Field/TextArea.tsx";
import useFormContext from "../../hooks/useFormContext.ts";


export const FormStep7 = () => {
  const { data, handleChange } = useFormContext();
  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Input
        helpfulText={'A light version of KYC/KYB to verify that the applicant is who they claim to be using a video call. Much like how Catalyst is doing it.'}
        id="liveliness"
        label={'Liveliness verification'}
        name="liveliness"
        onChange={handleChange}
        value={data.liveliness}
      />
      <Input
        helpfulText={'Write your X handle here and send a DM to @IntersectMBO to verify ownershop of the account'}
        id="xverification"
        label={'X verification'}
        name="xverification"
        onChange={handleChange}
        value={data.xverification}
      />
      <TextArea
        helpfulText={'Are you acting as DRep, as SPO, or both, or would have any other role that could be percieved as conflict of interest as a Constitutional Committee member?'}
        id="conflictOfInterest"
        label={'Conflict of Interest'}
        name="conflictOfInterest"
        onChange={handleChange}
        value={data.conflictOfInterest}
      />
      <Input
        id="drepId"
        label={'If DRep, please provide DRep ID'}
        name="drepId"
        onChange={handleChange}
        value={data.drepId}
      />
      <Input
        id="stakeId"
        label={'If SPO, please provide Stake ID'}
        name="stakeId"
        onChange={handleChange}
        value={data.stakeId}
      />
    </Box>
  );
}
