import React, { useEffect, useState } from "react";
import {
    Box,
    Grid,
    Paper,
    Typography,
    Button,
    List,
    ListItem,
    Fade,
} from "@mui/material";
import theme from "../../common/styles/theme";
import {
    NomineeArrayFixture,
    NomineeFixture,
    userGuideData,
} from "../../__fixtures__/categories";
import HoverCircle from "../../components/common/HoverCircle/HoverCircle";
import Ellipses from "../../assets/ellipse.svg";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";

const UserGuide: React.FC = () => {
    const categoriesData = userGuideData;
    const [selectedCategory, setSelectedCategory] = useState(
        categoriesData[0].category,
    );
    const [selectedNominee, setSelectedNominee] = useState<number | undefined>(0);
    const [learMoreCategory, setLearMoreCategory] = useState("");
    const [openLearMoreCategory, setOpenLearMoreCategory] = useState(false);
    const [openVotingModal, setOpenVotingModal] = useState(false);

    const [fadeChecked, setFadeChecked] = useState(true);

    const isMobile = useIsPortrait();

    useEffect(() => {
        if (fadeChecked) {
            setSelectedCategory(selectedCategory);
        }
    }, [fadeChecked, selectedCategory]);

    const handleClickMenuItem = (category) => {
        if (category !== selectedCategory) {
            setFadeChecked(false);
            setTimeout(() => {
                setSelectedCategory(category);
                setFadeChecked(true);
            }, 200);
        }
    };

    const handleSelectNominee = (id: number) => {
        if (selectedNominee !== id) {
            setSelectedNominee(id);
        } else {
            setSelectedNominee(-1);
        }
    };

    const handleLearnMoreClick = (event, category) => {
        event.stopPropagation();
        setLearMoreCategory(category);
        setOpenLearMoreCategory(true);
    };



    return (
        <Box sx={{ width: "100%" }}>
            <Grid container>
                <Grid
                    item
                    xs={12}
                    md={2.4}
                    lg={2}
                    sx={{
                        position: isMobile ? "" : "sticky",
                        top: 0,
                        height: isMobile ? "" : "100%",
                        overflow: "auto",
                    }}
                >
                    {isMobile ? (
                        <>
                            <Box
                                sx={{
                                    overflowX: "auto",
                                    width: "100%",
                                    maxWidth: "100vw",
                                    "&::-webkit-scrollbar": {
                                        display: "none",
                                    },
                                    scrollbarWidth: "none",
                                    msOverflowStyle: "none",
                                    marginTop: "14px",
                                }}
                            >
                                <List
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                        padding: 0,
                                        margin: 0,
                                    }}
                                >
                                    {categoriesData.map((category, index) => (
                                        <ListItem
                                            onClick={() => handleClickMenuItem(category.category)}
                                            key={index}
                                            sx={{
                                                display: "flex",
                                                marginRight: "8px",
                                                whiteSpace: "nowrap",
                                            }}
                                        >
                                            <Typography
                                                sx={{
                                                    color:
                                                        category.category === selectedCategory
                                                            ? theme.palette.background.default
                                                            : theme.palette.text.neutralLightest,
                                                    background:
                                                        category.category === selectedCategory
                                                            ? theme.palette.secondary.main
                                                            : "none",
                                                    padding: "8px 12px",
                                                    borderRadius: "12px",
                                                    fontSize: "16px",
                                                    fontWeight: 500,
                                                    lineHeight: "24px",
                                                    cursor: "pointer",
                                                }}
                                            >
                                                {category.category}
                                            </Typography>
                                        </ListItem>
                                    ))}
                                </List>
                            </Box>
                        </>
                    ) : (
                        <>
                            <List
                                sx={{
                                    borderRight: "1px solid #737380",
                                }}
                            >
                                {categoriesData.map((category: NomineeArrayFixture, index) => (
                                    <ListItem
                                        onClick={() => handleClickMenuItem(category.category)}
                                        key={index}
                                    >
                                        {category.category === selectedCategory ? (
                                            <>
                                                <Box
                                                    sx={{
                                                        display: "flex",
                                                        padding: "8px 12px",
                                                        alignItems: "center",
                                                        gap: "10px",
                                                        alignSelf: "stretch",
                                                        borderRadius: "12px",
                                                        background: theme.palette.secondary.main,
                                                        color: theme.palette.background.default,
                                                        fontSize: "16px",
                                                        fontStyle: "normal",
                                                        fontWeight: 500,
                                                        lineHeight: "24px",
                                                        cursor: "pointer",
                                                        width: "100%",
                                                    }}
                                                >
                                                    <Typography
                                                        sx={{
                                                            gap: "10px",
                                                            alignSelf: "stretch",
                                                            borderRadius: "12px",
                                                            fontSize: "16px",
                                                            fontStyle: "normal",
                                                            fontWeight: 500,
                                                            lineHeight: "24px",
                                                            cursor: "pointer",
                                                            width: "100%",
                                                        }}
                                                    >
                                                        {category.category}
                                                    </Typography>
                                                </Box>
                                            </>
                                        ) : (
                                            <>
                                                <Typography
                                                    sx={{
                                                        color: theme.palette.text.neutralLightest,
                                                        fontSize: "16px",
                                                        fontStyle: "normal",
                                                        fontWeight: 500,
                                                        lineHeight: "24px",
                                                        cursor: "pointer",
                                                    }}
                                                >
                                                    {category.category}
                                                </Typography>
                                            </>
                                        )}
                                    </ListItem>
                                ))}
                            </List>
                        </>
                    )}
                </Grid>
                <Grid
                    item
                    xs={12}
                    md={9.6}
                    lg={10}
                    sx={{
                        p: theme.spacing(2),
                        background: "transparent",
                        paddingLeft: isMobile ? "" : "40px",
                    }}
                >
                    hey
                </Grid>
            </Grid>
            <img
                src={Ellipses}
                style={{
                    position: "fixed",
                    right: "0",
                    top: "70%",
                    transform: "translateY(-40%)",
                    zIndex: "-1",
                    width: "70%",
                }}
            />
        </Box>
    );
};

export { UserGuide };
