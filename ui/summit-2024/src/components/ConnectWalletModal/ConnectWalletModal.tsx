import { useEffect, useState } from "react";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../utils/utils";
import { env } from "../../common/constants/env";
import ConnectWalletList from "../ConnectWalletList/ConnectWalletList";
import Modal from "../common/Modal/Modal";
import {
  ConnectWalletFlow,
  IWalletInfo,
} from "../ConnectWalletList/ConnectWalletList.types";
import { ConnectWalletProps } from "./ConnectWalletModal.type";
import { eventBus, EventName } from "../../utils/EventBus";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { useAppDispatch } from "../../store/hooks";
import { setConnectedWallet } from "../../store/reducers/userCache";
import { ToastType } from "../common/Toast/Toast.types";
import { initialConnectedWallet } from "../../store/reducers/userCache/initialState";

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
  // @ts-ignore
  const [onPeerConnectReject, setOnPeerConnectReject] = useState(() => () => {
    /*TODO */
  });

  const {
    connect,
    dAppConnect,
    stakeAddress,
    enabledWallet,
    meerkatAddress,
    initDappConnect,
    disconnect,
  } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const isMobile = useIsPortrait();

  const onConnectWalletError = (e: Error) => {
    eventBus.publish(EventName.ShowToast, e.message, ToastType.Error);
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

  const onConnectError = (e: Error) => {
    eventBus.publish(EventName.ShowToast, e.message, ToastType.Error);
  };

  useEffect(() => {
    if (dAppConnect.current === null) {
      const verifyConnection = (
        walletInfo: IWalletInfo,
        callback: (granted: boolean, autoconnect: boolean) => void,
      ) => {
        setPeerConnectWalletInfo(walletInfo);
        setCurrentPath(ConnectWalletFlow.ACCEPT_CONNECTION);

        setOnPeerConnectAccept(() => () => callback(true, true));
        setOnPeerConnectReject(() => () => callback(false, false));
      };

      const onApiInject = async (name: string) => {
        console.log("onApiInject");
        console.log(name);
        if (name === "idw_p2p") {
          const api = window.cardano && window.cardano[name];
          if (api) {
            const enabledApi = await api.enable();
            const keriIdentifier =
              await enabledApi.experimental.getKeriIdentifier();
            dispatch(
              setConnectedWallet({
                address: keriIdentifier,
                name: api.name,
                icon: api.icon,
                requestAutoconnect: true,
                version: api.version,
              }),
            );
            eventBus.publish(
              EventName.ShowToast,
              `${name} Wallet connected successfully`,
            );
            props.handleCloseConnectWalletModal();
          } else {
            eventBus.publish(
              EventName.ShowToast,
              `Timeout while connecting P2P ${name} wallet`,
              ToastType.Error,
            );
          }
        } else {
          connect(
            name,
            () => {
              if (stakeAddress && enabledWallet) {
                dispatch(
                  setConnectedWallet({
                    address: stakeAddress,
                    name: enabledWallet,
                    icon: window.cardano[name].icon,
                    version: window.cardano[name].version,
                  }),
                );

                eventBus.publish(
                  EventName.ShowToast,
                  `${name} Wallet connected successfully`,
                );
                props.handleCloseConnectWalletModal();
              }
            },
            (e: Error) => {
              eventBus.publish(EventName.ShowToast, e.message, ToastType.Error);
            },
          ).catch((e) => console.error(e));
        }
      };

      const onApiEject = (name: string): void => {
        console.log("onApiEject");
        console.log(name);
        dispatch(setConnectedWallet(initialConnectedWallet));
        setPeerConnectWalletInfo(undefined);
        eventBus.publish(
          EventName.ShowToast,
          `${name} Wallet disconnected successfully`,
        );
        disconnect();
      };

      const onP2PConnect = (): void => {
        console.log("onP2PConnect");
        console.log(peerConnectWalletInfo);
        if (peerConnectWalletInfo?.address) {
          dispatch(setConnectedWallet(peerConnectWalletInfo));
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

  const handleConnectExtensionWallet = async (walletName: string) => {
    await connect(
      walletName,
      () => {
        dispatch(
          setConnectedWallet({
            address: stakeAddress || "",
            name: walletName,
            icon: window.cardano[walletName].icon,
            version: window.cardano[walletName].version,
          }),
        );
        eventBus.publish(EventName.CloseConnectWalletModal);
        eventBus.publish(EventName.ShowToast, "Wallet connected successfully");
      },
      onConnectError,
    );
  };

  const handleAcceptP2PWallet = () => {
    if (peerConnectWalletInfo) {
      onPeerConnectAccept();
      connect(peerConnectWalletInfo.name).then(async () => {
        if (peerConnectWalletInfo.name === "idw_p2p") {
          const start = Date.now();
          const interval = 100;
          const timeout = 5000;

          const checkApi = setInterval(async () => {
            const api =
              // @ts-ignore
              window.cardano && window.cardano[peerConnectWalletInfo.name];
            if (api || Date.now() - start > timeout) {
              clearInterval(checkApi);
              if (api) {
                const enabledApi = await api.enable();
                const keriIdentifier =
                  await enabledApi.experimental.getKeriIdentifier();
                dispatch(
                  setConnectedWallet({
                    ...peerConnectWalletInfo,
                    address: keriIdentifier.id,
                  }),
                );
              } else {
                eventBus.publish(
                  EventName.ShowToast,
                  `Timeout while connecting P2P ${peerConnectWalletInfo.name} wallet`,
                  ToastType.Error,
                );
              }
              handleModalClose();
            }
          }, interval);
        } else {
          handleModalClose();
        }
      });
    }
  };

  const handleModalClose = () => {
    props.handleCloseConnectWalletModal();
    setTimeout(() => {
      setConnectCurrentPaths([ConnectWalletFlow.SELECT_WALLET]);
    }, 500);
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
            : "Connect Peer Wallet"
        }
        onClose={() => handleModalClose()}
        width={isMobile ? "auto" : "450px"}
        backButton={connectCurrentPaths[0] !== ConnectWalletFlow.SELECT_WALLET}
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
          closeModal={() => handleModalClose()}
          connectExtensionWallet={(walletName: string) =>
            handleConnectExtensionWallet(walletName)
          }
          handleOnPeerConnectAccept={() => handleAcceptP2PWallet()}
        />
      </Modal>
    </>
  );
};

export { ConnectWalletModal };
