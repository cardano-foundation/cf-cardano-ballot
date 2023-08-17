import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { IntroductionPage } from 'pages/Introduction/Introduction';
import { VotePage } from 'pages/Vote/Vote';
import { Leaderboard } from 'pages/Leaderboard/Leaderboard';

export const PAGE_PATH = '/';

export const ROUTES = {
  INTRO: PAGE_PATH,
  VOTE: `${PAGE_PATH}vote`,
  LEADERBOARD: `${PAGE_PATH}leaderboard`,
};

export const PageRoutes = () => (
  <Routes>
    <Route
      path={ROUTES.INTRO}
      element={<IntroductionPage />}
    />
    <Route
      path={ROUTES.VOTE}
      element={<VotePage />}
    />
    <Route
      path={ROUTES.LEADERBOARD}
      element={<Leaderboard />}
    />
  </Routes>
);
