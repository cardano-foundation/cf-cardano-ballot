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
  USER_GUIDE: `${PAGE_PATH}user-guide`,
  TERMS_AND_CONDITIONS: `${PAGE_PATH}terms-and-conditions`,
  PRIVACY_POLICY: `${PAGE_PATH}privacy-policy`,
  NOMINEES_BY_ID: `${PAGE_PATH}nominees/:categoryId`,
  PAGE_NOT_FOUND: `${PAGE_PATH}404`,
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
          path={ROUTES.USER_GUIDE}
          element={<UserGuide />}
        />
        <Route
          path={ROUTES.TERMS_AND_CONDITIONS}
          element={<TermsAndConditions />}
        />
        <Route
          path={ROUTES.PRIVACY_POLICY}
          element={<PrivacyPolicy />}
        />
        <Route
          path={ROUTES.PAGE_NOT_FOUND}
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
