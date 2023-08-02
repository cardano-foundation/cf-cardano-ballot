import { createTheme } from '@mui/material/styles';
import { red } from '@mui/material/colors';
import componentsOverride from './overrides';

const theme = createTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 391,
      md: 900,
      lg: 1200,
      xl: 1536,
    },
  },
  palette: {
    primary: {
      main: '#061D3C',
    },
    secondary: {
      main: '#F5F9FF',
    },
    error: {
      main: red.A400,
    },
  },
});

theme.components = componentsOverride(theme);

export default theme;
