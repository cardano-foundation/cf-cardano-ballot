import React from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

import { Button } from "../atoms/Button";

import styles from './FormCard.module.scss';
import {ICONS} from "../../consts";

type FormCardProps = {
  title: string;
  children: React.ReactNode;
};

export const FormCard = ({ children, title }: FormCardProps) => {

  return (
    <Box className={styles.container}>
      <Typography variant="h1">{ title }</Typography>
      {children}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', paddingTop: '16px' }}>
        <Button variant="text">
          Back
        </Button>
        <Button
          variant="text"
          endIcon={<img src={ICONS.arrowCircleRight} alt="arrow right" />}
        >
          Next
        </Button>
      </Box>
    </Box>
  );
}
