import {
  Theme as MuiTheme,
  ThemeOptions as MuiThemeOptions,
  PaletteOptions as MuiPalette,
} from "@mui/material/styles";

declare module "@mui/material/styles" {
  type BreakpointOverrides = {
    xxs: number;
    xs: number;
    sm: number;
    md: number;
    lg: number;
    xl: number;
  };
  interface PaletteOptions extends MuiPalette {
    accentOrange: string;
    accentYellow: string;
    arcticWhite: string;
    boxShadow1: string;
    boxShadow2: string;
    darkPurple: string;
    errorRed: string;
    highlightBlue: string;
    inputRed: string;
    negativeRed: string;
    neutralGray: string;
    orangeDark: string;
    neutralWhite: string;
    positiveGreen: string;
    primaryBlue: string;
    secondaryBlue: string;
    specialCyan: string;
    specialCyanBorder: string;
    lightBlue: string;
    textBlack: string;
    textGray: string;
    lightOrange: string;
    fadedPurple: string;
  }

  interface Theme extends MuiTheme {
    breakpoints: BreakpointOverrides;
    palette: Palette;
  }

  interface ThemeOptions extends MuiThemeOptions {
    breakpoints: { values: BreakpointOverrides };
    palette: Palette;
  }
}
