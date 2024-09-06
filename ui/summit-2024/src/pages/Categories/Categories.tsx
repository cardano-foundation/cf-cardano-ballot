import React, { useEffect, useMemo, useState } from "react";
import { Box, Typography, useMediaQuery, Drawer } from "@mui/material";
import theme from "../../common/styles/theme";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import { VoteNowModal } from "./components/VoteNowModal";
import { ViewReceipt } from "./components/ViewReceipt";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { getEventCache } from "../../store/reducers/eventCache";
import {
  Category,
  Proposal,
} from "../../store/reducers/eventCache/eventCache.types";
import { PageBase } from "../BasePage";
import { Nominees } from "./components/Nominees";
import { Winners } from "./components/Winners";
import { BioModal } from "./components/BioModal";
import Layout from "../../components/Layout/Layout";
import Ellipses from "../../assets/ellipse.svg";
import { v4 as uuidv4 } from "uuid";
import {
  buildCanonicalVoteInputJson,
  submitVoteWithDigitalSignature,
  getSlotNumber,
  submitGetUserVotes,
  getVoteReceipt,
  getVoteReceipts,
} from "../../common/api/voteService";
import { eventBus, EventName } from "../../utils/EventBus";
import {
  getConnectedWallet,
  getWalletIsVerified,
} from "../../store/reducers/userCache";
import { getUserInSession, tokenIsExpired } from "../../utils/session";
import { parseError } from "../../common/constants/errors";
import {
  getReceipts,
  getVotes,
  setVoteReceipt,
  setVoteReceipts,
  setVotes,
} from "../../store/reducers/votesCache";
import { ToastType } from "../../components/common/Toast/Toast.types";
import { resolveWalletType } from "../../common/api/utils";
import {
  getSignedMessagePromise,
  resolveCardanoNetwork,
  signMessageWithWallet,
} from "../../utils/utils";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { env } from "../../common/constants/env";
import { formatISODate } from "../../utils/utils";

interface CategoriesProps {
  embedded?: boolean;
}

