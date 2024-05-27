import React from "react";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import Modal from "../../../components/common/Modal/Modal";
import { Box, Typography } from "@mui/material";
import XIcon from "../../../assets/x.svg";
import LinkedinIcon from "../../../assets/linkedin.svg";
import theme from "../../../common/styles/theme";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";

const VoteNowModal: React.FC = ({ isOpen, onClose, selectedNominee }) => {
  const isMobile = useIsPortrait();

  return (
    <>
      <Modal
        id="vote-now-modal"
        isOpen={isOpen}
        name="vote-now-modal"
        title="Voting now"
        onClose={onClose}
        width={isMobile ? "auto" : "450px"}
      >
        <CustomButton
          colorVariant="primary"
          sx={{
            width: "100%",
              mt: "50px",
              mb: "20px"
          }}
        >
          Vote for {selectedNominee.name}
        </CustomButton>
      </Modal>
    </>
  );
};

export { VoteNowModal };
