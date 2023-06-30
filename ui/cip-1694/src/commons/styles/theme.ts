import { createTheme } from '@mui/material/styles';
import { red } from '@mui/material/colors';
import componentsOverride from './overrides';

const theme = createTheme({
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