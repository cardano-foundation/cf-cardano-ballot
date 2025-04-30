import { useParams } from 'react-router-dom';

import Avatar from "@mui/material/Avatar";
import Box from '@mui/material/Box';
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Divider from '@mui/material/Divider';
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

  const { allCandidates, isAllCandidatesLoading } = useGetAllCandidates();

  let candidate: Candidate | undefined = undefined;

  if (allCandidates) {
    candidate = allCandidates.find(item => item.candidate.id === Number(id));
  }

  const candidateType = candidate?.candidate.candidateType;

  const socialDiscord = candidate?.candidate.socialDiscord;
  const socialLinkedin = candidate?.candidate.socialLinkedin;

  const socialLinks: { type: string, link: string }[] = [];
  // socialLinks.push({ type: 'Website', link: ''});
  socialDiscord && socialDiscord.length && socialLinks.push({ type: 'Discord', link: socialDiscord});
  socialLinkedin && socialLinkedin.length && socialLinks.push({ type: 'Linkedin', link: socialLinkedin});

  const members = candidate?.members;

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
    <Layout>
      <TopNav title={'Candidate Details'} navigateBack={true} />
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
                    {candidate && (
                      <Box sx={{position: 'relative'}}>
                        <Avatar sx={{
                          width: 68,
                          height: 68,
                          color: candidateType === "individual" ? '#582603' : candidateType === "company" ? '#13491B' : '#3052F5',
                          backgroundColor: candidateType === "individual" ? '#FDE1CE' : candidateType === "company" ? '#CEF3D4' : '#EDEBFF',
                        }}>
                          {getInitials(candidate.candidate.name)}
                        </Avatar>
                        {candidate.candidate.verified && <img src={ICONS.verifiedIcon} alt="verified" style={{ position: 'absolute', bottom: '0', right: '0' }}/>}
                      </Box>
                    )}
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '6px', justifyContent: 'center' }}>
                      <Box sx={{
                        display: 'flex',
                        gap: '24px',
                      }}>
                        <Typography variant="h2">{candidate?.candidate.name}</Typography>
                        {candidateType && (
                          <Chip
                            label={chipText(candidateType)}
                            sx={{
                              borderRadius: '100px',
                              color: candidateType === "individual" ? '#803705' : candidateType === "company" ? '#13491B' : '#3052F5',
                              backgroundColor: candidateType === "individual" ? '#FEF3EB' : candidateType === "company" ? '#EBFAED' : '#EDEBFF',
                            }}
                          />
                        )}
                      </Box>
                      {candidateType === "company" && <Typography variant="body1">{candidate?.registrationNumber}</Typography>}
                    </Box>
                  </Box>

                  <Box sx={{ paddingRight: '16px', paddingLeft: '16px', display: 'flex', flexDirection: 'column', gap: '12px'}}>
                    <Box>
                      <Typography variant="overline">COLD CREDENTIAL</Typography>
                      <Typography variant="body1" sx={{ paddingTop: '6px' }}>84aebcfd3e00d0f87af918fc4b5e00135f407e379893df7e7d392c6a</Typography>
                    </Box>
                    <Box>
                      <Typography variant="caption">GOVERNANCE ACTION RATIONALE</Typography>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.governanceActionRationale}
                      </Typography>
                      <Typography variant="caption">
                        The applicant has provided a written rationale for the fictitious governance action detailed <Link href={'#'}>here</Link>.
                      </Typography>
                    </Box>
                    <Box sx={{ padding: '8px 0' }}>
                      <Typography variant="overline">Public Point of Contact</Typography>
                      <Typography variant="body2">
                        <Link href={`mailto: ${candidate?.candidate.publicContact}`}>{candidate?.candidate.publicContact}</Link>
                      </Typography>
                    </Box>
                    <Divider />
                    {candidateType !== 'consortium' && (
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">Country of Residency</Typography>
                        <Typography variant="body1" color="#506288">
                          {candidate?.candidate.country}
                        </Typography>
                      </Box>
                    )}
                    <Box>
                      <Typography variant="caption">ABOUT</Typography>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.about}
                      </Typography>
                    </Box>
                    <Box>
                      <Typography variant="caption">BIO</Typography>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.bio}
                      </Typography>
                    </Box>
                    <Box>
                      <Typography variant="caption">ADDITIONAL INFO</Typography>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.additionalInfo}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', gap: '40px' }}>
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">VIDEO</Typography>
                        <Typography variant="body2">
                          {candidate?.candidate.videoPresentationLink && (
                            <Link href={candidate?.candidate.videoPresentationLink}>
                              {candidate?.candidate.videoPresentationLink}
                            </Link>
                          )}
                        </Typography>
                      </Box>
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">WEBSITE</Typography>
                        <Typography variant="body2">
                          {false && (
                            <Link href={'#'}></Link>
                          )}
                        </Typography>
                      </Box>
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">DISCORD</Typography>
                        <Typography variant="body2">
                          {candidate?.candidate.socialDiscord && (
                            <Link href={candidate?.candidate.socialDiscord}>
                              {candidate?.candidate.socialDiscord}
                            </Link>
                          )}
                        </Typography>
                      </Box>
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">LINKEDIN</Typography>
                        <Typography variant="body2">
                          {candidate?.candidate.socialLinkedin && (
                            <Link href={candidate?.candidate.socialLinkedin}>
                              {candidate?.candidate.socialLinkedin}
                            </Link>
                          )}
                        </Typography>
                      </Box>
                    </Box>
                  </Box>
                </Box>

                {!!members?.length && (
                  <>
                    <Box sx={{ padding: '24px 0 12px' }}>
                      <Typography variant="h2">Members</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '43px' }}>
                      {members.map((member) => (
                        <MemberCard
                          name={member.name}
                          bio={member.bio}
                          country={member.country}
                          initials={getInitials(member.name)}
                          socialLinkedin={member.socialLinkedin}
                          socialDiscord={member.socialDiscord}
                        />
                      ))}
                    </Box>
                  </>
                )}

                <Box sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '16px',
                  backgroundColor: 'white',
                  borderRadius: '16px',
                  padding: '20px 24px 32px 24px',
                  boxShadow: '0px 20px 25px -5px #212A3D14',
                }}>
                  <Box sx={{ padding: '0 16px' }}>
                    <Typography variant="h3">Additional candidate information</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: '10px', padding: '0 16px' }}>
                    <Box>
                      <Typography variant="caption">Why do you wish to serve on the Constitutional Committee?</Typography>
                      <Box sx={{ paddingBottom: '16px'}}>
                        <Typography variant="body1">{candidate?.candidate.reasonToServe}</Typography>
                      </Box>
                    </Box>
                    <Box>
                      <Typography variant="caption">EXPERIENCE</Typography>
                      <Box sx={{ paddingBottom: '16px'}}>
                        <Typography variant="body1">{candidate?.candidate.governanceExperience}</Typography>
                      </Box>
                    </Box>
                    <Box>
                      <Typography variant="caption">How will you communicate with the Cardano community about your descision making?</Typography>
                      <Box sx={{ paddingBottom: '16px'}}>
                        <Typography variant="body1">{candidate?.candidate.communicationStrategy}</Typography>
                      </Box>
                    </Box>
                    <Box>
                      <Typography variant="caption">Cardano Ecosystem Contributions</Typography>
                      <Box sx={{ paddingBottom: '16px'}}>
                        <Typography variant="body1">{candidate?.candidate.ecosystemContributions}</Typography>
                      </Box>
                    </Box>
                    <Box>
                      <Typography variant="caption">Do you have any expertise in constitutional law or law in general? If so please describe</Typography>
                      <Box sx={{ paddingBottom: '16px'}}>
                        <Typography variant="body1">{candidate?.candidate.legalExpertise}</Typography>
                      </Box>
                    </Box>
                    {candidateType === 'individual' && (
                      <Box>
                        <Typography variant="caption">Estimate the average number of hours per week you can dedicate to the committe</Typography>
                        <Box sx={{ paddingBottom: '16px'}}>
                          <Typography variant="body1">{candidate?.candidate.weeklyCommitmentHours && `${candidate?.candidate.weeklyCommitmentHours} h`}</Typography>
                        </Box>
                      </Box>
                    )}
                  </Box>
                </Box>
              </Box>
            </>
          )}
        </Box>
      </Box>
    </Layout>
  )
}
