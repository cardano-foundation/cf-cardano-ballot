import React from 'react';
import moment from 'moment';
import { EVENT_END_TIME, EVENT_END_TIME_FORMAT } from 'common/constants/appConstants';
import styles from './CountDownTimer.module.scss';

export default function CountDownTimer() {
  const date = EVENT_END_TIME;
  const endTime = moment(date, EVENT_END_TIME_FORMAT).format('D MMMM YYYY, h:mm a');

  return (
    <span
      className={styles.container}
      data-testid="count-down-timer"
    >
      Voting closes: <b>{endTime}</b>
    </span>
  );
}
