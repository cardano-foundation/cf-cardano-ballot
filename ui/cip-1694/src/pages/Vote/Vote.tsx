import React, { useEffect } from "react";
import { useMemo, useState } from "react";
import { VoteProps } from "./Vote.types";
import { v4 as uuidv4 } from "uuid";
import { useTheme } from "@mui/material/styles";
import { Grid, Container, Typography, Button } from "@mui/material";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import DoneIcon from "@mui/icons-material/Done";
import CloseIcon from "@mui/icons-material/Close";
import AccountBalanceWalletIcon from "@mui/icons-material/AccountBalanceWallet";
import DoDisturbIcon from "@mui/icons-material/DoDisturb";
import toast from "react-hot-toast";
import CountDownTimer from "../../components/CountDownTimer/CountDownTimer";
import OptionCard from "../../components/OptionCard/OptionCard";
import { OptionItem } from "../../components/OptionCard/OptionCard.types";
import { buildCanonicalVoteInputJson } from "../../commons/utils/voteUtils";
import { voteService } from "../../commons/api/voteService";
import "./Vote.scss";
import { EVENT_ID } from "../../commons/constants/appConstants";

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

interface SignedMessage {
  signature: string;
  key: string;
}

const Vote = () => {
  const theme = useTheme();
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const [isDisabled, setIsDisabled] = useState<boolean>(false);
  const [optionId, setOptionId] = useState("");
  const [absoluteSlot, setAbsoluteSlot] = useState("");
  const [votingPower, setVotingPower] = useState("");

  //TODO usecases:
  //Voting power is not available - User not staking.. Can not vote. We need to have error popup.
  //Voting can be submitted twice till onchain submission
  //get Voter receipt from Endpoint
  //User not valid after 2 hours

  useEffect(() => {
    initialise();
  }, []);

  const initialise = () => {
    (!isConnected || optionId === "") && setIsDisabled(true);
  };

  const onChangeOption = (option: string) => {
    if (option !== null) {
      setOptionId(option);
      setIsDisabled(false);
    } else {
      setIsDisabled(true);
    }
  };

  const canonicalVoteInput = useMemo(
    () =>
      buildCanonicalVoteInputJson({
        option: optionId?.toUpperCase(),
        voter: stakeAddress,
        voteId: uuidv4(),
        slotNumber: absoluteSlot,
        votePower: votingPower,
      }),
    [isConnected, optionId, stakeAddress, absoluteSlot, votingPower]
  );
  
  const notify = (message: string) => toast(message);

  const handleSubmit = async () => {
    if (!isConnected) {
      notify("Connect your wallet to vote");
    } else if (isConnected) {
      await voteService.getSlotNumber().then((response) => {
        setAbsoluteSlot(response?.absoluteSlot || null);
        if (isConnected && stakeAddress) {
          voteService.getVotingPower(EVENT_ID, stakeAddress)
            .then((response) => {
              const votingPower = response.votingPower;
              setVotingPower(votingPower || null);
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
                        } else if (
                          data.status === 400 &&
                          data.title === "EXPIRED_SLOT"
                        ) {
                          notify("CIP-93's envelope slot is expired!");
                        } else {
                          notify("You vote has been successfully submitted!");
                          setOptionId("");
                          setIsDisabled(true);
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
            });
        }
      });
    }
  };

  const signObject = isConnected ? JSON.parse(canonicalVoteInput) : null;

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
              {!isConnected ? "Connect wallet to vote" : "Submit Your Vote"}
            </Button>
          </Grid>
        </Grid>
      </Container>
    </div>
  );
};

export default Vote;
