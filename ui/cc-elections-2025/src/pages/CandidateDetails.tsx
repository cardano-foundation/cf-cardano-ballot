import { useParams, useNavigate } from 'react-router-dom';

import Avatar from "@mui/material/Avatar";
import Box from '@mui/material/Box';
import CircularProgress from "@mui/material/CircularProgress";
import IconButton from "@mui/material/IconButton";
import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";

import { ICONS } from "@consts";
import { useGetAllCandidates } from "@hooks";
import { Candidate } from "@models";
import { getInitials } from "@utils";

import { Layout } from '@/components/Layout/Layout';
import { MemberCard } from "@/components/molecules/MemberCard.tsx";
import { TopNav } from "@/components/TopNav.tsx";

export const CandidateDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const { allCandidates, isAllCandidatesLoading } = useGetAllCandidates();

  let individual: Candidate | undefined = undefined;

  if (allCandidates) {
    individual = allCandidates.find(item => item.candidate.id === Number(id));
  }

  const socialDiscord = individual?.candidate.socialDiscord;
  const socialLinkedin = individual?.candidate.socialLinkedin;

  const socialLinks: { type: string, link: string }[] = [];
  socialLinks.push({ type: 'Website', link: 'https://www.mywesbite.com/'});
  socialDiscord && socialDiscord.length && socialLinks.push({ type: 'Discord', link: socialDiscord});
  socialLinkedin && socialLinkedin.length && socialLinks.push({ type: 'Linkedin', link: socialLinkedin});

  const members = individual?.members;

  return (
    <Layout>
      <TopNav />
      <Box>
        <Box sx={{ padding: '0 64px' }}>

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
            <>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '24px 0' }}>
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
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px', paddingBottom: '48px' }}>

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
                    {individual && (<Avatar sx={{ width: 80, height: 80 }}>{getInitials(individual.candidate.name)}</Avatar>)}
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '6px', justifyContent: 'center' }}>
                      <Typography variant="h2">{individual?.candidate.name}</Typography>
                      <Typography variant="body1">{individual?.candidate.email}</Typography>
                    </Box>
                  </Box>

                  <Box sx={{ paddingRight: '16px', paddingLeft: '16px'}}>
                    <Typography variant="caption">SUMMARY</Typography>
                    <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                      {individual?.candidate.about}
                    </Typography>
                    {socialLinks.length && (
                      <Box sx={{ display: 'flex' }}>
                        {socialLinks.map((socialLink) => (
                          <Box sx={{ flex: '0 0 33.3333%', padding: '8px 0' }}>
                            <Typography variant="overline">{socialLink.type}</Typography>
                            <Box>
                              <Link href={socialLink.link}>{socialLink.link}</Link>
                            </Box>
                          </Box>
                        ))}
                      </Box>
                    )}
                  </Box>
                </Box>

                {members?.length && (
                <>
                  <Box sx={{ padding: '24px 0 12px' }}>
                    <Typography variant="h2">Members</Typography>
                    <Typography variant="body1">Lorem ipsum dolor sit amet, consectetur adipiscing elit.</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '43px' }}>
                    {members.map((member) => (
                      <MemberCard
                        name={member.name}
                        email={'bilbo.baggins@shiremail.com'}
                        initials={getInitials(member.name)}
                        website={'https://www.mywesbite.com/'}
                        socialLinkedin={member.socialLinkedin}
                        socialDiscord={member.socialDiscord}
                      />
                    ))}
                  </Box>
                </>
                )}

              </Box>
            </>
          )}
        </Box>
      </Box>
    </Layout>
  )
}
