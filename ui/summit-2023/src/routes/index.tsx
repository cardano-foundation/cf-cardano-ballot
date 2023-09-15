import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { Home } from '../pages/Home';
import { Nominees } from '../pages/Nominees';
import { Leaderboard } from '../pages/Leaderboard';
import { UserGuide } from '../pages/UserGuide';
import { TermsAndConditions } from '../pages/Legal/TermsAndConditions';
import { PrivacyPolicy } from '../pages/Legal/PrivacyPolicy';
import { NotFound } from '../pages/NotFound';
import { Categories } from '../pages/Categories';

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: `${PAGE_PATH}`,
  CATEGORIES: `${PAGE_PATH}categories`,
  NOMINEES: `${PAGE_PATH}nominees`,
  LEADERBOARD: `${PAGE_PATH}leaderboard`,
  USERGUIDE: `${PAGE_PATH}user-guide`,
  TERMSANDCONDITIONS: `${PAGE_PATH}termsandconditions`,
  PRIVACYPOLICY: `${PAGE_PATH}privacypolicy`,
  NOMINEES_BY_ID: `${PAGE_PATH}nominees/:categoryId`,
  NOT_FOUND: `${PAGE_PATH}404`,
};

const PageRouter = () => {
  return (
    <>
      <Routes>
        <Route
          path={ROUTES.INTRO}
          element={<Home />}
        />
        <Route
          path={ROUTES.CATEGORIES}
          element={<Categories />}
        />
        <Route
          path={ROUTES.NOMINEES_BY_ID}
          element={<Nominees />}
        />
        <Route
          path={ROUTES.NOMINEES}
          element={<Categories />}
        />
        <Route
          path={ROUTES.LEADERBOARD}
          element={<Leaderboard />}
        />
        <Route
          path={ROUTES.USERGUIDE}
          element={<UserGuide />}
        />
        <Route
          path={ROUTES.TERMSANDCONDITIONS}
          element={<TermsAndConditions />}
        />
        <Route
          path={ROUTES.PRIVACYPOLICY}
          element={<PrivacyPolicy />}
        />
        <Route
          path={ROUTES.NOT_FOUND}
          element={<NotFound />}
        />
        <Route
          path="*"
          element={<NotFound />}
        />
      </Routes>
    </>
  );
};

export { PageRouter };
