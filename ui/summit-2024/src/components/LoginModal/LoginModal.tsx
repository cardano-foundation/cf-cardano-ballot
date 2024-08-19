import React from "react";
import { Box, Typography } from "@mui/material";
import { CustomButton } from "../common/CustomButton/CustomButton";
import Modal from "../common/Modal/Modal";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import theme from "../../common/styles/theme";

interface CategoriesProps {
  isOpen: boolean;
  isLogging: boolean;
  handleLogin: () => void;
  handleCloseModal: () => void;
}
const LoginModal: React.FC<CategoriesProps> = ({
  isOpen,
  isLogging,
  handleLogin,
  handleCloseModal,
}) => {
  const isMobile = useIsPortrait();
  return (
    <>
      <Modal
        id="login-modal"
        isOpen={isOpen}
        name="login-modal"
        title="Login"
        onClose={() => handleCloseModal()}
        width={isMobile ? "100%" : "450px"}
      >
        <Box
          component="div"
          sx={{
            width: {
              xs: "100%",
              sm: "400px",
            },
            display: "flex",
            justifyContent: "center",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Typography
            sx={{
              color: theme.palette.text.neutralLightest,
              textAlign: "center",
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: "500",
              lineHeight: "24px",
            }}
          >
            Login in order to see your vote receipts
          </Typography>
          <CustomButton
            onClick={() => handleLogin()}
            colorVariant="primary"
            sx={{
              minWidth: "256px",
              mt: "24px",
              mb: "28px",
            }}
            disabled={isLogging}
          >
            Login
          </CustomButton>
        </Box>
      </Modal>
    </>
  );
};

export { LoginModal };
