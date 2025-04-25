import Box from "@mui/material/Box";
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";

import { Input, SearchIcon } from '@atoms';
import { ICONS } from "@consts";
import { getInitials } from "@utils";
import { CandidatesListItem } from "./CandidatesListItem/CandidatesListItem.tsx";
import { Candidate } from "@models";

type CandidatesListProps = {
  candidates: Candidate[];
};

export const CandidatesList = ({ candidates }: CandidatesListProps) => {
  return (
    <Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: '12px', padding: '40px 0 24px' }}>
        <Typography variant="h2">Candidates List</Typography>
        <Box sx={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
          <Input
            id="search"
            name="search"
            type="text"
            sx={{ width: '322px', backgroundColor: 'white', padding: '11px 12px' }}
            placeholder="Search ..."
            startAdornment={
              <InputAdornment position={"start"}>
                <SearchIcon />
              </InputAdornment>
            }
          />
          <IconButton>
            <img src={ICONS.filterIcon} alt="" />
          </IconButton>
          <IconButton>
            <img src={ICONS.sortDescendingIcon} alt="" />
          </IconButton>
        </Box>
      </Box>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '43px', paddingBottom: '48px' }}>
        {candidates.map((candidate) => (
          <CandidatesListItem
            bio={candidate.candidate.about}
            candidateType={candidate.candidate.candidateType}
            id={candidate.candidate.id}
            initials={getInitials(candidate.candidate.name)}
            key={candidate.candidate.name}
            name={candidate.candidate.name}
          />
        ))}
      </Box>
    </Box>
  );
}
