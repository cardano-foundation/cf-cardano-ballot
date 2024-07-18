import React, { useState } from "react";
import {Box, Drawer, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import { PageBase } from "../BasePage";
import { ViewReceipt } from "../Categories/components/ViewReceipt";
import { STATE } from "../Categories/components/ViewReceipt.type";
import theme from "../../common/styles/theme";
import positionIcon from "../../assets/positionIcon.svg";
import nomineeIcon from "../../assets/nomineeIcon.svg";
import votesIcon from "../../assets/votesIcon.svg";

const ReceiptHistory: React.FC = () => {
  const [openViewReceipt, setOpenViewReceipt] = useState(true);

    const handleReceiptClick = (nomineeId: number) => {

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
                    {[1,2,4,5,6].map((nominee, index) => (
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
                                        src={positionIcon}
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
                                    <img
                                        src={nomineeIcon}
                                        alt="Total Votes"
                                        width="24"
                                        height="24"
                                    />
                                    <Typography
                                        onClick={() => handleReceiptClick(nominee)}
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
                                    <img
                                        src={votesIcon}
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
                                    <img
                                        src={votesIcon}
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
                                        c56ec4b8b251...1ba5f097eb71
                                    </Typography>
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
