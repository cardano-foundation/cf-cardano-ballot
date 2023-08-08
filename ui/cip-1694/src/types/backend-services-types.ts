/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-08-03 21:01:18.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface Account {
    stakeAddress: string;
    votingPower?: string;
    votingPowerAsset?: VotingPowerAsset;
}

export interface AccountBuilder {
}

export interface ChainTip {
    absoluteSlot: number;
    hash: string;
    epochNo: number;
}

export interface ChainTipBuilder {
}

export interface L1MerkleCommitment {
    votes: Vote[];
    root: MerkleElement<Vote>;
    event: Event;
}

export interface L1MerkleTree {
    root: MerkleElement<Vote>;
    rootHash: string;
    transactionHash: string;
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

export interface TransactionDetails {
    transactionHash: string;
    absoluteSlot: number;
    blockHash: string;
    transactionsConfirmations: number;
    finalityScore: FinalityScore;
}

export interface TransactionDetailsBuilder {
}

export interface TransactionMetadataLabelCbor {
    tx_hash: string;
    cbor_metadata: string;
}

export interface TransactionMetadataLabelCborBuilder {
}

export interface TxBody {
    txDataHex: string;
}

export interface VoteReceipt {
    id: string;
    event: string;
    category: string;
    proposal: string;
    votingPower: string;
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
}

export interface Category extends AbstractTimestampEntity {
    id: string;
    gdprProtection: boolean;
    version: SchemaVersion;
    event: Event;
    proposals: Proposal[];
    absoluteSlot: number;
    valid: boolean;
}

export interface CategoryBuilder {
}

export interface Event extends AbstractTimestampEntity {
    id: string;
    team: string;
    votingEventType: VotingEventType;
    votingPowerAsset?: VotingPowerAsset;
    allowVoteChanging: boolean;
    categoryResultsWhileVoting: boolean;
    startEpoch?: number;
    endEpoch?: number;
    startSlot?: number;
    endSlot?: number;
    snapshotEpoch?: number;
    version: SchemaVersion;
    categories: Category[];
    absoluteSlot: number;
    valid: boolean;
}

export interface EventBuilder {
}

export interface Proposal extends AbstractTimestampEntity {
    id: string;
    name: string;
    category: Category;
    absoluteSlot: number;
}

export interface ProposalBuilder {
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

export interface CategoryReference {
    id: string;
    gdprProtection: boolean;
    presentationName: string;
    proposals: ProposalReference[];
}

export interface CategoryReferenceBuilder {
}

export interface EventReference {
    id: string;
    team: string;
    presentationName: string;
    votingEventType: VotingEventType;
    startSlot?: number;
    endSlot?: number;
    startEpoch?: number;
    eventStart: Date;
    eventEnd: Date;
    snapshotTime?: Date;
    endEpoch?: number;
    snapshotEpoch?: number;
    categories: CategoryReference[];
    active: boolean;
    finished: boolean;
}

export interface EventReferenceBuilder {
}

export interface ProposalReference {
    id: string;
    name: string;
    presentationName: string;
}

export interface ProposalReferenceBuilder {
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

export interface CategoryRegistrationEnvelope {
    type: OnChainEventType;
    name: string;
    event: string;
    schemaVersion: string;
    creationSlot: number;
    gdprProtection: boolean;
    allowVoteChanging: boolean;
    categoryResultsWhileVoting: boolean;
    proposals: ProposalEnvelope[];
}

export interface CategoryRegistrationEnvelopeBuilder {
}

export interface EventRegistrationEnvelope {
    type: OnChainEventType;
    name: string;
    team: string;
    schemaVersion: string;
    creationSlot: number;
    allowVoteChanging: boolean;
    categoryResultsWhileVoting: boolean;
    votingEventType: VotingEventType;
    votingPowerAsset?: VotingPowerAsset;
    startEpoch?: number;
    endEpoch?: number;
    startSlot?: number;
    endSlot?: number;
    snapshotEpoch?: number;
}

export interface EventRegistrationEnvelopeBuilder {
}

export interface FullMetadataScanEnvelope {
    address: string;
    network: string;
}

export interface ProposalEnvelope {
    id: string;
    name: string;
}

export interface ProposalEnvelopeBuilder {
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

export interface AccountService {
}

export interface DefaultAccountService extends AccountService {
}

export interface DefaultAccountService__Autowiring {
}

export interface DefaultAccountService__BeanDefinitions {
}

export interface StakeAddressVerificationService {
}

export interface StakeAddressVerificationService__Autowiring {
}

export interface StakeAddressVerificationService__BeanDefinitions {
}

export interface BlockchainDataChainTipService {
    chainTip: ChainTip;
}

export interface BlockchainDataMetadataService {
}

export interface BlockchainDataStakePoolService {
}

export interface BlockchainDataTransactionDetailsService {
}

export interface BlockchainTransactionSubmissionService {
}

export interface Noop extends BlockchainTransactionSubmissionService {
}

export interface AbstractBlockfrostService {
}

export interface BlockfrostBlockchainDataMetadataService extends AbstractBlockfrostService, BlockchainDataMetadataService {
}

export interface BlockfrostBlockchainDataStakePoolService extends AbstractBlockfrostService, BlockchainDataStakePoolService {
}

export interface BlockfrostBlockchainDataTipService extends AbstractBlockfrostService, BlockchainDataChainTipService {
}

export interface BlockfrostBlockchainDataTransactionDetailsService extends AbstractBlockfrostService, BlockchainDataTransactionDetailsService {
}

export interface BlockfrostTransactionSubmissionService extends AbstractBlockfrostService, BlockchainTransactionSubmissionService {
}

export interface CardanoSubmitApiBlockchainTransactionSubmissionService extends BlockchainTransactionSubmissionService {
}

export interface CardanoSubmitApiBlockchainTransactionSubmissionService__Autowiring {
}

export interface CardanoSubmitApiBlockchainTransactionSubmissionService__BeanDefinitions {
}

export interface CborService {
}

export interface CborService__BeanDefinitions {
}

export interface CustomEpochService {
}

export interface CustomEpochService__Autowiring {
}

export interface CustomEpochService__BeanDefinitions {
}

export interface ExpirationService {
}

export interface ExpirationService__Autowiring {
}

export interface ExpirationService__BeanDefinitions {
}

export interface LocalisationService {
}

export interface LocalisationService__BeanDefinitions {
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

export interface MerkleTreeService {
}

export interface MerkleTreeService__Autowiring {
}

export interface MerkleTreeService__BeanDefinitions {
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

export interface CustomMetadataProcessor {
}

export interface CustomMetadataProcessor__Autowiring {
}

export interface CustomMetadataProcessor__BeanDefinitions {
}

export interface MetadataService {
}

export interface MetadataService__Autowiring {
}

export interface MetadataService__BeanDefinitions {
}

export interface ReferenceDataService {
}

export interface ReferenceDataService__Autowiring {
}

export interface ReferenceDataService__BeanDefinitions {
}

export interface ReferencePresentationService {
}

export interface ReferencePresentationService__Autowiring {
}

export interface ReferencePresentationService__BeanDefinitions {
}

export interface RollbackHandler {
}

export interface RollbackHandler__Autowiring {
}

export interface RollbackHandler__BeanDefinitions {
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

export interface DefaultVoteService extends VoteService {
}

export interface DefaultVoteService__Autowiring {
}

export interface DefaultVoteService__BeanDefinitions {
}

export interface VoteService {
}

export interface VotingPowerService {
}

export interface VotingPowerService__Autowiring {
}

export interface VotingPowerService__BeanDefinitions {
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

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type MerkleProofType = "Left" | "Right";

export type Status = "BASIC" | "PARTIAL" | "ROLLBACK" | "FULL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type Web3Action = "CAST_VOTE" | "VIEW_VOTE_RECEIPT" | "FULL_METADATA_SCAN";
