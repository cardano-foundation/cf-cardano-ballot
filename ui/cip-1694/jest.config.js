module.exports = {
  roots: ['<rootDir>/src'],
  testTimeout: 60000,
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['./jest.setup.js'],
  transform: {
    '^.+\\.(ts|tsx)$': 'ts-jest',
  },
  moduleNameMapper: {
    '.*\\.(scss|sass|css|less)$': '<rootDir>/test/__mocks__/styleMock.js',
    '.*\\.(jpg|jpeg|png|gif|eot|otf|webp|ttf|woff|woff2)$': '<rootDir>/test/__mocks__/fileMock.js',
    '.*\\.svg*$': '<rootDir>/test/__mocks__/svgMock.js',
    '^lodash-es$': 'lodash',
    '^common/(.*)': '<rootDir>/src/common/$1',
    '^pages/(.*)': '<rootDir>/src/pages/$1',
    '^components/(.*)': '<rootDir>/src/components/$1',
  },
};
