import { Dispatch, FC, SetStateAction } from "react";
import { Box } from "@mui/material";

import { DataActionsFilters, DataActionsSorting } from "@molecules";
import { OrderActionsChip } from "./OrderActionsChip";
import InputAdornment from "@mui/material/InputAdornment";
import {Input, SearchIcon} from "@atoms";

type DataActionsBarProps = {
  chosenFilters?: string[];
  chosenFiltersLength?: number;
  chosenSorting: string;
  closeFilters?: () => void;
  closeSorts: () => void;
  filterOptions?: {
    key: string;
    label: string;
  }[];
  filtersOpen?: boolean;
  filtersTitle?: string;
  isFiltering?: boolean;
  searchText: string;
  setChosenFilters?: Dispatch<SetStateAction<string[]>>;
  setChosenSorting: Dispatch<SetStateAction<string>>;
  setFiltersOpen?: Dispatch<SetStateAction<boolean>>;
  setSearchText: Dispatch<SetStateAction<string>>;
  setSortOpen: Dispatch<SetStateAction<boolean>>;
  sortOpen: boolean;
  sortOptions?: {
    key: string;
    label: string;
  }[];
};

export const DataActionsBar: FC<DataActionsBarProps> = ({ ...props }) => {
  const {
    chosenFilters = [],
    chosenFiltersLength,
    chosenSorting,
    closeFilters = () => {},
    closeSorts,
    filterOptions = [],
    filtersOpen,
    filtersTitle,
    isFiltering = true,
    searchText,
    setChosenFilters = () => {},
    setChosenSorting,
    setFiltersOpen,
    setSearchText,
    setSortOpen,
    sortOpen,
    sortOptions = [],
  } = props;

  return (
    <Box sx={{ display: 'flex', gap: '24px', alignItems: 'center' }}>
      <Input
        id="search"
        name="search"
        type="text"
        sx={{ width: '322px', backgroundColor: 'white', padding: '11px 12px' }}
        placeholder="Search ..."
        value={searchText}
        onChange={(event) => setSearchText(event.target.value)}
        startAdornment={
          <InputAdornment position={"start"}>
            <SearchIcon />
          </InputAdornment>
        }
      />
      <OrderActionsChip
        chosenFiltersLength={chosenFiltersLength}
        filtersOpen={filtersOpen}
        isFiltering={isFiltering}
        setFiltersOpen={setFiltersOpen}
        chosenSorting={chosenSorting}
        setSortOpen={setSortOpen}
        sortOpen={sortOpen}
      >
        {filtersOpen && (
          <DataActionsFilters
            chosenFilters={chosenFilters}
            setChosenFilters={setChosenFilters}
            closeFilters={closeFilters}
            options={filterOptions}
            title={filtersTitle}
          />
        )}
        {sortOpen && (
          <DataActionsSorting
            chosenSorting={chosenSorting}
            setChosenSorting={setChosenSorting}
            closeSorts={closeSorts}
            options={sortOptions}
          />
        )}
      </OrderActionsChip>
    </Box>
  );
};
