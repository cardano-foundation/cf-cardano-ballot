import { useParams, useNavigate } from 'react-router-dom';

import Box from '@mui/material/Box';
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";

import { Layout } from '../components/Layout/Layout';
import { ICONS } from "@consts";
import Avatar from "@mui/material/Avatar";
import { CandidatesList } from "../components/CandidatesList.tsx";
import { useGetAllCandidates } from "@hooks";
import CircularProgress from "@mui/material/CircularProgress";


export const CandidateDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const { allCandidates, isAllCandidatesLoading } = useGetAllCandidates();

  const getInitials = (name: string) => {
    const names = name.split(' ');
    const initials = names.map((name) => name[0]).join('');
    return initials;
  }

  let individual;

  if (allCandidates) {
    individual = allCandidates.find(item => item.candidate.id === Number(id));
  }

  return (
    <Layout>
      <Box sx={{ padding: '40px' }}>

        {!allCandidates || isAllCandidatesLoading ? (
          <Box
            sx={{
              alignItems: "center",
              display: "flex",
              flex: 1,
              justifyContent: "center",
            }}
          >
            <CircularProgress color="secondary" />
          </Box>
        ) : (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: '48px' }}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>

              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between'}}>
                <Box sx={{ display: 'flex', gap: '8px' }}>
                  <IconButton onClick={() => navigate(-1)}>
                    <img src={ICONS.arrowLeftIcon} alt="" />
                  </IconButton>
                  <Typography variant="h1">{individual?.candidate.name}</Typography>
                </Box>
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
                  {individual && (<Avatar sx={{ width: 56, height: 56 }}>{getInitials(individual.candidate.name)}</Avatar>)}
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: '2px' }}>
                    <Typography variant="h2">{individual?.candidate.name}</Typography>
                    <Typography variant="body1">{individual?.candidate.email}</Typography>
                  </Box>
                </Box>

                <Box sx={{ paddingRight: '16px', paddingLeft: '16px'}}>
                  <Typography variant="caption">SUMMARY</Typography>
                  <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                    {individual?.candidate.about}
                  </Typography>
                  {individual && (individual?.candidate.socialX || individual?.candidate.socialDiscord) && (
                    <Box sx={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '12px',
                      paddingTop: '12px',
                      paddingBottom: '12px'
                    }}>
                      <Typography variant="caption">SOCIAL MEDIA</Typography>
                      <Box sx={{ display: 'flex', gap: '24px' }}>
                        {individual.candidate.socialDiscord && (
                          <Link href={individual.candidate.socialDiscord}>
                            <img src={ICONS.discordIcon} alt="discord" />
                          </Link>
                        )}
                        {individual.candidate.socialX && (
                          <Link href={individual.candidate.socialX}>
                            <img src={ICONS.x_twitterIcon} alt="x (twitter)" />
                          </Link>
                        )}
                      </Box>
                    </Box>
                  )}
                </Box>
              </Box>
            </Box>

            <CandidatesList candidates={allCandidates} />
          </Box>
        )}
      </Box>
    </Layout>
  )
}
