import React from 'react';
import { formatUTCDate } from 'common/utils/dateUtils';
import styles from './EventTime.module.scss';

type Props = {
  eventHasntStarted?: boolean;
  eventHasFinished?: boolean;
  endTime: string;
  startTime: string;
};

export const EventTime = ({ endTime, startTime, eventHasntStarted, eventHasFinished }: Props) => (
  <span
    className={styles.container}
    data-testid="event-time"
  >
    {eventHasntStarted ? (
      <>
        Vote from:{' '}
        <b>
          {formatUTCDate(startTime)} - {formatUTCDate(endTime)}
        </b>
      </>
    ) : (
      <>
        {eventHasFinished ? 'The vote closed on' : 'Voting closes:'} <b>{formatUTCDate(endTime)}</b>
      </>
    )}
  </span>
);
