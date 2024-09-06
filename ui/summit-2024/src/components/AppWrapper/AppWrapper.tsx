import { ReactNode, useEffect } from "react";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { env } from "../../common/constants/env";
import {getEventCache, setEventCache} from "../../store/reducers/eventCache";
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
import {getVoteReceipts, submitGetUserVotes} from "../../common/api/voteService";
import {setVoteReceipts, setVotes} from "../../store/reducers/votesCache";
import { parseError } from "../../common/constants/errors";
import event2024Extended from "../../common/resources/data/summit2024Content.json";

const AppWrapper = (props: { children: ReactNode }) => {
  const dispatch = useAppDispatch();
  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);
  const eventCache = useAppSelector(getEventCache);
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
          if (Array.isArray(response) && response.length) {
            dispatch(setVotes(response));
            getVoteReceipts(session.accessToken).then((receipts) => {
              // @ts-ignore
              dispatch(setVoteReceipts(receipts));
            });
          }
        })
        .catch((e) => {
          if (process.env.NODE_ENV === "development") {
            console.log(`Failed to fetch user votes, ${parseError(e.message)}`);
          }
        });
    };

    const walletIsConnectedAndVerified = connectedWallet.address.length && walletIsVerified;
    if (walletIsConnectedAndVerified && !isExpired) {
      updateUserVotes();
    }
  }, [connectedWallet.address, walletIsVerified, isExpired]);

  useEffect(() => {
    const checkWalletVerification = async () => {
      const isVerifiedResult = await getIsVerified(connectedWallet.address);
      // @ts-ignore
      if (isVerifiedResult?.verified) {
        // @ts-ignore
        dispatch(setWalletIsVerified(isVerifiedResult.verified));
      } else if (eventCache.active){
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

  const mergeEventData = (eventData, staticData) => {
    const mergedCategories = eventData.categories.map((category) => {
      const staticCategory = staticData.categories.find((cat) => cat.id === category.id);

      if (staticCategory) {
        const mergedProposals = category.proposals.map((proposal) => {
          const staticProposal = staticCategory.proposals.find((p) => p.id === proposal.id);

          if (staticProposal) {
            // TODO: update reducer types
            return {
              ...proposal,
              name: staticProposal.presentationName || proposal.name,
              x: staticProposal.x || null,
              linkedin: staticProposal.linkedin || null,
              url: staticProposal.url || null,
            };
          }
          return proposal;
        });

        return {
          ...category,
          name: staticCategory.presentationName?.length ? staticCategory.presentationName : category.id,
          desc: staticCategory.desc,
          proposals: mergedProposals
        };
      }
      return category;
    });

    return {
      ...eventData,
      categories: mergedCategories,
    };
  };

  const initApp = async () => {
    if (env.USING_FIXTURES) {
      dispatch(setEventCache(eventDataFixture));
    } else {
      const eventData = await getEventData(env.EVENT_ID);

      // @ts-ignore
      if (!eventData?.error) {
        const mergedEventData = mergeEventData(eventData, event2024Extended);
        // @ts-ignore
        dispatch(setEventCache(mergedEventData));
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
