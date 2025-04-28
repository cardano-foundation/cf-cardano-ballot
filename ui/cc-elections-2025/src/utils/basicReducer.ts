export interface BasicReducer<T> {
  type: any;
  state: any;
  (prevState: T, newState: Partial<T>): T;
}

export const basicReducer = <T>(prevState: T, newState: Partial<T>): T => ({
  ...prevState,
  ...newState,
});
