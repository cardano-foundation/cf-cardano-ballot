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
    website
  }: MemberCardProps
) => {
  const socialLinks: { type: string, link: string }[] = [];
  website && website.length && socialLinks.push({ type: 'Website', link: website});
  socialDiscord && socialDiscord.length && socialLinks.push({ type: 'Discord', link: socialDiscord});
  socialLinkedin && socialLinkedin.length && socialLinks.push({ type: 'Linkedin', link: socialLinkedin});

  return (
    <Box sx={{
      backgroundColor: 'white',
      borderRadius: '16px',
      boxShadow: '0px 20px 25px -5px #212A3D14',
      display: 'flex',
      flexDirection: 'column',
      gap: '6px',
      minWidth: '300px',
      padding: '24px',
      width: 'calc(50% - 70px)',
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
      <Box sx={{ padding: '8px 0' }}>
        <Typography variant="overline">Geographic Representation</Typography>
        <Typography variant="body1" color="#506288">
          {country}
        </Typography>
      </Box>
      <Box>
        <Typography variant="caption">BIO</Typography>
        <Typography variant="body1" color="#506288" sx={{ paddingBottom: '16px' }}>
          {bio}
        </Typography>
      </Box>
      <Box>
        {socialLinks.map((socialLink) => (
          <Box sx={{ padding: '8px 0' }} key={socialLink.type}>
            <Link variant="overline" href={socialLink.link}>{socialLink.type}</Link>
          </Box>
        ))}
      </Box>

    </Box>
  );
}
