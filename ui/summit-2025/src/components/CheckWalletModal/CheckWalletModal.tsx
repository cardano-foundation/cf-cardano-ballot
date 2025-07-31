import React, {useEffect} from "react";
import {Avatar, Box, Typography} from "@mui/material";
import { CustomButton } from "../common/CustomButton/CustomButton";
import Modal from "../common/Modal/Modal";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import theme from "../../common/styles/theme";
import {useAppSelector} from "../../store/hooks";
import {getConnectedWallet} from "../../store/reducers/userCache";
import {eventBus, EventName} from "../../utils/EventBus";

interface CheckWalletModalProps {
  isOpen: boolean;
  handleOpenModal: () => void;
  handleCloseModal: () => void;
}

const CheckWalletModal: React.FC<CheckWalletModalProps> = ({
  isOpen,
  handleOpenModal,
  handleCloseModal,
}) => {
  const isMobile = useIsPortrait();
  const connectedWallet = useAppSelector(getConnectedWallet);

    useEffect(() => {

        const closeCheckWalletModal = () => {
            handleCloseModal();
        };
        const openCheckWalletModal = () => {
            handleOpenModal();
        };

        eventBus.subscribe(EventName.CloseCheckWalletModal, closeCheckWalletModal);
        eventBus.subscribe(EventName.OpenCheckWalletModal, openCheckWalletModal);

        return () => {
            eventBus.unsubscribe(
                EventName.CloseCheckWalletModal,
                closeCheckWalletModal
            );
            eventBus.unsubscribe(
                EventName.OpenCheckWalletModal,
                openCheckWalletModal
            );
        };
    }, []);

  return (
    <>
      <Modal
        id="login-modal"
        isOpen={isOpen}
        name="login-modal"
        title="Verify Your Vote"
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
            <Avatar
                src={connectedWallet.icon}
                style={{ width: "53px", height: "auto", margin: "20px 16px" }}
            />
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
            Please refer to your wallet application to verify your vote.
          </Typography>
          <CustomButton
            onClick={() => handleCloseModal()}
            colorVariant="primary"
            sx={{
              minWidth: "100%",
              mt: "24px",
              mb: "28px",
            }}
          >
            Ok
          </CustomButton>
        </Box>
      </Modal>
    </>
  );
};

export { CheckWalletModal };
