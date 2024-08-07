import React, {useEffect, useState} from "react";
import { Box, Typography } from "@mui/material";
import { CustomButton } from "../common/CustomButton/CustomButton";
import Modal from "../common/Modal/Modal";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import theme from "../../common/styles/theme";
import {eventBus, EventName} from "../../utils/EventBus";

const LoginModal: React.FC = () => {
  const isMobile = useIsPortrait();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isLogging, setIsLogging] = useState<boolean>(false);

    useEffect(() => {
        const openLoginModal = () => {
            setIsOpen(true);
        };
        const closeVerifyWalletModal = () => {
            setIsOpen(false);
        };
        eventBus.subscribe(EventName.OpenLoginModal, openLoginModal);
        eventBus.subscribe(
            EventName.CloseLoginModal,
            closeVerifyWalletModal,
        );

        return () => {
            eventBus.unsubscribe(
                EventName.OpenLoginModal,
                openLoginModal,
            );
            eventBus.unsubscribe(
                EventName.CloseLoginModal,
                closeVerifyWalletModal,
            );
        };
    }, []);

  const handleLogin = () => {
    setIsLogging(true);
  };
  const handleCloseModal= () => {
    setIsOpen(false);
    setIsLogging(false);
  };

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
              sm: "450px",
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
              mt: "50px",
              mb: "20px",
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
