import React, { useState } from "react";
import { Verify } from "./components/Verify/Verify";
import { Success } from "./components/Success/Success";

export const VerifyVote = () => {
  const [explorer, setExplorer] = useState("");

  return (
    <>
      <Verify opened={!explorer} onConfirm={setExplorer} />
      <Success opened={!!explorer} />
    </>
  );
};
