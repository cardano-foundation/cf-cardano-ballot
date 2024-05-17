import { createTheme } from '@mui/material/styles';
import componentsOverride from './overrides';
import { createBreakpoints } from '@mui/system';

declare module '@mui/material/styles' {
  interface BreakpointOverrides {
    xxl: true;
  }
}
const breakpoints = createBreakpoints({ values: { xs: 0, sm: 600, md: 1200, lg: 1400, xl: 1900, xxl: 2300 } });

const theme = createTheme({
  palette: {
    primary: {
      main: '#03021F',
    },
    secondary: {
      main: '#F5F9FF',
    },
    error: {
      main: '#C20024',
    },
  },
  breakpoints,
});

theme.components = componentsOverride(theme);

export default theme;
