import { MerkleProof, VoteReceipt } from "types/voting-app-types";

export type VerifyModalProps = {
  onConfirm: (explorer: string) => void;
  opened: boolean;
};

export enum SECTIONS {
  VERIFY = "verify",
  CHOSE_EXPLORER = "chose_explorer",
}

export enum ERRORS {
  VERIFY = "verify",
  JSON = "json",
  UNSUPPORTED_EVENT = "UNSUPPORTED_EVENT",
}

export type voteProof = {
  transactionHash: MerkleProof["transactionHash"];
  steps: MerkleProof["steps"];
  rootHash: MerkleProof["rootHash"];
  coseSignature: VoteReceipt["coseSignature"];
  cosePublicKey: VoteReceipt["cosePublicKey"];
};
