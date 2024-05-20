import { createTheme } from "@mui/material/styles";
import componentsOverride from "./overrides";
import { createBreakpoints } from "@mui/system";

declare module "@mui/material/styles" {
  interface BreakpointOverrides {
    xxl: true;
  }
}

const breakpoints = createBreakpoints({
  values: { xs: 0, sm: 600, md: 1200, lg: 1400, xl: 1900, xxl: 2300 },
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
      main: "#0C7BC5",
      light: "#40A0D7",
      dark: "#005691",
    },
    secondary: {
      main: "#EE9766",
      light: "#FFB989",
      dark: "#C76A3D",
    },
    error: {
      main: "#f44336",
    },
    background: {
      default: "#121212",
      neutralDark: "#272727",
      neutralDarkest: "#121212",
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
