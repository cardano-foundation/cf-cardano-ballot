import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Checkbox } from "@/components/molecules/Field/Checkbox";

import { useRegisterFormContext } from "@hooks";

export const FormStep1 = () => {
  const { data, handleChange, error } = useRegisterFormContext();

  return (
    <Box sx={{ paddingTop: '32px', display: 'flex', flexDirection: 'column', gap: '32px' }}>
      <Box>
        <Typography variant="body1">
          Welcome to the application form for those who aspire to become members of the Constitutional Committee (CC). The CC plays a vital role in shaping the future of the Cardano ecosystem by interpreting and upholding the Cardano Constitution.
        </Typography>
        <Typography variant="body1">
          This application is open to all interested parties, including individuals, companies, and consortia, who are committed to contributing to the governance of Cardano. We seek candidates who possess a strong understanding of blockchain technology, a keen interest in decentralized governance, and a dedication to the principles outlined in the Cardano Constitution.
        </Typography>
      </Box>
      <Box>
        <Checkbox
          checked={data.guidelines}
          errorMessage={error && error.guidelines ? 'This field is required.' : ''}
          label={'*I have read and agree to the'}
          link={'https://docs.intersectmbo.org/cardano/cardano-governance/cardano-constitution/2025-constitutional-committee-elections/guidelines-for-participation-in-a-constitutional-committee-election'}
          linkText={'guidelines'}
          name="guidelines"
          value={data.guidelines}
          onChange={handleChange}
        />
        <Checkbox
          checked={data.termsOfUse}
          errorMessage={error && error.termsOfUse ? 'This field is required.' : ''}
          label={'*I have read and agree to the'}
          link={'https://docs.intersectmbo.org/legal/policies-and-conditions/terms-of-use'}
          linkText={'Terms of Use'}
          name="termsOfUse"
          value={data.termsOfUse}
          onChange={handleChange}
        />
        <Checkbox
          checked={data.privacyPolicy}
          errorMessage={error && error.privacyPolicy ? 'This field is required.' : ''}
          label={'*I have read and agree to the'}
          link={'https://docs.intersectmbo.org/legal/policies-and-conditions/privacy-policy'}
          linkText={'Privacy Policy'}
          name="privacyPolicy"
          value={data.privacyPolicy}
          onChange={handleChange}
        />
      </Box>
    </Box>
  )
}
