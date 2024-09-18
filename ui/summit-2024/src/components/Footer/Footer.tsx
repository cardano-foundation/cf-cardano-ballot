import {
  Grid,
  Typography,
  Box,
  Link,
  Tooltip,
  IconButton,
} from "@mui/material";
import SupportIcon from "@mui/icons-material/SupportAgent";
import { env } from "../../common/constants/env";
import discordLogo from "../../assets/discord.svg";
import { i18n } from "../../i18n";
import { eventBus, EventName } from "../../utils/EventBus";

const Footer = () => {
  const handleOpenTerms = () => {
    eventBus.publish(EventName.OpenTermsModal);
  };
  return (
    <>
      <Box
        component="div"
        sx={{
          width: "100%",
          mt: 4,
          padding: 2,
          textAlign: "center",
        }}
      >
        <Grid
          container
          spacing={2}
          justifyContent="space-between"
          alignItems="center"
          sx={{
            marginLeft: {
              xs: "-1%",
            },
          }}
        >
          <Grid
            item
            xs={12}
            md={4}
            sx={{ textAlign: { xs: "center", md: "left" } }}
          >
            <Typography>
              © {new Date().getFullYear()}{" "}
              <Link
                href="https://summit.cardano.org/"
                target="_blank"
                rel="noopener"
                sx={{
                  color: "text.primary",
                  textDecoration: "underline",
                }}
              >
                Cardano Summit
              </Link>
              . All rights reserved.
            </Typography>
          </Grid>
          <Grid item xs={12} md={8}>
            <Grid
              container
              spacing={2}
              justifyContent={{ xs: "center", md: "flex-end" }}
              alignItems="center"
            >
              <Grid
                item
                xs={12}
                md={6}
                sx={{
                  justifyContent: {
                    xs: "center",
                    md: "flex-end",
                  },
                  display: "flex",
                }}
              >
                <Typography >
                  <Link
                    sx={{
                      color: "text.primary",
                      textDecoration: "underline",
                      marginRight: 1,
                      cursor: "pointer",
                    }}
                    onClick={() => handleOpenTerms()}
                  >
                    {i18n.t("footer.menu.termsAndConditions")}
                  </Link>
                  <Link
                    sx={{
                      color: "text.primary",
                      textDecoration: "underline",
                      marginRight: 1,
                      cursor: "pointer",
                    }}
                    onClick={() => handleOpenTerms()}
                  >
                    {i18n.t("footer.menu.privacyPolicy")}
                  </Link>
                  <span>
                    Version {env.APP_VERSION}(
                    <Link
                      href="https://status.voting.summit.cardano.org/"
                      target="_blank"
                      rel="noopener"
                      sx={{
                        color: "text.primary",
                        textDecoration: "underline",
                      }}
                    >
                      Status
                    </Link>
                    )
                  </span>
                </Typography>
              </Grid>
              <Grid
                item
                xs="auto"
                md="auto"
                sx={{
                  display: "flex",
                  justifyContent: { xs: "center", sm: "flex-end" },
                  alignItems: "center",
                  maxWidth: { xs: "100%", md: "auto" },
                  marginLeft: {
                    xs: "0px",
                    md: "80px",
                  },
                }}
              >
                <Tooltip title="Get support" placement="top">
                  <IconButton
                    onClick={() =>
                      window.open(env.DISCORD_SUPPORT_CHANNEL_URL, "_blank")
                    }
                    sx={{ p: 0, color: "secondary.main", marginRight: "40px" }}
                  >
                    <SupportIcon sx={{ color: "text.primary" }} />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Join our Discord" placement="top">
                  <IconButton
                    onClick={() =>
                      window.open(env.DISCORD_SUPPORT_CHANNEL_URL, "_blank")
                    }
                    sx={{ p: 0, color: "secondary.main" }}
                  >
                    <img
                      src={discordLogo}
                      alt="Discord"
                      style={{ cursor: "pointer" }}
                    />
                  </IconButton>
                </Tooltip>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Box>
      <Box
        component="div"
        sx={{
          height: {
            xs: "100px",
            md: "40px",
          },
        }}
      />
    </>
  );
};

export { Footer };
