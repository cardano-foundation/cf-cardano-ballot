import { Box, Link, Typography } from "@mui/material";

export const Footer = () => {
  return (
    <Box sx={{
      backgroundColor: 'transparent',
      display: 'flex',
      justifyContent: 'center',
    }}>
      <Box sx={{
        backgroundColor: 'transparent',
        boxShadow: 'none',
        width: { xs: '100%', md: '100%', lg: '1440px' },
        maxWidth: '100%',
      }}>
        <Box sx={{
          padding: { xxs: '16px 16px', md: '16px 32px', xl: '16px 64px'},
        }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="body1" sx={{ padding: '16px' }}>© 2025 Intersect MBO</Typography>
            <Box sx={{
              display: 'flex',
              gap: '16px',
              alignItems: 'center',
            }}>
              <Box sx={{ padding: '14px 16px' }}>
                <Link
                  href="https://docs.intersectmbo.org/cardano/cardano-governance/cardano-constitution/2025-constitutional-committee-elections/guidelines-for-participation-in-a-constitutional-committee-election"
                  rel="noopener"
                  sx={{ color: '#212A3D', textDecoration: 'none' }}
                  target="_blank"
                  variant="button"
                >
                  Guides
                </Link>
              </Box>
              <Box sx={{ padding: '14px 16px' }}>
                <Link
                  href="https://docs.intersectmbo.org/legal/policies-and-conditions/terms-of-use"
                  rel="noopener"
                  sx={{ color: '#212A3D', textDecoration: 'none' }}
                  target="_blank"
                  variant="button"
                >
                  Terms of Use
                </Link>
              </Box>
              <Box sx={{ padding: '14px 16px' }}>
                <Link
                  href="https://docs.intersectmbo.org/legal/policies-and-conditions/privacy-policy"
                  rel="noopener"
                  sx={{ color: '#212A3D', textDecoration: 'none' }}
                  target="_blank"
                  variant="button"
                >
                  Privacy policy
                </Link>
              </Box>
            </Box>
          </Box>
        </Box>
      </Box>
    </Box>
  )
}
