import { cleanup } from "@testing-library/react";
import "@testing-library/jest-dom";

describe("App", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });
  describe("verify section", () => {
    test.todo("should render proper state", async () => {});
    test.todo("should handle not valid JSON error", async () => {});
    test.todo("should handle unsuported event error", async () => {});
    test.todo("should handle other errors", async () => {});
    test.todo("should verify vote and switch sections", async () => {});
  });
  describe("chose explorer section", () => {
    test.todo("should render proper state", async () => {});
    test.todo("should handle explorer selection", async () => {});
    test.todo("should navigate to success modal", async () => {});
  });
});
