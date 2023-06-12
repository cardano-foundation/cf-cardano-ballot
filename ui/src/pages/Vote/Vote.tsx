import { useMemo, useState } from "react";
import { VoteProps } from "./Vote.types";
import { useTheme } from "@mui/material/styles";
import { Grid, Container, Typography, Button } from "@mui/material";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import DoneIcon from "@mui/icons-material/Done";
import CloseIcon from "@mui/icons-material/Close";
import DoDisturbIcon from "@mui/icons-material/DoDisturb";
import CountDownTimer from "../../components/CountDownTimer/CountDownTimer";
import OptionCard from "../../components/OptionCard/OptionCard";
import { OptionItem } from "../../components/OptionCard/OptionCard.types";
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
  const [isSigned, setIsSigned] = useState(false);
  const handleSubmit = () => {
    const requestVoteObject = {
      // payload object
      voter: stakeAddress
    };

    try {
        // vote submission
        console.log(requestVoteObject.voter);
    } catch (e) {
      console.log(e);
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
            <OptionCard items={items} />
          </Grid>

          <Grid item>
            <Button
              size="large"
              variant="contained"
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
