/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-08-31 14:58:35.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface L1MerkleCommitment {
    votes: Vote[];
    root: MerkleElement<Vote>;
    eventId: string;
}

export interface L1MerkleTree {
    root: MerkleElement<Vote>;
    rootHash: string;
    transactionHash: string;
    absoluteSlot: number;
}

export interface L1MerkleTreeBuilder {
}

export interface L1SubmissionData {
    txHash: string;
    slot: number;
}

export interface Leaderboard {
}

export interface ByCategory {
    category: string;
    proposals: { [index: string]: Votes };
}

export interface ByCategoryBuilder {
}

export interface ByEvent {
    event: string;
    totalVotesCount: number;
    totalVotingPower: string;
}

export interface ByEventBuilder {
}

export interface LeaderboardBuilder {
}

export interface Votes {
    votes: number;
    votingPower: string;
}

export interface TxBody {
    txDataHex: string;
}

export interface VoteReceipt {
    id: string;
    event: string;
    category: string;
    proposal: string;
    votingPower?: string;
    voterStakingAddress: string;
    coseSignature: string;
    cosePublicKey?: string;
    status: Status;
    merkleProof: MerkleProof;
    finalityScore?: FinalityScore;
    votedAtSlot: string;
}

export interface MerkleProof {
    transactionHash: string;
    absoluteSlot?: number;
    blockHash?: string;
    rootHash: string;
    steps: MerkleProofItem[];
}

export interface MerkleProofBuilder {
}

export interface MerkleProofItem {
    type: MerkleProofType;
    hash: string;
}

export interface MerkleProofItemBuilder {
}

export interface VoteReceiptBuilder {
}

export interface AbstractTimestampEntity {
    createdAt: Date;
    updatedAt: Date;
}

export interface Vote extends AbstractTimestampEntity {
    id: string;
    eventId: string;
    categoryId: string;
    proposalId: string;
    voterStakingAddress: string;
    coseSignature: string;
    cosePublicKey?: string;
    votingPower?: number;
    votedAtSlot: number;
}

export interface VoteBuilder {
}

export interface VoteMerkleProof extends AbstractTimestampEntity {
    voteId: string;
    eventId: string;
    rootHash: string;
    l1TransactionHash: string;
    proofItemsJson: string;
    invalidated: boolean;
    absoluteSlot: number;
}

export interface VoteMerkleProofBuilder {
}

export interface CIP93Envelope<T> {
    uri: string;
    action: string;
    actionText: string;
    slot: string;
    data: T;
}

export interface CIP93EnvelopeBuilder<T> {
}

export interface SignedWeb3Request {
    coseSignature: string;
    cosePublicKey?: string;
}

export interface SignedWeb3RequestBuilder {
}

export interface ViewVoteReceiptEnvelope {
    address: string;
    network: string;
    event: string;
    category: string;
}

export interface VoteEnvelope {
    id: string;
    address: string;
    event: string;
    category: string;
    proposal: string;
    proposalText?: string;
    network: string;
    votedAt: string;
    votingPower?: string;
}

export interface VoteEnvelopeBuilder {
}

export interface StakeAddressVerificationService {
}

export interface StakeAddressVerificationService__Autowiring {
}

export interface StakeAddressVerificationService__BeanDefinitions {
}

export interface BackendServiceBlockchainTransactionSubmissionService extends BlockchainTransactionSubmissionService {
}

export interface BlockchainTransactionSubmissionService {
}

export interface Noop extends BlockchainTransactionSubmissionService {
}

export interface CardanoSubmitApiBlockchainTransactionSubmissionService extends BlockchainTransactionSubmissionService {
}

export interface ExpirationService {
}

export interface ExpirationService__Autowiring {
}

export interface ExpirationService__BeanDefinitions {
}

export interface JsonService {
}

export interface JsonService__Autowiring {
}

export interface JsonService__BeanDefinitions {
}

export interface DefaultLeaderBoardService extends LeaderBoardService {
}

export interface DefaultLeaderBoardService__Autowiring {
}

export interface DefaultLeaderBoardService__BeanDefinitions {
}

export interface LeaderBoardService {
}

export interface MerkleProofSerdeService {
}

export interface MerkleProofSerdeService__Autowiring {
}

export interface MerkleProofSerdeService__BeanDefinitions {
}

export interface VoteCommitmentService {
}

export interface VoteCommitmentService__Autowiring {
}

export interface VoteCommitmentService__BeanDefinitions {
}

export interface VoteMerkleProofService {
}

export interface VoteMerkleProofService__Autowiring {
}

export interface VoteMerkleProofService__BeanDefinitions {
}

export interface DefaultTransactionSubmissionService extends TransactionSubmissionService {
}

export interface DefaultTransactionSubmissionService__Autowiring {
}

export interface DefaultTransactionSubmissionService__BeanDefinitions {
}

export interface L1SubmissionService {
}

export interface L1SubmissionService__Autowiring {
}

export interface L1SubmissionService__BeanDefinitions {
}

export interface L1TransactionCreator {
}

export interface L1TransactionCreator__Autowiring {
}

export interface L1TransactionCreator__BeanDefinitions {
}

export interface MetadataSerialiser {
}

export interface MetadataSerialiser__BeanDefinitions {
}

export interface TransactionSubmissionService {
}

export interface DefaultVoteService extends VoteService {
}

export interface DefaultVoteService__Autowiring {
}

export interface DefaultVoteService__BeanDefinitions {
}

export interface VoteService {
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

export interface MerkleElement<T> {
    empty: boolean;
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

export type OnChainEventType = "COMMITMENTS" | "EVENT_REGISTRATION" | "CATEGORY_REGISTRATION";

export type SchemaVersion = "V1";

export type MerkleProofType = "Left" | "Right";

export type Status = "BASIC" | "PARTIAL" | "ROLLBACK" | "FULL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type Web3Action = "CAST_VOTE" | "VIEW_VOTE_RECEIPT" | "FULL_METADATA_SCAN";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";
