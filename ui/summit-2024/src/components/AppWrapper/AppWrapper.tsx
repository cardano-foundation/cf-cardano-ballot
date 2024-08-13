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
  getWalletIsVerified,
  setConnectedWallet,
  setWalletIsVerified,
} from "../../store/reducers/userCache";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../utils/utils";
import { getUserInSession, tokenIsExpired } from "../../utils/session";
import { submitGetUserVotes } from "../../common/api/voteService";
import { setVotes } from "../../store/reducers/votesCache";
import { parseError } from "../../common/constants/errors";

const AppWrapper = (props: { children: ReactNode }) => {
  const dispatch = useAppDispatch();
  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);
  const walletIsVerified = useAppSelector(getWalletIsVerified);
  const connectedWallet = useAppSelector(getConnectedWallet);
  const { stakeAddress, enabledWallet } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  useEffect(() => {
    initApp();
  }, []);

  useEffect(() => {
    const updateUserVotes = async () => {
      submitGetUserVotes(session.accessToken)
        .then((response) => {
          // @ts-ignore
          dispatch(setVotes(response));
        })
        .catch((e) => {
          if (process.env.NODE_ENV === "development") {
            console.log(`Failed to fetch user votes, ${parseError(e.message)}`);
          }
        });
    };
    if (connectedWallet.address.length && walletIsVerified && !isExpired) {
      updateUserVotes();
    }
  }, [connectedWallet.address, walletIsVerified]);

  useEffect(() => {
    const checkWalletVerification = async () => {
      const isVerifiedResult = await getIsVerified(connectedWallet.address);
      // @ts-ignore
      if (!isVerifiedResult?.error) {
        // @ts-ignore
        dispatch(setWalletIsVerified(isVerifiedResult.verified));
      } else {
        eventBus.publish(EventName.OpenVerifyWalletModal);
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
