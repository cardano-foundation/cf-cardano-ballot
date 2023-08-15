import React, { useState, MouseEvent, useEffect } from 'react';
import cn from 'classnames';
import { Grid, Typography } from '@mui/material';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { ProposalReference } from 'types/backend-services-types';
import { OptionProps } from './OptionCard.types';
import styles from './OptionCard.module.scss';

export const OptionCard = ({
  items,
  onChangeOption,
  disabled,
  selectedOption,
}: OptionProps<ProposalReference['name']>) => {
  const [active, setActive] = useState(selectedOption || '');

  const handleChange = (_event: MouseEvent<HTMLElement>, _active: string | null) => {
    if (disabled) return;
    setActive(_active);
    onChangeOption(_active);
  };

  useEffect(() => {
    if (selectedOption) {
      setActive(selectedOption);
    }
  }, [selectedOption]);

  return (
    <Grid
      container
      direction="row"
      justifyContent="center"
      width="flex"
      margin={{ md: '40px 0px', xs: '25px 0px' }}
    >
      <ToggleButtonGroup
        disabled={disabled}
        sx={{ width: '100%', flexDirection: { xs: 'column', md: 'row' }, gap: { xs: '20px', md: '51px' } }}
        color="primary"
        value={active}
        exclusive
        onChange={handleChange}
        aria-label="cip-1694 poll options"
      >
        {items?.map((option) => (
          <ToggleButton
            sx={{
              height: { xs: '62px', md: '138px' },
              borderRadius: { xs: '8px !important', md: '16px !important' },
              padding: '0px 20px',
              maxWidth: 'auto',
            }}
            value={option.name}
            className={cn(styles.optionCard, { [styles.selected]: active === option.name })}
            key={option.label}
            data-testid='option-card'
          >
            <Grid
              item
              md={4}
              xs={12}
              container
              direction={{ xs: 'row', md: 'column' }}
              gap="15px"
              justifyContent={{ md: 'center', xs: 'flex-start' }}
              alignItems="center"
            >
              {option.icon}
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
