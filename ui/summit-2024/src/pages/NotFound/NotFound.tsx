import React from "react";
import { Box, Typography, useMediaQuery, useTheme } from "@mui/material";
import { PageBase } from "../BasePage";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import theme from "../../common/styles/theme";
import notFoundBg from "../../assets/bg/notFoundBg.svg";
import ArchBg from "../../assets/bg/archBg.png";

const NotFound: React.FC = () => {
    const currentTheme = useTheme();
    const isMobile = useMediaQuery(currentTheme.breakpoints.down("sm"));
    const isTablet = useMediaQuery(currentTheme.breakpoints.between('sm', 'md'));

    return (
        <PageBase title="NotFound">
            <Box
                component="div"
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    marginBottom: "180px",
                    position: "relative",
                    paddingX: "20px"
                }}
            >
                <img
                    src={ArchBg}
                    alt="Background"
                    style={{
                        maxWidth: isMobile ? '100%' : '604px',
                        objectFit: 'contain',
                        marginTop: isMobile ? "60px" : "120px"
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
                        marginTop: isMobile ? 55 : 65,
                    }}
                >
                    <Typography
                        sx={{
                            color: theme.palette.text.neutralLightest,
                            textAlign: "center",
                            textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                            fontFamily: "Dosis",
                            fontSize: isMobile ? "44px" : "88px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: isMobile ? "44px" : "88px",
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
                            fontSize: isMobile ? "44px" : "88px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: isMobile ? "44px" : "88px",
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
                            marginLeft: isMobile ? "0px" : "12px"
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
                    top: isMobile ? "90%": "70%",
                    transform: "translateY(-40%)",
                    zIndex: "-1",
                    width: isTablet ? "50%" : "70%",
                    height: "auto",
                }}
            />
        </PageBase>
    );
};

export { NotFound };
