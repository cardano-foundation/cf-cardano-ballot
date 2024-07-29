import Layout from "../../components/Layout/Layout";
import { Typography, Grid } from "@mui/material";
import { UserGuideCard } from "./components/UserGuideCard";
import { userGuideMenu } from "../../__fixtures__/userGuide";
import theme from "../../common/styles/theme";
import {PageBase} from "../BasePage";
import {CustomButton} from "../../components/common/CustomButton/CustomButton";
import {ROUTES} from "../../routes";
import {useNavigate} from "react-router-dom";

const UserGuide = () => {
    const navigate = useNavigate();

    const handleNavigate = (pathname: string) => {
        navigate(pathname);
    };

    const optionsForScroll = [
        {
            label: userGuideMenu[0].label,
            content: (
                <>
                    <Typography
                        sx={{
                            marginTop: "24px",
                            color: theme.palette.text.neutralLightest,
                            fontFamily: "Dosis",
                            fontSize: "24px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: "28px",
                            marginBottom: "16px",
                        }}
                    >
                        {userGuideMenu[0].title}
                    </Typography>
                    <Grid container spacing={2}>
                        {userGuideMenu[0].sections.map((section) => {
                            return (
                                <Grid item xs={12} sm={6}>
                                    <UserGuideCard
                                        number={section.number}
                                        title={section.title}
                                        description={section.description}
                                        link={section.link}
                                    />
                                </Grid>
                            );
                        })}
                    </Grid>
                </>
            )
        },
        {
            label: userGuideMenu[1].label,
            content: (
                <>
                    <Typography
                        sx={{
                            marginTop: "24px",
                            color: theme.palette.text.neutralLightest,
                            fontFamily: "Dosis",
                            fontSize: "24px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: "28px",
                            marginBottom: "16px",
                        }}
                    >
                        {userGuideMenu[1].title}
                    </Typography>
                    <Grid container spacing={2}>
                        {userGuideMenu[1].sections.map((section) => {
                            return (
                                <Grid item xs={12} sm={6}>
                                    <UserGuideCard
                                        number={section.number}
                                        title={section.title}
                                        description={section.description}
                                        link={section.link}
                                    />
                                </Grid>
                            );
                        })}
                    </Grid>
                </>
            )
        },
        {
            label: userGuideMenu[2].label,
            content: (
                <>
                    <Typography
                        sx={{
                            marginTop: "24px",
                            color: theme.palette.text.neutralLightest,
                            fontFamily: "Dosis",
                            fontSize: "24px",
                            fontStyle: "normal",
                            fontWeight: 700,
                            lineHeight: "28px",
                            marginBottom: "16px",
                        }}
                    >
                        {userGuideMenu[2].title}
                    </Typography>
                    <Grid container spacing={2}>
                        {userGuideMenu[2].sections.map((section) => {
                            return (
                                <Grid item xs={12} sm={6} md={4}>
                                    <UserGuideCard
                                        number={section.number}
                                        title={section.title}
                                        description={section.description}
                                        link={section.link}
                                    />
                                </Grid>
                            );
                        })}
                    </Grid>
                </>
            )
        }
    ];

    const bottom = (
        <>
            <Grid
                container
                spacing={2}
                justifyContent="center"
                sx={{
                    marginTop: "24px",
                }}
            >
                <Grid
                    item
                    sx={{
                        width: {
                            xs: "100%",
                            md: "auto",
                        },
                    }}
                >
                    <CustomButton
                        onClick={() => handleNavigate(ROUTES.CATEGORIES)}
                        colorVariant="primary"
                        sx={{
                            width: {
                                xs: "100%",
                                md: "auto",
                            },
                        }}
                    >
                        Vote Now
                    </CustomButton>
                </Grid>
            </Grid>
        </>
    )
    return (
        <>
            <PageBase title="User Guide">
                <Layout menuOptions={optionsForScroll} title="User Guide" bottom={bottom} mode="scroll" />
            </PageBase>
        </>
    );
};

export default UserGuide;
