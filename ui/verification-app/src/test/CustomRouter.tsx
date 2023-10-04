import { MemoryHistory } from 'history';
import React, { useState, useLayoutEffect } from 'react';
import { Router, RouterProps } from 'react-router-dom';

export const CustomRouter = ({ history, ...props }: { history: MemoryHistory } & Partial<RouterProps>) => {
  const [state, setState] = useState({
    action: history.action,
    location: history.location,
  });

  useLayoutEffect(() => history.listen(setState), [history]);
  return (
    <Router
      {...props}
      location={state.location}
      navigationType={state.action}
      navigator={history}
    />
  );
};
