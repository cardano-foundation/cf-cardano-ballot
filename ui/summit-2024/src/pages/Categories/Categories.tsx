import React, { useState } from "react";
import {
  Box,
  Grid,
  Paper,
  Typography,
  Checkbox,
  Button,
  List,
  ListItem,
  ListItemText,
  useTheme,
} from "@mui/material";
import theme from "../../common/styles/theme";
import { CategoriesNames } from "../../__fixtures__/categories";
import ellipse from "../../assets/ellipses2.svg";
import DoneIcon from '@mui/icons-material/Done';
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import HoverCircle from "../../components/common/HoverCircle/HoverCircle";

const Categories: React.FC = () => {
  const categoriesData = CategoriesNames;
  const [selectedNominee, setSelectedNominee] = useState("");

  const handleSelectNominee = (nominee: string) => {
      console.log("nominee");
      console.log(nominee);
    setSelectedNominee(nominee);
  };
  return (
    <Box sx={{ width: "100%" }}>
      <Grid container>
        <Grid
          item
          xs={12}
          sm={3}
          md={2.4}
          lg={2}
          sx={{
            position: "sticky",
            top: 0,
            maxHeight: "100vh",
            overflow: "auto",
            borderRight: "1px solid #737380",
          }}
        >
          <Typography
            sx={{
              color: theme.palette.text.neutralLightest,
              fontFamily: "Dosis",
              fontSize: "32px",
              fontStyle: "normal",
              fontWeight: 700,
              lineHeight: "36px",
            }}
          >
            Categories ({categoriesData.length})
          </Typography>
          <List>
            {categoriesData.map((category, index) => (
              <ListItem key={index}>
                <Typography
                  sx={{
                    color: theme.palette.text.neutralLightest,
                    fontSize: "16px",
                    fontStyle: "normal",
                    fontWeight: 500,
                    lineHeight: "24px",
                  }}
                >
                  {category}
                </Typography>
              </ListItem>
            ))}
          </List>
        </Grid>
        <Grid
          item
          xs={12}
          sm={9}
          md={9.6}
          lg={10}
          sx={{
            p: theme.spacing(2),
            bgcolor: theme.palette.background.default,
            paddingLeft: "40px",
          }}
        >
          <Grid container spacing={2}>
            {categoriesData.map((category, index) => (
              <Grid
                item
                xs={12}
                sm={6}
                md={4}
                key={index}
                onClick={() => handleSelectNominee(category)}
              >
                <Paper
                  elevation={3}
                  sx={{
                    width: 309,
                    height: 240,
                    flexShrink: 0,
                    borderRadius: "24px",
                    border: "2px solid rgba(18, 18, 18, 0.01)",
                    backdropFilter: "blur(5px)",
                    p: theme.spacing(2),
                    position: "relative",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "space-between",
                  }}
                >
                    <Box sx={{ position: 'absolute', right: 8, top: 8 }}>
                        <HoverCircle selected={selectedNominee === category}/>
                    </Box>
                  <Typography
                    variant="h6"
                    sx={{
                      color: "var(--neutralLightest, #FAF9F6)",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontFamily: "Dosis",
                      fontSize: "28px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "32px",
                      width: "229px",
                      mt: 3,
                      ml: 1
                    }}
                  >
                    {category}
                  </Typography>
                  <Button
                    sx={{
                      display: "flex",
                      width: 229,
                      padding: "16px 24px",
                      justifyContent: "center",
                      alignItems: "center",
                      gap: 1,
                      borderRadius: "12px",
                      border: "1px solid var(--neutralLightest, #FAF9F6)",
                      color: "var(--neutralLightest, #FAF9F6)",
                      fontSize: "16px",
                      fontStyle: "normal",
                      fontWeight: 500,
                      lineHeight: "24px",
                      mt: "auto",
                      mx: "auto",
                      textTransform: "none",
                      "&:hover": {
                        border: "1px solid var(--neutralLightest, #FAF9F6)",
                        color: "var(--neutralLightest, #FAF9F6)",
                      },
                    }}
                  >
                    Learn More
                  </Button>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Grid>
      </Grid>
    </Box>
  );
};

export { Categories };
