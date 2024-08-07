import React, { useEffect, useState } from "react";
import { Box, Typography } from "@mui/material";
import { CustomButton } from "../common/CustomButton/CustomButton";
import Modal from "../common/Modal/Modal";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import theme from "../../common/styles/theme";
import { eventBus, EventName } from "../../utils/EventBus";
import {
  getSlotNumber,
  submitGetUserVotes,
} from "../../common/api/voteService";
import {
  buildCanonicalLoginJson,
  submitLogin,
} from "../../common/api/loginService";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import {
  getConnectedWallet,
  setIsLogin,
  setUserVotes,
} from "../../store/reducers/userCache";
import { resolveWalletType } from "../../common/api/utils";
import { useSignatures } from "../../common/hooks/useSignatures";
import { ToastType } from "../common/Toast/Toast.types";
import { saveUserInSession } from "../../utils/session";
import { parseError } from "../../common/constants/errors";

const LoginModal: React.FC = () => {
  const dispatch = useAppDispatch();

  const isMobile = useIsPortrait();
  const connectedWallet = useAppSelector(getConnectedWallet);

  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isLogging, setIsLogging] = useState<boolean>(false);
  const { signWithWallet } = useSignatures();
  useEffect(() => {
    const openLoginModal = () => {
      setIsOpen(true);
    };
    const closeVerifyWalletModal = () => {
      setIsOpen(false);
    };
    eventBus.subscribe(EventName.OpenLoginModal, openLoginModal);
    eventBus.subscribe(EventName.CloseLoginModal, closeVerifyWalletModal);

    return () => {
      eventBus.unsubscribe(EventName.OpenLoginModal, openLoginModal);
      eventBus.unsubscribe(EventName.CloseLoginModal, closeVerifyWalletModal);
    };
  }, []);

  const handleLogin = async () => {
    try {
      setIsLogging(true);
      // @ts-ignore
      const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
      const canonicalLoginInput = buildCanonicalLoginJson({
        walletId: connectedWallet.address,
        walletType: resolveWalletType(connectedWallet.address),
        slotNumber: absoluteSlot.toString(),
      });

      const loginSignatureResult = await signWithWallet(
        canonicalLoginInput,
        connectedWallet.address,
        resolveWalletType(connectedWallet.address),
      );

      if (!loginSignatureResult.success) {
        eventBus.publish(
          EventName.ShowToast,
          loginSignatureResult.error || "Error while signing",
          ToastType.Error,
        );
        return;
      }

      submitLogin(
          // @ts-ignore
        loginSignatureResult.result,
        resolveWalletType(connectedWallet.address),
      )
        .then((response) => {
          const newSession = {
            // @ts-ignore
            accessToken: response.accessToken,
            // @ts-ignore
            expiresAt: response.expiresAt,
          };
          saveUserInSession(newSession);
          dispatch(setIsLogin(true));
          eventBus.publish(EventName.ShowToast, "Login successfully");
          submitGetUserVotes(newSession?.accessToken)
            .then((uVotes) => {
              if (uVotes) {
                // @ts-ignore
                dispatch(setUserVotes(uVotes));
              }
              handleCloseModal();
            })
            .catch((e) => {
              setIsLogging(false);
              eventBus.publish(
                EventName.ShowToast,
                parseError(e.message),
                ToastType.Error,
              );
            });
        })
        .catch((e) => {
          setIsLogging(false);
          eventBus.publish(
            EventName.ShowToast,
            parseError(e.message),
            ToastType.Error,
          );
        });
    } catch (e) {
      setIsLogging(false);
      // @ts-ignore
      eventBus.publish(EventName.ShowToast, e.message, ToastType.Error);
    }
  };

  const handleCloseModal = () => {
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
