import { createTheme } from "@mui/material";

// TODO: lets' scope these under the component folder?
export default function toggleButton(theme: ReturnType<typeof createTheme>) {
  return {
    MuiToggleButton: {
      styleOverrides: {
        root: {
          "&.Mui-selected": {
            backgroundColor: theme.palette.primary.main,
            color: "#92ffc0",
          },
        },
      },
    },
  };
}
