import { createTheme } from "@mui/material";
import toggleButton from "./ToggleButton";

export default function ComponentsOverrides(
  theme: ReturnType<typeof createTheme>,
) {
  return Object.assign(toggleButton(theme));
}
