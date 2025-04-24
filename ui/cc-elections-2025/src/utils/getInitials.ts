

export const getInitials = (name: string) => {
  const names = name.split(' ');
  return names.map((name) => name[0]).join('');
}
