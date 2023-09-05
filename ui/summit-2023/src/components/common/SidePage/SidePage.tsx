import React, { MouseEvent, KeyboardEvent, Fragment } from 'react';
import Drawer from '@mui/material/Drawer';
import { SidePageProps } from './SidePage.types';
import styles from './SidePage.module.scss';

export default function SidePage({ children, anchor, open, setOpen }: SidePageProps) {
  const toggleDrawer = (event: KeyboardEvent | MouseEvent) => {
    if (
      event.type === 'keydown' &&
      ((event as KeyboardEvent).key === 'Tab' || (event as KeyboardEvent).key === 'Shift')
    ) {
      return;
    }
    setOpen(false);
  };

  return (
    <Fragment key={anchor}>
      <Drawer
        classes={{ paper: styles.root }}
        anchor={anchor}
        open={open}
        onClose={toggleDrawer}
        data-testid="side-drawer"
      >
        {children}
      </Drawer>
    </Fragment>
  );
}
