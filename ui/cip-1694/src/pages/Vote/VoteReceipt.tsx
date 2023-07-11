import React, { useState } from "react";
import Grid from "@mui/material/Grid";
import { Typography, Container } from "@mui/material";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import StarIcon from "@mui/icons-material/Star";

const VoteReceipt = () => {
  return (
    <>
      <Container>
        <Grid
          container
          direction="column"
          justifyContent="center"
          alignItems="center"
          spacing={2}
        >
          <Grid
            item
            xs={12}
            sx={{ mt: 2, mb: 1, width: 400 }}
          >
            <Typography variant="h4">Your Vote Receipt</Typography>
          </Grid>

          <Grid
            item
            xs={6}
          >
            <Typography variant="h5">
              CIP 1694 Pre Ratification Event
            </Typography>
          </Grid>
          <Grid
            item
            xs={6}
          >
            <Typography variant="h3">Next steps...</Typography>
          </Grid>
          <Grid
            item
            xs={6}
          >
            <List
              sx={{ width: "100%", maxWidth: 360, bgcolor: "background.paper" }}
              aria-label="contacts"
            >
              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "blue" }} />
                  </ListItemIcon>
                  <ListItemText primary="Vote Receipt Implementation" />
                </ListItemButton>
              </ListItem>

              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "orange" }} />
                  </ListItemIcon>
                  <ListItemText primary="Get Access Token and Store in session" />
                </ListItemButton>
              </ListItem>

              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "orange" }} />
                  </ListItemIcon>
                  <ListItemText primary="Poll Stats Implementation" />
                </ListItemButton>
              </ListItem>
              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "orange" }} />
                  </ListItemIcon>
                  <ListItemText primary="Formatting Lovelaces" />
                </ListItemButton>
              </ListItem>
              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "orange" }} />
                  </ListItemIcon>
                  <ListItemText primary="Verification App" />
                </ListItemButton>
              </ListItem>

              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "orange" }} />
                  </ListItemIcon>
                  <ListItemText primary="Cypress component testing" />
                </ListItemButton>
              </ListItem>

              <ListItem disablePadding>
                <ListItemButton>
                  <ListItemIcon>
                    <StarIcon sx={{ color: "orange" }} />
                  </ListItemIcon>
                  <ListItemText primary="Cypress e2e testing" />
                </ListItemButton>
              </ListItem>
            </List>
          </Grid>
          <Grid
            item
            xs={6}
          >
            <Typography variant="subtitle1">
              Your digital Receipt goes here...
            </Typography>
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export default VoteReceipt;
