export interface UserState {
  isLoggedIn: boolean;
  isVerified: boolean;
  termsAndPrivacy: boolean;
  error: string;
  user?: any;
}
