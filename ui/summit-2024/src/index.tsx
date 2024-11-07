import React from "react";
import ReactDOM from "react-dom/client";
import { Provider } from "react-redux";
import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import { MatomoProvider, createInstance } from "@datapunt/matomo-tracker-react";
import theme from "./common/styles/theme";
import { AppWrapper } from "./components/AppWrapper/AppWrapper";
import App from "./App";
import { store } from "./store";
import { env } from "./common/constants/env";

const instance = createInstance({
  urlBase: env.MATOMO_BASE_URL,
  siteId: parseInt(env.MATOMO_PROJECT_ID, 10),
  trackerUrl: `${env.MATOMO_BASE_URL}/matomo.php`,
  srcUrl: `${env.MATOMO_BASE_URL}/matomo.js`,
  disabled: false,
  heartBeat: {
    active: true,
    seconds: 10,
  },
  linkTracking: true,
});

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {/* @ts-ignore */}
        <MatomoProvider value={instance}>
          <AppWrapper>
            <App />
          </AppWrapper>
        </MatomoProvider>
      </ThemeProvider>
    </Provider>
  </React.StrictMode>,
);
