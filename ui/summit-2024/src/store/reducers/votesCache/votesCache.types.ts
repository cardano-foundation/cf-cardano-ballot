import { VoteReceipt } from "../../../types/voting-app-types";

interface VotesCacheProps {
  votes: {
    [categoryId: string]: string;
  };
  receipts: {
    [categoryId: string]: VoteReceipt;
  };
}

export type { VotesCacheProps };
