import React from 'react';
type Anchor = 'top' | 'left' | 'bottom' | 'right';

interface SidePageProps {
  children: React.ReactElement;
  anchor: Anchor;
  open: boolean;
  setOpen: (open: boolean) => void;
}

export type { SidePageProps };
