import { ChangeEvent } from "react";
import {
  ButtonProps as MUIButtonProps,
  CheckboxProps as MUICheckboxProps,
  InputBaseProps,
  TypographyProps as MUITypographyProps,
  TextareaAutosizeProps,
  SxProps,
} from "@mui/material";
import * as TooltipMUI from "@mui/material/Tooltip";

export type ButtonProps = Omit<MUIButtonProps, "size"> & {
  isLoading?: boolean;
  size?: "small" | "medium" | "large" | "extraLarge";
  dataTestId?: string;
};

export type LoadingButtonProps = ButtonProps & {
  isLoading?: boolean;
};

export type TypographyProps = Pick<
  MUITypographyProps,
  "color" | "lineHeight" | "sx" | "component"
> & {
  children?: React.ReactNode;
  fontSize?: number;
  fontWeight?: 100 | 200 | 300 | 400 | 500 | 600 | 700 | 800 | 900;
  variant?:
    | "headline1"
    | "headline2"
    | "headline3"
    | "headline4"
    | "headline5"
    | "title1"
    | "title2"
    | "body1"
    | "body2"
    | "caption";
};

export type InputProps = InputBaseProps & {
  dataTestId?: string;
  errorMessage?: string;
};

export type SpacerProps = {
  x?: number;
  y?: number;
};

export type CheckboxProps = Omit<MUICheckboxProps, "onChange" | "value"> & {
  dataTestId?: string;
  errorMessage?: string;
  onChange: (event: ChangeEvent<HTMLInputElement>) => void;
  value: boolean;
};

export type FormErrorMessageProps = {
  dataTestId?: string;
  errorMessage?: string;
  errorStyles?: MUITypographyProps;
};

export type FormHelpfulTextProps = {
  dataTestId?: string;
  helpfulText?: string;
  helpfulTextStyle?: MUITypographyProps;
  sx?: SxProps;
};

export type TextAreaProps = TextareaAutosizeProps & {
  errorMessage?: string;
  isModifiedLayout?: boolean;
};

export type InfoTextProps = {
  label: string;
  sx?: SxProps;
};

export type TooltipProps = Omit<TooltipMUI.TooltipProps, "title"> & {
  heading?: string;
  paragraphOne?: string;
  paragraphTwo?: string;
};
