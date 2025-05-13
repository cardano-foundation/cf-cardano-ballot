import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";

type MemberCardProps = {
  name: string;
  country: string;
  bio: string;
  conflictOfInterest?: string;
  drepId?: string;
  stakeId?: string;
  website?: string;
  socialLinkedin?: string;
  socialDiscord?: string;
  socialX?: string;
  socialTelegram?: string;
  socialOther?: string;
  initials: string;
}

export const MemberCard = (
  {
    bio,
    country,
    initials,
    name,
    stakeId,
    drepId,
    conflictOfInterest,
    socialDiscord,
    socialLinkedin,
    website,
    socialX,
    socialTelegram,
    socialOther,
  }: MemberCardProps
) => {
  const socialLinks: { type: string, link: string }[] = [];
  socialX && socialX.length && socialLinks.push({ type: 'X (Twitter)', link: socialX});
  socialLinkedin && socialLinkedin.length && socialLinks.push({ type: 'Linkedin', link: socialLinkedin});
  socialDiscord && socialDiscord.length && socialLinks.push({ type: 'Discord', link: socialDiscord});
  socialTelegram && socialTelegram.length && socialLinks.push({ type: 'Telegram', link: socialTelegram});
  website && website.length && socialLinks.push({ type: 'Website', link: website});
  socialOther && socialOther.length && socialLinks.push({ type: 'Other', link: socialOther});

  return (
    <Box sx={{
      backgroundColor: 'white',
      borderRadius: '16px',
      boxShadow: '0px 20px 25px -5px #212A3D14',
      display: 'flex',
      flexDirection: 'column',
      gap: '6px',
      minWidth: '258px',
      padding: '24px',
      width: { xxs: '100%', md: 'calc(50% - 60px)' },
    }}>
      <Box sx={{ display: 'flex', gap: '12px' }}>
        <Avatar
          sx={{
            width: 48,
            height: 48,
            color: '#3052F5',
            backgroundColor: '#EDEBFF',
          }}
        >
          {initials}
        </Avatar>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '6px', justifyContent: 'center' }}>
          <Typography variant="h4" component="h3">{name}</Typography>
        </Box>
      </Box>
      {country && (
        <Box sx={{ padding: '8px 0' }}>
          <Typography variant="subtitle2" component="h4">GEOGRAPHIC REPRESENTATION</Typography>
          <Typography variant="body1" color="#506288">
            {country}
          </Typography>
        </Box>
      )}
      <Box>
        <Typography variant="subtitle2" component="h4">BIO</Typography>
        <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
          {bio}
        </Typography>
      </Box>
      {conflictOfInterest && (
        <Box>
          <Typography variant="subtitle2" component="h4">CONFLICT OF INTEREST</Typography>
          <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
            {conflictOfInterest}
          </Typography>
        </Box>
      )}
      {drepId && (
        <Box>
          <Typography variant="subtitle2" component="h4">DREP ID</Typography>
          <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
            {drepId}
          </Typography>
        </Box>
      )}
      {stakeId && (
        <Box>
          <Typography variant="subtitle2" component="h4">STAKE ID</Typography>
          <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
            {stakeId}
          </Typography>
        </Box>
      )}
      <Box sx={{display: 'flex', columnGap: '40px', flexWrap: 'wrap'}}>
        {socialLinks.map((socialLink) => (
          <Box sx={{ padding: '8px 0' }} key={socialLink.type}>
            <Link variant="body2" href={socialLink.link} target="_blank" rel="noopener">{socialLink.type}</Link>
          </Box>
        ))}
      </Box>

    </Box>
  );
}
