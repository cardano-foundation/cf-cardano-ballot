import { VoteReceipt } from "../../../types/voting-app-types";

interface VoteCacheProps {
  categoryId: string,
  proposalId: string
}
interface VotesCacheProps {
  votes: VoteCacheProps[];
  receipts: {
    [categoryId: string]: VoteReceipt;
  };
}

export type { VoteCacheProps, VotesCacheProps };
