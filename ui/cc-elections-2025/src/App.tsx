import { Route, Routes } from "react-router-dom";

import { Home } from '@pages';
import { ThankYou } from '@pages';
import { CandidateDetails } from '@pages';
import { RegisterForm } from "@pages";

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/candidateDetails/:id" element={<CandidateDetails />} />
      <Route path="/registerCandidate" element={<RegisterForm />} />
      <Route path="/thankYou" element={<ThankYou />} />
    </Routes>
  );
}
