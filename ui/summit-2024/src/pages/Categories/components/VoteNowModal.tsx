import React from "react";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import Modal from "../../../components/common/Modal/Modal";
import { Box } from "@mui/material";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import { Proposal } from "../../../store/reducers/eventCache/eventCache.types";

interface VoteModalProps {
  isOpen: boolean;
  onClose: () => void;
  selectedNominee?: Proposal;
}
const VoteNowModal: React.FC<VoteModalProps> = ({
  isOpen,
  onClose,
  selectedNominee,
}) => {
  const isMobile = useIsPortrait();
  return (
    <>
      <Modal
        id="vote-now-modal"
        isOpen={isOpen}
        name="vote-now-modal"
        title="Voting now"
        onClose={onClose}
        width={isMobile ? "100%" : "450px"}
      >
        <Box
          component="div"
          sx={{
            width: isMobile ? "100%" : "400px",
            display: "flex",
            justifyContent: "center",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <CustomButton
            colorVariant="primary"
            sx={{
              minWidth: "256px",
              mt: "50px",
              mb: "20px",
            }}
          >
            Vote for {selectedNominee?.id}
          </CustomButton>
        </Box>
      </Modal>
    </>
  );
};

export { VoteNowModal };