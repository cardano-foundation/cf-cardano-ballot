import React from 'react';
import styles from './Layout.module.scss';

type LayoutProps = {
  children: React.ReactNode;
};

export const Layout = ({ children }: LayoutProps) => (
  <div className={styles.container}>
    <div className={styles.content}>{children}</div>
  </div>
);
