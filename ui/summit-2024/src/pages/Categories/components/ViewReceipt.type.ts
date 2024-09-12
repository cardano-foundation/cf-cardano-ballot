interface ViewReceiptProps {
  categoryId: string;
  close: () => void;
}
enum STATE {
  BASIC = "BASIC",
  PARTIAL = "PARTIAL",
  ROLLBACK = "ROLLBACK",
  FULL = "FULL",
}

export type { ViewReceiptProps };

export { STATE };
