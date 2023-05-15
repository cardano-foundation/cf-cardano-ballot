import React from 'react';
import {
  Route,
  Routes
} from "react-router-dom";
import IntroductionPage from "../../pages/Introduction/Introduction";

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: `${PAGE_PATH}`
};

const PageRouter = () => {
  return (
    <>
      <Routes>
        <Route path={ROUTES.INTRO} element={<IntroductionPage />} />
      </Routes>
    </>
  );
};

export { PageRouter };