import React, { useEffect } from "react";
import { useMemo, useState } from "react";
import { VoteProps } from "./Vote.types";
import { v4 as uuidv4 } from "uuid";
import { useTheme } from "@mui/material/styles";
import { Grid, Container, Typography, Button } from "@mui/material";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import DoneIcon from "@mui/icons-material/Done";
import CloseIcon from "@mui/icons-material/Close";
import DoDisturbIcon from "@mui/icons-material/DoDisturb";
import CountDownTimer from "../../components/CountDownTimer/CountDownTimer";
import OptionCard from "../../components/OptionCard/OptionCard";
import { OptionItem } from "../../components/OptionCard/OptionCard.types";
import { JsonViewer } from "@textea/json-viewer";
import { buildCanonicalVoteInputJson } from "../../commons/utils/voteUtils";
import { voteService } from "../../commons/api/voteService";
import "./Vote.scss";

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
  signature: string,
  key: string
}

const Vote = () => {
  const theme = useTheme();
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const [isSigned, setIsSigned] = useState<boolean>(false);
  const [optionId, setOptionId] = useState("");
  const [absoluteSlot, setAbsoluteSlot] = useState("");
  const [votingPower, setVotingPower] = useState("");

  //TODO usecases:
  //Voting power is not available - User not staking.. Can not vote. We need to have error popup.
  //Voting can be submitted twice till onchain submission
  //get Voter receipt from Endpoint
  //User not valid after 2 hours

  useEffect(() => {
    slotFromTimestamp();
    votingPowerOfUser();
  }, []);

  const slotFromTimestamp = async () => {
    const response = await voteService.getSlotNumber();
    setAbsoluteSlot(response?.absoluteSlot || null);
  };

  const votingPowerOfUser = async () => {
    const response = await voteService.getVotingPower();
    setVotingPower(response.votingPower || null);
  };

  const onChangeOption = (option: string) => {
    setOptionId(option);
  };

  const canonicalVoteInput = useMemo(
    () =>
      buildCanonicalVoteInputJson({
        option: optionId,
        voter: stakeAddress,
        voteId: uuidv4(),
        slotNumber: absoluteSlot,
        votePower: votingPower,
      }),
    [isConnected, optionId, stakeAddress, absoluteSlot, votingPower]
  );

  const handleSubmit = async () => {
    if (!isConnected && votingPower === null) return;
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
              if (data.error && data.error.length) {
                console.log(data.error);
              } else {
                console.log(data);
              }
            })
            .catch((err) => {
              console.log(err);
            });
        } catch (e) {
          console.log(e);
        }
      } catch (error) {
        //todo error log
    }

  });
}

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
            <OptionCard items={items} onChangeOption={onChangeOption}/>
          </Grid>

          <Grid item>
            <Button
              size="large"
              variant="contained"
              disabled={!isConnected || votingPower === null || optionId === ""}
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
              {!isConnected ? 'Connect wallet to vote' : 'Submit Your Vote'}
            </Button>
          </Grid>
        </Grid>
      </Container>
    </div>
  );
};

export default Vote;
