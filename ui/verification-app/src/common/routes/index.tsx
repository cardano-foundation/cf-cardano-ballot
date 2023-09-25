import { VerifyVote } from "pages/VerifyVote/VerifyVote";
import React from "react";
import { Route, Routes } from "react-router-dom";

export const PAGE_PATH = "/";

export const ROUTES = {
  MAIN: PAGE_PATH,
};

export const PageRoutes = () => (
  <Routes>
    <Route path={ROUTES.MAIN} element={<VerifyVote />} />
  </Routes>
);
