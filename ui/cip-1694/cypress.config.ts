import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    supportFile: false,
    viewportWidth: 1024,
    viewportHeight: 768,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },

  component: {
    devServer: {
      framework: "create-react-app",
      bundler: "webpack",
    },
  },
});
