import { FC, useCallback } from "react";
import { To } from "react-router-dom";
import { Box, CircularProgress, Typography } from "@mui/material";

import { useCardano } from "@context";
import { theme } from "@/theme";

export interface WalletOption {
  icon: string;
  label: string;
  name: string;
  cip95Available: boolean;
  dataTestId?: string;
  pathToNavigate?: To;
}

export const WalletOptionButton: FC<WalletOption> = ({
  dataTestId,
  icon,
  label,
  name,
  cip95Available
}) => {
  const { enable, isEnableLoading } = useCardano();
  const {
    palette: { lightBlue },
  } = theme;

  const enableByWalletName = useCallback(async () => {
    if (isEnableLoading) return;

    const result = await enable(name);

    if (result?.stakeKey) {
      return;
    }

  }, [enable, isEnableLoading]);

  return (
    <Box
      data-testid={dataTestId}
      sx={{
        alignItems: "center",
        border: isEnableLoading ? "none" : `1px solid ${lightBlue}`,
        bgcolor: isEnableLoading ? "#EAE9F0" : "white",
        borderRadius: "100px",
        boxShadow: isEnableLoading ? undefined : "0px 0px 11px 0px #24223230",
        boxSizing: "border-box",
        cursor: cip95Available
          ? isEnableLoading
            ? "default"
            : "pointer"
          : "unset",
        display: "flex",
        justifyContent: "space-between",
        marginBottom: "16px",
        padding: "12px 13px 12px 13px",
        transition: "background .2s",
        position: "relative",
        width: "100%",
        "&:hover": isEnableLoading
          ? undefined
          : {
              background: lightBlue,
            },
      }}
      key={name}
      onClick={enableByWalletName}
    >
      <img
        alt={`${name} icon`}
        src={icon}
        style={{
          height: "24px",
          width: "24px",
          filter: isEnableLoading ? "grayscale(100%)" : "none",
        }}
      />
      <Typography
        color={isEnableLoading ? "#C1BED3" : "primaryBlue"}
        sx={{
          fontSize: "16px",
          fontWeight: "500",
        }}
      >
        {name ?? label}
      </Typography>
      <div style={{ height: "24px", width: "24px" }} />
      {isEnableLoading === name && (
        <Box
          position="absolute"
          left={0}
          right={0}
          display="flex"
          justifyContent="center"
        >
          <CircularProgress size={26} />
        </Box>
      )}
    </Box>
  );
};
