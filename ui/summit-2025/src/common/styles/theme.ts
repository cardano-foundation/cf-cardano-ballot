import { createTheme } from "@mui/material/styles";
import componentsOverride from "./overrides";
import { createBreakpoints } from "@mui/system";

import "@mui/material/styles";

declare module "@mui/material/styles" {
  interface PaletteColorOptions {
    main?: string;
    light?: string;
    dark?: string;
    text?: string;
  }

  interface SimplePaletteColorOptions {
    main: string;
    light?: string;
    dark?: string;
    contrastText?: string;
  }

  interface TypeBackground {
    neutralDark?: string;
    darker?: string;
    disabled?: string;
  }

  interface Palette {
    background: TypeBackground;
  }

  interface TypeText {
    neutralLight?: string;
    neutralLightest?: string;
  }

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
    fontFamily: '"Open Sans", sans-serif',
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
      main: "#FF6444",
      light: "#FFB989",
      dark: "#C76A3D",
    },
    error: {
      main: "#f44336",
      text: "#FF4D4D",
    },
    background: {
      default: "#050C25",
      neutralDark: "#3E4C73",
      darker: "#343434",
      disabled: "#737380",
    },
    text: {
      primary: "#E8EDFF",
      secondary: "#FAF2EB",
      neutralLight: "#3E4C73",
      neutralLightest: "#E8EDFF",
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
