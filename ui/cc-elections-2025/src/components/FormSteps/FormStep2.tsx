import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";
import { Input } from "@/components/molecules/Field/Input";

import { useRegisterFormContext } from "@hooks";


export const FormStep2 = () => {
  const { data, handleChange } = useRegisterFormContext();

  return (
    <>
      <Box sx={{ padding: '24px 0', display: 'flex', flexDirection: 'column', gap: '12px' }}>
        <Typography variant="h3">Governance Action Rationale</Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
          <Box>
            <Typography variant="body1">
              In addition to the main application requirements, candidates have the opportunity to undertake an optional three-step task. Completing this task allows applicants to demonstrate key skills relevant to the CC role: technical proficiency in credential management (Steps 1 & 2) and the analytical ability to evaluate governance actions within Cardano's framework (Step 3), all performed in the SanchoNet environment. The overall objective is to successfully generate cold credentials, authorize hot credentials using them, and then use the hot credentials to evaluate, vote on, and justify a position on a governance action on SanchoNet, submitting a copy of their rationale to the form.
            </Typography>
            <Box sx={{ marginTop: '16px' }}>
              <Link
                href="https://docs.google.com/document/u/1/d/1u7fh-CS1LPYldI3vghZZWrrXCvsLrU1f--MRCTW7ZR4/edit"
                variant="body2"
                target="_blank"
                rel="noopenner"
              >
                Read the full task descriptions here.
              </Link>
            </Box>
          </Box>

          <Input
            id="governanceActionRationale"
            label={'Paste your submitted vote rationale here'}
            name="governanceActionRationale"
            onChange={handleChange}
            value={data.governanceActionRationale}
          />
        </Box>
      </Box>
    </>
  )
}
