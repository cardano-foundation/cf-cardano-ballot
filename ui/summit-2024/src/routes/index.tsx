import { useEffect } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import { Home } from "../pages/Home";
import { Leaderboard } from "../pages/Leaderboard";
import { ReceiptHistory } from "../pages/ReceiptHistory";
import { NotFound } from "../pages/NotFound";
import { UserGuide } from "../pages/UserGuide";
import TermsAndConditions from "../pages/TermsAndPolicy/TermsAndConditions";
import { Categories } from "../pages/Categories";

export const PAGE_PATH = "/";

export const ROUTES = {
  LANDING: `${PAGE_PATH}`,
  CATEGORIES: `${PAGE_PATH}categories`,
  RECEIPTS: `${PAGE_PATH}receipts`,
  LEADERBOARD: `${PAGE_PATH}leaderboard`,
  USER_GUIDE: `${PAGE_PATH}user-guide`,
  TERMS_AND_CONDITIONS: `${PAGE_PATH}terms-and-conditions`,
  PRIVACY_POLICY: `${PAGE_PATH}privacy-policy`,
  NOMINEES_BY_ID: `${PAGE_PATH}nominees/:categoryId`,
};

const PageRouter = () => {
  const location = useLocation();

  useEffect(() => {
    // TODO: tmp fix
    window.scrollTo(0, 0);
  }, [location.pathname]);

  return (
    <>
      <Routes>
        <Route path={ROUTES.LANDING} element={<Home />} />
        <Route path={ROUTES.CATEGORIES} element={<Categories />} />
        <Route path={ROUTES.RECEIPTS} element={<ReceiptHistory />} />
        <Route path={ROUTES.LEADERBOARD} element={<Leaderboard />} />
        <Route path={ROUTES.USER_GUIDE} element={<UserGuide />} />
        <Route
          path={ROUTES.TERMS_AND_CONDITIONS}
          element={<TermsAndConditions />}
        />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </>
  );
};

export { PageRouter };
