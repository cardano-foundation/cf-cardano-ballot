import { cleanup } from "@testing-library/react";
import "@testing-library/jest-dom";

describe("App", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test.todo("should render routes", async () => {});

  test.todo("should render toast provider", async () => {});
});
