import React from "react";
import ArchBg from "../../assets/bg/archBg.png";
import { Box, Typography, useMediaQuery } from "@mui/material";
import { PageBase } from "../BasePage";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import theme from "../../common/styles/theme";
import notFoundBg from "../../assets/bg/notFoundBg.svg";

const NotFound: React.FC = () => {
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));


    return (
        <PageBase title="Categories">
            <Box
                component="div"
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    marginBottom: "180px",
                    position: "relative",
                }}
            >
                <img
                    src={ArchBg}
                    alt="Background"
                    style={{
                        maxWidth: '604px', // No excede de 604px
                        objectFit: 'contain',
                        marginTop: "120px"
                    }}
                />
                <Box
                    component="div"
                    sx={{
                        position: 'absolute',
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "center",
                        alignItems: "center",
                        marginTop: 65,
                    }}
                >
                    <Typography
                        sx={{
                            color: theme.palette.text.neutralLightest,
                            textAlign: "center",
                            textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                            fontFamily: "Dosis",
                            fontSize: "88px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: "88px",
                        }}
                    >
                        404
                    </Typography>
                    <Typography
                        sx={{
                            color: theme.palette.text.neutralLightest,
                            textAlign: "center",
                            textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                            fontFamily: "Dosis",
                            fontSize: "88px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: "88px",
                        }}
                    >
                        Page Not Found
                    </Typography>
                    <Box
                        component="div"
                        sx={{
                            width: "100%",
                            display: "flex",
                            flexDirection: isMobile ? "column" : "row",
                            justifyContent: "center",
                            gap: 1,
                            marginTop: "48px"
                        }}
                    >
                        <CustomButton colorVariant="primary">Start Voting</CustomButton>
                        <CustomButton colorVariant="secondary" sx={{
                            marginLeft: "12px"
                        }}>Back to Home</CustomButton>
                    </Box>
                </Box>

            </Box>
            <Box
                component="div"
                sx={{
                height: "96px"
            }}/>
            <img
                src={notFoundBg}
                style={{
                    position: "fixed",
                    right: "0",
                    top: "60%",
                    transform: "translateY(-40%)",
                    zIndex: "-1",
                    width: "70%",
                    height: isMobile ? "auto" : "auto",
                }}
            />
        </PageBase>
    );
};

export { NotFound };
