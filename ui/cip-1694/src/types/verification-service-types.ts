/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-08-07 17:31:36.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface TransactionMetadataLabelCbor {
    tx_hash: string;
    cbor_metadata: string;
}

export interface TransactionMetadataLabelCborBuilder {
}

export interface Vote {
    coseSignature: string;
    cosePublicKey?: string;
}

export interface VoteBuilder {
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
    absoluteSlot: number;
    categories: Category[];
    valid: boolean;
}

export interface EventBuilder {
}

export interface MerkleRootHash extends AbstractTimestampEntity {
    merkleRootHash: string;
    eventId: string;
    absoluteSlot: number;
}

export interface MerkleRootHashBuilder {
}

export interface Proposal extends AbstractTimestampEntity {
    id: string;
    name: string;
    category: Category;
    absoluteSlot: number;
}

export interface ProposalBuilder {
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
    endEpoch?: number;
    snapshotEpoch?: number;
    categories: CategoryReference[];
    active: boolean;
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

export interface CommitmentsEnvelope {
    type: OnChainEventType;
    schemaVersion: string;
    creationSlot: number;
    commitments: { [index: string]: { [index: string]: string } };
}

export interface CommitmentsEnvelopeBuilder {
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

export interface ProposalEnvelope {
    id: string;
    name: string;
}

export interface ProposalEnvelopeBuilder {
}

export interface CborService {
}

export interface CborService__BeanDefinitions {
}

export interface VoteVerificationService {
}

export interface VoteVerificationService__Autowiring {
}

export interface VoteVerificationService__BeanDefinitions {
}

export interface CustomMetadataProcessor {
}

export interface CustomMetadataProcessor__Autowiring {
}

export interface CustomMetadataProcessor__BeanDefinitions {
}

export interface CustomMetadataStorage extends TxMetadataStorageImpl {
}

export interface CustomMetadataStorage__Autowiring {
}

export interface CustomMetadataStorage__BeanDefinitions {
}

export interface MetadataEventHandler {
}

export interface MetadataEventHandler__Autowiring {
}

export interface MetadataEventHandler__BeanDefinitions {
}

export interface ReferenceDataService {
}

export interface ReferenceDataService__Autowiring {
}

export interface ReferenceDataService__BeanDefinitions {
}

export interface RollbackHandler {
}

export interface RollbackHandler__Autowiring {
}

export interface RollbackHandler__BeanDefinitions {
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

export interface TxMetadataStorageImpl extends TxMetadataStorage {
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

export interface TxMetadataStorage {
}

export interface Comparable<T> {
}

export interface Iterable<T> {
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type OnChainEventType = "COMMITMENTS" | "EVENT_REGISTRATION" | "CATEGORY_REGISTRATION";

export type SchemaVersion = "V1";

export type MerkleProofType = "Left" | "Right";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";
