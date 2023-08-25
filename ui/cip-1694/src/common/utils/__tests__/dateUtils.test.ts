import { eventMock_active } from 'test/mocks';
import { formatUTCDate, getDateAndMonth } from '../dateUtils';

describe('dateUtils: ', () => {
  test('formatUTCDate', () => {
    expect(formatUTCDate(eventMock_active.eventStart.toString())).toEqual('2023-07-06 00:00 UTC');
  });
  test('getDateAndMonth', () => {
    expect(getDateAndMonth(eventMock_active.eventStart.toString())).toEqual('6 July');
  });
});
