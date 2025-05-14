import Box from "@mui/material/Box";
import { Input } from "@/components/molecules/Field/Input";
import { TextArea } from "@/components/molecules/Field/TextArea";

import { useRegisterFormContext } from "@hooks";

export const FormStep7 = () => {
  const { candidateType, data, handleChange } = useRegisterFormContext();
  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Input
        helpfulText={'Write your X handle here and send a DM to @IntersectMBO to verify ownership of the account'}
        id="xverification"
        label={'X verification'}
        name="xverification"
        onChange={handleChange}
        value={data.xverification}
      />
      {candidateType !== 'consortium' && (
        <>
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
        </>
      )}
    </Box>
  );
}
