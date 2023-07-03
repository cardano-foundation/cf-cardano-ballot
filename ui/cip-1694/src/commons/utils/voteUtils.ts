import { canonicalize } from "json-canonicalize";
import {
  TARGET_NETWORK,
  EVENT_ID,
  CATEGORY_ID,
} from "../constants/appConstants";

export const buildCanonicalVoteInputJson = (voteInput: {
  option: any;
  voteId: any;
  voter: any;
  slotNumber: string;
  votePower: string;
}) => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    uri: "https://evoting.cardano.org/voltaire",
    action: "CAST_VOTE",
    actionText: "Cast Vote",
    slot: voteInput.slotNumber,
    data: {
      voteId: voteInput.voteId,
      address: voteInput.voter,
      event: EVENT_ID,
      category: CATEGORY_ID,
      proposal: voteInput.option, //TODO: need an api agreement
      proposalText: voteInput.option,
      network: TARGET_NETWORK,
      votedAt: voteInput.slotNumber,
      votingPower: voteInput.votePower,
    },
  });
};
