import Layout from "../../components/Layout/Layout";
import { Typography, Grid } from "@mui/material";
import { UserGuideCard } from "./components/UserGuideCard";
import { userGuideMenu } from "../../__fixtures__/userGuide";
import theme from "../../common/styles/theme";
import { PageBase } from "../BasePage";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import { ROUTES } from "../../routes";
import { useNavigate } from "react-router-dom";
import SupportedWalletsList from "./components/SupportedWalletList";

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
              color: theme.palette.text.primary,
              fontFamily: "Tomorrow",
              fontSize: "32px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "32px",
              marginBottom: "24px",
            }}
          >
            {userGuideMenu[0].title}
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <UserGuideCard
                number={userGuideMenu[0].sections[0].number}
                title={userGuideMenu[0].sections[0].title}
                description={userGuideMenu[0].sections[0].description}
                link={userGuideMenu[0].sections[0].link}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <UserGuideCard
                number={userGuideMenu[0].sections[1].number}
                title={userGuideMenu[0].sections[1].title}
                description={userGuideMenu[0].sections[1].description}
                link={userGuideMenu[0].sections[1].link}
              />
            </Grid>
          </Grid>
        </>
      ),
    },
    {
      label: userGuideMenu[1].label,
      content: (
        <>
          <Typography
            sx={{
              marginTop: "80px",
              color: theme.palette.text.primary,
              fontFamily: "Tomorrow",
              fontSize: "32px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "32px",
              marginBottom: "24px",
            }}
          >
            {userGuideMenu[1].title}
          </Typography>
          <Grid container spacing={3}>
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
      ),
    },
    {
      label: userGuideMenu[2].label,
      content: (
        <>
          <Typography
            sx={{
              marginTop: "80px",
              color: theme.palette.text.primary,
              fontFamily: "Tomorrow",
              fontSize: "32px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "32px",
              marginBottom: "24px",
            }}
          >
            {userGuideMenu[2].title}
          </Typography>
          <Grid container spacing={3}>
            {userGuideMenu[2].sections.map((section) => {
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
      ),
    },
  ];

  const bottom = (
    <>
      <Grid
        container
        spacing={2}
        justifyContent="center"
        sx={{
          marginTop: "8px",
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
  );
  return (
    <>
      <PageBase title="User Guide">
        <Layout title="Get Voting" optionsTitle="User Guide" menuOptions={optionsForScroll} bottom={bottom} mode="scroll" />
      </PageBase>
      <SupportedWalletsList />
    </>
  );
};

export { UserGuide };
