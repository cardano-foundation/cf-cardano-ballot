import { useState } from 'react';

export const useToggle = (initialState: any) => {
	const [isToggled, setIsToggled] = useState(initialState);
	const toggle = () => setIsToggled(!isToggled);
	return [isToggled, toggle];
}