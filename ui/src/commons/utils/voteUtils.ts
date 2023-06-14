import { canonicalize } from "json-canonicalize";
import { TARGET_NETWORK } from "../constants/appConstants";

function sanitize(value: any) {
  if (value === null || typeof value === "undefined") {
    return "";
  } else {
    return value;
  }
}

export const slotFromTimestamp = (date: Date) => {
  return Math.floor(date.getTime() / 1000 - 1660003200);
};

export const buildCanonicalVoteInputJson = (voteInput: {
  option: any;
  voteId: any;
  voter: any;
}) => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    uri: sanitize("https://example.com/vote"),
    action: sanitize("CAST_VOTE"),
    actionText: sanitize("Submit Vote"),
    slot: `${slotFromTimestamp(startOfCurrentDay)}`,
    vote: {
      voteId: sanitize(sanitize(voteInput.voteId)),
      stakeAddress: sanitize(voteInput.voter),
      eventName: "CIP-1694-Pre-Ratification",
      categoryName: "Pre-Ratification",
      proposalName: sanitize(voteInput.option),
      network: TARGET_NETWORK,
      votedAt: `${slotFromTimestamp(startOfCurrentDay)}`,
    },
  });
};