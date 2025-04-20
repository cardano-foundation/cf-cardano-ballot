import React from 'react';
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Input } from "../molecules/Field/Input.tsx";
import { useConsortiumFormContext } from "@hooks";
import {TextArea} from "@/components/molecules/Field/TextArea.tsx";


export const FormStep5 = () => {
  const { data, handleMemberChange } = useConsortiumFormContext();

  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {Array.from({ length: data.membersAmount }).map((_item, index) => (
        <React.Fragment key={index}>
          <Typography variant={"h3"}>{`Member ${index + 1}`}</Typography>
          <Input
            id="name"
            label={'Name or Alias'}
            name="name"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].name}
          />
          <Input
            id="country"
            label={'Country of Registration'}
            name="country"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].country}
          />
          <TextArea
            helpfulText={'Extended information about your company, your relevant experience, technical and governance background etc'}
            id="bio"
            label={'Member bio'}
            name="bio"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].bio}
          />
          <Box>
            <Typography variant="subtitle2">Social media (Will be made public)</Typography>
            <Box sx={{ paddingTop: '4px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <Input
                name="socialX"
                onChange={(event) => handleMemberChange(event, index)}
                value={data.members[index].socialX}
              />
              <Input
                name="socialLinkedin"
                onChange={(event) => handleMemberChange(event, index)}
                value={data.members[index].socialLinkedin}
              />
              <Input
                name="socialDiscord"
                onChange={(event) => handleMemberChange(event, index)}
                value={data.members[index].socialDiscord}
              />
              <Input
                name="socialTelegram"
                onChange={(event) => handleMemberChange(event, index)}
                value={data.members[index].socialTelegram}
              />
              <Input
                name="socialOther"
                onChange={(event) => handleMemberChange(event, index)}
                value={data.members[index].socialOther}
              />
            </Box>
          </Box>
          <Input
            helpfulText={'Write your X handle here and send a DM to @IntersectMBO to verify ownershop of the account'}
            id="xverification"
            label={'X verification'}
            name="xverification"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].xverification}
          />
          <Input
            helpfulText={'A light version of KYC/KYB to verify that the applicant is who they claim to be using a video call. Much like how Catalyst is doing it.'}
            id="liveliness"
            label={'Liveliness verification'}
            name="liveliness"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].liveliness}
          />
          <TextArea
            helpfulText={'Are you acting as DRep, as SPO, or both, or would have any other role that could be percieved as conflict of interest as a Constitutional Committee member?'}
            id="conflictOfInterest"
            label={'Conflict of Interest'}
            name="conflictOfInterest"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].conflictOfInterest}
          />
          <Input
            id="drepId"
            label={'If DRep, please provide DRep ID'}
            name="drepId"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].drepId}
          />
          <Input
            id="stakeId"
            label={'If SPO, please provide Stake ID'}
            name="stakeId"
            onChange={(event) => handleMemberChange(event, index)}
            value={data.members[index].stakeId}
          />
        </React.Fragment>
      ))}
    </Box>
  );
}
