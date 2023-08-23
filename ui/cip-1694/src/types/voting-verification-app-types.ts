/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-08-23 10:07:22.

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

export interface VoteVerificationService {
}

export interface VoteVerificationService__BeanDefinitions {
}

export interface Problem {
    instance: URI;
    type: URI;
    parameters: { [index: string]: any };
    status: StatusType;
    title: string;
    detail: string;
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
    lazy: boolean;
    async: boolean;
}

export interface Comparable<T> {
}

export interface Iterable<T> {
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type MerkleProofType = "L" | "R";
