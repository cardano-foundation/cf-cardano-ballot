import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";
import {TextArea} from "../molecules/Field/TextArea.tsx";
import useFormContext from "../../hooks/useFormContext.ts";


export const FormStep3 = () => {
  const { data, handleChange } = useFormContext();
  return (
    <Box sx={{ paddingTop: '32px', display: 'flex', flexDirection: 'column', gap: '32px' }}>
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

      <TextArea label={'Submit your rationale here'} id="rationale" value={data.rationale} name="rationale" onChange={(event) => handleChange(event)} />
    </Box>
  );
}
