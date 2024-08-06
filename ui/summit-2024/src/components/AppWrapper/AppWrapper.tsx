import { ReactNode, useEffect } from "react";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { env } from "../../common/constants/env";
import { setEventCache } from "../../store/reducers/eventCache";
import { getEventData } from "../../common/api/eventDataService";
import { eventBus, EventName } from "../../utils/EventBus";
import { eventDataFixture } from "../../__fixtures__/event";
import { ToastType } from "../common/Toast/Toast.types";
import { getIsVerified } from "../../common/api/verificationService";
import {
  getConnectedWallet,
  setConnectedWallet,
  setWalletIsVerified,
} from "../../store/reducers/userCache";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../utils/utils";

const AppWrapper = (props: { children: ReactNode }) => {
  const dispatch = useAppDispatch();
  const connectedWallet = useAppSelector(getConnectedWallet);
  const { stakeAddress, enabledWallet } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  useEffect(() => {
    initApp();
  }, []);

  useEffect(() => {
    const checkWalletVerification = async () => {
      const isVerifiedResult = await getIsVerified(connectedWallet.address);
      // @ts-ignore
      if (!isVerifiedResult?.error) {
        // @ts-ignore
        dispatch(setWalletIsVerified(isVerifiedResult.verified));
      }
    };
    if (connectedWallet.address?.length) {
      checkWalletVerification();
    }
  }, [connectedWallet.address]);

  useEffect(() => {
    if (stakeAddress && enabledWallet) {
      dispatch(
        setConnectedWallet({
          address: stakeAddress,
          name: enabledWallet,
          icon: window.cardano[enabledWallet].icon,
          version: window.cardano[enabledWallet].version,
        }),
      );
    }
  }, [stakeAddress, connectedWallet.address]);

  const initApp = async () => {
    if (env.USING_FIXTURES) {
      dispatch(setEventCache(eventDataFixture));
    } else {
      const eventData = await getEventData(env.EVENT_ID);
      // @ts-ignore
      if (!eventData?.error) {
        // @ts-ignore
        dispatch(setEventCache(eventData));
      } else {
        eventBus.publish(
          EventName.ShowToast,
          "Failed to load event data",
          ToastType.Error,
        );
      }
    }
  };

  return <>{props.children}</>;
};

export { AppWrapper };
