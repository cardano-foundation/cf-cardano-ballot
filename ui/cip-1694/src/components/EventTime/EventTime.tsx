import React from 'react';
import { Skeleton } from '@mui/material';
import { formatUTCDate } from 'common/utils/dateUtils';
import styles from './EventTime.module.scss';

type Props = {
  eventHasntStarted?: boolean;
  eventHasFinished?: boolean;
  endTime: string;
  startTime: string;
};

export const EventTime = ({ endTime, startTime, eventHasntStarted, eventHasFinished }: Props) => {
  const title = eventHasntStarted ? 'Vote from:' : eventHasFinished ? 'The vote closed on' : 'Ballot closes:';
  const time = eventHasntStarted ? `${formatUTCDate(startTime)} - ${formatUTCDate(endTime)}` : formatUTCDate(endTime);
  const showPlaceholder = !endTime && !startTime;

  return (
    <span
      className={styles.container}
      data-testid="event-time"
    >
      {title}{' '}
      {showPlaceholder ? (
        <Skeleton
          variant="text"
          data-testid="event-time-loader"
          className={styles.skeleton}
        />
      ) : (
        <b>{time}</b>
      )}
    </span>
  );
};
