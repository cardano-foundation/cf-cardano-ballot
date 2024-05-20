import { capitalizeFirstLetter } from "../../utils/utils";

const ERRORS = {
  INVALID_NETWORK: "Network not valid, please check your wallet configuration",
  EVENT_NOT_FOUND: "Event not found",
  EVENT_IS_NOT_ACTIVE: "Event is not active yet",
  VOTE_NOT_FOUND: "Vote not found",
  VOTE_CANNOT_BE_CHANGED: "Vote cannot be changed",
  USER_ALREADY_VERIFIED: "User already verified",
  EVENT_ALREADY_FINISHED: "Event already finished",
  INVALID_PHONE_NUMBER: "Invalid phone number",
  PHONE_ALREADY_USED: "Phone already used",
  INVALID_VERIFICATION_CODE: "Invalid verification code",
  PENDING_USER_VERIFICATION_NOT_FOUND: "Pending user verification not found",
  MAX_VERIFICATION_ATTEMPTS_REACHED: "Max verification attempts reached",
  ACTION_NOT_ALLOWED: "Action not allowed",
  VOTING_RESULTS_NOT_AVAILABLE: "Voting results not available",
  INVALID_CIP30_DATA_SIGNATURE: "Invalid wallet signature",
  "INVALID_CIP-30-SIGNATURE": "Invalid wallet signature",
};

const parseError = (errorMessage: string) => {
  return Object.keys(ERRORS).includes(errorMessage)
    ? ERRORS[errorMessage]
    : capitalizeFirstLetter(errorMessage);
};

export { ERRORS, parseError };
