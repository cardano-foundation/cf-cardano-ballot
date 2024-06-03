import React from "react";
import { Box, IconButton, Paper, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import theme from "../../../common/styles/theme";

interface ViewReceiptProps {}
const ViewReceipt: React.FC<ViewReceiptProps> = () => {
  return (
    <>
        <div style={{
            backgroundColor: theme.palette.background.default,
            height: "100%"
        }}>
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    padding: 1,
                    width: "450px",
                }}
            >
                <Typography
                    variant="h6"
                    sx={{
                        flexGrow: 1,
                        color: "var(--neutralLightest, #FAF9F6)",
                        textAlign: "center",
                        fontFamily: "Dosis",
                        fontSize: "28px",
                        fontWeight: 700,
                        lineHeight: "32px",
                    }}
                >
                    Vote Receipt
                </Typography>
                <IconButton
                    sx={{
                        display: "inline-flex",
                        padding: "12px",
                        borderRadius: "12px",
                        background: "var(--neutralDark, #272727)",
                    }}
                >
                    <CloseIcon sx={{ color: "#fff" }} />
                </IconButton>
            </Box>
            <Box
                sx={{
                    margin: "28px",
                }}
            >
                <Paper
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        alignSelf: "stretch",
                        color: "var(--neutralLight, #D2D2D9)",
                        fontFamily: "Roboto",
                        fontSize: "12px",
                        fontWeight: 500,
                        lineHeight: "20px",
                        padding: 2,
                        gap: 2,
                        borderRadius: "12px",
                        background: "var(--neutralDark, #272727)",
                    }}
                >
                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "space-between",
                            width: "100%",
                            padding: 1,
                        }}
                    >
                        dxas
                    </Box>
                    <Box
                        sx={{
                            alignSelf: "center",
                        }}
                    >
                        <CloseIcon />
                        <Typography>Botón Central</Typography>
                    </Box>
                </Paper>
            </Box>
        </div>

    </>
  );
};

export { ViewReceipt };
