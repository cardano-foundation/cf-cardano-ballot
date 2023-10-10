export const monthNames = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
];

export const formatUTCDate = (date: string) => {
  if (!date) return '';

  const parsedDate = new Date(date);
  const monthName = monthNames[parsedDate.getUTCMonth()];

  const isoDate = parsedDate.toISOString();
  return `${isoDate.substring(0, 4)} ${monthName} ${isoDate.substring(5, 7)}th ${isoDate.substring(11, 16)} UTC`;
};



export const getMonthName = (index: number) => monthNames[index];

export const getDateAndMonth = (date: string) => {
  if (!date) return '';
  const isoDate = new Date(date).toISOString();
  return `${+isoDate.substring(8, 10)} ${getMonthName(+isoDate.substring(5, 7) - 1)}`;
};
