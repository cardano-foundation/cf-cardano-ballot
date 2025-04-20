import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";
import {Input} from "../molecules/Field/Input.tsx";
import { useCompanyFormContext } from "@hooks";


export const FormStep2 = () => {
  const { data, handleChange } = useCompanyFormContext();
  return (
    <Box sx={{ paddingTop: '32px', display: 'flex', flexDirection: 'column', gap: '32px' }}>
      <Typography variant="body1">
        Generating cold credentials is a fundamental capability required for all CC members. This task assesses your ability to perform this essential function on SanchoNet. You may use any method you prefer to generate these credentials. How-to guides are provided below as a helpful resource, but you are not obligated to use either. After successfully generating your cold credential, please submit the resulting credential key in the field below. This hash will be used to verify your ability to generate these credentials.
      </Typography>
      <Box>
        <Box>
          <Link
            href="https://credential-manager.readthedocs.io/en/latest/index.html"
            variant="body2"
          >
            CC Credential Manager instructions
          </Link>
        </Box>
        <Box>
          <Link
            href="https://youtube.com/playlist?list=PLWYf5eQbRdbUPdt9UT-Vjhi6b840WSIWg&feature=shared"
            variant="body2"
          >
            Cardano Governance: Credential Manager & Constitutional Committee Workshops
          </Link>
        </Box>
      </Box>
      <Input
        id="coldCredential"
        label={'Submit your cold credentials here'}
        name="coldCredential"
        onChange={handleChange}
        value={data.coldCredential}
      />
    </Box>
  )
}
