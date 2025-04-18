import { Route, Routes } from "react-router-dom";

import { IndividualForm } from './pages/IndividualForm.tsx';
import { CompanyForm } from './pages/CompanyForm.tsx';
import { ConsortiumForm } from './pages/ConsortiumForm.tsx';
import { Home } from './pages/Home';
import { ChooseForm } from './pages/ChooseForm';
import { ThankYou } from './pages/ThankYou';
import { CandidateDetails } from './pages/CandidateDetails';

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/candidateDetails/:id" element={<CandidateDetails />} />
      <Route path="/chooseForm" element={<ChooseForm />} />
      <Route path="/individulalCandidate" element={<IndividualForm />} />
      <Route path="/companyCandidate" element={<CompanyForm />} />
      <Route path="/consortiumCandidate" element={<ConsortiumForm />} />
      <Route path="/thankYou" element={<ThankYou />} />
    </Routes>
  );
}
