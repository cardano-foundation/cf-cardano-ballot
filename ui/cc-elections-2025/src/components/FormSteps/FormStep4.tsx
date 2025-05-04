import { Fragment, useMemo } from "react";
import Box from "@mui/material/Box";
import Divider from '@mui/material/Divider';
import MenuItem from "@mui/material/MenuItem";
import Typography from "@mui/material/Typography";

import { Input } from "@/components/molecules/Field/Input";
import { TextArea } from "@/components/molecules/Field/TextArea";
import { Select } from "@/components/molecules/Field/Select";
import { Button } from "@atoms";

import { ICONS } from "@consts";
import { useRegisterFormContext } from "@hooks";
import { geographicRepresentationList } from "@utils";

export const FormStep4 = () => {
  const { data, error, setData, memberInit, handleMemberChange } = useRegisterFormContext();
  const options = useMemo(() => geographicRepresentationList(), []);

  const handleOnClick = () => {
    if (data.membersAmount >= 20) return;
    setData(prevData => ({...prevData, members: [...prevData.members, memberInit ], membersAmount: prevData.membersAmount + 1}));
  }

  return (
    <Box sx={{ paddingTop: '16px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {Array.from({ length: data.membersAmount }).map((_item, index) => (
        <Fragment key={index}>
          <Typography variant={"h3"}>{`Member ${index + 1}`}</Typography>
          <Input
            errorMessage={error && error.members && error.members[index] && error.members[index].name ? 'This field is required.' : ''}
            id="name"
            label={'Name or Alias*'}
            name="name"
            onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
            value={data.members[index].name}
          />
          <Select
            id="country"
            label={'Geographic Representation'}
            name="country"
            onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
            displayEmpty={true}
            value={data.members[index].country}
          >
            <MenuItem disabled value="">
              Choose from list
            </MenuItem>
            {options.map((option) => (
              <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
            ))}
          </Select>
          <TextArea
            errorMessage={error && error.members && error.members[index] && error.members[index].bio ? 'This field is required.' : ''}
            helpfulText={'Extended information about your company, your relevant experience, technical and governance background etc'}
            id="bio"
            label={'Member bio*'}
            name="bio"
            onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
            value={data.members[index].bio}
          />
          <Box>
            <Typography variant="subtitle2">Social media (Will be made public)</Typography>
            <Box sx={{ paddingTop: '4px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <Input
                name="socialX"
                onChange={
                  (event) => handleMemberChange && handleMemberChange(event, index)
                }
                placeholder={'X (Twitter)'}
                value={data.members[index].socialX}
              />
              <Input
                name="socialLinkedin"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'LinkedIn'}
                value={data.members[index].socialLinkedin}
              />
              <Input
                name="socialDiscord"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Discord'}
                value={data.members[index].socialDiscord}
              />
              <Input
                name="socialTelegram"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Telegram'}
                value={data.members[index].socialTelegram}
              />
              <Input
                name="socialWebsite"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Website'}
                value={data.members[index].socialWebsite}
              />
              <Input
                name="socialOther"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Other'}
                value={data.members[index].socialOther}
              />
            </Box>
          </Box>
          <TextArea
            helpfulText={'Are you acting as DRep, as SPO, or both, or would have any other role that could be percieved as conflict of interest as a Constitutional Committee member?'}
            id="conflictOfInterest"
            label={'Conflict of Interest'}
            name="conflictOfInterest"
            onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
            value={data.members[index].conflictOfInterest}
          />
          <Input
            id="drepId"
            label={'If DRep, please provide DRep ID'}
            name="drepId"
            onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
            value={data.members[index].drepId}
          />
          <Input
            id="stakeId"
            label={'If SPO, please provide Stake ID'}
            name="stakeId"
            onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
            value={data.members[index].stakeId}
          />
          <Divider />
        </Fragment>
      ))}
      {data.membersAmount < 20 && (
        <Box sx={{ textAlign: "center" }}>
          <Button
            variant="outlined"
            onClick={handleOnClick}
            endIcon={<img src={ICONS.plusIcon} alt="" />}
          >
            Add member
          </Button>
        </Box>
      )}
    </Box>
  );
}
