import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { Home } from '../pages/Home';
import { Nominees } from '../pages/Nominees';
import {Categories} from '../pages/Categories';

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: `${PAGE_PATH}`,
  CATEGORIES: `${PAGE_PATH}categories`,
  NOMINEES: `${PAGE_PATH}nominees`,
  NOMINEES_BY_ID: `${PAGE_PATH}nominees/:id`,
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
      </Routes>
    </>
  );
};

export { PageRouter };
