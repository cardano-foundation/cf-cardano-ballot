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
  getWalletIdentifier,
  setWalletIdentifier,
  setWalletIsVerified,
} from "../../store/reducers/userCache";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../utils/utils";

const AppWrapper = (props: { children: ReactNode }) => {
  const dispatch = useAppDispatch();
  const walletIdentifier = useAppSelector(getWalletIdentifier);

  const { stakeAddress } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  useEffect(() => {
    initApp();
  }, []);

  useEffect(() => {
    if (stakeAddress) {
      dispatch(setWalletIdentifier(stakeAddress));
    }
  }, [stakeAddress]);

  useEffect(() => {
    const checkWalletVerification = async () => {
      const isVerifiedResult = await getIsVerified(walletIdentifier);
      // @ts-ignore
      if (!isVerifiedResult?.error) {
        // @ts-ignore
        dispatch(setWalletIsVerified(isVerifiedResult.verified));
      } else {
        eventBus.publish(
          EventName.ShowToast,
          "Failed to check wallet verification",
          ToastType.Error,
        );
      }
    };
    if (walletIdentifier?.length) {
      checkWalletVerification();
    }
  }, [walletIdentifier]);

  useEffect(() => {
    if (stakeAddress) {
      dispatch(setWalletIdentifier(stakeAddress));
    }
  }, [stakeAddress]);

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
