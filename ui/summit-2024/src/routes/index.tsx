import React from "react";
import { Route, Routes } from "react-router-dom";
import { Home } from "../pages/Home";

export const PAGE_PATH = "/";

export const ROUTES = {
  LANDING: `${PAGE_PATH}`,
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
        <Route path={ROUTES.LANDING} element={<Home />} />
      </Routes>
    </>
  );
};

export { PageRouter };
