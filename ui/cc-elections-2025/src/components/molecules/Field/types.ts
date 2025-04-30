import { BoxProps, TypographyProps as MUITypographyProps } from "@mui/material";

import {
  CheckboxProps,
  InputProps,
  TextAreaProps,
  TypographyProps,
  SelectProps,
} from "@atoms";

export type InputFieldProps = InputProps & {
  errorDataTestId?: string;
  errorMessage?: string;
  errorStyles?: MUITypographyProps;
  helpfulTextDataTestId?: string;
  helpfulText?: string;
  helpfulTextStyle?: MUITypographyProps;
  label?: string;
  labelStyles?: TypographyProps;
  layoutStyles?: BoxProps;
};

export type CheckboxFieldProps = CheckboxProps & {
  errorMessage?: string;
  errorStyles?: MUITypographyProps;
  helpfulText?: string;
  helpfulTextStyle?: MUITypographyProps;
  label?: string;
  labelStyles?: TypographyProps;
  layoutStyles?: BoxProps;
  link?: string;
  linkText?: string;
};

export type TextAreaFieldProps = TextAreaProps & {
  errorMessage?: string;
  errorStyles?: MUITypographyProps;
  helpfulText?: string;
  helpfulTextStyle?: MUITypographyProps;
  hideLabel?: boolean;
  label?: string;
  labelStyles?: TypographyProps;
  layoutStyles?: BoxProps;
};

export type SelectFieldProps = SelectProps & {
  errorDataTestId?: string;
  errorMessage?: string;
  errorStyles?: MUITypographyProps;
  helpfulTextDataTestId?: string;
  helpfulText?: string;
  helpfulTextStyle?: MUITypographyProps;
  label?: string;
  labelStyles?: TypographyProps;
  layoutStyles?: BoxProps;
};
