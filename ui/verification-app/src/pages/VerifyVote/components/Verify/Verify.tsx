import React, { useState, useCallback } from "react";
import toast from "react-hot-toast";
import cn from "classnames";
import BlockIcon from "@mui/icons-material/Block";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import { Button } from "@mui/material";
import { MerkleProof, VoteReceipt } from "types/voting-app-types";
import * as verificationService from "common/api/verificationService";
import { Toast } from "common/components/Toast/Toast";
import { VerifyVoteSection } from "./components/VerifyVoteSection";
import styles from "../../VerifyVote.module.scss";
import { ChoseExplorerSection } from "./components/ChoseExplorerSection";
import { Loader } from "common/components/Loader/Loader";

enum SECTIONS {
  VERIFY = "verify",
  CHOSE_EXPLORER = "chose_explorer",
}

// TODO: move to types file
type voteProof = {
  transactionHash: MerkleProof["transactionHash"];
  steps: MerkleProof["steps"];
  rootHash: MerkleProof["rootHash"];
  coseSignature: VoteReceipt["coseSignature"];
  cosePublicKey: VoteReceipt["cosePublicKey"];
};

const titles = {
  [SECTIONS.VERIFY]: "Verify your vote",
  [SECTIONS.CHOSE_EXPLORER]: "Viewing transaction details",
};

const description = {
  [SECTIONS.VERIFY]:
    'To authenticate your vote, please paste your Vote Proof into the text field below. After this, click on the "Verify" button to complete the verification process.',
  [SECTIONS.CHOSE_EXPLORER]:
    "Where would you like to see your transaction details displayed after your verification has been completed?",
};

enum ERRORS {
  VERIFY = "verify",
  JSON = "json",
  UNSUPPORTED_EVENT = "UNSUPPORTED_EVENT",
}

const errors = {
  [ERRORS.VERIFY]: "Unable to verify vote receipt. Please try again",
  [ERRORS.JSON]: "Invalid JSON. Please try again",
  [ERRORS.UNSUPPORTED_EVENT]: "Unsupported event",
};

const cta = {
  [SECTIONS.VERIFY]: "Verify",
  [SECTIONS.CHOSE_EXPLORER]: "Confirm",
};

type VerifyProps = {
  onConfirm: (explorer: string) => void;
  opened: boolean;
};

export const Verify = ({ opened, onConfirm }: VerifyProps) => {
  const [activeSection, setActiveSection] = useState(SECTIONS.VERIFY);
  const [voteProof, setVoteProof] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const [explorer, setExplorer] = useState<string>("");
  const [txHash, setTxHash] = useState<string>("");

  const handleVerify = useCallback(async () => {
    try {
      const {
        transactionHash,
        rootHash = "",
        steps = [],
        coseSignature,
        cosePublicKey,
      }: voteProof = JSON.parse(voteProof);

      // TODO: validate MerkleProof eg: check if rootHash is string etc. https://stackoverflow.com/questions/63629256/parsing-json-to-interface-in-typescript-and-check-if-it-is-ok
      setIsLoading(true);
      const verified = await verificationService.verifyVote({
        rootHash,
        voteCoseSignature: coseSignature,
        voteCosePublicKey: cosePublicKey,
        steps,
      });
      if ("verified" in verified && typeof verified?.verified === "boolean") {
        setTxHash(transactionHash);
        setActiveSection(SECTIONS.CHOSE_EXPLORER);
      }
    } catch (error) {
      if (process.env.NODE_ENV === "development") {
        console.log("Failed to verify vote", error?.message);
      }

      const message = error?.message?.endsWith("is not valid JSON")
        ? errors[ERRORS.JSON]
        : error?.message === ERRORS.UNSUPPORTED_EVENT
        ? errors[ERRORS.UNSUPPORTED_EVENT]
        : errors[ERRORS.VERIFY];

      toast(
        <Toast
          message={message}
          error
          icon={<BlockIcon style={{ fontSize: "19px", color: "#F5F9FF" }} />}
        />
      );
      setActiveSection(SECTIONS.CHOSE_EXPLORER);
      setIsLoading(false);
    }
  }, [voteProof]);

  const handleNext = useCallback(() => {
    onConfirm(`${explorer}/${txHash}`);
  }, [explorer, onConfirm, txHash]);

  return (
    <Dialog
      disableEscapeKeyDown
      aria-labelledby="dialog-title"
      aria-describedby="dialog-description"
      open={opened}
      maxWidth="xl" // To set width more then 600px
      sx={{ "& .MuiBackdrop-root": { bgcolor: "#F5F9FF" } }}
      PaperProps={{
        sx: {
          width: "750px",
          borderRadius: "16px",
          bgcolor: "#F5F9FF",
          boxShadow: "2px 5px 50px 0px rgba(57, 72, 108, 0.20)",
        },
      }}
    >
      <DialogTitle
        id="dialog-title"
        sx={{
          padding: "50px 50px 20px 50px",
          color: "#061D3C",
          fontSize: 28,
          fontFamily: "Roboto",
          fontWeight: "600",
        }}
      >
        {titles[activeSection]}
      </DialogTitle>

      {/* CONTENT */}

      <DialogContent sx={{ padding: "0px 50px 25px 50px !important" }}>
        <DialogContentText
          id="dialog-description"
          sx={{
            pb: "25px",
            color: "#39486C",
            fontSize: "16px",
            fontFamily: "Roboto",
            fontWeight: "400",
            wordWrap: "break-word",
          }}
        >
          {description[activeSection]}
        </DialogContentText>

        {activeSection === SECTIONS.VERIFY && (
          <VerifyVoteSection
            voteProof={voteProof}
            setVoteProof={setVoteProof}
          />
        )}

        {activeSection === SECTIONS.CHOSE_EXPLORER && (
          <ChoseExplorerSection setExplorer={setExplorer} explorer={explorer} />
        )}
      </DialogContent>

      <DialogActions
        sx={{ padding: "0px 50px 50px 50px !important" }}
        className={styles.actionsArea}
        style={{ justifyContent: "start" }}
      >
        <Button
          variant="contained"
          onClick={
            activeSection === SECTIONS.VERIFY ? handleVerify : handleNext
          }
          className={cn(styles.verifyButton, { [styles.loading]: isLoading })}
          disabled={
            (activeSection === SECTIONS.VERIFY ? !voteProof : !explorer) ||
            isLoading
          }
        >
          {isLoading ? <Loader /> : cta[activeSection]}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
