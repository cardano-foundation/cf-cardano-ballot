import { useEffect } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import { Home } from "../pages/Home";
import { Categories } from "../pages/Categories";
import { Leaderboard } from "../pages/Leaderboard/Leaderboard";
import { ReceiptHistory } from "../pages/ReceiptHistory/ReceiptHistory";
import { NotFound } from "../pages/NotFound/NotFound";
import { TermsAndConditions } from "../pages/TermsAndPolicy/TermsAndConditions";
import TermsAndConditions2 from "../pages/TermsAndPolicy2/TermsAndConditions2";
import UserGuide from "../pages/UserGuide/UserGuide";

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
        <Route
          path="terms2"
          element={<TermsAndConditions2 />}
        />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </>
  );
};

export { PageRouter };
