/* eslint-disable no-var */
var getItemMock = jest.fn();
var setItemMock = jest.fn();
var removeItemMock = jest.fn();
var clearMock = jest.fn();
import {
  saveUserInSession,
  USER_SESSION_KEY,
  getUserInSession,
  clearUserInSessionStorage,
  tokenIsExpired,
} from '../session';

const sessionStorageMock = (() => ({
  getItem: getItemMock,
  setItem: setItemMock,
  removeItem: removeItemMock,
  clear: clearMock,
}))();

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

describe('session: ', () => {
  test('saveUserInSession', () => {
    const accessToken = 'accessToken';
    const expiresAt = 'expiresAt';
    const session = { accessToken, expiresAt };
    saveUserInSession(session);
    expect(setItemMock).toBeCalledWith(USER_SESSION_KEY, JSON.stringify(session));
  });
  test('getUserInSession', () => {
    const accessToken = 'accessToken';
    const expiresAt = 'expiresAt';
    const session = { accessToken, expiresAt };

    getItemMock.mockReset();
    getItemMock.mockReturnValue(JSON.stringify(session));

    expect(getUserInSession()).toEqual(JSON.parse(JSON.stringify(session)));
  });
  test('clearUserInSessionStorage', () => {
    clearUserInSessionStorage();
    expect(removeItemMock).toBeCalledWith(USER_SESSION_KEY);
    expect(clearMock).toBeCalled();
  });
  test('tokenIsExpired', () => {
    const expiresAt = new Date().toLocaleString();
    expect(tokenIsExpired(expiresAt)).toBeTruthy();
  });
});
