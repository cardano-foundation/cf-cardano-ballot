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
    slot: `${slotFromTimestamp(startOfCurrentDay)}`, // not a string
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

/* {
  "uri": "https://evoting.cardano.org/voltaire",
  "action": "CAST_VOTE",
  "actionText": "Cast Vote",
  "slot": 123,
  "data": {
        "id": "e21faf06-8b77-4474-a73b-2226fcdb7329",
        "address": "stake1u8uekde7k8x8n9lh0zjnhymz66sqdpa0ms02z8cshajptac0d3j32",
        "event": "CIP-1694_Pre_Ratification_9D06",
        "category": "CIP-1694_Pre_Ratification",
        "proposal": "YES",
        "network": "PREPROD",
        "votedAt": 123,
        "votingPower": 12344444
  }
} */