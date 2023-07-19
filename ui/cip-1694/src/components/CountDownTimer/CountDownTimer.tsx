import React from 'react';
import moment from 'moment';
import styles from './CountDownTimer.module.scss'

export default function CountDownTimer() {
  const endTime = moment('09-01-2023', 'MM-DD-YYYY').format('D MMMM YYYY, h:mm a'); // summit date

  return <div className={styles.container} data-testid="count-down-timer">Voting closes: <b>{endTime}</b></div>;
}
