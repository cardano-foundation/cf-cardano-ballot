import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";

type MemberCardProps = {
  name: string;
  country: string;
  bio: string;
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
    socialDiscord,
    socialLinkedin,
    website,
    socialX,
    socialTelegram,
    socialOther,
  }: MemberCardProps
) => {
  const socialLinks: { type: string, link: string }[] = [];
  website && website.length && socialLinks.push({ type: 'Website', link: website});
  socialDiscord && socialDiscord.length && socialLinks.push({ type: 'Discord', link: socialDiscord});
  socialLinkedin && socialLinkedin.length && socialLinks.push({ type: 'Linkedin', link: socialLinkedin});
  socialX && socialX.length && socialLinks.push({ type: 'X (Twitter)', link: socialX});
  socialTelegram && socialTelegram.length && socialLinks.push({ type: 'Telegram', link: socialTelegram});
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
          <Typography variant="h4">{name}</Typography>
        </Box>
      </Box>
      {country && (
        <Box sx={{ padding: '8px 0' }}>
          <Typography variant="overline">Geographic Representation</Typography>
          <Typography variant="body1" color="#506288">
            {country}
          </Typography>
        </Box>
      )}
      <Box>
        <Typography variant="caption">BIO</Typography>
        <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
          {bio}
        </Typography>
      </Box>
      <Box sx={{display: 'flex', columnGap: '40px', flexWrap: 'wrap'}}>
        {socialLinks.map((socialLink) => (
          <Box sx={{ padding: '8px 0' }} key={socialLink.type}>
            <Link variant="overline" href={socialLink.link}>{socialLink.type}</Link>
          </Box>
        ))}
      </Box>

    </Box>
  );
}