const Categories: React.FC<CategoriesProps> = ({ embedded }) => {
  const isTablet = useMediaQuery(theme.breakpoints.down("md"));
  const eventCache = useAppSelector(getEventCache);
  const connectedWallet = useAppSelector(getConnectedWallet);
  const walletIdentifierIsVerified = useAppSelector(getWalletIsVerified);
  const receipts = useAppSelector(getReceipts);
  const userVotes = useAppSelector(getVotes);
  const categoriesData = eventCache.categories;

  const [showWinners] = useState(eventCache.proposalsReveal);

  const [selectedCategory, setSelectedCategory] = useState(
    categoriesData[0].id,
  );

  const [selectedNominee, setSelectedNominee] = useState<string | undefined>(
    undefined,
  );

  const [openVotingModal, setOpenVotingModal] = useState(false);
  const [openViewReceipt, setOpenViewReceipt] = useState(false);

  const [bioModalContent, setBioModalContent] = useState<Proposal | null>(null);
  const [openLearMoreCategory, setOpenLearMoreCategory] = useState(false);

  const [fadeChecked, setFadeChecked] = useState(true);

  const session = getUserInSession();
  const dispatch = useAppDispatch();

  const showEventDate =
    eventCache.notStarted ||
    (eventCache.finished && !eventCache.proposalsReveal); // If the event has not started or it's just before the reveal
  const showVotingButton =
    eventCache.active || (eventCache.finished && !eventCache.proposalsReveal); // If the event has not started or the results have been revealed we prevent the voting bottom to show up

  const { signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  let categoryToRender = categoriesData.find((c) => c.name === selectedCategory);
  if (categoryToRender === undefined) {
    categoryToRender = categoriesData[0];
  }



  const nomineeToVote = useMemo(() => {
    return categoryToRender?.proposals.find(p => p.id === selectedNominee);
  }, [selectedNominee, categoryToRender]);

  const categoryAlreadyVoted = !!userVotes?.find(
    (vote) => vote.categoryId === categoryToRender?.id,
  );

  const signMessagePromisified = useMemo(
    () => getSignedMessagePromise(signMessage),
    [signMessage],
  );

  useEffect(() => {
    // Example: http://localhost:3000/categories?category=ambassador&nominee=63123e7f-dfc3-481e-bb9d-fed1d9f6e9b9
    const params = new URLSearchParams(window.location.search);
    const categoryParam = params.get("category");
    const nomineeParam = params.get("nominee");

    const category = categoriesData.find(
      (c) => c.id.toUpperCase() === categoryParam?.toUpperCase(),
    );

    if (!category) return;
    const nominee = category.proposals.find(
      (p) => p.id.toUpperCase() === nomineeParam?.toUpperCase(),
    );
    if (!nominee) return;
    handleOpenLearnMoreModal(nominee.id);
  }, [eventCache.categories]);

  useEffect(() => {
    if (fadeChecked) {
      setSelectedCategory(selectedCategory);
    }
  }, [fadeChecked, selectedCategory]);

  const handleClickMenuItem = (category: string) => {
    if (category !== selectedCategory) {
      setFadeChecked(false);
      setSelectedCategory(category);
      setTimeout(() => {
        setFadeChecked(true);
      }, 200);
    }
  };

  const handleSelectNominee = (id: string) => {
    setSelectedNominee(prevNominee => prevNominee === id ? undefined : id);
  };

  const findProposalById = (categories: Category[], proposalId: string) => {
    for (const category of categories) {
      for (const proposal of category.proposals) {
        if (proposal.id === proposalId) {
          return proposal;
        }
      }
    }
    return null;
  };

  const handleOpenLearnMoreModal = (nomineeId: string) => {
    const bioModalContent = findProposalById(categoriesData, nomineeId);
    setBioModalContent(bioModalContent);
    setOpenLearMoreCategory(true);
  };

  const handleOpenViewReceipt = () => {
    setOpenViewReceipt(true);
  };

  const handleSignIn = () => {};

  const handleViewReceipt = async () => {
    if (!categoryToRender) return;
    if (receipts[categoryToRender?.id] !== undefined) {
      setOpenViewReceipt(true);
    }
    if (!tokenIsExpired(session?.expiresAt)) {
      await getVoteReceipt(categoryToRender?.id, session?.accessToken)
        .then((r) => {
          dispatch(
            // @ts-ignore
            setVoteReceipt({ categoryId: categoryToRender?.id, receipt: r }),
          );
          setOpenViewReceipt(true);
        })
        .catch((e) => {
          eventBus.publish(EventName.ShowToast, e.message, ToastType.Error);
        });
    } else {
      eventBus.publish(
        EventName.ShowToast,
        "Login to see your vote receipt",
        ToastType.Error,
      );
    }
  };
  const handleOpenActionButton = () => {
    if (showWinners) {
      handleOpenViewReceipt();
    } else {
      if (!connectedWallet.address.length) {
        eventBus.publish(
          EventName.ShowToast,
          "Connect your wallet in order to vote",
          ToastType.Error,
        );
        return;
      }
      if (!walletIdentifierIsVerified) {
        eventBus.publish(
          EventName.ShowToast,
          "Verify your wallet in order to vote",
          ToastType.Error,
        );
        return;
      }
      setOpenVotingModal(true);
    }
  };

  const submitVote = async () => {
    if (eventCache?.finished) {
      eventBus.publish(EventName.ShowToast, "The event already ended", "error");
      return;
    }

    const category = categoriesData.find((c) => c.name === selectedCategory);

    const proposalId = category?.proposals?.find(p => p.id === selectedNominee)?.id

    if (!category?.id || !proposalId) {
      eventBus.publish(EventName.ShowToast, "Nominee not selected", "error");
      return;
    }

    try {
      // @ts-ignore
      const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
      const canonicalVoteInput = buildCanonicalVoteInputJson({
        voteId: uuidv4(),
        categoryId: category.id,
        proposalId: proposalId,
        walletId: connectedWallet.address,
        walletType: resolveWalletType(connectedWallet.address),
        slotNumber: absoluteSlot.toString(),
      });

      const requestVoteResult = await signMessageWithWallet(
        connectedWallet,
        canonicalVoteInput,
        signMessagePromisified,
      );

      if (!requestVoteResult.success) {
        eventBus.publish(
          EventName.ShowToast,
          requestVoteResult.error || "Error while signing",
          ToastType.Error,
        );
        return;
      }

      const submitVoteResult = await submitVoteWithDigitalSignature(
        // @ts-ignore
        requestVoteResult.result,
        resolveWalletType(connectedWallet.address),
      );

      // @ts-ignore
      if (submitVoteResult.error && submitVoteResult.message) {
        eventBus.publish(
          EventName.ShowToast,
          // @ts-ignore
          submitVoteResult.message || "Error while voting",
          ToastType.Error,
        );
        return;
      }
      eventBus.publish(EventName.ShowToast, "Vote submitted successfully");

      // @ts-ignore
      dispatch(setVotes([...userVotes, { categoryId, proposalId }]));
      // TODO: refactor
      if (session && !tokenIsExpired(session?.expiresAt)) {
        getVoteReceipts(session?.accessToken).then((receipts) => {
          // @ts-ignore
          if (receipts.error) {
            // @ts-ignore
            eventBus.publish(EventName.ShowToast, r.message, ToastType.Error);
            return;
          }
          // @ts-ignore
          dispatch(setVoteReceipts(receipts));
        });
        submitGetUserVotes(session?.accessToken)
          .then((votes) => {
            if (votes) {
              // @ts-ignore
              dispatch(setVotes(votes));
            }
          })
          .catch((e) => {
            if (process.env.NODE_ENV === "development") {
              console.log(
                `Failed to fetch user votes, ${parseError(e.message)}`,
              );
            }
          });
      } else {
        eventBus.publish(
          EventName.OpenLoginModal,
          "Login to see your vote receipt.",
        );
      }
      setOpenVotingModal(false);
    } catch (e) {
      eventBus.publish(
        EventName.ShowToast,
        // @ts-ignore
        e.message && e.message.length ? parseError(e.message) : "Action failed",
        "error",
      );
    }
  };

  const renderActionButton = () => {
    if (categoryAlreadyVoted && !session) {
      return {
        label: "Sign In",
        action: handleSignIn,
        disabled: false,
      };
    } else if (categoryAlreadyVoted) {
      return {
        label: "View Receipt",
        action: handleViewReceipt,
        disabled: false,
      };
    } else {
      return {
        label: "Vote Now",
        action: submitVote,
        disabled: !walletIdentifierIsVerified && !selectedNominee,
      };
    }
  };

  const optionsForMenu = categoriesData.map((category: Category) => {
    return {
      label: category.name || category.id,
      content: (
        <>
          <Box
            component="div"
            sx={{
              width: "100%",
              marginBottom: "32px",
              display: "flex",
              flexDirection: "column",
              alignItems: "flex-start",
            }}
          >
            <Typography
              // TODO: remove after demo
              variant="h5"
              sx={{ fontWeight: "bold", fontFamily: "Dosis" }}
            >
              {category.name} Nominees ({category.proposals?.length})
            </Typography>
            <Typography
              sx={{
                color: "text.secondary",
                maxWidth: { xs: "70%", md: "80%" },
              }}
            >
              {category.desc}
            </Typography>

            {showEventDate ? ( // If the event has not started or it's just before the reveal
              <Typography // TODO: Formatting
                sx={{
                  color: "text.secondary",
                  maxWidth: { xs: "70%", md: "80%" },
                }}
              >
                {eventCache.notStarted
                  ? "Voting Opens " + formatISODate(eventCache.eventStartDate)
                  : "Results Announced " +
                    formatISODate(eventCache.proposalsRevealDate)}
              </Typography>
            ) : undefined}

            {showVotingButton ? ( // If the event has not started or the results have been revealed we prevent the voting bottom to show up
              <CustomButton
                onClick={() => renderActionButton().action()}
                sx={{
                  mt: -6,
                  alignSelf: "flex-end",
                  display: isTablet ? "none" : "inline-block",
                }}
                colorVariant="primary"
                disabled={renderActionButton().disabled}
              >
                {renderActionButton().label}
              </CustomButton>
            ) : undefined}
          </Box>
          {showWinners ? (
            <Winners
              fadeChecked={fadeChecked}
              nominees={category.proposals}
              handleSelectedNominee={handleSelectNominee}
              selectedNominee={selectedNominee}
              handleOpenLearnMore={handleOpenLearnMoreModal}
            />
          ) : (
            <Nominees
              fadeChecked={fadeChecked}
              nominees={category.proposals}
              categoryAlreadyVoted={categoryAlreadyVoted}
              handleSelectedNominee={handleSelectNominee}
              selectedNominee={selectedNominee}
              handleOpenLearnMore={handleOpenLearnMoreModal}
            />
          )}
        </>
      ),
    };
  });

  const bottom = (
    <>
      {isTablet && (
        <Box
          component="div"
          sx={{
            zIndex: 3,
            position: "fixed",
            left: 0,
            right: 0,
            bottom: 0,
            width: "100%",
            backgroundColor: theme.palette.background.default,
            px: "20px",
            marginBottom: "20x",
            display: "flex",
            justifyContent: "center",
            overflow: "none",
          }}
        >
          <CustomButton
            onClick={() => handleOpenActionButton()}
            sx={{ width: "100%", height: "48px", my: "24px" }}
            colorVariant="primary"
            disabled={!selectedNominee}
          >
            {!showWinners ? <>Vote Now</> : <>View Receipt</>}
          </CustomButton>
        </Box>
      )}
    </>
  );

  return (
    <>
      <PageBase title="Categories">
        <Box
          component="div"
          sx={{
            marginTop: embedded ? "0px" : "60px",
            paddingX: embedded ? "0px" : "16px",
          }}
        >
          <Layout
            title={eventCache?.active ? "Categories" : ""}
            menuOptions={optionsForMenu}
            bottom={bottom}
            mode="change"
            defaultOption={0}
            onSelectMenuOption={(option) => handleClickMenuItem(option)}
          />
        </Box>
        <img
          src={Ellipses}
          style={{
            position: "fixed",
            right: "0",
            top: "70%",
            transform: "translateY(-25%)",
            zIndex: "-1",
            width: "70%",
          }}
        />
        <VoteNowModal
          isOpen={openVotingModal}
          selectedNominee={nomineeToVote}
          onClickVote={() => submitVote()}
          onClose={() => setOpenVotingModal(false)}
        />
        <BioModal
          nominee={bioModalContent}
          isOpen={openLearMoreCategory}
          title={bioModalContent?.name}
          onClose={() => setOpenLearMoreCategory(false)}
        />
        <Drawer
          open={openViewReceipt}
          anchor="right"
          onClose={() => setOpenViewReceipt(false)}
        >
          <ViewReceipt
            categoryId={categoryToRender.id}
            close={() => setOpenViewReceipt(false)}
          />
        </Drawer>
      </PageBase>
    </>
  );
};

export { Categories };
