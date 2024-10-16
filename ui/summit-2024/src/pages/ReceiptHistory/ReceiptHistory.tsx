import React, { useState } from "react";
import {
  Box,
  Drawer,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { PageBase } from "../BasePage";
import { ViewReceipt } from "../Categories/components/ViewReceipt";
import { STATE } from "../Categories/components/ViewReceipt.type";
import theme from "../../common/styles/theme";
import nomineeIcon from "../../assets/nomineeIcon.svg";
import rightArrowIcon from "../../assets/rightArrowIcon.svg";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import { copyToClipboard } from "../../utils/utils";
import { eventBus, EventName } from "../../utils/EventBus";

const ReceiptHistory: React.FC = () => {
  const [openViewReceipt, setOpenViewReceipt] = useState(false);
  const [copied, setCopied] = React.useState(false);

  const handleReceiptClick = () => {
    setOpenViewReceipt(true);
  };

  const handleCopy = async (transactionId: string) => {
    await copyToClipboard(transactionId);
    setCopied(true);
    eventBus.publish(EventName.ShowToast, "Copied to clipboard successfully");
    setTimeout(() => setCopied(false), 1000);
  };

  const ReceiptsList = () => (
    <TableContainer>
      <Table sx={{ borderCollapse: "separate", borderSpacing: "0 4px" }}>
        <TableHead sx={{ background: "transparent" }}>
          <TableRow>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
              }}
            >
              Voted For
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
              }}
            >
              Category Name
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
              }}
            >
              Voted Timestamp
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
              }}
            >
              Transaction Hash
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {[1, 2, 4, 5, 6, 7, 8, 9, 10].map((_: number, index) => (
            <TableRow
              key={index}
              sx={{
                borderRadius: "8px",
                overflow: "hidden",
                height: "72px",
              }}
            >
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  border: "none",
                  borderTopLeftRadius: "20px",
                  borderBottomLeftRadius: "20px",
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontFamily: "Dosis",
                  fontSize: "20px",
                  fontStyle: "normal",
                  fontWeight: 700,
                  lineHeight: "24px",
                  padding: "28px 24px",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                  }}
                >
                  <img
                    src={nomineeIcon}
                    alt="Total Votes"
                    width="24"
                    height="24"
                  />
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                    }}
                  >
                    Plutus Bear Pop-Tart
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  border: "none",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                  }}
                >
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                      cursor: "pointer",
                    }}
                  >
                    Ambassador
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  border: "none",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                  }}
                >
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                    }}
                  >
                    09/17/2024 15:18:34
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  border: "none",
                  borderTopRightRadius: "20px",
                  borderBottomRightRadius: "20px",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                  }}
                >
                  {copied ? (
                    <CheckCircleOutlineIcon
                      sx={{
                        width: "20px",
                        height: "20px",
                      }}
                    />
                  ) : (
                    <ContentCopyIcon
                      onClick={() => handleCopy("c56ec4b8b251...1ba5f097eb71")}
                      sx={{
                        width: "20px",
                        height: "20px",
                        cursor: "pointer",
                      }}
                    />
                  )}
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                    }}
                  >
                    c56ec4b8b251...1ba5f097eb71
                  </Typography>
                  <Box
                    component="div"
                    sx={{
                      marginLeft: "auto",
                      marginRight: "28px",
                    }}
                  >
                    <img
                      onClick={() => handleReceiptClick()}
                      src={rightArrowIcon}
                      alt="Total Votes"
                      width="24"
                      height="24"
                      style={{
                        cursor: "pointer",
                      }}
                    />
                  </Box>
                </Box>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );

  return (
    <>
      <PageBase title="Categories">
        <Box
          component="div"
          sx={{
            height: "28px",
          }}
        />
        <Typography
          sx={{
            color: theme.palette.text.neutralLightest,
            fontSize: "32px",
            fontStyle: "normal",
            fontWeight: 700,
            fontFamily: "Dosis",
            lineHeight: "36px",
            marginTop: "60px",
            marginBottom: "32px",
          }}
        >
          Vote Receipt History
        </Typography>
        <ReceiptsList />
        <Drawer
          open={openViewReceipt}
          anchor="right"
          onClose={() => setOpenViewReceipt(false)}
        >
          <ViewReceipt
            state={STATE.ROLLBACK}
            close={() => setOpenViewReceipt(false)}
          />
        </Drawer>
      </PageBase>
    </>
  );
};

export { ReceiptHistory };
