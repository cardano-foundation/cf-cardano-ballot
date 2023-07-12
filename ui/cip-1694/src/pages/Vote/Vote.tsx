import React, { useEffect } from "react";
import { useMemo, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import { useTheme } from "@mui/material/styles";
import { Grid, Container, Typography, Button } from "@mui/material";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import DoneIcon from "@mui/icons-material/Done";
import CloseIcon from "@mui/icons-material/Close";
import DoDisturbIcon from "@mui/icons-material/DoDisturb";
import ReceiptIcon from "@mui/icons-material/Receipt";
import toast from "react-hot-toast";
import CountDownTimer from "../../components/CountDownTimer/CountDownTimer";
import OptionCard from "../../components/OptionCard/OptionCard";
import { OptionItem } from "../../components/OptionCard/OptionCard.types";
import SidePage from "../../components/common/SidePage/SidePage";
import { buildCanonicalVoteInputJson } from "../../common/utils/voteUtils";
import { voteService } from "../../common/api/voteService";
import VoteReceipt from "./VoteReceipt";
import "./Vote.scss";
import { EVENT_ID } from "../../common/constants/appConstants";
import { useToggle } from "../../common/hooks/useToggle";
import ConnectWalletModal from "../../components/ConnectWalletModal/ConnectWalletModal";

const items: OptionItem[] = [
  {
    label: "Yes",
    icon: <DoneIcon />,
  },
  {
    label: "No",
    icon: <CloseIcon />,
  },
  {
    label: "Abstain",
    icon: <DoDisturbIcon />,
  },
];

const Vote = () => {
  const theme = useTheme();
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const [isDisabled, setIsDisabled] = useState<boolean>(false);
  const [showVoteReceipt, setShowVoteReceipt] = useState<boolean>(false);
  const [openAuthDialog, setOpenAuthDialog] = useState<boolean>(false);
  const [optionId, setOptionId] = useState("");
  const [absoluteSlot, setAbsoluteSlot] = useState("");
  const [votingPower, setVotingPower] = useState("");
  const [isToggledReceipt, toggleReceipt] = useToggle(false);

  useEffect(() => {
    initialise();
  }, []);

  const initialise = () => {
    optionId === "" && setIsDisabled(true);
    (!isConnected && showVoteReceipt) && setShowVoteReceipt(true);
  };

  const onChangeOption = (option: string) => {
    if (option !== null) {
      setOptionId(option);
      setIsDisabled(false);
    } else {
      setIsDisabled(true);
    }
  };

  const handleCloseAuthDialog = () => {
    setOpenAuthDialog(false);
  };

  const onConnectWallet = () => {
    setOpenAuthDialog(false);
    notify("Wallet Connected!");
  };

  const notify = (message: string) => toast(message);

  const handleSubmit = async () => {
    if (!isConnected) {
      setOpenAuthDialog(true);
    } else if (isConnected) {
      voteService.getSlotNumber().then((response) => {
        const absoluteSlot = response?.absoluteSlot.toString();
        setAbsoluteSlot(absoluteSlot);
        if (isConnected && stakeAddress) {
          voteService
            .getVotingPower(EVENT_ID, stakeAddress)
            .then((response) => {
              const votingPower = response.votingPower;
              if (absoluteSlot !== "" && votingPower !== "") {
                const canonicalVoteInput = buildCanonicalVoteInputJson({
                  option: optionId?.toUpperCase(),
                  voter: stakeAddress,
                  voteId: uuidv4(),
                  slotNumber: absoluteSlot,
                  votePower: votingPower,
                });
                signMessage(canonicalVoteInput, async (signature, key) => {
                  try {
                    const requestVoteObject = {
                      cosePublicKey: key,
                      coseSignature: isConnected && signature,
                    };

                    try {
                      voteService
                        .castAVoteWithDigitalSignature(requestVoteObject)
                        .then((data) => {
                          if (
                            data.status === 400 &&
                            data.title === "INVALID_VOTING_POWER"
                          ) {
                            notify(
                              "To cast a vote, Voting Power should be more than 0"
                            );
                            setOptionId("");
                            setIsDisabled(true);
                          } else if (
                            data.status === 400 &&
                            data.title === "EXPIRED_SLOT"
                          ) {
                            notify("CIP-93's envelope slot is expired!");
                            setOptionId("");
                            setIsDisabled(true);
                          } else if (
                            data.status == 400 &&
                            data.title === "VOTE_CANNOT_BE_CHANGED"
                          ) {
                            notify(
                              "You have already voted! Vote cannot be changed for this stake address"
                            );
                            setOptionId("");
                            setIsDisabled(true);
                          } else {
                            notify("You vote has been successfully submitted!");
                            setOptionId("");
                            setShowVoteReceipt(true);
                          }
                        })
                        .catch((err) => {
                          notify(err);
                        });
                    } catch (e) {
                      console.log(e);
                    }
                  } catch (error) {
                    console.log(error);
                  }
                });
              }
            });
        }
      });
    }
  };

  return (
    <div className="vote">
      <Container>
        <Grid
          container
          direction="column"
          justifyContent="left"
          alignItems="left"
          spacing={5}
        >
          <Grid item>
            <Typography
              variant="h5"
              sx={{
                color: "text.primary",
                textAlign: "left",
                fontWeight: 600,
                fontSize: 28,
              }}
            >
              Do you want CIP-1694 that will allow On-Chain Governance,
              implemented on the Cardano Blockchain?
            </Typography>
          </Grid>
          <Grid item>
            <Typography
              variant="body1"
              sx={{
                color: "text.primary",
                textAlign: "left",
                fontWeight: 400,
              }}
            >
              Time left to vote: <CountDownTimer />
            </Typography>
          </Grid>

          <Grid item>
            <OptionCard
              items={items}
              onChangeOption={onChangeOption}
            />
          </Grid>
          <Grid item>
            <Grid
              container
              direction="row"
              justifyContent={"center"}
            >
              <Grid
                item
                sx={{ m: theme.spacing(2) }}
              >
                {!showVoteReceipt && (
                  <Button
                    size="large"
                    variant="contained"
                    disabled={isDisabled}
                    onClick={() => handleSubmit()}
                    sx={{
                      marginTop: "0px !important",
                      height: { xs: "50px", sm: "60px", lg: "70px" },
                      fontSize: "25px",
                      fontWeight: 700,
                      textTransform: "none",
                      borderRadius: "16px !important",
                      color: "#fff !important",
                      fontFamily: "Roboto Bold",
                      backgroundColor: theme.palette.primary.main,
                    }}
                  >
                    {!isConnected
                      ? "Connect wallet to vote"
                      : "Submit Your Vote"}
                  </Button>
                )}
              </Grid>
              <Grid
                item
                sx={{ m: theme.spacing(2) }}
              >
                {showVoteReceipt && (
                  <Button
                    variant="contained"
                    onClick={() => toggleReceipt(true)}
                    aria-label="Receipt"
                    sx={{
                      marginTop: "0px !important",
                      height: { xs: "50px", sm: "60px", lg: "70px" },
                      fontSize: "25px",
                      fontWeight: 700,
                      textTransform: "none",
                      borderRadius: "16px !important",
                      color: "#fff !important",
                      fontFamily: "Roboto Bold",
                      backgroundColor: theme.palette.primary.main,
                    }}
                    startIcon={<ReceiptIcon />}
                  >
                    View Receipt
                  </Button>
                )}
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Container>
      <SidePage
        anchor="right"
        open={isToggledReceipt}
        setOpen={toggleReceipt}
      >
        <VoteReceipt />
      </SidePage>
      <ConnectWalletModal
        openStatus={openAuthDialog}
        onCloseFn={handleCloseAuthDialog}
        name="connect-wallet-list"
        id="connect-wallet-list"
        title="Choose your preferred wallet to connect"
        action="true"
        buttonLabel="Close"
        onConnectWallet={onConnectWallet}
      />
    </div>
  );
};

export default Vote;
