import { Box, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

import {
  gray,
} from "@consts";
import { useCardano } from "@context";
import { Card } from "./Card";

export const WalletInfoCard = () => {
  const { address, disconnectWallet } = useCardano();
  const  navigate = useNavigate();

  const onClickDisconnect = async () => {
    await disconnectWallet();

    navigate("/");
  };

  return (
    address && (
      <Card border elevation={0} sx={{ p: 1.5 }}>
        <Typography color={gray.c300} fontSize={12} fontWeight={500}>
          {"Connected Wallet:"}
        </Typography>
        <Box sx={{ alignItems: "center", display: "flex" }}>
          <Typography
            sx={{
              flex: 1,
              fontSize: 14,
              fontWeight: 400,
              overflow: "hidden",
              textOverflow: "ellipsis",
              width: 10,
            }}
          >
            {address}
          </Typography>
          <Button
            data-testid="disconnect-button"
            variant="text"
            onClick={onClickDisconnect}
          >
            {"Disconnect"}
          </Button>
        </Box>
      </Card>
    )
  );
};
