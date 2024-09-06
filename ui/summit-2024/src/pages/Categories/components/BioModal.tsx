import React from "react";
import Modal from "../../../components/common/Modal/Modal";
import {Box, useMediaQuery} from "@mui/material";
import XIcon from "../../../assets/x.svg";
import LinkedinIcon from "../../../assets/linkedin.svg";
import theme from "../../../common/styles/theme";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";

interface BioModalProps {
  nominee: any;
  isOpen: boolean;
  title: string | null| undefined;
  onClose: () => void;
}

const BioModal: React.FC<BioModalProps> = ({ nominee, isOpen, title, onClose }) => {
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <>
      <Modal
        id="connect-wallet-modal"
        isOpen={isOpen}
        name="connect-wallet-modal"
        title={title ? title : ""}
        leftTitle
        onClose={onClose}
        width={isMobile ? "auto" : "450px"}
      >
        <Box
          component="div"
          sx={{ display: "flex", alignItems: "center", my: "24px" }}
        >
            {nominee?.x ? <Box
                component="div"
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
                onClick={() => window.open(nominee.x, '_blank')}
            >
                <img src={XIcon} alt="X Icon" />
            </Box> : null}
            {nominee?.linkedin ? <Box
                component="div"
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
                onClick={() => window.open(nominee.linkedin, '_blank')}
            >
                <img src={LinkedinIcon} alt="X Icon" />
            </Box> : null}
        </Box>
          {
              nominee?.url ? <CustomButton
                  colorVariant="secondary"
                  sx={{
                      width: "100%",
                  }}
                  onClick={() => window.open(nominee.url, '_blank')}
              >
                  Visit Website
              </CustomButton> : null
          }

      </Modal>
    </>
  );
};

export { BioModal };
