import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import {ICONS} from "../consts";
import {CandidatesListItem} from "./CandidatesListItem/CandidatesListItem.tsx";
import {IndividualCandidate} from "../types/apiData.ts";

type CandidatesListProps = {
  candidates: IndividualCandidate[];
}

export const CandidatesList = ({ candidates }: CandidatesListProps) => {

  const getInitials = (name: string) => {
    const names = name.split(' ');
    const initials = names.map((name) => name[0]).join('');
    return initials;
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <Typography variant="h2">Candidates List</Typography>
        <Typography variant="body1">Lorem ipsum</Typography>
      </Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <Box sx={{ display: 'flex', gap: '24px' }}>
          <IconButton>
            <img src={ICONS.filterIcon} alt="" />
          </IconButton>
          <IconButton>
            <img src={ICONS.sortDescendingIcon} alt="" />
          </IconButton>
        </Box>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {candidates.map((candidate) => (
            <CandidatesListItem id={candidate.candidate.id} initials={getInitials(candidate.candidate.name)} name={candidate.candidate.name} key={candidate.candidate.name} />
          ))}
        </Box>
      </Box>
    </Box>
  );
}
