import React from "react";
import { Fade, Grid } from "@mui/material";
import { Proposal } from "../../../store/reducers/eventCache/eventCache.types";
import { NomineeCard } from "./NomineeCard";

interface NomineesProps {
  fadeChecked: boolean;
  nominees: Proposal[];
  categoryAlreadyVoted: boolean;
  selectedNominee: string | undefined;
  handleSelectedNominee: (nomineeId: string) => void;
  handleOpenLearnMore: (nomineeId: string) => void;
}

const Nominees: React.FC<NomineesProps> = ({
  fadeChecked,
  nominees,
  categoryAlreadyVoted,
  selectedNominee,
  handleSelectedNominee,
  handleOpenLearnMore,
}) => {
  const handleSelectNominee = (id: string) => {
    handleSelectedNominee(id);
  };

  const handleLearnMoreClick = (
    event: React.MouseEvent<HTMLElement>,
    nomineeId: string,
  ) => {
    event.stopPropagation();
    handleOpenLearnMore(nomineeId);
  };

  return (
    <>
      <Fade in={fadeChecked} timeout={200}>
        <Grid
          container
          spacing={2}
          justifyContent="left"
          alignItems="flex-start"
        >
          {nominees?.length &&
            nominees.map((nominee: Proposal, index) => (
              <NomineeCard
                key={index}
                nominee={nominee}
                categoryAlreadyVoted={categoryAlreadyVoted}
                selectedNominee={selectedNominee}
                handleSelectNominee={handleSelectNominee}
                handleLearnMoreClick={handleLearnMoreClick}
              />
            ))}
        </Grid>
      </Fade>
    </>
  );
};

export { Nominees };
