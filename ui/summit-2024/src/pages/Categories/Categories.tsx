import React, { useEffect, useState } from "react";
import { Box, Typography, useMediaQuery, Drawer } from "@mui/material";
import theme from "../../common/styles/theme";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import { VoteNowModal } from "./components/VoteNowModal";
import { ViewReceipt } from "./components/ViewReceipt";
import { STATE } from "./components/ViewReceipt.type";
import { useAppSelector } from "../../store/hooks";
import { getEventCache } from "../../store/reducers/eventCache";
import { Category } from "../../store/reducers/eventCache/eventCache.types";
import { PageBase } from "../BasePage";
import { Nominees } from "./components/Nominees";
import { Winners } from "./components/Winners";
import { BioModal } from "./components/BioModal";
import Layout from "../../components/Layout/Layout";
import Ellipses from "../../assets/ellipse.svg";

const Categories: React.FC = () => {
  const isTablet = useMediaQuery(theme.breakpoints.down("md"));
  const eventCache = useAppSelector(getEventCache);

  console.log("eventCache");
  console.log(eventCache);

  const categoriesData = eventCache.categories;

  const [showWinners, setShowWinners] = useState(eventCache.finished);

  const [selectedCategory, setSelectedCategory] = useState(
    categoriesData[0].id,
  );

  const [selectedNominee, setSelectedNominee] = useState<string | undefined>(
    undefined,
  );

  const [openVotingModal, setOpenVotingModal] = useState(false);
  const [openViewReceipt, setOpenViewReceipt] = useState(false);

  const [learMoreCategory, setLearMoreCategory] = useState("");
  const [openLearMoreCategory, setOpenLearMoreCategory] = useState(false);

  const [fadeChecked, setFadeChecked] = useState(true);

  useEffect(() => {
    // Example: http://localhost:3000/categories?category=ambassador&nominee=63123e7f-dfc3-481e-bb9d-fed1d9f6e9b9
    const params = new URLSearchParams(window.location.search);
    const categoryParam = params.get("category");
    const nomineeParam = params.get("nominee");

    const category = categoriesData.find(
      (c) => c.id.toUpperCase() === categoryParam?.toUpperCase(),
    );

    if (!category) return;
    const nominee = category.proposals.find(
      (p) => p.id.toUpperCase() === nomineeParam?.toUpperCase(),
    );
    if (!nominee) return;
    handleOpenLearnMoreModal(nominee.id);
  }, [eventCache.categories]);

  useEffect(() => {
    if (fadeChecked) {
      setSelectedCategory(selectedCategory);
    }
  }, [fadeChecked, selectedCategory]);

  const handleClickMenuItem = (category: string) => {
    if (category !== selectedCategory) {
      setFadeChecked(false);
      setTimeout(() => {
        setFadeChecked(true);
      }, 200);
    }
  };

  const handleSelectNominee = (id: string) => {
    if (selectedNominee !== id) {
      setSelectedNominee(id);
    } else {
      setSelectedNominee(undefined);
    }
  };

  const handleOpenLearnMoreModal = (nomineeId: string) => {
    setLearMoreCategory(nomineeId);
    setOpenLearMoreCategory(true);
  };

  const handleOpenViewReceipt = () => {
    setOpenViewReceipt(true);
  };
  const handleOpenActionButton = () => {
    if (showWinners) {
      handleOpenViewReceipt();
    } else {
      setOpenVotingModal(true);
    }
  };

  let categoryToRender = categoriesData.find((c) => c.id === selectedCategory);
  if (categoryToRender === undefined) {
    categoryToRender = categoriesData[0];
  }

  const nomineeToVote = categoryToRender.proposals?.find(
    (n) => n.id === selectedNominee,
  );

  const optionsForMenu = categoriesData.map((category: Category) => {
    return {
      label: category.id,
      content: (
        <>
          <Box
            component="div"
            sx={{
              width: "100%",
              marginBottom: "32px",
              display: "flex",
              flexDirection: "column",
              alignItems: "flex-start",
            }}
          >
            <Typography
              // TODO: remove after demo
              onClick={() => setShowWinners(!showWinners)}
              variant="h5"
              sx={{ fontWeight: "bold", fontFamily: "Dosis" }}
            >
              {category.id} Nominees ({category.proposals?.length})
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
              onClick={() => handleOpenActionButton()}
              sx={{
                mt: -6,
                alignSelf: "flex-end",
                display: isTablet ? "none" : "inline-block",
              }}
              colorVariant="primary"
              disabled={!selectedNominee}
            >
              {!showWinners ? <>Vote Now</> : <>View Receipt</>}
            </CustomButton>
          </Box>
          {showWinners ? (
            <Winners
              fadeChecked={fadeChecked}
              nominees={category.proposals}
              handleSelectedNominee={handleSelectNominee}
              selectedNominee={selectedNominee}
              handleOpenLearnMore={handleOpenLearnMoreModal}
            />
          ) : (
            <Nominees
              fadeChecked={fadeChecked}
              nominees={category.proposals}
              handleSelectedNominee={handleSelectNominee}
              selectedNominee={selectedNominee}
              handleOpenLearnMore={handleOpenLearnMoreModal}
            />
          )}
        </>
      ),
    };
  });

  const bottom = (
    <>
      {isTablet && (
        <Box
          component="div"
          sx={{
            zIndex: 3,
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
            onClick={() => handleOpenActionButton()}
            sx={{ width: "100%", height: "48px", my: "24px" }}
            colorVariant="primary"
            disabled={!selectedNominee}
          >
            {!showWinners ? <>Vote Now</> : <>View Receipt</>}
          </CustomButton>
        </Box>
      )}
    </>
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
        <Layout
          menuOptions={optionsForMenu}
          bottom={bottom}
          mode="change"
          defaultOption={0}
          onSelectMenuOption={(option) => handleClickMenuItem(option)}
        />
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
        <VoteNowModal
          isOpen={openVotingModal}
          onClose={() => setOpenVotingModal(false)}
          selectedNominee={nomineeToVote}
        />
        <BioModal
          isOpen={openLearMoreCategory}
          title={learMoreCategory}
          onClose={() => setOpenLearMoreCategory(false)}
        />
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

export { Categories };
