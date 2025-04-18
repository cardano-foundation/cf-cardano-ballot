import { Dispatch, FC, SetStateAction } from "react";
import { Box, InputBase } from "@mui/material";
import Search from "@mui/icons-material/Search";

import { DataActionsFilters, DataActionsSorting } from "@molecules";
import { OrderActionsChip } from "./OrderActionsChip";
import { theme } from "@/theme";

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
  const {
    palette: { boxShadow2 },
  } = theme;

  return (
    <Box alignItems="center" display="flex" justifyContent="space-between">
      <InputBase
        inputProps={{ "data-testid": "search-input" }}
        onChange={(e) => setSearchText(e.target.value)}
        placeholder="Search..."
        value={searchText}
        startAdornment={
          <Search
            style={{
              color: "#99ADDE",
              height: 16,
              marginRight: 4,
              width: 16,
            }}
          />
        }
        sx={{
          bgcolor: "white",
          border: 1,
          borderColor: "secondaryBlue",
          borderRadius: 50,
          boxShadow: `2px 2px 20px 0px ${boxShadow2}`,
          fontSize: 11,
          fontWeight: 500,
          height: 48,
          padding: "16px 24px",
          maxWidth: 500,
        }}
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
