import React, { createContext, useContext, useEffect, useState } from "react";
import { useMediaQuery } from "@mui/material";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../utils/utils";
import { env } from "../../common/constants/env";
import ConnectWalletList from "../ConnectWalletList/ConnectWalletList";
import Modal from "../common/Modal/Modal";
import theme from "../../common/styles/theme";
import {
  ConnectWalletFlow,
  IWalletInfo,
} from "../ConnectWalletList/ConnectWalletList.types";
import { useDispatch } from "react-redux";
import { ToastType } from "../common/Toast/Toast.types";
import {
  ConnectWalletContextType,
  ConnectWalletProps,
} from "./ConnectWalletModal.type";

const ConnectWalletContext = createContext<ConnectWalletContextType | null>(
  null,
);

const useConnectWalletContext = () => {
  const context = useContext(ConnectWalletContext);
  if (context === null)
    throw new Error("ConnectWalletContext was not provided");
  return context;
};

const ConnectWalletModal = (props: ConnectWalletProps) => {
  const [connectCurrentPaths, setConnectCurrentPaths] = useState<
    ConnectWalletFlow[]
  >([ConnectWalletFlow.SELECT_WALLET]);
  const [startPeerConnect, setStartPeerConnect] = useState(false);

  const [peerConnectWalletInfo, setPeerConnectWalletInfo] = useState<
    IWalletInfo | undefined
  >(undefined);

  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>("common");
  const [toastOpen, setToastOpen] = useState(false);

  const dispatch = useDispatch();
  const {
    stakeAddress,
    isConnected,
    disconnect,
    connect,
    dAppConnect,
    meerkatAddress,
    initDappConnect,
    signMessage,
  } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const contextValue = {
    peerConnectWalletInfo,
    meerkatAddress,
    isMobile,
  };

  const onConnectWalletError = (error: Error) => {};

  const handleOpenPeerConnect = () => {};

  const handleBack = () => {
    if (connectCurrentPaths.length > 1) {
      setConnectCurrentPaths((prevPaths) => prevPaths.slice(1));
    }
  };

  const setCurrentPath = (currentPath) => {
    const filteredPaths = connectCurrentPaths.filter((p) => p !== currentPath);
    return setConnectCurrentPaths([currentPath, ...filteredPaths]);
  };

  const onConnectWallet = () => {};

  const getModalProps = () => {
    switch (connectCurrentPaths[0]) {
      case ConnectWalletFlow.SELECT_WALLET:
        return {
          title:
            "In order to vote, first you will need to connect your Wallet.",
        };
      case ConnectWalletFlow.CONNECT_IDENTITY_WALLET:
        return {
          title: "Use IDW Wallet to connect",
        };
      case ConnectWalletFlow.CONNECT_CIP45_WALLET:
        return {
          title: "",
        };
      case ConnectWalletFlow.ACCEPT_CONNECTION:
        return {
          title: "",
        };
    }
  };

  const modalProps = getModalProps();
  return (
    <>
      <ConnectWalletContext.Provider value={contextValue}>
        <Modal
          id="connect-wallet-modal"
          isOpen={props.showPeerConnect}
          name="connect-wallet-modal"
          title={
            connectCurrentPaths[0] === ConnectWalletFlow.CONNECT_IDENTITY_WALLET
              ? "Connect Identity Wallet"
              : "Connect Wallet"
          }
          onClose={() => props.handleCloseConnectWalletModal()}
          width={isMobile ? "auto" : "450px"}
          backButton={
            connectCurrentPaths[0] !== ConnectWalletFlow.SELECT_WALLET
          }
          onBack={() => handleBack()}
        >
          <ConnectWalletList
            description={modalProps.title}
            onConnectWallet={onConnectWallet}
            onConnectError={(error: Error) => onConnectWalletError(error)}
            onOpenPeerConnect={() => handleOpenPeerConnect()}
            currentPath={connectCurrentPaths[0]}
            setCurrentPath={(currentPath: ConnectWalletFlow) =>
              setCurrentPath(currentPath)
            }
            closeModal={() => props.handleCloseConnectWalletModal()}
          />
        </Modal>
      </ConnectWalletContext.Provider>
    </>
  );
};

export { ConnectWalletModal, useConnectWalletContext };
