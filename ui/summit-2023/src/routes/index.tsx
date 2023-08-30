import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { Home } from '../pages/Home';
import { Categories } from '../pages/Categories';

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: `${PAGE_PATH}`,
  CATEGORIES: `${PAGE_PATH}categories`,
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
      </Routes>
    </>
  );
};

export { PageRouter };
