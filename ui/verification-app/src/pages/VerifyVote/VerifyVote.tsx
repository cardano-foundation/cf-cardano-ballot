import React, { useState } from 'react';
import { VerifyModal } from './components/VerifyModal/VerifyModal';
import { SuccessModal } from './components/SuccessModal/SuccessModal';

export const VerifyVote = () => {
  const [explorerLink, setExplorerLink] = useState('');

  return (
    <>
      <VerifyModal opened={!explorerLink} onConfirm={setExplorerLink} />
      <SuccessModal opened={!!explorerLink} explorerLink={explorerLink} />
    </>
  );
};
