import { Dispatch, SetStateAction } from "react";
import { Box } from "@mui/material";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";

import { useScreenDimension } from "@hooks";
import { ICONS } from "@consts";
import { theme } from "@/theme";

type Props = {
  filtersOpen?: boolean;
  setFiltersOpen?: Dispatch<SetStateAction<boolean>>;
  chosenFiltersLength?: number;
  chosenSorting: string;
  sortOpen: boolean;
  setSortOpen: Dispatch<SetStateAction<boolean>>;
  children?: React.ReactNode;
  isFiltering?: boolean;
};

export const OrderActionsChip = (props: Props) => {
  const { isMobile } = useScreenDimension();

  const {
    palette: { secondary },
  } = theme;

  const {
    filtersOpen,
    setFiltersOpen = () => {},
    chosenFiltersLength = 0,
    sortOpen,
    setSortOpen,
    isFiltering = true,
    children,
  } = props;

  return (
    <Box
      display="flex"
      width="min-content"
      alignItems="center"
      gap={isMobile ? "8px" : "24px"}
      position="relative"
    >
      {isFiltering && (
        <IconButton
          onClick={() => {
            setSortOpen(false);
            if (isFiltering) {
              setFiltersOpen(!filtersOpen);
            }
          }}
          data-testid="filters-button"
          aria-label="Filter candidates"
        >
          <img
            alt="filter"
            src={ICONS.filterIcon}
            aria-hidden="true"
          />
          {!filtersOpen && chosenFiltersLength > 0 && (
            <Box
              sx={{
                alignItems: "center",
                background: secondary.main,
                borderRadius: "100%",
                color: "white",
                display: "flex",
                fontSize: "12px",
                height: "16px",
                justifyContent: "center",
                position: "absolute",
                right: "-3px",
                top: "0",
                width: "16px",
              }}
            >
              <Typography variant="caption" color="#FFFFFF">
                {chosenFiltersLength}
              </Typography>
            </Box>
          )}
        </IconButton>
      )}
      <IconButton
        onClick={() => {
          if (isFiltering) {
            setFiltersOpen(false);
          }
          setSortOpen(!sortOpen);
        }}
        data-testid="sort-button"
        aria-label="Sort candidates"
      >
        <img
          src={ICONS.sortDescendingIcon}
          alt=""
          aria-hidden="true"
        />
      </IconButton>
      {children}
    </Box>
  );
};
