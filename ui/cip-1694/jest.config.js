module.exports = {
  collectCoverage: true,
  collectCoverageFrom: ['src/**/*.ts', 'src/**/*.tsx'],
  coveragePathIgnorePatterns: ['node_modules', 'setupTests.ts', 'setupProxy.ts', 'env.ts', '.d.ts', 'types', 'test/*'],
  coverageThreshold: {
    global: {
      branches: 84,
      functions: 92,
      lines: 92,
      statements: 93,
    },
  },
  roots: ['<rootDir>/src'],
  testTimeout: 60000,
  testEnvironment: 'jsdom',
  globals: {
    IS_REACT_ACT_ENVIRONMENT: true,
  },
  setupFilesAfterEnv: ['./jest.setup.js'],
  transform: {
    '^.+\\.(ts|tsx)$': 'ts-jest',
  },
  moduleNameMapper: {
    '.*\\.(scss|sass|css|less)$': '<rootDir>/src/test/__mocks__/styleMock.js',
    '.*\\.(jpg|jpeg|png|gif|eot|otf|webp|ttf|woff|woff2)$': '<rootDir>/src/test/__mocks__/fileMock.js',
    '.*\\.(pdf)$': '<rootDir>/src/test/__mocks__/pdfFileMock.js',
    '.*\\.svg*$': '<rootDir>/src/test/__mocks__/svgMock.js',
    '^lodash-es$': 'lodash',
    '^common/(.*)': '<rootDir>/src/common/$1',
    '^pages/(.*)': '<rootDir>/src/pages/$1',
    '^components/(.*)': '<rootDir>/src/components/$1',
    '^test/(.*)': '<rootDir>/src/test/$1',
  },
};
