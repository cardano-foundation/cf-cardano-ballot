import { Box } from "@mui/material";
import { useScreenDimension } from "@/hooks";

interface Props {
  children: React.ReactNode;
}

export const ModalContents = ({ children }: Props) => {
  const { isMobile } = useScreenDimension();

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      px={isMobile ? 0 : 3}
    >
      {children}
    </Box>
  );
};
