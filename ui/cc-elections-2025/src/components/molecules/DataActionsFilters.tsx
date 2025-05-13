import { Dispatch, Fragment, SetStateAction, useCallback, useRef } from "react";
import {
  Box,
  Checkbox,
  FormControlLabel,
  FormLabel,
  Typography,
} from "@mui/material";

import { useOnClickOutside, useScreenDimension } from "@hooks";

interface Props {
  chosenFilters: string[][];
  setChosenFilters: Dispatch<SetStateAction<string[][]>>;
  closeFilters: () => void;
  options: {
    key: string;
    label: string;
  }[][];
  title?: string[];
}

export const DataActionsFilters = ({
  chosenFilters,
  setChosenFilters,
  closeFilters,
  options,
  title,
}: Props) => {
  const handleFilterChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
      const filters = chosenFilters.map((filter, i) => {
        if (i === index) {
          if (e.target.checked) {
            return [...filter, e.target.name];
          } else {
            return filter.filter((str) => str !== e.target.name);
          }
        } else {
          return filter;
        }
      });
      setChosenFilters(filters);
    },
    [chosenFilters, setChosenFilters],
  );

  const { isMobile } = useScreenDimension();

  const wrapperRef = useRef<HTMLDivElement>(null);
  useOnClickOutside(wrapperRef, closeFilters);

  return (
    <Box
      display="flex"
      flexDirection="column"
      position="absolute"
      sx={{
        background: "#FBFBFF",
        boxShadow: "1px 2px 11px 0px #00123D5E",
        borderRadius: "10px",
        padding: "12px 0px",
        width: {
          xxs: "250px",
          md: "415px",
        },
        zIndex: "1",
        right: isMobile ? "59px" : "64px",
        top: "53px",
        maxHeight: "200px",
        overflowY: "scroll",
      }}
      ref={wrapperRef}
    >
      {options.map((option, index) => (
        <Fragment key={index}>
          {title && (
            <FormLabel
              sx={{
                fontSize: 14,
                fontWeight: 500,
                color: "#9792B5",
                paddingX: "20px",
              }}
            >
              {title[index]}
            </FormLabel>
          )}
          {option.map((item) => (
            <Box
              key={item.key}
              paddingX="20px"
              sx={[{ "&:hover": { bgcolor: "#E6EBF7" } }]}
              bgcolor={
                chosenFilters[index]?.includes(item.key) ? "#FFF0E7" : "transparent"
              }
            >
              <FormControlLabel
                control={
                  <Checkbox
                    inputProps={{
                      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                      // @ts-expect-error
                      "data-testid": `${item.label.replace(/ /g, "")}-checkbox`,
                    }}
                    onChange={(e) => handleFilterChange(e, index)}
                    name={item.key}
                    checked={chosenFilters[index]?.includes(item.key)}
                  />
                }
                label={
                  <Typography fontSize={14} fontWeight={500}>
                    {item.label}
                  </Typography>
                }
              />
            </Box>
          ))}
        </Fragment>
      ))}
    </Box>
  );
};
