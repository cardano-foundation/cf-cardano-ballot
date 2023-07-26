import React, { useState, MouseEvent } from 'react';
import cn from 'classnames';
import { Grid, Typography } from '@mui/material';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { OptionProps } from './OptionCard.types';
import styles from './OptionCard.module.scss';

export const OptionCard = ({ items, onChangeOption, disabled }: OptionProps) => {
  const [active, setActive] = useState('');

  const handleChange = (_event: MouseEvent<HTMLElement>, _active: string | null) => {
    if (disabled) return;
    setActive(_active);
    onChangeOption(_active);
  };

  return (
    <Grid
      container
      direction="row"
      justifyContent={'center'}
      width={'flex'}
    >
      <ToggleButtonGroup
        sx={{ width: '100%' }}
        color="primary"
        value={active}
        exclusive
        onChange={handleChange}
        aria-label="cip-1694 poll options"
        className={styles.optionCardGrouo}
      >
        {items.map((option) => (
          <ToggleButton
            value={option.label}
            className={cn(styles.optionCard, { [styles.selected]: active === option.label })}
            key={option.label}
          >
            <Grid
              item
              sm={4}
              xs={12}
            >
              <Typography component="div">{option.icon}</Typography>
              <Typography
                className={styles.label}
                component="div"
                variant="h5"
              >
                {option.label}
              </Typography>
            </Grid>
          </ToggleButton>
        ))}
      </ToggleButtonGroup>
    </Grid>
  );
};
