import Typography from "@mui/material/Typography";
import type { SxProps } from "@mui/system";

interface Props {
  children: React.ReactNode;
  sx?: SxProps;
}

export const ModalHeader = ({ children, sx }: Props) => (
  <Typography
    marginBottom="8px"
    fontSize="28px"
    fontWeight="500"
    textAlign="center"
    sx={sx}
  >
    {children}
  </Typography>
);
