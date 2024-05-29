import { createTheme } from "@mui/material/styles";
import componentsOverride from "./overrides";
import { createBreakpoints } from "@mui/system";

declare module "@mui/material/styles" {
  interface BreakpointOverrides {
    xs: true;
    sm: true;
    tablet: true;
    md: true;
    lg: true;
    xl: true;
    xxl: true;
  }
}

const breakpoints = createBreakpoints({
  values: {
    xs: 0,
    sm: 600,
    tablet: 744,
    md: 1024,
    lg: 1400,
    xl: 1900,
    xxl: 2300,
  },
});

const theme = createTheme({
  typography: {
    fontFamily: '"Roboto", sans-serif',
    body1: {
      fontSize: "16px",
      fontStyle: "normal",
      fontWeight: 500,
      lineHeight: "24px",
      color: "text.primary",
    },
  },
  palette: {
    mode: "dark",
    primary: {
      main: "#121212",
      light: "#D2D2D9",
      dark: "#272727",
    },
    secondary: {
      main: "#EE9766",
      light: "#FFB989",
      dark: "#C76A3D",
    },
    error: {
      main: "#f44336",
      text: "#FF878C",
    },
    background: {
      default: "#121212",
      neutralDark: "#272727",
      darker: "#343434",
      disabled: "#737380",
    },
    text: {
      primary: "#D2D2D9",
      secondary: "#b3b3b3",
      neutralLight: "#D2D2D9",
      neutralLightest: "#FAF9F6",
    },
  },
  components: {
    ...componentsOverride,
    MuiContainer: {
      styleOverrides: {
        root: {
          maxWidth: "1440px",
        },
      },
    },
  },
  breakpoints,
});

export default theme;
