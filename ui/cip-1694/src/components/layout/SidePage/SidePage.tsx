import * as React from "react";
import Drawer from "@mui/material/Drawer";
import { SidePageProps } from "./SidePage.types";

export default function SidePage({
  children,
  anchor,
  open,
  setOpen,
}: SidePageProps) {
  const toggleDrawer = (event: React.KeyboardEvent | React.MouseEvent) => {
    if (
      event.type === "keydown" &&
      ((event as React.KeyboardEvent).key === "Tab" ||
        (event as React.KeyboardEvent).key === "Shift")
    ) {
      return;
    }
    setOpen(false);
  };

  return (
    <React.Fragment key={anchor}>
      <Drawer
        anchor={anchor}
        open={open}
        onClose={toggleDrawer}
      >
        {children}
      </Drawer>
    </React.Fragment>
  );
}
