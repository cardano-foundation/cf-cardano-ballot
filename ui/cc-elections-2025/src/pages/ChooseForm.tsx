import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { TopNav } from '../components/TopNav';
import { Layout } from "../components/Layout/Layout";
import { ICONS } from "../consts"
import { ApplyCard } from "../components/molecules/ApplyCard";
import {useNavigate} from "react-router-dom";


export const ChooseForm = () => {

  const navigate = useNavigate();

  const applyCardData = [
    {
      title: 'Individual candidate',
      subTitle: 'This is the card subtitle',
      iconUrl: ICONS.userIcon,
      handleClick: () => navigate('/individulalCandidate'),
    },
    {
      title: 'Company Candidate',
      subTitle: 'This is the card subtitle',
      iconUrl: ICONS.userGroupIcon,
      handleClick: () => navigate('/companyCandidate'),
    },
    {
      title: 'Consortium Candidate',
      subTitle: 'This is the card subtitle',
      iconUrl: ICONS.userGroupIcon,
      handleClick: () => navigate('/consortiumCandidate'),
    }
  ];

  return (
    <Layout>
      <Box>
        <TopNav />
        <Box sx={{ display: 'flex', justifyContent: 'center', marginTop: '44px' }}>
          <Typography variant="h1">Apply as a candidate</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: '24px', marginTop: '56px', justifyContent: 'center' }}>
          {applyCardData.map((card) => (
            <ApplyCard key={card.title} {...card} />
          ))}
        </Box>
      </Box>
    </Layout>
  );
}
