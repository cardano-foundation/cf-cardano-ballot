import { createTheme } from '@mui/material/styles';
import { red } from '@mui/material/colors';

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

export default theme;