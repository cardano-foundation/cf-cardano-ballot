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
import { eVoteService } from "../../commons/api/voteService";
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

const Vote = () => {
  const theme = useTheme();
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const [eVoteSign, setEVoteSign] = useState("");
  const [eVoteSignKey, setEVoteSignKey] = useState("");
  const [isSigned, setIsSigned] = useState(false);
  const [optionId, setOptionId] = useState("");


  //TODO:
  //get reference from Endpoint
  //http://localhost:8080/api/reference/event/CIP-1694_Pre_Ratification_9D06

  //TODO:
  //get Voting Power via Endpoint
  //http://localhost:8080/api/account/CIP-1694_Pre_Ratification_9D06/stake_test1urcnqgzt2x8hpsvej4zfudehahknm8lux894pmqwg5qshgcrn346q

  //TODO:
  //get Slot number from network
  //http://localhost:8080/api/blockchain/tip

  //TODO usecases:
  //Voting power is not available - User not staking.. Can not vote. We need to have error popup.
  //Voting can be submitted twice till onchain submission

  //TODO Voter receipt
  //get Voter receipt from Endpoint
  //

  //TODO User not valid after 2 hours



  const canonicalVoteInput = useMemo(
    () =>
      buildCanonicalVoteInputJson({
        option: optionId,
        voter: stakeAddress,
        voteId: uuidv4(),
        //votingPower
      }),
    [isConnected, optionId, stakeAddress]
  );

  const generateCIP8EVoteSignature = async () => {
    if (!isConnected) return;
    await signMessage(canonicalVoteInput, (signature, key) => {
      if (signature && signature.length) {
        setEVoteSign(signature);
        setEVoteSignKey(String(key));
        setIsSigned(true);
      }
    });
  };

  const handleSubmit = async () => {
    await generateCIP8EVoteSignature().then(() => {
      const requestVoteObject = {
        cosePublicKey: eVoteSignKey,
        coseSignature: isConnected && eVoteSign,
      };
      console.log(requestVoteObject.cosePublicKey);
      try {
        eVoteService
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
            <OptionCard items={items} />
          </Grid>

          <Grid item>
            <Button
              size="large"
              variant="contained"
              disabled={isSigned}
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
              Submit Your Vote
            </Button>
          </Grid>
        </Grid>
      </Container>
    </div>
  );
};

export default Vote;
