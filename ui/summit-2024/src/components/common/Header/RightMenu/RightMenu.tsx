import React, { useState } from "react";
import { Box, Drawer, List, ListItem, ListItemText } from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import { ROUTES } from "../../../../routes";

const RightMenu = ({ menuIsOpen, setMenuIsOpen }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleClickMenu = (option: string) => {
    if (option !== location.pathname) {
      navigate(option);
      setMenuIsOpen(false);
    }
  };

  return (
    <>
      <Drawer
        anchor="right"
        open={menuIsOpen}
        onClose={() => setMenuIsOpen(false)}
      >
        <Box
          sx={{
            width: 250,
            height: "100%",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            paddingTop: 8,
          }}
        >
          <List>
            <ListItem button onClick={() => handleClickMenu(ROUTES.CATEGORIES)}>
              <ListItemText primary="Categories" />
            </ListItem>
            <ListItem
              button
              onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
            >
              <ListItemText primary="Leaderboard" />
            </ListItem>
            <ListItem button onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}>
              <ListItemText primary="User Guide" />
            </ListItem>
          </List>
        </Box>
      </Drawer>
    </>
  );
};

export { RightMenu };
