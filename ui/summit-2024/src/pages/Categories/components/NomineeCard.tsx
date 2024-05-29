import React from "react";
import { Grid, Paper, Typography, Button, Box } from "@mui/material";
import { NomineeFixture } from "../../../__fixtures__/categories";
import HoverCircle from "../../../components/common/HoverCircle/HoverCircle";
import theme from "../../../common/styles/theme";
import nomineeBg from "../../../assets/bg/nomineeCard.svg";

interface NomineeCardProps {
  nominee: NomineeFixture;
  selectedNominee: number | undefined;
  handleSelectNominee: (id: number) => void;
  handleLearnMoreClick: (
    event: React.MouseEvent<HTMLButtonElement, MouseEvent>,
    name: string,
  ) => void;
}

const NomineeCard: React.FC<NomineeCardProps> = ({
  nominee,
  selectedNominee,
  handleSelectNominee,
  handleLearnMoreClick,
}) => {
  return (
    <Grid
      item
      xs={12}
      sm={6}
      md={4}
      sx={{
        width: "100px !important",
        display: "flex",
        justifyContent: "center",
      }}
    >
      <Paper
        onClick={() => handleSelectNominee(nominee.id)}
        elevation={3}
        sx={{
          width: "100%",
          maxWidth: {
            xs: "100%",
            sm: "340px",
          },
          height: "202px",
          flexShrink: 0,
          borderRadius: "24px",
          border: `1px solid ${
            selectedNominee === nominee.id
              ? theme.palette.secondary.main
              : theme.palette.background.default
          }`,
          backdropFilter: "blur(5px)",
          position: "relative",
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between",
          cursor: "pointer",
          backgroundImage: `url(${nomineeBg})`,
          backgroundSize: "160% 160%",
          backgroundPosition: "center",
        }}
      >
        <Box
          sx={{
            p: { xs: 1, sm: 2 },
          }}
        >
          <Box sx={{ position: "absolute", right: 8, top: 8 }}>
            <HoverCircle selected={selectedNominee === nominee.id} />
          </Box>
          <Typography
            variant="h6"
            sx={{
              color: theme.palette.text.neutralLightest,
              textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
              fontFamily: "Dosis",
              fontSize: "28px",
              fontStyle: "normal",
              fontWeight: 700,
              lineHeight: "32px",
              mt: 3,
              ml: 1,
            }}
          >
            {nominee.name}
          </Typography>
        </Box>

        <Box
          sx={{
            width: "100%",
            borderTop: `1px solid ${theme.palette.background.disabled}`,
            height: "48px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Typography
            onClick={(event) => {
              event.stopPropagation();
              handleLearnMoreClick(event, nominee.name);
            }}
            sx={{
              color: theme.palette.text.neutralLight,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
            }}
          >
            Learn More
          </Typography>
        </Box>
      </Paper>
    </Grid>
  );
};

export { NomineeCard };
