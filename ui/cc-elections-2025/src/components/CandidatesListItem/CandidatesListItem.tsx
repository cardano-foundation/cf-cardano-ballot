import {useNavigate} from "react-router-dom";

import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { Button } from '../atoms';

type CandidatesListItemProps = {
  id: number;
  name: string;
  initials: string;
}

export const CandidatesListItem = (props: CandidatesListItemProps) => {
  const  navigate = useNavigate();

  const handleClick = () => {
    navigate(`/candidateDetails/${props.id}`);
  }

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', backgroundColor: 'white', borderRadius: '16px', boxShadow: '0px 20px 25px -5px #212A3D14' }}>
      <Box sx={{ paddingTop: '8px', paddingBottom: '8px', paddingLeft: '12px' }}>
        <Box sx={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
          <Avatar sx={{ width: 56, height: 56 }}>{props.initials}</Avatar>
          <Typography variant="h3">
            {props.name}
          </Typography>
        </Box>
      </Box>
      <Box sx={{ paddingTop: '8px', paddingBottom: '8px', paddingRight: '24px', paddingLeft: '24px' }}>
        <Button variant="text" onClick={handleClick}>Read more</Button>
      </Box>
    </Box>
  )
}
