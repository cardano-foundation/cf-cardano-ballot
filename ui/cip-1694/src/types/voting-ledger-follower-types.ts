/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-09-01 18:09:47.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface Account {
    stakeAddress: string;
    accountStatus: AccountStatus;
    epochNo: number;
    votingPower?: string;
    votingPowerAsset?: VotingPowerAsset;
    network: CardanoNetwork;
}

export interface AccountBuilder {
}

export interface ChainTip {
    absoluteSlot: number;
    hash: string;
    epochNo: number;
    network: CardanoNetwork;
}

export interface ChainTipBuilder {
}

export interface EraData {
    startAbsoluteSlot: number;
    endAbsoluteSlot: number;
    epochStartBlock: number;
    epochEndBlock: number;
    epochStartTime: number;
    epochEndTime: number;
    startEpochNo: number;
    endEpochNo: number;
    previousEra?: Era;
    nextEra?: Era;
}

export interface TransactionDetails {
    transactionHash: string;
    absoluteSlot: number;
    blockHash: string;
    transactionsConfirmations: number;
    finalityScore: FinalityScore;
    network: CardanoNetwork;
}

export interface TransactionDetailsBuilder {
}

export interface TransactionMetadataLabelCbor {
    tx_hash: string;
    slot: number;
    cbor_metadata: string;
}

export interface TransactionMetadataLabelCborBuilder {
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
    highLevelResultsWhileVoting: boolean;
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

export interface MerkleRootHash extends AbstractTimestampEntity {
    merkleRootHash: string;
    eventId: string;
    absoluteSlot: number;
}

export interface MerkleRootHashBuilder {
}

export interface Proposal extends AbstractTimestampEntity {
    id: string;
    name?: string;
    category: Category;
    absoluteSlot: number;
}

export interface ProposalBuilder {
}

export interface CategoryPresentation {
    id: string;
    gdprProtection: boolean;
    proposals: ProposalPresentation[];
}

export interface CategoryPresentationBuilder {
}

export interface EventPresentation {
    id: string;
    team: string;
    votingEventType: VotingEventType;
    startSlot?: number;
    endSlot?: number;
    startEpoch?: number;
    eventStartDate?: Date;
    eventEndDate?: Date;
    snapshotTime?: Date;
    endEpoch?: number;
    snapshotEpoch?: number;
    categories: CategoryPresentation[];
    active: boolean;
    allowVoteChanging: boolean;
    notStarted: boolean;
    finished: boolean;
    categoryResultsWhileVoting: boolean;
    highLevelResultsWhileVoting: boolean;
}

export interface EventPresentationBuilder {
}

export interface ProposalPresentation {
    id: string;
    name?: string;
}

export interface ProposalPresentationBuilder {
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
    highLevelResultsWhileVoting: boolean;
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

export interface AccountService {
}

export interface DefaultAccountService extends AccountService {
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
    chainTip: Either<Problem, ChainTip>;
}

export interface BlockchainDataStakePoolService {
}

export interface BlockchainDataTransactionDetailsService {
}

export interface FixedBlockchainDataStakePoolService extends BlockchainDataStakePoolService {
}

export interface BackendServiceBlockchainDataChainTipService extends BlockchainDataChainTipService {
}

export interface BackendServiceBlockchainDataStakePoolService extends BlockchainDataStakePoolService {
}

export interface BackendServiceBlockchainDataTransactionDetailsService extends BlockchainDataTransactionDetailsService {
}

export interface CborService {
}

export interface CborService__BeanDefinitions {
}

export interface ChainSyncService {
    syncStatus: SyncStatus;
}

export interface SyncStatus {
    isSynced: boolean;
    diff?: number;
    ex?: Exception;
}

export interface ChainSyncService__Autowiring {
}

export interface ChainSyncService__BeanDefinitions {
}

export interface CustomEpochService {
}

export interface CustomEpochService__Autowiring {
}

export interface CustomEpochService__BeanDefinitions {
}

export interface CustomEraService {
}

export interface CustomEraService__Autowiring {
}

export interface CustomEraService__BeanDefinitions {
}

export interface ExpirationService {
}

export interface ExpirationService__Autowiring {
}

export interface ExpirationService__BeanDefinitions {
}

export interface YaciStoreTipHealthIndicator extends HealthIndicator {
}

export interface YaciStoreTipHealthIndicator__BeanDefinitions {
}

export interface JsonService {
}

export interface JsonService__Autowiring {
}

export interface JsonService__BeanDefinitions {
}

export interface CustomMetadataProcessor {
}

export interface CustomMetadataProcessor__Autowiring {
}

export interface CustomMetadataProcessor__BeanDefinitions {
}

export interface CustomMetadataService {
}

export interface CustomMetadataService__Autowiring {
}

export interface CustomMetadataService__BeanDefinitions {
}

export interface ReferenceDataService {
}

export interface ReferenceDataService__Autowiring {
}

export interface ReferenceDataService__BeanDefinitions {
}

export interface ReferencePresentationService {
}

export interface ReferencePresentationService__BeanDefinitions {
}

export interface RollbackHandler {
}

export interface RollbackHandler__Autowiring {
}

export interface RollbackHandler__BeanDefinitions {
}

export interface MerkleRootHashService {
}

export interface MerkleRootHashService__BeanDefinitions {
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
    detail: string;
    title: string;
}

export interface Serializable {
}

export interface Exception extends Throwable {
}

export interface HealthIndicator extends HealthContributor {
}

export interface URI extends Comparable<URI>, Serializable {
}

export interface StatusType {
    statusCode: number;
    reasonPhrase: string;
}

export interface Value<T> extends Iterable<T> {
    singleValued: boolean;
    empty: boolean;
    orNull: T;
    lazy: boolean;
    async: boolean;
}

export interface Throwable extends Serializable {
    cause: Throwable;
    stackTrace: StackTraceElement[];
    message: string;
    suppressed: Throwable[];
    localizedMessage: string;
}

export interface StackTraceElement extends Serializable {
    classLoaderName: string;
    moduleName: string;
    moduleVersion: string;
    methodName: string;
    fileName: string;
    lineNumber: number;
    nativeMethod: boolean;
    className: string;
}

export interface HealthContributor {
}

export interface Comparable<T> {
}

export interface Iterable<T> {
}

export type AccountStatus = "ELIGIBLE" | "NOT_ELIGIBLE";

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type OnChainEventType = "COMMITMENTS" | "EVENT_REGISTRATION" | "CATEGORY_REGISTRATION";

export type SchemaVersion = "V1";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type Web3Action = "CAST_VOTE" | "VIEW_VOTE_RECEIPT" | "FULL_METADATA_SCAN";

export type Era = "Byron" | "Shelley" | "Allegra" | "Mary" | "Alonzo" | "Babbage";
