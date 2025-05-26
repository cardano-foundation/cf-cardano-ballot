import { useEffect, useState } from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

import { geographicRepresentationList, getInitials } from "@utils";
import { CandidatesListItem } from "./CandidatesListItem/CandidatesListItem.tsx";
import { Candidate } from "@models";
import {DataActionsBar} from "@/components/molecules";

type CandidatesListProps = {
  candidates: Candidate[];
  isEditActive: boolean;
};

export const CandidatesList = ({ candidates, isEditActive }: CandidatesListProps) => {
  const [filteredCandidates, setFilteredCandidates] = useState<Candidate[]>(candidates);
  const [sortOpen, setSortOpen] = useState<boolean>(false);
  const [chosenSorting, setChosenSorting] = useState<string>("Random");
  const [searchText, setSearchText] = useState<string>("");
  const [filterOpen, setFilterOpen] = useState<boolean>(false);
  const [chosenFilters, setChosenFilters] = useState<string[][]>([[],[],[]]);

  const geographicRepresentation = geographicRepresentationList().map(item => ({ key: item.label, label: item.label }));

  const filterOptions = [
    [
      { key: "Individual", label: "Individual" },
      { key: "Company", label: "Company" },
      { key: "Consortium", label: "Consortium" },
    ],
    [
      { key: "Yes", label: "Yes" },
      { key: "No", label: "No" },
    ],
    geographicRepresentation
  ];

  const sortOptions = [
    { key: "Random", label: "Random" },
    { key: "Name", label: "Name" },
  ];

  useEffect(() => {
    setFilteredCandidates(candidates);
  }, [candidates]);

  useEffect(() => {
    let candidatesTemp = candidates
      .filter((candidate) => candidate.candidate.name.toLowerCase().includes(searchText.toLowerCase()));

    if(chosenFilters[0].length > 0) {
      candidatesTemp = candidatesTemp.filter((candidate) => chosenFilters[0].map(filter => filter.toLowerCase()).includes(candidate.candidate.candidateType));
    }

    if(chosenFilters[1].length > 0) {
      candidatesTemp = candidatesTemp.filter((candidate => chosenFilters[1].map(filter => filter === "Yes").includes(candidate.candidate.verified)));
    }

    if(chosenFilters[2].length > 0) {
      candidatesTemp = candidatesTemp.filter((candidate => chosenFilters[2].map(filter => filter).includes(candidate.candidate.country)));
    }

    if (chosenSorting === "Random") {
      setFilteredCandidates(candidatesTemp);
    } else if (chosenSorting === "Name") {
      setFilteredCandidates(candidatesTemp.sort((a, b) => a.candidate.name.localeCompare(b.candidate.name)));
    }
  }, [chosenSorting, searchText, chosenFilters]);

  return (
    <Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: '12px', padding: '40px 0 24px' }}>
        <Typography variant="h2">Candidates List</Typography>
        <Box>
          <DataActionsBar
            chosenSorting={chosenSorting}
            closeSorts={() => setSortOpen(false)}
            closeFilters={() => setFilterOpen(false)}
            searchText={searchText}
            setChosenSorting={setChosenSorting}
            setSearchText={setSearchText}
            setSortOpen={setSortOpen}
            sortOpen={sortOpen}
            filterOptions={filterOptions}
            filtersTitle={['Candidate Type', 'Verified Applicant', 'Geographic Representation']}
            sortOptions={sortOptions}
            filtersOpen={filterOpen}
            setFiltersOpen={setFilterOpen}
            setChosenFilters={setChosenFilters}
            chosenFilters={chosenFilters}
            chosenFiltersLength={chosenFilters.flat().length}
          />
        </Box>
      </Box>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '24px', paddingBottom: '24px', minHeight: '277px' }}>
        {filteredCandidates.map((candidate) => (
          <CandidatesListItem
            bio={candidate.candidate.about}
            candidateType={candidate.candidate.candidateType}
            id={candidate.candidate.id}
            initials={getInitials(candidate.candidate.name)}
            key={candidate.candidate.id}
            name={candidate.candidate.name}
            verified={candidate.candidate.verified}
            walletAddress={candidate.candidate.walletAddress}
            isEditActive={isEditActive}
          />
        ))}
      </Box>
    </Box>
  );
}
