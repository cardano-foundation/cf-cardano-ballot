import React from "react";
import { Typography, Grid, IconButton, Tooltip } from "@mui/material";
import styles from "./Footer.module.scss";
import discordLogo from "../../../common/resources/images/discord-icon.svg";
import SupportIcon from "@mui/icons-material/SupportAgent";
import { env } from "common/constants/env";
import { NavLink } from "react-router-dom";
import { i18n } from "i18n";
import { openNewTab } from "../../../utils/utils";

const Footer: React.FC = () => {
  return (
    <Grid
      container
      spacing={1}
      direction={{ sm: "column", md: "row" }}
      justifyContent="space-between"
      alignItems="center"
      className={styles.footer}
    >
      <Grid item xs={12} sm={6}>
        <Typography variant="body2">
          © {new Date().getFullYear()}{" "}
          <NavLink
            to="https://summit.cardano.org/"
            target="_blank"
            rel="noopener"
          >
            Cardano Summit
          </NavLink>
          . <span color="inherit">All rights reserved.</span>
        </Typography>
      </Grid>
      <Grid item xs={12} sm={"auto"}>
        <Grid
          container
          spacing={2}
          direction={{ sm: "column", md: "row" }}
          sx={{ textAlignLast: { xs: "center", sm: "center", md: "right" } }}
        >
          <Grid item xs={12} sm={"auto"}>
            <NavLink to="/terms-and-conditions">
              <Typography
                variant="body2"
                justifyContent="center"
                className={styles.link}
              >
                {i18n.t("footer.menu.termsAndConditions")}
              </Typography>
            </NavLink>
          </Grid>

          <Grid item xs={12} sm={"auto"}>
            <NavLink to="/privacy-policy">
              <Typography
                variant="body2"
                justifyContent="center"
                className={styles.link}
              >
                {i18n.t("footer.menu.privacyPolicy")}
              </Typography>
            </NavLink>
          </Grid>

          <Grid item xs={12} sm={"auto"}>
            <Grid
              container
              spacing={1}
              direction="row"
              justifyContent="center"
              alignItems="center"
            >
              <Grid
                item
                xs={6}
                sm={"auto"}
                sx={{ pt: "0 !important", textAlign: { xs: "end" } }}
              >
                <Tooltip title="Get support" placement="top">
                  <IconButton
                    onClick={() => openNewTab(env.DISCORD_SUPPORT_CHANNEL_URL)}
                    size="large"
                    sx={{ p: 0, color: "#434656" }}
                  >
                    <SupportIcon />
                  </IconButton>
                </Tooltip>
              </Grid>
              <Grid item xs={6} sm={"auto"} sx={{ textAlign: { xs: "start" } }}>
                <Tooltip title="Join our Discord" placement="top">
                  <Typography variant="body2">
                    <img
                      onClick={() => openNewTab(env.DISCORD_CHANNEL_URL)}
                      src={discordLogo}
                      alt="Discord"
                      style={{ height: "25px", cursor: "pointer" }}
                    />
                  </Typography>
                </Tooltip>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

export { Footer };
