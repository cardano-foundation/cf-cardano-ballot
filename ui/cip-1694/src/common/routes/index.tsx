import React from 'react';
import { Route, Routes } from 'react-router-dom';
import IntroductionPage from '../../pages/Introduction/Introduction';
import VotePage from '../../pages/Vote/Vote';

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: `${PAGE_PATH}`,
  VOTE: '/vote',
};

const PageRouter = () => {
  return (
    <>
      <Routes>
        <Route
          path={ROUTES.INTRO}
          element={<IntroductionPage />}
        />
        <Route
          path={ROUTES.VOTE}
          element={<VotePage />}
        />
      </Routes>
    </>
  );
};

export { PageRouter };
