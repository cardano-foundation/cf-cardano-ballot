import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Checkbox } from "@/components/molecules/Field/Checkbox";

import { useRegisterFormContext } from "@hooks";

export const FormStep3 = () => {
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
      <Checkbox
        checked={data.termsAndCondition}
        errorMessage={error && error.termsAndCondition ? 'This field is required.' : ''}
        label={'*I have read and agree to the'}
        link={'https://docs.google.com/document/d/1lMQRDfcn3ncisXQYXpnNyZAkcdMk9dXb6N8_PRYnh8g/edit?usp=sharing'}
        linkText={'guidelines'}
        name="termsAndCondition"
        value={data.termsAndCondition}
        onChange={handleChange}
      />
    </Box>
  )
}
