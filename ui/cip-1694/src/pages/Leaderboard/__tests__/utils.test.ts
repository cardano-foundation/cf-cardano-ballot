import { eventMock_active } from 'test/mocks';
import { formatUTCDate, getPercentage } from '../utils';

describe('utils: ', () => {
  test('formatUTCDate', () => {
    expect(formatUTCDate(eventMock_active.eventStart.toString())).toEqual('2023 00:00 UTC');
  });
  test('getDateAndMonth', () => {
    expect(getPercentage(20, 100)).toEqual(20);
  });
});
