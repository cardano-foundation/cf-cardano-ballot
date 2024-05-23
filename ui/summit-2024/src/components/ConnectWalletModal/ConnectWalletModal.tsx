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
import { ToastType } from "../common/Toast/Toast.types";
import {
  ConnectWalletContextType,
  ConnectWalletProps,
} from "./ConnectWalletModal.type";
import { eventBus } from "../../utils/EventBus";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";

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
  const [peerConnectWalletInfo, setPeerConnectWalletInfo] = useState<
    IWalletInfo | undefined
  >(undefined);
  const [connectCurrentPaths, setConnectCurrentPaths] = useState<
    ConnectWalletFlow[]
  >([ConnectWalletFlow.SELECT_WALLET]);

  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>(ToastType.Common);
  const [toastOpen, setToastOpen] = useState(false);
  const [onPeerConnectAccept, setOnPeerConnectAccept] = useState(() => () => {
    /*TODO */
  });
  const [onPeerConnectReject, setOnPeerConnectReject] = useState(() => () => {
    /*TODO */
  });

  const { connect, dAppConnect, meerkatAddress, initDappConnect } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const isMobile = useIsPortrait();

  const contextValue = {
    isMobile,
    meerkatAddress,
    peerConnectWalletInfo,
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

  const onConnectWallet = () => {
    eventBus.publish("closeConnectWalletModal");
    eventBus.publish("showToast", "Wallet connected successfully");
  };
  const onConnectError = () => {
    eventBus.publish(
      "showToast",
      "Unable to connect wallet. Please try again",
      "error",
    );
  };

  useEffect(() => {
    if (dAppConnect.current === null) {
      const verifyConnection = (
        walletInfo: IWalletInfo,
        callback: (granted: boolean, autoconnect: boolean) => void,
      ) => {
        setPeerConnectWalletInfo(walletInfo);
        setCurrentPath(ConnectWalletFlow.ACCEPT_CONNECTION);

        if (walletInfo.requestAutoconnect) {
          //setModalMessage(`Do you want to automatically connect to wallet ${walletInfo.name} (${walletInfo.address})?`);
          setOnPeerConnectAccept(() => () => callback(true, true));
          setOnPeerConnectReject(() => () => callback(false, false));
        } else {
          // setModalMessage(`Do you want to connect to wallet ${walletInfo.name} (${walletInfo.address})?`);
          setOnPeerConnectAccept(() => () => callback(true, false));
          setOnPeerConnectReject(() => () => callback(false, false));
        }
      };

      const onApiInject = (name: string, address: string): void => {
        connect(
          name,
          () => {
            props.handleCloseConnectWalletModal();
            eventBus.publish("showToast", "Wallet connected successfully");
          },
          () => {
            eventBus.publish(
              "showToast",
              "Unable to connect wallet. Please try again",
              "error",
            );
          },
        ).catch((e) => console.error(e));
      };

      const onApiEject = (name: string, address: string): void => {
        setPeerConnectWalletInfo(undefined);
        eventBus.publish("showToast", "Wallet disconnected successfully");
      };

      const onP2PConnect = (
        address: string,
        walletInfo?: IWalletInfo,
      ): void => {
        // TODO
      };

      initDappConnect(
        "Cardano Summit 2023",
        env.FRONTEND_URL,
        verifyConnection,
        onApiInject,
        onApiEject,
        [],
        onP2PConnect,
      );
    }
  }, []);

  const handleAccept = () => {
    if (peerConnectWalletInfo) {
      onPeerConnectAccept();
      connect(peerConnectWalletInfo.name).then(() => {
        props.handleCloseConnectWalletModal();
      });
    }
  };

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
            meerkatAddress={meerkatAddress}
            onConnectWallet={onConnectWallet}
            onConnectError={(error: Error) => onConnectWalletError(error)}
            onOpenPeerConnect={() => handleOpenPeerConnect()}
            currentPath={connectCurrentPaths[0]}
            setCurrentPath={(currentPath: ConnectWalletFlow) =>
              setCurrentPath(currentPath)
            }
            closeModal={() => props.handleCloseConnectWalletModal()}
            connectExtensionWallet={(walletName: string) =>
              connect(walletName, onConnectWallet, onConnectError)
            }
            handleOnPeerConnectAccept={() => handleAccept()}
          />
        </Modal>
      </ConnectWalletContext.Provider>
    </>
  );
};

export { ConnectWalletModal, useConnectWalletContext };
