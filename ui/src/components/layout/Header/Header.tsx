import { Grid, Typography } from "@mui/material";
import { useTheme, styled } from "@mui/material/styles";
import { useNavigate, useLocation } from "react-router-dom";
import CountDownTimer from "../../CountDownTimer/CountDownTimer";

const LogoImg = styled("img")(() => ({
  width: "230px",
  left: "137px",
  top: "20px",
}));

const HeaderStyle = styled("header")(({ theme }) => ({
  top: 0,
  left: 0,
  zIndex: 9,
  width: "100vw",
  height: "auto",
  display: "flex",
  position: "static",
  alignItems: "center",
  padding: theme.spacing(2),
  justifyContent: "space-between",
}));

export default function Header() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const handleLogoClick = () => {
    navigate("/");
  };
  return (
    <HeaderStyle>
      <Grid
        container
        direction={{ xs: "column", sm: "row" }}
        justifyContent={{ sm: "center", md: "space-between" }}
        alignItems="center"
      >
        <Grid
          item
          xs={12}
          sm={"auto"}
        >
          <LogoImg
            src="/static/Cardano_Ballot_black.png"
            onClick={handleLogoClick}
            sx={{ cursor: "pointer", ml: { xs: 0, sm: 1 } }}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={"auto"}
        >
          {location.pathname === "/vote" ? (
            <Typography
              variant="body2"
              color="text.secondary"
              align="center"
            >
              Connect Wallet
            </Typography>
          ) : (
            <Typography
              variant="body2"
              color="text.secondary"
              align="center"
            >
              Time left to vote: <CountDownTimer />
            </Typography>
          )}
        </Grid>
      </Grid>
    </HeaderStyle>
  );
}
