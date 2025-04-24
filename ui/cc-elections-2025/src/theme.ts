import { createTheme } from "@mui/material/styles";
import {
  cyan,
  errorRed,
  orange,
  primaryBlue,
  progressYellow,
  successGreen,
} from "./consts";

export type Theme = typeof theme;

export const theme = createTheme({
  breakpoints: {
    values: {
      xxs: 0,
      xs: 375,
      sm: 425,
      md: 768,
      lg: 1024,
      xl: 1440,
    },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        ":root": {
          fontFamily: "Poppins, Arial",
        },
      },
    },
    MuiAccordion: {
      styleOverrides: {
        root: {
          borderRadius: `12px !important`,
        },
      },
    },
    MuiSelect: {
      styleOverrides: {
        select: {
          padding: "4px 0 5px",
        },
      }
    },
    MuiOutlinedInput: {
      styleOverrides: {
        notchedOutline: {
          borderWidth: 0,
        },
      }
    },
    MuiInputBase: {
      styleOverrides: {
        root: {
          borderColor: "rgba(191, 200, 217, 0.38)",
          borderWidth: 1,
          borderStyle: "solid",
          borderRadius: 4,
          padding: "8px 16px",
          width: "100%",
        },
      },
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          fontSize: "14px",
          fontWeight: 500,
          lineHeight: 1.5,
          letterSpacing: "0.4px",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 99,
          textTransform: "none",
          padding: "12px 24px",
          fontWeight: 700,
        },
        outlined: (props) => ({
          borderColor: props.theme.palette.lightBlue,
        }),
      },
    },
    MuiChip: {
      variants: [
        {
          props: { color: "default", variant: "filled" },
          style: {
            backgroundColor: '#EDEBFF',
            color: '#212a3d',
            borderRadius: '8px',
          },
        },
        {
          props: { color: "success", variant: "filled" },
          style: {
            backgroundColor: successGreen.c200,
            color: successGreen.c700,
          },
        },
        {
          props: { color: "error", variant: "filled" },
          style: {
            backgroundColor: errorRed.c100,
            color: errorRed.c500,
          },
        },
        {
          props: { color: "warning", variant: "filled" },
          style: {
            backgroundColor: progressYellow.c200,
            color: orange.c700,
          },
        },
        {
          props: { color: "info", variant: "filled" },
          style: {
            backgroundColor: cyan.c100,
            color: cyan.c500,
          },
        },
      ],
      styleOverrides: {
        root: {
          fontSize: "14px",
          fontWeight: 400,
          height: 33,
        },
        filledPrimary: {
          backgroundColor: primaryBlue.c100,
          color: primaryBlue.c500,
        },
        filledSecondary: {
          backgroundColor: orange.c100,
          color: orange.c600,
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
        },
      },
    },
    MuiPopover: {
      defaultProps: {
        elevation: 2,
      },
    },
  },
  typography: {
    fontFamily: "Poppins, Arial",
    h1: {
      fontSize: "33px",
      fontWeight: 500,
      lineHeight: 1.3,
    },
    h2: {
      fontSize: "23px",
      fontWeight: 500,
      lineHeight: 1.3,
      letterSpacing: "0.06px",
    },
    h3: {
      fontSize: "19px",
      fontWeight: 500,
      lineHeight: 1.3,
    },
    body1: {
      fontSize: "16px",
      lineHeight: 1.5,
      letterSpacing: "0.5px",
    },
    body2: {
      fontSize: "14px",
      lineHeight: 1.5,
      letterSpacing: "0.25px",
    },
    button: {
      fontSize: "14px",
      fontWeight: 700,
      lineHeight: 1.5,
    },
    caption: {
      fontSize: "12px",
      lineHeight: 1.5,
      color: "#506288",
    },
    subtitle2: {
      fontSize: "14px",
      fontWeight: 500,
      lineHeight: 1.5,
      letterSpacing: "0.4px",
    },
    allVariants: {
      color: "#212a3d",
    },
  },
  palette: {
    accentOrange: "#F29339",
    accentYellow: "#F2D9A9",
    arcticWhite: "#FBFBFF",
    boxShadow1: "rgba(0, 18, 61, 0.37)",
    boxShadow2: "rgba(47, 98, 220, 0.2)",
    darkPurple: "rgba(36, 34, 50, 1)",
    errorRed: "#9E2323",
    fadedPurple: "#716E88",
    highlightBlue: "#C2EFF299",
    inputRed: "#FAEAEB",
    lightBlue: "#D6E2FF",
    lightOrange: "#FFCBAD",
    negativeRed: "#E58282",
    neutralGray: "#8E908E",
    neutralWhite: "#FFFFFF",
    orangeDark: "#803205",
    positiveGreen: "#5CC165",
    primary: { main: "#3052F5" },
    primaryBlue: "#0033AD",
    secondary: { main: "#D63F1E" },
    secondaryBlue: "#6F99FF",
    specialCyan: "#1C94B2",
    specialCyanBorder: "#77BFD1",
    textBlack: "#242232",
    textGray: "#525252",
  },
});

theme.shadows[1] =
  "0px 1px 2px 0px rgba(0, 51, 173, 0.08), 0px 1px 6px 1px rgba(0, 51, 173, 0.15)";
theme.shadows[2] =
  "0px 1px 2px 0px rgba(0, 51, 173, 0.08), 0px 2px 10px 2px rgba(0, 51, 173, 0.15)";
theme.shadows[3] =
  "0px 1px 3px 0px rgba(0, 51, 173, 0.08), 0px 4px 12px 3px rgba(0, 51, 173, 0.15)";
theme.shadows[4] =
  "0px 2px 3px 0px rgba(0, 51, 173, 0.08), 0px 6px 14px 4px rgba(0, 51, 173, 0.15)";
theme.shadows[5] =
  "0px 4px 4px 0px rgba(0, 51, 173, 0.08), 0px 8px 20px 6px rgba(0, 51, 173, 0.15)";
