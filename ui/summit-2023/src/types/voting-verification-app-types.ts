/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-09-05 15:59:25.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface CoseWrappedVote {
    coseSignature: string;
    cosePublicKey?: string;
}

export interface CoseWrappedVoteBuilder {
}

export interface VoteVerificationRequest {
    rootHash: string;
    voteCoseSignature: string;
    voteCosePublicKey?: string;
    steps?: MerkleProofItem[];
}

export interface MerkleProofItem {
    type: MerkleProofType;
    hash: string;
}

export interface MerkleProofItemBuilder {
}

export interface VoteVerificationRequestBuilder {
}

export interface VoteVerificationResult {
    network: CardanoNetwork;
    verified: boolean;
}

export interface VoteVerificationService {
}

export interface VoteVerificationService__BeanDefinitions {
}

export interface Problem {
    instance: URI;
    type: URI;
    parameters: { [index: string]: any };
    status: StatusType;
    detail: string;
    title: string;
}

export interface Serializable {
}

export interface URI extends Comparable<URI>, Serializable {
}

export interface StatusType {
    statusCode: number;
    reasonPhrase: string;
}

export interface Value<T> extends Iterable<T> {
    empty: boolean;
    singleValued: boolean;
    orNull: T;
    async: boolean;
    lazy: boolean;
}

export interface Comparable<T> {
}

export interface Iterable<T> {
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type MerkleProofType = "L" | "R";
