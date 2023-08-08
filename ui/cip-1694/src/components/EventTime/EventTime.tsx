import React from 'react';
import styles from './EventTime.module.scss';

type Props = {
  eventHasntStarted?: boolean;
  eventHasFinished?: boolean;
  endTime: Date;
  startTime: Date;
};

export const EventTime = ({ endTime, startTime, eventHasntStarted, eventHasFinished }: Props) => (
  <span
    className={styles.container}
    data-testid="count-down-timer"
  >
    {eventHasntStarted ? (
      <>
        Vote from:{' '}
        <b>
          {startTime?.toString()} - {endTime?.toString()}
        </b>
      </>
    ) : (
      <>
        {eventHasFinished ? 'The vote closed on' : 'Voting closes:'} <b>{endTime?.toString()}</b>
      </>
    )}
  </span>
);
