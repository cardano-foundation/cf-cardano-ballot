import {Fragment, useMemo, useRef, useImperativeHandle} from "react";
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

import type { Step4RefsType } from '@/types/formData.ts';

type FormStep4Props = {
  ref: React.Ref<Step4RefsType>;
}

export const FormStep4 = ({ ref }: FormStep4Props) => {
  const { data, error, setData, memberInit, handleMemberChange } = useRegisterFormContext();
  const options = useMemo(() => geographicRepresentationList(), []);

  const inputRefs = useRef<Record<number, Record<string, HTMLInputElement | HTMLTextAreaElement | null>>>({});

  useImperativeHandle(ref, () => ({
    focusField: (index: number, name: string) => {
      inputRefs.current[index]?.[name]?.focus();
    },
  }));

  const handleAddClick = () => {
    if (data.membersAmount >= 20) return;
    setData(prevData => ({...prevData, members: [...prevData.members, memberInit ], membersAmount: prevData.membersAmount + 1}));
  }

  const handleRemoveClick = () => {
    if (data.membersAmount <= 2) return;
    setData(prevData => ({...prevData, members: prevData.members.slice(0, -1), membersAmount: prevData.membersAmount - 1}));
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
            ref={(el) => {
              if (!inputRefs.current[index]) inputRefs.current[index] = {};
              inputRefs.current[index].name = el;
            }}
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
            ref={(el) => {
              if (!inputRefs.current[index]) inputRefs.current[index] = {};
              inputRefs.current[index].bio = el;
            }}
          />
          <Box>
            <Typography variant="subtitle2">Social media (Will be made public)</Typography>
            <Box sx={{ paddingTop: '4px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <Input
                errorMessage={error && error.members && error.members[index] && error.members[index].socialX ? 'Enter a valid X URL: https://x.com/Your_Username' : ''}
                name="socialX"
                onChange={
                  (event) => handleMemberChange && handleMemberChange(event, index)
                }
                placeholder={'X (Twitter)'}
                value={data.members[index].socialX}
                ref={(el) => {
                  if (!inputRefs.current[index]) inputRefs.current[index] = {};
                  inputRefs.current[index].socialX = el;
                }}
              />
              <Input
                errorMessage={error && error.members && error.members[index] && error.members[index].socialLinkedin ? 'Enter a valid LinkedIn URL: https://www.linkedin.com/in/Your_Username' : ''}
                name="socialLinkedin"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'LinkedIn'}
                value={data.members[index].socialLinkedin}
                ref={(el) => {
                  if (!inputRefs.current[index]) inputRefs.current[index] = {};
                  inputRefs.current[index].socialLinkedin = el;
                }}
              />
              <Input
                errorMessage={error && error.members && error.members[index] && error.members[index].socialDiscord ? 'Enter a valid Discord URL: https://discordapp.com/users/Your_User_ID' : ''}
                name="socialDiscord"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Discord'}
                value={data.members[index].socialDiscord}
                ref={(el) => {
                  if (!inputRefs.current[index]) inputRefs.current[index] = {};
                  inputRefs.current[index].socialDiscord = el;
                }}
              />
              <Input
                errorMessage={error && error.members && error.members[index] && error.members[index].socialTelegram ? 'Enter a valid Telegram URL: https://t.me/Your_Username' : ''}
                name="socialTelegram"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Telegram'}
                value={data.members[index].socialTelegram}
                ref={(el) => {
                  if (!inputRefs.current[index]) inputRefs.current[index] = {};
                  inputRefs.current[index].socialTelegram = el;
                }}
              />
              <Input
                errorMessage={error && error.members && error.members[index] && error.members[index].socialWebsite ? 'Enter a valid URL' : ''}
                name="socialWebsite"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Website'}
                value={data.members[index].socialWebsite}
                ref={(el) => {
                  if (!inputRefs.current[index]) inputRefs.current[index] = {};
                  inputRefs.current[index].socialWebsite = el;
                }}
              />
              <Input
                errorMessage={error && error.members && error.members[index] && error.members[index].socialOther ? 'Enter a valid URL' : ''}
                name="socialOther"
                onChange={(event) => handleMemberChange && handleMemberChange(event, index)}
                placeholder={'Other'}
                value={data.members[index].socialOther}
                ref={(el) => {
                  if (!inputRefs.current[index]) inputRefs.current[index] = {};
                  inputRefs.current[index].socialOther = el;
                }}
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

      <Box sx={{ display: "flex", gap: "16px", justifyContent: "center" }}>
        {data.membersAmount < 20 && (
          <Button
            variant="outlined"
            onClick={handleAddClick}
            endIcon={<img src={ICONS.plusIcon} alt="plus" />}
          >
            Add member
          </Button>
        )}
        {data.membersAmount > 2 && (
          <Button
            variant="outlined"
            onClick={handleRemoveClick}
            endIcon={<img src={ICONS.minusIcon} alt="minus" />}
          >
            Remove member
          </Button>
        )}
      </Box>

    </Box>
  );
}
