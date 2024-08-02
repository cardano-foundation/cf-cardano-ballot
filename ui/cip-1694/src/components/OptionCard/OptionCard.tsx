import React, { useState, MouseEvent, useEffect } from 'react';
import cn from 'classnames';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { Grid, Skeleton, Typography, Box } from '@mui/material';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { ProposalPresentation } from 'types/voting-ledger-follower-types';
import { OptionProps } from './OptionCard.types';
import styles from './OptionCard.module.scss';

export const OptionCard = ({
  items,
  onChangeOption,
  disabled,
  selectedOption,
}: OptionProps<ProposalPresentation['name']>) => {
  const [active, setActive] = useState(selectedOption || '');

  const handleChange = (_event: MouseEvent<HTMLElement>, _active: string | null) => {
    if (disabled) return;
    setActive(_active);
    onChangeOption(_active);
  };

  useEffect(() => {
    setActive(selectedOption);
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
        sx={{ width: '100%', flexDirection: { xs: 'column', md: 'row' }, gap: { xs: '20px', md: '24px' } }}
        color="primary"
        value={active}
        exclusive
        onChange={handleChange}
        aria-label="cip-1694 poll options"
      >
        {!items?.length &&
          Array.from({ length: 3 }).map((_el, index) => (
            <Grid
              key={index}
              item
              md={4}
              xs={12}
              container
              direction={{ xs: 'row', md: 'column' }}
              justifyContent={{ md: 'center', xs: 'flex-start' }}
              alignItems="center"
              height={{ xs: '62px', md: '138px' }}
              minHeight={{ xs: '62px', md: '138px' }}
            >
              <Skeleton
                sx={{ borderRadius: '16px' }}
                variant="rounded"
                height="100%"
                width="100%"
                data-testid="option-card-loader"
              />
            </Grid>
          ))}
        {items?.map((option) => (
          <ToggleButton
            sx={{
              height: { xs: '62px', md: '138px' },
              minHeight: { xs: '62px', md: '138px' },
              borderRadius: { xs: '8px !important', md: '16px !important' },
              padding: '16px 16px',
              maxWidth: 'auto',
            }}
            value={option.name}
            className={cn(styles.optionCard, { [styles.selected]: active === option.name })}
            key={option.id}
            data-testid="option-card"
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
            <Box
              sx={{ top: { xs: '50%', md: '16px' }, transform: { xs: 'translateY(-50%)', md: 'none' } }}
              className={styles.checkContainer}
            >
              {active === option.name && <CheckCircleIcon className={styles.checkIcon} />}
            </Box>
          </ToggleButton>
        ))}
      </ToggleButtonGroup>
    </Grid>
  );
};
