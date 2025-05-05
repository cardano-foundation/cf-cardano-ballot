import { useParams } from 'react-router-dom';

import Avatar from "@mui/material/Avatar";
import Box from '@mui/material/Box';
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Divider from '@mui/material/Divider';
import Link from "@mui/material/Link";
import Tooltip from "@mui/material/Tooltip";
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

  const members = candidate?.members;

  const chipText = (candidateType: "individual" | "company" | "consortium") => {
    return candidateType?.charAt(0).toUpperCase() + candidateType?.slice(1);
  };

  return (
    <Layout>
      <TopNav title={'Candidate Details'} navigateBack={true} />
      <Box>
        <Box sx={{ padding: { xxs: '0 16px', md: '0 32px', xl: '0 64px'} }}>

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
                  <Box sx={{
                    display: 'flex',
                    gap: '24px',
                    alignItems: 'center',
                    flexWrap: 'wrap-reverse',
                  }}>
                    <Box sx={{ display: 'flex', gap: '16px', alignItems: 'center'}}>
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
                          {candidate.candidate.verified && (
                            <Tooltip title={'Verified applicant'}>
                              <img src={ICONS.verifiedIcon} alt="verified" style={{ position: 'absolute', bottom: '0', right: '0' }}/>
                            </Tooltip>
                          )}
                        </Box>
                      )}
                      <Typography variant="h2">{candidate?.candidate.name}</Typography>
                    </Box>
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

                  <Box sx={{ paddingRight: '16px', paddingLeft: '16px', display: 'flex', flexDirection: 'column', gap: '12px'}}>
                    {candidate?.candidate.publicContact !== '' && (
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">Public Point of Contact</Typography>
                        <Box>
                          <Link variant="body2" target="_blank" rel="noopener" href={`mailto: ${candidate?.candidate.publicContact}`}>{candidate?.candidate.publicContact}</Link>
                        </Box>
                      </Box>
                    )}
                    <Divider />
                    {(candidateType !== 'consortium' && candidate?.candidate.country !== '') && (
                      <Box sx={{ padding: '8px 0' }}>
                        <Typography variant="overline">Geographic Representation</Typography>
                        <Typography variant="body1" color="#506288">
                          {candidate?.candidate.country}
                        </Typography>
                      </Box>
                    )}
                    {candidate?.candidate.about !== '' && (
                      <Box>
                        <Typography variant="caption">ABOUT</Typography>
                        <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                          {candidate?.candidate.about}
                        </Typography>
                      </Box>
                    )}
                    {candidate?.candidate.bio !== '' && (
                    <Box>
                      <Typography variant="caption">BIO</Typography>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.bio}
                      </Typography>
                    </Box>
                    )}
                    {candidate?.candidate.additionalInfo !== '' && (
                    <Box>
                      <Typography variant="caption">ADDITIONAL INFO</Typography>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.additionalInfo}
                      </Typography>
                    </Box>
                    )}
                    <Box sx={{display: 'flex', columnGap: '40px', flexWrap: 'wrap'}}>
                      {candidate?.candidate.videoPresentationLink && (
                        <Box sx={{padding: '8px 0'}}>
                          <Link variant="overline" href={candidate?.candidate.videoPresentationLink} target="_blank" rel="noopener">YOUTUBE</Link>
                        </Box>
                      )}
                      {candidate?.candidate.socialWebsite && (
                        <Box sx={{ padding: '8px 0' }}>
                          <Link variant="overline" href={candidate?.candidate.socialWebsite} target="_blank" rel="noopener">WEBSITE</Link>
                        </Box>
                      )}
                      {candidate?.candidate.socialDiscord && (
                        <Box sx={{ padding: '8px 0' }}>
                          <Link variant="overline" href={candidate?.candidate.socialDiscord} target="_blank" rel="noopener">DISCORD</Link>
                        </Box>
                      )}
                      {candidate?.candidate.socialLinkedin && (
                        <Box sx={{ padding: '8px 0' }}>
                          <Link variant="overline" href={candidate?.candidate.socialLinkedin} target="_blank" rel="noopener">LINKEDIN</Link>
                        </Box>
                      )}
                      {candidate?.candidate.socialX && (
                        <Box sx={{ padding: '8px 0' }}>
                          <Link variant="overline" href={candidate?.candidate.socialX} target="_blank" rel="noopener">X (TWITTER)</Link>
                        </Box>
                      )}
                      {candidate?.candidate.socialTelegram && (
                        <Box sx={{ padding: '8px 0' }}>
                          <Link variant="overline" href={candidate?.candidate.socialTelegram} target="_blank" rel="noopener">TELEGRAM</Link>
                        </Box>
                      )}
                      {candidate?.candidate.socialOther && (
                        <Box sx={{ padding: '8px 0' }}>
                          <Link variant="overline" href={candidate?.candidate.socialOther} target="_blank" rel="noopener">OTHER</Link>
                        </Box>
                      )}
                    </Box>
                  </Box>
                </Box>

                {!!members?.length && (
                  <>
                    <Box sx={{ padding: '24px 0 12px' }}>
                      <Typography variant="h2">Members</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '24px' }}>
                      {members.map((member) => (
                        <MemberCard
                          name={member.name}
                          bio={member.bio}
                          country={member.country}
                          initials={getInitials(member.name)}
                          conflictOfInterest={member.conflictOfInterest}
                          drepId={member.drepId}
                          stakeId={member.stakeId}
                          socialLinkedin={member.socialLinkedin}
                          socialDiscord={member.socialDiscord}
                          website={member.socialWebsite}
                          socialX={member.socialX}
                          socialTelegram={member.socialTelegram}
                          socialOther={member.socialOther}
                        />
                      ))}
                    </Box>
                  </>
                )}

                {(
                  candidate?.candidate.reasonToServe ||
                  candidate?.candidate.governanceExperience ||
                  candidate?.candidate.communicationStrategy ||
                  candidate?.candidate.ecosystemContributions ||
                  candidate?.candidate.legalExpertise ||
                  (candidateType === 'individual' && candidate?.candidate.weeklyCommitmentHours) ||
                  candidate?.candidate.conflictOfInterest ||
                  candidate?.candidate.drepId ||
                  candidate?.candidate.stakeId
                ) && (
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
                      {candidate?.candidate.reasonToServe && (
                        <Box>
                          <Typography variant="subtitle2">Why do you wish to serve on the Constitutional Committee?</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.reasonToServe}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.governanceExperience && (
                        <Box>
                          <Typography variant="subtitle2">EXPERIENCE</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.governanceExperience}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.communicationStrategy && (
                        <Box>
                          <Typography variant="subtitle2">How will you communicate with the Cardano community about your descision making?</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.communicationStrategy}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.ecosystemContributions && (
                        <Box>
                          <Typography variant="subtitle2">Cardano Ecosystem Contributions</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.ecosystemContributions}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.legalExpertise && (
                        <Box>
                          <Typography variant="subtitle2">Do you have any expertise in constitutional law or law in general? If so please describe</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.legalExpertise}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidateType === 'individual' && candidate?.candidate.weeklyCommitmentHours && (
                        <Box>
                          <Typography variant="subtitle2">Estimate the average number of hours per week you can dedicate to the committe</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.weeklyCommitmentHours}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.conflictOfInterest && (
                        <Box>
                          <Typography variant="subtitle2">Conflict of Interest</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.conflictOfInterest}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.drepId && (
                        <Box>
                          <Typography variant="subtitle2">DRep ID</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.drepId}</Typography>
                          </Box>
                        </Box>
                      )}
                      {candidate?.candidate.stakeId && (
                        <Box>
                          <Typography variant="subtitle2">Stake ID</Typography>
                          <Box sx={{ paddingBottom: '16px'}}>
                            <Typography variant="body1">{candidate?.candidate.stakeId}</Typography>
                          </Box>
                        </Box>
                      )}
                    </Box>
                  </Box>
                )}

                {candidate?.candidate.governanceActionRationale && (
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
                      <Typography variant="h3">GOVERNANCE ACTION RATIONALE</Typography>
                    </Box>
                    <Box sx={{ padding: '0 16px' }}>
                      <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                        {candidate?.candidate.governanceActionRationale}
                      </Typography>
                      <Typography variant="caption">
                        The applicant has provided a written rationale for the fictitious governance action detailed <Link variant="caption" target="_blank" rel="noopener" href="https://ipfs.io/ipfs/bafkreiew3wxdtgytkrtg3h7jzlspgfiktpxz7x3onz2yaa345ekrg7jz5q">here</Link>.
                      </Typography>
                    </Box>
                  </Box>
                )}
              </Box>
            </>
          )}
        </Box>
      </Box>
    </Layout>
  )
}
