import React from "react";
import { Box, Drawer, List, ListItem, ListItemText } from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import { ROUTES } from "../../../routes";

interface RightMenuProps {
    menuIsOpen: boolean;
    setMenuIsOpen: (isOpen: boolean) => void;
}

const RightMenu: React.FC<RightMenuProps> = ({ menuIsOpen, setMenuIsOpen }) => {
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
          component="div"
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
            <ListItem onClick={() => handleClickMenu(ROUTES.CATEGORIES)}>
              <ListItemText primary="Categories" />
            </ListItem>
            <ListItem
              onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
            >
              <ListItemText primary="Leaderboard" />
            </ListItem>
            <ListItem onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}>
              <ListItemText primary="User Guide" />
            </ListItem>
          </List>
        </Box>
      </Drawer>
    </>
  );
};

export { RightMenu };
