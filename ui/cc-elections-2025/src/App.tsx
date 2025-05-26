import { useCallback, useEffect } from "react";
import { Route, Routes } from "react-router-dom";

import { Modal, ScrollToTop } from "@atoms";
import { useCardano, useModal } from "@context";
import { useDateNow, useWalletConnectionListener } from "@hooks";

import {
  CandidateDetails,
  EditCandicate,
  Home,
  RegisterForm,
  ThankYou,
} from '@pages';

import {
  callAll,
  getItemFromLocalStorage,
  WALLET_LS_KEY,
  removeItemFromLocalStorage,
} from "@utils";

export const App = () => {
  const { enable, isEnabled } = useCardano();
  const { modal, openModal, modals } = useModal();

  const now = useDateNow();

  const applyEndTime = Date.parse(import.meta.env.VITE_APPLY_END_DATE) - now;

  const isApplyActive = applyEndTime > 0;

  const isEditActive = Date.parse(import.meta.env.VITE_EDIT_END_DATE) > now;

  const isVoteActive = now >= Date.parse(import.meta.env.VITE_EDIT_END_DATE) && Date.parse(import.meta.env.VITE_VOTE_END_DATE) > now;

  useWalletConnectionListener();

  const checkTheWalletIsActive = useCallback(() => {
    const walletName = getItemFromLocalStorage(`${WALLET_LS_KEY}_name`);
    if (window.cardano) {
      const walletExtensions = Object.keys(window.cardano);
      if (walletName && walletExtensions.includes(walletName)) {
        enable(walletName);
        return;
      }
    }
    if (
      (!window.cardano && walletName) ||
      (walletName && !Object.keys(window.cardano).includes(walletName))
    ) {
      removeItemFromLocalStorage(`${WALLET_LS_KEY}_name`);
      removeItemFromLocalStorage(`${WALLET_LS_KEY}_stake_key`);
    }
  }, []);

  useEffect(() => {
    checkTheWalletIsActive();
  }, [checkTheWalletIsActive]);


  return (
    <>
      <ScrollToTop />
      <Routes>
        <Route
          path="/"
          element={
            <Home
              applyEndTime={applyEndTime}
              isEditActive={isEditActive}
              isVoteActive={isVoteActive}
            />
          }
        />
        <Route path="/candidateDetails/:id" element={<CandidateDetails isEditActive={isEditActive} />} />
        {isEnabled && isApplyActive && <Route path="/registerCandidate" element={<RegisterForm />} />}
        {isEnabled && isEditActive && <Route path="/editCandidate/:id" element={<EditCandicate />} /> }
        <Route path="/thankYou" element={<ThankYou />} />
      </Routes>
      {modals[modal.type]?.component && (
        <Modal
          open={Boolean(modals[modal.type].component)}
          handleClose={
            !modals[modal.type].preventDismiss
              ? callAll(modals[modal.type]?.onClose, () =>
                openModal({ type: "none", state: null }),
              )
              : undefined
          }
        >
          {modals[modal.type].component!}
        </Modal>
      )}
    </>
  );
}
