import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { Home } from '../pages/Home';
import { Proposals } from '../pages/Proposals';
import { NotFound } from '../pages/NotFound';
import { Categories } from '../pages/Categories';

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: `${PAGE_PATH}`,
  CATEGORIES: `${PAGE_PATH}categories`,
  PROPOSALS: `${PAGE_PATH}proposals`,
  PROPOSALS_BY_ID: `${PAGE_PATH}proposals/:categoryId`,
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
          path={ROUTES.PROPOSALS_BY_ID}
          element={<Proposals />}
        />
        <Route
          path={ROUTES.PROPOSALS}
          element={<Categories />}
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
