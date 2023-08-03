import React from 'react';
import styles from './CountDownTimer.module.scss';

export default function CountDownTimer({ endTime }: { endTime: Date }) {
  return (
    <span
      className={styles.container}
      data-testid="count-down-timer"
    >
      Voting closes: <b>{endTime?.toString()}</b>
    </span>
  );
}
