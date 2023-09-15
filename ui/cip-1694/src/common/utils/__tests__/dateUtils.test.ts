import { eventMock_active } from 'test/mocks';
import { formatUTCDate, getDateAndMonth } from '../dateUtils';

describe('dateUtils: ', () => {
  test('formatUTCDate', () => {
    expect(formatUTCDate(eventMock_active.eventStartDate.toString())).toEqual('2023-09-14 00:00 UTC');
  });
  test('getDateAndMonth', () => {
    expect(getDateAndMonth(eventMock_active.eventStartDate.toString())).toEqual('14 September');
  });
});
