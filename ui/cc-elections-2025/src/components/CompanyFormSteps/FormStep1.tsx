import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Checkbox } from "../molecules/Field/Checkbox.tsx";
import { useCompanyFormContext } from "@hooks";


export const FormStep1 = () => {
  const { data, handleChange, error } = useCompanyFormContext();

  return (
    <Box sx={{ paddingTop: '32px', display: 'flex', flexDirection: 'column', gap: '32px' }}>
      <Box>
        <Typography variant="body1">
          Welcome to the application form for those who aspire to become members of the Constitutional Committee (CC). The CC plays a vital role in shaping the future of the Cardano ecosystem by interpreting and upholding the Cardano Constitution.
        </Typography>
        <Typography variant="body1">
          This application is open to all interested parties, including individuals, companies, and consortia, who are committed to contributing to the governance of Cardano.
        </Typography>
      </Box>
      <Checkbox
        checked={data.termsAndCondition}
        errorMessage={error && error.termsAndCondition ? 'This field is required.' : ''}
        label={'*I agree to terms & conditions'}
        name="termsAndCondition"
        value={data.termsAndCondition}
        onChange={handleChange}
      />
    </Box>
  )
}
