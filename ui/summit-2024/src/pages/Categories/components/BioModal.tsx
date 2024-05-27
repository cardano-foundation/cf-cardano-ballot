import React  from "react";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import Modal from "../../../components/common/Modal/Modal";
import { Box, Typography } from "@mui/material";
import XIcon from "../../../assets/x.svg";
import LinkedinIcon from "../../../assets/linkedin.svg";
import theme from "../../../common/styles/theme";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";

const BioModal: React.FC = ({ isOpen, title, onClose }) => {
  const isMobile = useIsPortrait();

  return (
    <>
      <Modal
        id="connect-wallet-modal"
        isOpen={isOpen}
        name="connect-wallet-modal"
        title={title}
        leftTitle
        onClose={onClose}
        width={isMobile ? "auto" : "450px"}
      >
        <Typography
          variant="subtitle1"
          color={theme.palette.text.neutralLightest}
        >
          Cardano Foundation
        </Typography>
        <Box sx={{ display: "flex", alignItems: "center", my: "24px" }}>
          <Box
            sx={{
              mr: "12px",
              padding: "16px",
              border: `1px solid ${theme.palette.text.neutralLightest}`,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: "8px",
              borderRadius: "12px",
              cursor: "pointer",
            }}
          >
            <img src={XIcon} alt="X Icon" />
          </Box>
          <Box
            sx={{
              padding: "16px",
              border: `1px solid ${theme.palette.text.neutralLightest}`,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: "8px",
              borderRadius: "12px",
              cursor: "pointer",
            }}
          >
            <img src={LinkedinIcon} alt="LinkedIn Icon" />
          </Box>
        </Box>

        <Typography variant="body2" sx={{ my: 2 }} >
          Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
          eiusmod tempor incididunt ut labore et dolore magna aliqua. Habitant
          morbi tristique senectus et netus. In massa tempor nec feugiat nisl
          pretium fusce id. Scelerisque felis imperdiet proin fermentum leo vel
          orci. Tortor condimentum lacinia quis vel eros donec ac. Malesuada
          bibendum arcu vitae elementum curabitur vitae nunc sed velit. Nunc
          aliquet bibendum enim facilisis gravida neque convallis a. Egestas
          pretium aenean pharetra magna ac placerat vestibulum. Volutpat
          maecenas volutpat blandit aliquam etiam.
        </Typography>
        <CustomButton
          colorVariant="secondary"
          sx={{
            width: "100%",
          }}
        >
          Visit Website
        </CustomButton>
      </Modal>
    </>
  );
};

export { BioModal };
