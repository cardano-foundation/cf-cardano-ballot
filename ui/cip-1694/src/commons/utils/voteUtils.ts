import { canonicalize } from "json-canonicalize";
import {
  TARGET_NETWORK,
  EVENT_ID,
  CATEGORY_ID,
} from "../constants/appConstants";
import { eVoteService } from "../api/voteService";

function sanitize(value: any) {
  if (value === null || typeof value === "undefined") {
    return "";
  } else {
    return value;
  }
}

export const slotFromTimestamp = async () => {
  try {
    const response = await eVoteService.getSlotNumber();
    console.log(response.absoluteSlot);
    return response.absoluteSlot;
  } catch (e: any) {
    if (e.response === 400) {
      console.log(e);
    }
  }
};

export const votingPowerOfUser = async () => {
  try {
    const response = await eVoteService.getVotingPower();
    console.log(response.votingPower);
    return response.votingPower;
  } catch (e: any) {
    if (e.response === 400) {
      console.log(e);
    }
  }
};

export const buildCanonicalVoteInputJson = (voteInput: {
  option: any;
  voteId: any;
  voter: any;
}) => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    uri: "https://evoting.cardano.org/voltaire",
    action: "CAST_VOTE",
    actionText: "Cast Vote",
    slot: slotFromTimestamp(), // not a string
    data: {
      id: sanitize(voteInput.voteId),
      address: sanitize(voteInput.voter),
      event: EVENT_ID,
      category: CATEGORY_ID,
      proposal: sanitize(voteInput.option),
      network: TARGET_NETWORK,
      votedAt: slotFromTimestamp(),
      votingPower: votingPowerOfUser(),
    },
  });
};
