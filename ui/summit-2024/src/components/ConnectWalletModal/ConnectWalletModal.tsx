import { createContext, useContext, useEffect, useState } from "react";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../utils/utils";
import { env } from "../../common/constants/env";
import ConnectWalletList from "../ConnectWalletList/ConnectWalletList";
import Modal from "../common/Modal/Modal";
import {
  ConnectWalletFlow,
  IWalletInfo,
} from "../ConnectWalletList/ConnectWalletList.types";
import {
  ConnectWalletContextType,
  ConnectWalletProps,
} from "./ConnectWalletModal.type";
import { eventBus } from "../../utils/EventBus";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import {useAppDispatch} from "../../store/hooks";
import {setIdentifier} from "../../store/reducers/userCache";

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
  const dispatch = useAppDispatch();
  const [peerConnectWalletInfo, setPeerConnectWalletInfo] = useState<
    IWalletInfo | undefined
  >(undefined);
  const [connectCurrentPaths, setConnectCurrentPaths] = useState<
    ConnectWalletFlow[]
  >([ConnectWalletFlow.SELECT_WALLET]);

  const [onPeerConnectAccept, setOnPeerConnectAccept] = useState(() => () => {
    /*TODO */
  });
  const [onPeerConnectReject, setOnPeerConnectReject] = useState(() => () => {
    /*TODO */
  });

  const { connect, dAppConnect, meerkatAddress, initDappConnect, disconnect, stakeAddress } =
    useCardano({
      limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
    });

  const isMobile = useIsPortrait();

  const onConnectWalletError = (e: Error) => {
    eventBus.publish("showToast", e.message, "error");
  };

  const handleOpenPeerConnect = () => {};

  const handleBack = () => {
    if (connectCurrentPaths.length > 1) {
      setConnectCurrentPaths((prevPaths) => prevPaths.slice(1));
    }
  };

  const setCurrentPath = (currentPath: ConnectWalletFlow) => {
    const filteredPaths = connectCurrentPaths.filter((p) => p !== currentPath);
    return setConnectCurrentPaths([currentPath, ...filteredPaths]);
  };

  const onConnectWallet = () => {
    eventBus.publish("closeConnectWalletModal");
    eventBus.publish("showToast", "Wallet connected successfully");
  };

  const onConnectError = (e: Error) => {
    eventBus.publish("showToast", e.message, "error");
  };

  useEffect(() => {
    if (stakeAddress){
      console.log("stakeAddress");
      console.log(stakeAddress);
      dispatch(setIdentifier(stakeAddress));
    }
  }, [stakeAddress]);

  useEffect(() => {
    if (dAppConnect.current === null) {
      const verifyConnection = (
        walletInfo: IWalletInfo,
        callback: (granted: boolean, autoconnect: boolean) => void,
      ) => {
        console.log("verifyConnection");
        console.log(walletInfo);
        setPeerConnectWalletInfo(walletInfo);
        setCurrentPath(ConnectWalletFlow.ACCEPT_CONNECTION);

        setOnPeerConnectAccept(() => () => callback(true, true));
        setOnPeerConnectReject(() => () => callback(false, false));
      };

      const onApiInject = (name: string): void => {
        connect(
          name,
          () => {
            props.handleCloseConnectWalletModal();
            eventBus.publish("showToast",`${name} Wallet connected successfully`);
          },
          (e:Error) => {
            eventBus.publish(
              "showToast",
              e.message,
              "error",
            );
          },
        ).catch((e) => console.error(e));
      };

      const onApiEject = (name: string): void => {
        setPeerConnectWalletInfo(undefined);
        eventBus.publish("showToast", `${name} Wallet disconnected successfully`);
        disconnect();
      };

      const onP2PConnect = (): void => {
        if (peerConnectWalletInfo?.address){
          dispatch(setIdentifier(peerConnectWalletInfo.address))
        }
      };

      initDappConnect(
        "Cardano Summit 2024",
        env.FRONTEND_URL,
        verifyConnection,
        onApiInject,
        onApiEject,
        [],
        onP2PConnect,
      );
    }
  }, []);

  const handleConnectWallet = (walletName: string) => {
    console.log("handleConnectWallet");
    console.log(walletName);
    connect(walletName, onConnectWallet, onConnectError);
  };

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
            peerConnectWalletInfo={peerConnectWalletInfo}
            onConnectError={(error: Error) => onConnectWalletError(error)}
            onOpenPeerConnect={() => handleOpenPeerConnect()}
            currentPath={connectCurrentPaths[0]}
            setCurrentPath={(currentPath: ConnectWalletFlow) =>
                setCurrentPath(currentPath)
            }
            closeModal={() => props.handleCloseConnectWalletModal()}
            connectExtensionWallet={(walletName: string) =>
                handleConnectWallet(walletName)
            }
            handleOnPeerConnectAccept={() => handleAccept()}
        />
      </Modal>
    </>
  );
};

export { ConnectWalletModal, useConnectWalletContext };
