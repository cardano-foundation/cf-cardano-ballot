import { VoteReceipt } from "../../../types/voting-app-types";

interface ReceiptsCacheProps {
  receipts: {
    [categoryId: string]: VoteReceipt;
  };
}

export type { ReceiptsCacheProps };
