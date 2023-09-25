const removeFromLocalStorage = (key: string): void => {
  localStorage.removeItem(key);
};

export { removeFromLocalStorage };
