import { useParams } from 'react-router-dom';

import Box from '@mui/material/Box';
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";

import { Layout } from '../components/Layout/Layout';
import {ICONS} from "../consts";
import Avatar from "@mui/material/Avatar";
import { CandidatesList } from "../components/CandidatesList.tsx";
import {IndividualCandidate} from "../types/apiData.ts";


export const CandidateDetails = () => {
  const { id } = useParams();

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
        "about": "Desription2. Lorem ipsum dolor sit amet2, consectetur adipiscing elit. Duis ac vulputate purus. Aenean nec justo quis nulla auctor molestie in nec orci. Sed ac tellus nisi. Donec porttitor ipsum ac nulla luctus fermentum. Vestibulum eget nisi pretium leo volutpat tincidunt vitae non risus. Quisque aliquam ultrices aliquam. Mauris id massa pulvinar, volutpat enim vitae, congue ex. Quisque ultrices magna quis tincidunt dictum.",
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

  const candidate = candidates.find(candidate => candidate.candidate.id === Number(id));

  const getInitials = (name: string) => {
    const names = name.split(' ');
    const initials = names.map((name) => name[0]).join('');
    return initials;
  }

  return (
    <Layout>
      <Box sx={{ padding: '40px' }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '48px' }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>

            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
              <Typography variant="h1">Candidate Details</Typography>
              <IconButton>
                <img src={ICONS.questionMarkCircleIcon} alt="" />
              </IconButton>
            </Box>

            <Box sx={{
              display: 'flex',
              flexDirection: 'column',
              gap: '16px',
              backgroundColor: 'white',
              borderRadius: '16px',
              padding: '20px 24px 32px 24px',
              boxShadow: '0px 20px 25px -5px #212A3D14',
            }}>

              <Box sx={{ display: 'flex', gap: '16px'}}>
                {candidate && (<Avatar sx={{ width: 56, height: 56 }}>{getInitials(candidate.candidate.name)}</Avatar>)}
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: '2px' }}>
                  <Typography variant="h2">{candidate?.candidate.name}</Typography>
                  <Typography variant="body1">{candidate?.candidate.email}</Typography>
                </Box>
              </Box>

              <Box sx={{ paddingRight: '16px', paddingLeft: '16px'}}>
                <Typography variant="caption">SUMMARY</Typography>
                <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                  {candidate?.candidate.about}
                </Typography>
                <Box sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '12px',
                  paddingTop: '12px',
                  paddingBottom: '12px'
                }}>
                  <Typography variant="caption">SOCIAL MEDIA</Typography>
                  <Box sx={{ display: 'flex', gap: '24px' }}>
                    <Link href="#">
                      <img src={ICONS.discordIcon} alt="discord" />
                    </Link>
                    <Link href="#">
                      <img src={ICONS.x_twitterIcon} alt="x (twitter)" />
                    </Link>
                  </Box>
                </Box>
              </Box>
            </Box>
          </Box>

          <CandidatesList candidates={candidates} />
        </Box>
      </Box>
    </Layout>
  )
}
