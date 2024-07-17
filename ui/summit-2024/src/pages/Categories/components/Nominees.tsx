import React, { useState } from "react";
import { Fade, Grid } from "@mui/material";
import { Proposal } from "../../../store/reducers/eventCache/eventCache.types";
import { NomineeCard } from "./NomineeCard";
import { BioModal } from "./BioModal";

interface NomineesProps {
  fadeChecked: boolean;
  nominees: Proposal[];
  selectedNominee: string | undefined;
  handleSelectedNominee: (nomineeId: string) => void;
}

const Nominees: React.FC<NomineesProps> = ({
  fadeChecked,
  nominees,
  selectedNominee,
  handleSelectedNominee,
}) => {
  const [learMoreCategory, setLearMoreCategory] = useState("");
  const [openLearMoreCategory, setOpenLearMoreCategory] = useState(false);

  const handleSelectNominee = (id: string) => {
    handleSelectedNominee(id);
  };

  const handleLearnMoreClick = (
    event: React.MouseEvent<HTMLElement>,
    category: string,
  ) => {
    event.stopPropagation();
    setLearMoreCategory(category);
    setOpenLearMoreCategory(true);
  };

  return (
    <>
      <Fade in={fadeChecked} timeout={200}>
        <Grid
          container
          spacing={2}
          justifyContent="center"
          alignItems="flex-start"
        >
          {nominees?.length &&
            nominees.map((nominee: Proposal, index) => (
              <NomineeCard
                key={index}
                nominee={nominee}
                selectedNominee={selectedNominee}
                handleSelectNominee={handleSelectNominee}
                handleLearnMoreClick={handleLearnMoreClick}
              />
            ))}
        </Grid>
      </Fade>
      <BioModal
        isOpen={openLearMoreCategory}
        title={learMoreCategory}
        onClose={() => setOpenLearMoreCategory(false)}
      />
    </>
  );
};

export { Nominees };
