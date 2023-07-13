type Anchor = 'top' | 'left' | 'bottom' | 'right';

interface SidePageProps {
  children: any;
  anchor: Anchor;
  open: boolean;
  setOpen: (open: boolean) => void;
}

export type { SidePageProps };
