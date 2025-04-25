import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";
import { Input } from "@/components/molecules/Field/Input";
import { TextArea } from "@/components/molecules/Field/TextArea";

import { useRegisterFormContext } from "@hooks";


export const FormStep1 = () => {
  const { data, handleChange } = useRegisterFormContext();

  return (
    <>
      <Box sx={{ padding: '24px 0', display: 'flex', flexDirection: 'column', gap: '12px' }}>
        <Typography variant="h3">Cold Credential Generation</Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
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
      </Box>
      <Box sx={{ padding: '24px 0', display: 'flex', flexDirection: 'column', gap: '12px' }}>
        <Typography variant="h3">Governance Action Rationale</Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
          <Box>
            <Typography variant="body1">
              This task assesses your ability to analyze a governance proposal and articulate a clear and reasoned vote. Below, you will find a fictitious governance action. Please read it carefully and write a rationale explaining how you would vote on this proposal and why. Your rationale should demonstrate your understanding of the governance process, your critical thinking skills, and your ability to communicate your reasoning effectively.
            </Typography>
            <Box sx={{ marginTop: '16px' }}>
              <Link
                href="https://credential-manager.readthedocs.io/en/latest/index.html"
                variant="body2"
              >
                Read the governance action here
              </Link>
            </Box>
          </Box>

          <TextArea
            id="governanceActionRationale"
            label={'Submit your rationale here'}
            name="governanceActionRationale"
            onChange={(event) => handleChange(event)}
            value={data.governanceActionRationale}
          />
        </Box>
      </Box>
    </>
  )
}
