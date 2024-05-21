import React from "react";
import { Grid, Typography, IconButton, Tooltip, Link } from "@mui/material";
import SupportIcon from "@mui/icons-material/SupportAgent";
import discordLogo from "../../../common/resources/images/discord-icon.svg";
import { i18n } from "../../../i18n";
import { env } from "../../../common/constants/env";

const Footer = () => {
  return (
    <Grid
      container
      spacing={2}
      sx={{
        background: "transparent",
        boxShadow: "none",
        minHeight: "44px",
        textAlign: "center",
        fontSize: "16px",
        fontWeight: 500,
        lineHeight: "24px",
        marginTop: "80px",
        marginBottom: "80px",
      }}
      justifyContent="space-between"
      alignItems="center"
    >
      <Grid item xs={12} md={4} sx={{ textAlign: "left", paddingLeft: "0 !important" }}>
        <Typography variant="body2" sx={{ color: "text.primary" }}>
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
      <Grid
        item
        xs={12}
        md={8}
        container
        spacing={2}
        direction={{ xs: "column", md: "row" }}
        justifyContent="flex-end"
        alignItems="center"
      >
        <Grid item>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
                <Link
                    href="/terms-and-conditions"
                    sx={{
                        color: "text.primary",
                        textDecoration: "underline",
                    }}
                >
                    {i18n.t("footer.menu.termsAndConditions")}
                </Link>
            </Typography>
        </Grid>

        <Grid item>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
                <Link
                    href="/privacy-policy"
                    sx={{
                        color: "text.primary",
                        textDecoration: "underline",
                    }}
                >
                    {i18n.t("footer.menu.privacyPolicy")}
                </Link>
            </Typography>
        </Grid>

        <Grid item>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
                Version {env.APP_VERSION}
                (<Link
                    href="https://summit.cardano.org/"
                    target="_blank"
                    rel="noopener"
                    sx={{
                        color: "text.primary",
                        textDecoration: "underline",
                    }}
                >
                    Status
                </Link>)
            </Typography>
        </Grid>

        <Grid item>
          <Tooltip title="Get support" placement="top">
            <IconButton
              onClick={() =>
                window.open(env.DISCORD_SUPPORT_CHANNEL_URL, "_blank")
              }
              sx={{ p: 0, color: "secondary.main" }}
            >
              <SupportIcon sx={{ color: "text.primary" }} />
            </IconButton>
          </Tooltip>
        </Grid>
        <Grid item>
          <Tooltip title="Join our Discord" placement="top">
            <IconButton
              onClick={() => window.open(env.DISCORD_CHANNEL_URL, "_blank")}
            >
              <img
                src={discordLogo}
                alt="Discord"
                style={{
                  height: "25px",
                  cursor: "pointer",
                  filter:
                    "invert(100%) sepia(100%) saturate(0%) hue-rotate(100deg)",
                }}
              />
            </IconButton>
          </Tooltip>
        </Grid>
      </Grid>
    </Grid>
  );
};

export { Footer };
