import React, { useEffect, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  List,
  ListItem,
  Fade,
  useMediaQuery,
  Drawer,
} from "@mui/material";
import theme from "../../common/styles/theme";
import {
  NomineeArrayFixture,
  nomineesData,
} from "../../__fixtures__/categories";
import Ellipses from "../../assets/ellipse.svg";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import { BioModal } from "./components/BioModal";
import { VoteNowModal } from "./components/VoteNowModal";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { NomineeCard } from "./components/NomineeCard";
import { ViewReceipt } from "./components/ViewReceipt";

const Categories: React.FC = () => {
  const isTablet = useMediaQuery(theme.breakpoints.down("md"));
  const categoriesData = nomineesData;
  const [selectedCategory, setSelectedCategory] = useState(
    categoriesData[0].category,
  );
  const [selectedNominee, setSelectedNominee] = useState<number | undefined>(0);
  const [learMoreCategory, setLearMoreCategory] = useState("");
  const [openLearMoreCategory, setOpenLearMoreCategory] = useState(false);
  const [openVotingModal, setOpenVotingModal] = useState(false);
  const [openViewReceipt, setOpenViewReceipt] = useState(true);

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

  const handleClickActionButton = () => {};

  const handleViewReceipt = () => {};

  const handleLearnMoreClick = (event, category) => {
    event.stopPropagation();
    setLearMoreCategory(category);
    setOpenLearMoreCategory(true);
  };

  let categoryToRender = categoriesData.find(
    (c) => c.category === selectedCategory,
  );
  if (!categoryToRender) {
    categoryToRender = categoriesData[0];
  }

  const nomineeToVote = categoryToRender.nominees?.find(
    (n) => n.id === selectedNominee,
  );

  return (
    <>
      <Box
        sx={{
          height: "28px",
        }}
      />
      <Box sx={{ width: "100%" }}>
        <Grid container>
          <Grid item xs={12} md={2.4} lg={2}>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                fontFamily: "Dosis",
                fontSize: "32px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "36px",
                marginTop: "20px",
                borderRight: "1px solid #737380",
                paddingLeft: "16px",
              }}
            >
              Categories ({categoriesData.length})
            </Typography>
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
                    position: "sticky",
                    top: 74,
                    zIndex: 1100,
                    overflowY: "auto",
                    maxHeight: "calc(100vh - 74px)",
                    borderRight: "1px solid #737380",
                  }}
                >
                  {categoriesData.map(
                    (category: NomineeArrayFixture, index) => (
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
                    ),
                  )}
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
            <Box
              sx={{
                width: "100%",
                marginBottom: "32px",
                display: "flex",
                flexDirection: "column",
                alignItems: "flex-start",
              }}
            >
              <Typography
                variant="h5"
                sx={{ fontWeight: "bold", fontFamily: "Dosis" }}
              >
                {categoryToRender.category} Nominees (
                {categoryToRender.nominees?.length})
              </Typography>
              <Typography
                sx={{
                  color: "text.secondary",
                  maxWidth: { xs: "70%", md: "80%" },
                }}
              >
                To commemorate the special commitment and work of a Cardano
                Ambassador.
              </Typography>
              <CustomButton
                onClick={() => setOpenVotingModal(true)}
                sx={{
                  mt: -6,
                  alignSelf: "flex-end",
                  display: isTablet ? "none" : "inline-block",
                }}
                colorVariant="primary"
                disabled={!selectedNominee}
              >
                Vote Now
              </CustomButton>
            </Box>

            <Fade in={fadeChecked} timeout={200}>
              <Grid
                container
                spacing={2}
                justifyContent="center"
                alignItems="flex-start"
              >
                {categoryToRender.nominees?.map((nominee, index) => (
                  <NomineeCard
                    key={index}
                    nominee={nominee}
                    selectedNominee={selectedNominee}
                    handleSelectNominee={handleSelectNominee}
                    handleLearnMoreClick={handleLearnMoreClick}
                  />
                ))}
              </Grid>
            </Fade>
          </Grid>
        </Grid>
        <img
          src={Ellipses}
          style={{
            position: "fixed",
            right: "0",
            top: "70%",
            transform: "translateY(-25%)",
            zIndex: "-1",
            width: "70%",
          }}
        />
        {isTablet && (
          <Box
            sx={{
              zIndex: 1,
              position: "fixed",
              left: 0,
              right: 0,
              bottom: 0,
              width: "100%",
              backgroundColor: theme.palette.background.default,
              px: "20px",
              marginBottom: "20x",
              display: "flex",
              justifyContent: "center",
              overflow: "none",
            }}
          >
            <CustomButton
              onClick={() => setOpenVotingModal(true)}
              sx={{ width: "100%", height: "48px", my: "24px" }}
              colorVariant="primary"
              disabled={!selectedNominee}
            >
              Vote Now
            </CustomButton>
          </Box>
        )}
        <BioModal
          isOpen={openLearMoreCategory}
          title={learMoreCategory}
          onClose={() => setOpenLearMoreCategory(false)}
        />
        <VoteNowModal
          isOpen={openVotingModal}
          onClose={() => setOpenVotingModal(false)}
          selectedNominee={nomineeToVote}
        />
      </Box>
      <Drawer
        open={openViewReceipt}
        anchor="right"
        onClose={() => setOpenViewReceipt(false)}
      >
        <ViewReceipt />
      </Drawer>
    </>
  );
};

export { Categories };
