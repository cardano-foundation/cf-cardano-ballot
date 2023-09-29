import { cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

describe('App', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });
  describe('verify section', () => {
    test.todo('should render proper state');
    test.todo('should handle not valid JSON error');
    test.todo('should handle unsuported event error');
    test.todo('should handle other errors');
    test.todo('should verify vote and switch sections');
  });
  describe('chose explorer section', () => {
    test.todo('should render proper state');
    test.todo('should handle explorer selection');
    test.todo('should navigate to success modal');
  });
});
