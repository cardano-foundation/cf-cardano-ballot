import { useNavigate } from "react-router-dom";

import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Chip from "@mui/material/Chip";
import Typography from '@mui/material/Typography';

import { Button } from '@atoms';

type CandidatesListItemProps = {
  id: number;
  name: string;
  initials: string;
  bio: string;
  candidateType: "individual" | "company" | "consortium";
};

export const CandidatesListItem = (props: CandidatesListItemProps) => {
  const  navigate = useNavigate();

  const handleClick = () => {
    navigate(`/candidateDetails/${props.id}`);
  };

  const chipText = (candidateType: "individual" | "company" | "consortium") => {
    switch (candidateType) {
      case "individual":
        return "Individual";
      case "company":
        return "Company";
      case "consortium":
        return "Group";
      default:
        return "Individual";
    }
  };

  return (
    <Box sx={{
      backgroundColor: 'white',
      borderRadius: '16px',
      boxShadow: '0px 20px 25px -5px #212A3D14',
      minWidth: '340px',
      width: 'calc(33.33% - 29px)',
    }}>
      <Box sx={{ display: 'flex', padding: '16px 12px 8px 24px', gap: '8px', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box sx={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
          <Avatar sx={{ width: 56, height: 56 }}>{props.initials}</Avatar>
          <Typography variant="h3">
            {props.name}
          </Typography>
        </Box>
        <Chip label={chipText(props.candidateType)} sx={{ borderRadius: '100px' }} />
      </Box>
      <Box sx={{ padding: '20px 24px 16px' }}>
        <Typography variant="body1">
          {props.bio.length > 140 ? `${props.bio.substring(0, 140)}...` : props.bio}
        </Typography>
      </Box>
      <Box sx={{ paddingBottom: '20px', paddingRight: '24px', paddingLeft: '24px' }}>
        <Button variant="text" onClick={handleClick}>Read more</Button>
      </Box>
    </Box>
  )
}
