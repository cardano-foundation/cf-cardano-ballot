import React, { useLayoutEffect, useState } from 'react';
import { ReactComponent as Loader1 } from 'common/resources/images/Loader1-icon.svg';
import { ReactComponent as Loader2 } from 'common/resources/images/Loader2-icon.svg';
import { ReactComponent as Loader3 } from 'common/resources/images/Loader3-icon.svg';
import styles from './Loader.module.scss';

const LoaderPieces = [
  <Loader1
    className={styles.loader}
    key={1}
  />,
  <Loader2
    className={styles.loader}
    key={2}
  />,
  <Loader3
    className={styles.loader}
    key={3}
  />,
  <Loader2
    className={styles.loader}
    key={4}
  />,
  <Loader1
    className={styles.loader}
    key={5}
  />,
];

export const Loader = () => {
  const [counter, setCounter] = useState(0);

  useLayoutEffect(() => {
    const newIntervalId = setInterval(() => {
      setCounter((count) => (count + 1) % LoaderPieces.length);
    }, 100);
    return () => {
      clearInterval(newIntervalId);
    };
  });

  return <>{LoaderPieces[counter]}</>;
};
