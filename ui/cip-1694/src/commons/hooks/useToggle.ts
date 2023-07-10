import { useState } from 'react';

export const useToggle = (initialState: boolean) => {
  const [isToggled, setIsToggled] = useState<boolean>(initialState);
  const toggle = () => setIsToggled(!isToggled);
  return [isToggled, toggle];
};
