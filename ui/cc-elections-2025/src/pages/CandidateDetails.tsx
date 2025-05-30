import {useNavigate, useParams} from 'react-router-dom';
import ReactJson from 'react-json-view';
import { Tweet } from 'react-tweet';
import YouTube from 'react-youtube';

import Avatar from "@mui/material/Avatar";
import Box from '@mui/material/Box';
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Divider from '@mui/material/Divider';
import Link from "@mui/material/Link";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";

import { ICONS } from "@consts";
import {useDeleteCandidate, useGetAllCandidates} from "@hooks";
import { Candidate } from "@models";
import { getInitials } from "@utils";

import { Layout } from '@/components/Layout/Layout';
import { MemberCard } from "@/components/molecules/MemberCard.tsx";
import { TopNav } from "@/components/TopNav.tsx";
import { Footer } from "@organisms";
import {Button} from "@atoms";
import {useCardano, useModal} from "@context";

type CandidateDetailsProps = {
  isEditActive: boolean;
}

export const CandidateDetails = ({ isEditActive }: CandidateDetailsProps) => {
  const { id } = useParams();
  const { closeModal, openModal } = useModal();
  const navigate = useNavigate();
  const { isEnabled, address } = useCardano();

  const { allCandidates, isAllCandidatesLoading } = useGetAllCandidates();

  let candidateItem: Candidate | undefined = undefined;

  let governanceActionRationale = undefined;

  if (allCandidates) {
    candidateItem = allCandidates.find(item => item.candidate.id === Number(id));
  }

  // 🔧 SAFELY call hook using fallback to avoid conditional call
  const candidateType = candidateItem?.candidate.candidateType ?? "individual";
  const deleteCandidate = useDeleteCandidate(candidateType);

  if (!candidateItem) {
    return null;
  }

  const candidate = candidateItem.candidate;

  const members = candidateItem.members;

  const chipText = (candidateType: "individual" | "company" | "consortium") => {
    return candidateType?.charAt(0).toUpperCase() + candidateType?.slice(1);
  };

  const youtubeParam = candidate.videoPresentationLink.match(/(?:http?s?:\/\/)?(?:www.)?(?:m.)?(?:music.)?youtu(?:\.?be)(?:\.com)?(?:(?:\w*.?:\/\/)?\w*.?\w*-?.?\w*\/(?:embed|e|v|watch|.*\/)?\??(?:feature=\w*\.?\w*)?&?(?:v=)?\/?)([\w\d_-]{11})(?:\S+)?/);

  const twitterParam = candidate.videoPresentationLink.match(/^https?:\/\/(www\.)?(x\.com|twitter\.com)\/[a-zA-Z0-9_]{1,15}\/status\/(\d+)$/);

  try {
    governanceActionRationale = candidate ? JSON.parse(candidate.governanceActionRationale) : '';
  } catch (e) {}

  const handleEdit = () => {
    navigate(`/editCandidate/${id}`);
  }

  const handleDelete = () => {
    openModal({
      type: "statusModal",
      state: {
        status: "info",
        title: 'Are you sure you want to delete this candidate?',
        message: 'This action cannot be undone.',
        cancelText: 'Cancel',
        onCancel: () => closeModal(),
        buttonText: 'Delete',
        onSubmit: () => {
          deleteCandidate.mutate(Number(id));
          closeModal();
          navigate('/');
        },
        dataTestId: "delete-confirm-modal",
      },
    });
  }

  return (
    <Box sx={{ backgroundColor: '#f2f4f8', minHeight: '100vh' }}>
      <TopNav title={'Candidate Details'} navigateBack={true} />
      <Layout>
        <Box sx={{ wordWrap: 'break-word', whiteSpace: 'pre-line' }}>
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
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                  {isEnabled && isEditActive && address === candidate.walletAddress && (
                    <Box sx={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '16px',
                      backgroundColor: 'white',
                      borderRadius: '16px',
                      padding: '20px 24px',
                      boxShadow: '0px 20px 25px -5px #212A3D14',
                    }}>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Button variant="text" onClick={handleEdit}>Edit</Button>
                        <Button variant="text" color="error" onClick={handleDelete}>Delete</Button>
                      </Box>
                    </Box>
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
                              color: '#3052F5',
                              backgroundColor: '#EDEBFF',
                            }}>
                              {getInitials(candidate.name)}
                            </Avatar>
                            {candidate.verified && (
                              <Tooltip title={'Authenticated X account'}>
                                <img src={ICONS.verifiedIcon} alt="verified" style={{ position: 'absolute', bottom: '0', right: '0' }}/>
                              </Tooltip>
                            )}
                          </Box>
                        )}
                        <Typography variant="h2" sx={{ wordWrap: 'break-word' }}>{candidate.name}</Typography>
                      </Box>
                      {candidateType && (
                        <Chip
                          label={chipText(candidateType)}
                          sx={{
                            borderRadius: '100px',
                            color: '#212A3D',
                            backgroundColor: '#EDEBFF',
                          }}
                        />
                      )}
                    </Box>

                    <Box sx={{
                      paddingRight: '16px',
                      paddingLeft: '16px',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '12px'
                    }}>
                      <Box sx={{padding: '8px 0'}}>
                        <Typography variant="subtitle2" component="h3">WALLET ADDRESS</Typography>
                        <Typography variant="body1" color="#506288"
                                    sx={{wordWrap: 'break-word'}}>{candidate.walletAddress}</Typography>
                      </Box>
                      {candidate.publicContact !== '' && (
                        <Box sx={{padding: '8px 0'}}>
                          <Typography variant="subtitle2" component="h3">CONTACT</Typography>
                          <Box sx={{wordWrap: 'break-word'}}>
                            <Link variant="body2" target="_blank" rel="noopener"
                                  href={`mailto: ${candidate.publicContact}`}>{candidate.publicContact}</Link>
                          </Box>
                        </Box>
                      )}
                      <Divider/>
                      {(candidateType !== 'consortium' && candidate.country !== '') && (
                        <Box sx={{padding: '8px 0'}}>
                          <Typography variant="subtitle2" component="h3">GEOGRAPHIC REPRESENTATION</Typography>
                          <Typography variant="body1" color="#506288">
                            {candidate.country}
                          </Typography>
                        </Box>
                      )}
                      {candidate.about !== '' && (
                        <Box>
                          <Typography variant="subtitle2" component="h3">ABOUT</Typography>
                          <Typography variant="body1" color="#506288"
                                      sx={{paddingBottom: '16px', wordWrap: 'break-word'}}>
                            {candidate.about}
                          </Typography>
                        </Box>
                      )}
                      {candidate.bio !== '' && (
                        <Box>
                          <Typography variant="subtitle2" component="h3">BIO</Typography>
                          <Typography variant="body1" color="#506288"
                                      sx={{paddingBottom: '16px', wordWrap: 'break-word'}}>
                            {candidate.bio}
                          </Typography>
                        </Box>
                      )}
                      {candidate.additionalInfo !== '' && (
                        <Box>
                          <Typography variant="subtitle2" component="h3">ADDITIONAL INFO</Typography>
                          <Typography variant="body1" color="#506288"
                                      sx={{paddingBottom: '16px', wordWrap: 'break-word'}}>
                            {candidate.additionalInfo}
                          </Typography>
                        </Box>
                      )}
                      <Box sx={{display: 'flex', columnGap: '40px', flexWrap: 'wrap'}}>
                        {candidate.socialX && (
                          <Box sx={{padding: '8px 0'}}>
                            <Link variant="body2" href={candidate.socialX} target="_blank" rel="noopener">X
                              (TWITTER)</Link>
                          </Box>
                        )}
                        {candidate.socialLinkedin && (
                          <Box sx={{padding: '8px 0'}}>
                            <Link variant="body2" href={candidate.socialLinkedin} target="_blank"
                                  rel="noopener">LINKEDIN</Link>
                          </Box>
                        )}
                        {candidate.socialDiscord && (
                          <Box sx={{padding: '8px 0'}}>
                            <Link variant="body2" href={candidate.socialDiscord} target="_blank"
                                  rel="noopener">DISCORD</Link>
                          </Box>
                        )}
                        {candidate.socialTelegram && (
                          <Box sx={{padding: '8px 0'}}>
                            <Link variant="body2" href={candidate.socialTelegram} target="_blank"
                                  rel="noopener">TELEGRAM</Link>
                          </Box>
                        )}
                        {candidate.socialWebsite && (
                          <Box sx={{padding: '8px 0'}}>
                            <Link variant="body2" href={candidate.socialWebsite} target="_blank"
                                  rel="noopener">WEBSITE</Link>
                          </Box>
                        )}
                        {candidate.socialOther && (
                          <Box sx={{padding: '8px 0'}}>
                            <Link variant="body2" href={candidate.socialOther} target="_blank"
                                  rel="noopener">OTHER</Link>
                          </Box>
                        )}
                      </Box>
                      {youtubeParam && youtubeParam[1] && (
                        <YouTube videoId={youtubeParam[1]} opts={{height: '315', width: '560',}} />
                      )}
                      {twitterParam && twitterParam[3] && (
                        <Tweet id={twitterParam[3]} />
                      )}
                    </Box>
                  </Box>

                  {!!members?.length && (
                    <>
                      <Box sx={{padding: '24px 0 12px'}}>
                        <Typography variant="h2">Members</Typography>
                      </Box>
                      <Box sx={{display: 'flex', flexWrap: 'wrap', gap: '24px'}}>
                        {members.map((member) => (
                          <MemberCard
                            key={member.id}
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
                    candidate.reasonToServe ||
                    candidate.governanceExperience ||
                    candidate.communicationStrategy ||
                    candidate.ecosystemContributions ||
                    candidate.legalExpertise ||
                    (candidateType === 'individual' && candidate.weeklyCommitmentHours) ||
                    candidate.conflictOfInterest ||
                    candidate.drepId ||
                    candidate.stakeId
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
                        {candidate.reasonToServe && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">WHY DO YOU WISH TO SERVE ON THE CONSTITUTIONAL COMMITTEE?</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.reasonToServe}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.governanceExperience && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">EXPERIENCE</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.governanceExperience}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.communicationStrategy && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">HOW WILL YOU COMMUNICATE WITH THE CARDANO COMMUNITY ABOUT YOUR DECISION MAKING?</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.communicationStrategy}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.ecosystemContributions && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">CARDANO ECOSYSTEM CONTRIBUTIONS</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.ecosystemContributions}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.legalExpertise && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">DO YOU HAVE ANY EXPERTISE IN CONSTITUTIONAL LAW OR LAW IN GENERAL? IF SO, PLEASE DESCRIBE</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.legalExpertise}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidateType === 'individual' && candidate.weeklyCommitmentHours && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">ESTIMATE THE AVERAGE NUMBER OF HOURS PER WEEK YOU CAN DEDICATE TO THE COMMITTEE</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.weeklyCommitmentHours}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.conflictOfInterest && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">CONFLICT OF INTEREST</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.conflictOfInterest}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.drepId && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">DREP ID</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.drepId}</Typography>
                            </Box>
                          </Box>
                        )}
                        {candidate.stakeId && (
                          <Box>
                            <Typography variant="subtitle2" component="h4">STAKE ID</Typography>
                            <Box sx={{ paddingBottom: '16px', wordWrap: 'break-word' }}>
                              <Typography variant="body1" color="#506288">{candidate.stakeId}</Typography>
                            </Box>
                          </Box>
                        )}
                      </Box>
                    </Box>
                  )}

                  {candidate.governanceActionRationale && (
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
                        <Typography variant="h3">Governance Action Rationale</Typography>
                      </Box>
                      <Box sx={{ padding: '0 16px', overflowWrap: 'anywhere' }}>
                        {governanceActionRationale ? (
                          <Box sx={{ paddingBottom: '16px' }}>
                            <ReactJson src={governanceActionRationale} />
                          </Box>
                        ) : (
                          <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
                            {candidate.governanceActionRationale}
                          </Typography>
                        )}
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
      <Footer />
    </Box>
  )
}
