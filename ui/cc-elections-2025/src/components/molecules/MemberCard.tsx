import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";

type MemberCardProps = {
  name: string;
  email: string;
  website?: string;
  socialLinkedin?: string;
  socialDiscord?: string;
  initials: string;
}

export const MemberCard = (
  {
    email,
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
      gap: '16px',
      minWidth: '300px',
      padding: '24px',
      width: 'calc(33.33% - 77px)',
    }}>
      <Avatar sx={{ width: 48, height: 48 }}>{initials}</Avatar>
      <Box>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          <Typography variant="h4">{name}</Typography>
          <Typography variant="body2">{email}</Typography>
        </Box>
        {socialLinks.map((socialLink) => (
          <Box sx={{ padding: '8px 0' }} key={socialLink.type}>
            <Typography variant="overline">{socialLink.type}</Typography>
            <Box>
              <Link href={socialLink.link}>{socialLink.link}</Link>
            </Box>
          </Box>
        ))}
      </Box>

    </Box>
  );
}
