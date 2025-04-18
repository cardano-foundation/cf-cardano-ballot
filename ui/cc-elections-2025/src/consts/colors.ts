type ColorKey =
  | "c50"
  | "c100"
  | "c200"
  | "c300"
  | "c400"
  | "c500"
  | "c600"
  | "c700"
  | "c800"
  | "c900";

type ColorType = Record<ColorKey, string>;

export const primaryBlue: ColorType = {
  c50: "#E6EBF7",
  c100: "#D6E2FF",
  c200: "#99ADDE",
  c300: "#6685CE",
  c400: "#335CBD",
  c500: "#0033AD",
  c600: "#002682",
  c700: "#001A57",
  c800: "#000D2B",
  c900: "#000511",
};

export const orange: ColorType = {
  c50: "#FFF0E7",
  c100: "#FFE0CE",
  c200: "#FFC19D",
  c300: "#FFA26C",
  c400: "#FF833B",
  c500: "#FF640A",
  c600: "#BF4B08",
  c700: "#803205",
  c800: "#401903",
  c900: "#1A0A01",
};

export const cyan: ColorType = {
  c50: "#E9F5F8",
  c100: "#D2EAF0",
  c200: "#A4D4E0",
  c300: "#77BFD1",
  c400: "#49A9C1",
  c500: "#1C94B2",
  c600: "#156F86",
  c700: "#0E4A59",
  c800: "#07252D",
  c900: "#030F12",
};

export const fadedPurple: ColorType = {
  c50: "#F5F5F8",
  c100: "#EAE9F0",
  c200: "#D5D3E1",
  c300: "#C1BED3",
  c400: "#ACA8C4",
  c500: "#9792B5",
  c600: "#716E88",
  c700: "#4C495B",
  c800: "#26252D",
  c900: "#0F0F12",
};

export const gray: ColorType = {
  c50: "#F4F4F4",
  c100: "#E8E9E8",
  c200: "#D2D3D2",
  c300: "#A5A6A5",
  c400: "#8E908E",
  c500: "#6B6C6B",
  c600: "#474847",
  c700: "#242424",
  c800: "#0E0E0E",
  c900: "#000000",
};

export const successGreen: ColorType = {
  c50: "#F0F9EE",
  c100: "#E0F2DC",
  c200: "#C0E4BA",
  c300: "#A1D797",
  c400: "#81C975",
  c500: "#62BC52",
  c600: "#4A8D3E",
  c700: "#315E29",
  c800: "#192F15",
  c900: "#0A1308",
};

export const progressYellow: ColorType = {
  c50: "#FCF6EA",
  c100: "#F8ECD4",
  c200: "#F2D9A9",
  c300: "#EBC67F",
  c400: "#E5B354",
  c500: "#DEA029",
  c600: "#A7781F",
  c700: "#6F5015",
  c800: "#38280A",
  c900: "#161004",
};

export const errorRed: ColorType = {
  c50: "#FBEBEB",
  c100: "#F6D5D5",
  c200: "#EDACAC",
  c300: "#E58282",
  c400: "#DC5959",
  c500: "#D32F2F",
  c600: "#9E2323",
  c700: "#6A1818",
  c800: "#350C0C",
  c900: "#150505",
};
