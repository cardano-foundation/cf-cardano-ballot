import { useState } from "react";

export const useToggle = (initialState: boolean): [boolean, () => void] => {
  const [isToggled, setIsToggled] = useState<boolean>(initialState);
  const toggle = () => setIsToggled(!isToggled);
  return [isToggled, toggle];
};
