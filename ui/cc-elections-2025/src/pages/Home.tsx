import { useNavigate } from "react-router-dom";

import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import Typography from "@mui/material/Typography";

import { ICONS } from "../consts"
import { Layout } from '../components/Layout/Layout';
import { Button } from '../components/atoms';
import { CandidatesList } from "../components/CandidatesList.tsx";
import {IndividualCandidate} from "../types/apiData.ts";

export const Home = () => {
  const  navigate = useNavigate();

  const candidates: IndividualCandidate[] = [
    {
      "candidate": {
        "id": 40991,
        "candidateType": "individual",
        "name": "Biblo Baggins",
        "email": "bilbo.baggins@shiremail.com",
        "country": "",
        "socialX": "",
        "socialLinkedin": "",
        "socialDiscord": "",
        "socialTelegram": "",
        "socialOther": "",
        "publicContact": "",
        "about": "Desription. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ac vulputate purus. Aenean nec justo quis nulla auctor molestie in nec orci. Sed ac tellus nisi. Donec porttitor ipsum ac nulla luctus fermentum. Vestibulum eget nisi pretium leo volutpat tincidunt vitae non risus. Quisque aliquam ultrices aliquam. Mauris id massa pulvinar, volutpat enim vitae, congue ex. Quisque ultrices magna quis tincidunt dictum.",
        "bio": "",
        "additionalInfo": "",
        "videoPresentationLink": "",
        "reasonToServe": "",
        "governanceExperience": "",
        "communicationStrategy": "",
        "ecosystemContributions": "",
        "legalExpertise": "",
        "weeklyCommitmentHours": 1073741824,
        "conflictOfInterest": "",
        "drepId": "",
        "stakeId": "",
        "createdAt": "2025-04-18T09:18:07.162Z",
        "updatedAt": "2025-04-18T09:18:07.162Z",
        "xverification": ""
      }
    },
    {
      "candidate": {
        "id": 40928,
        "candidateType": "individual",
        "name": "John Snow",
        "email": "john.snow@shiremail.com",
        "country": "",
        "socialX": "",
        "socialLinkedin": "",
        "socialDiscord": "",
        "socialTelegram": "",
        "socialOther": "",
        "publicContact": "",
        "about": "Desription. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ac vulputate purus. Aenean nec justo quis nulla auctor molestie in nec orci. Sed ac tellus nisi. Donec porttitor ipsum ac nulla luctus fermentum. Vestibulum eget nisi pretium leo volutpat tincidunt vitae non risus. Quisque aliquam ultrices aliquam. Mauris id massa pulvinar, volutpat enim vitae, congue ex. Quisque ultrices magna quis tincidunt dictum.",
        "bio": "",
        "additionalInfo": "",
        "videoPresentationLink": "",
        "reasonToServe": "",
        "governanceExperience": "",
        "communicationStrategy": "",
        "ecosystemContributions": "",
        "legalExpertise": "",
        "weeklyCommitmentHours": 1073741824,
        "conflictOfInterest": "",
        "drepId": "",
        "stakeId": "",
        "createdAt": "2025-04-18T09:18:07.162Z",
        "updatedAt": "2025-04-18T09:18:07.162Z",
        "xverification": ""
      }
    },
  ];

  return (
    <Layout>
      <Box sx={{ padding: '40px' }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '48px' }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
              <Typography variant="h1">Intersect Steering Committee</Typography>
              <IconButton>
                <img src={ICONS.questionMarkCircleIcon} alt="" />
              </IconButton>
            </Box>

            <Box sx={{ display: 'flex', flexDirection: 'column', gap: '16px', backgroundColor: 'white', borderRadius: '16px', padding: '20px 24px 32px 24px', boxShadow: '0px 20px 25px -5px #212A3D14' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
                <Typography variant="h2">Details</Typography>
                <Chip label="Vote type goes here" />
              </Box>
              <Box>
                <Typography variant="caption">CONTEXT</Typography>
                <Box sx={{ paddingBottom: '16px' }}>
                  <Typography variant="body1" color="#506288">
                    Desription. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ac vulputate purus. Aenean nec justo quis nulla auctor molestie in nec orci. Sed ac tellus nisi. Donec porttitor ipsum ac nulla luctus fermentum. Vestibulum eget nisi pretium leo volutpat tincidunt vitae non risus. Quisque aliquam ultrices aliquam. Mauris id massa pulvinar, volutpat enim vitae, congue ex. Quisque ultrices magna quis tincidunt dictum.
                  </Typography>
                </Box>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: '65px' }}>
                <Box sx={{ width: '267px', padding: '12px 0' }}>
                  <Typography variant="caption">APPLICATION PERIOD</Typography>
                  <Typography variant="body1">
                    May 1, 2024 10:30AM (UTC) - May 31, 2024 10:30AM (UTC)
                  </Typography>
                </Box>
                <Box sx={{ width: '267px', padding: '12px 0' }}>
                  <Typography variant="caption">VOTING PERIOD</Typography>
                  <Typography variant="body1">
                    June 1, 2024 10:30AM (UTC) - June 31, 2024 10:30AM (UTC)
                  </Typography>
                </Box>
                <Box sx={{ width: '267px', padding: '12px 0' }}>
                  <Typography variant="caption">RESULTS SHOWN</Typography>
                  <Typography variant="body1">
                    July 1, 2024 10:30AM (UTC)
                  </Typography>
                </Box>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', backgroundColor: '#FDE1CE', borderRadius: '4px', padding: '8px 16px' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: '8px'}}>
                  <img alt="" src={ICONS.userAddIcon} />
                  <Typography variant="subtitle2">4 more days to candidate yourself</Typography>
                </Box>
                <Button sx={{ borderRadius: 0 }} onClick={() => navigate('/chooseForm')}>Apply as a candidate</Button>
              </Box>
              <Box>
                <Typography variant="body2">Guides can be found <Link href="#">here</Link>.</Typography>
              </Box>
            </Box>
          </Box>

          <CandidatesList candidates={candidates} />

        </Box>
      </Box>
    </Layout>
  )
}
