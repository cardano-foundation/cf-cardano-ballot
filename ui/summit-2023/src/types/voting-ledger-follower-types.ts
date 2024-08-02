/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-11-20 08:56:34.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface Account {
    stakeAddress: string;
    epochNo: number;
    votingPower: string;
    votingPowerAsset: VotingPowerAsset;
    network: CardanoNetwork;
}

export interface AccountBuilder {
}

export interface CategoryResultsDatum {
    eventId: string;
    organiser: string;
    categoryId: string;
    results: { [index: string]: number };
}

export interface CategoryResultsDatumConverter extends BasePlutusDataConverter {
}

export interface ChainTip {
    absoluteSlot: number;
    hash: string;
    epochNo: number;
    network: CardanoNetwork;
    synced: boolean;
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

export interface EventAdditionalInfo {
    id: string;
    notStarted: boolean;
    started: boolean;
    finished: boolean;
    active: boolean;
    proposalsReveal: boolean;
    commitmentsWindowOpen: boolean;
}

export interface HydraTally {
    contractName: string;
    contractDescription: string;
    contractVersion: string;
    compiledScript: string;
    compiledScriptHash: string;
    compilerName: string;
    compilerVersion: string;
    plutusVersion: string;
    verificationKeyHashes: string;
    description?: string;
    verificationKeysHashesAsList: string[];
}

export interface HydraTallyBuilder {
}

export interface IsMerkleRootPresentResult {
    isPresent: boolean;
    network: CardanoNetwork;
}

export interface TallyResults {
    tallyName: string;
    tallyDescription?: string;
    tallyType: TallyType;
    eventId: string;
    categoryId: string;
    results: { [index: string]: number };
    metadata: { [index: string]: any };
}

export interface TallyResultsBuilder {
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

export interface Utxo {
    address: string;
    tx_hash: string;
    tx_index: number;
    inline_datum: string;
}

export interface UtxoBuilder {
    address: string;
    tx_hash: string;
    tx_index: number;
    inline_datum: string;
}

export interface AbstractTimestampEntity {
    createdAt: Date;
    updatedAt: Date;
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
    organisers: string;
    votingEventType: VotingEventType;
    votingPowerAsset?: VotingPowerAsset;
    allowVoteChanging: boolean;
    highLevelEventResultsWhileVoting: boolean;
    highLevelCategoryResultsWhileVoting: boolean;
    categoryResultsWhileVoting: boolean;
    startEpoch?: number;
    endEpoch?: number;
    snapshotEpoch?: number;
    proposalsRevealEpoch?: number;
    startSlot?: number;
    endSlot?: number;
    proposalsRevealSlot?: number;
    version: SchemaVersion;
    categories: Category[];
    absoluteSlot: number;
    tallies: Tally[];
    valid: boolean;
}

export interface EventBuilder {
}

export interface EventResultsCategoryResultsUtxoData {
    id: string;
    address: string;
    txHash: string;
    index: number;
    inlineDatum: string;
    absoluteSlot: number;
    witnessesHashes: string[];
}

export interface EventResultsCategoryResultsUtxoDataBuilder {
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

export interface Tally extends AbstractTimestampEntity {
    name: string;
    description?: string;
    type: TallyType;
    hydraTallyConfig: HydraTally;
}

export interface TallyBuilder {
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
    organisers: string;
    votingEventType: VotingEventType;
    startSlot?: number;
    endSlot?: number;
    proposalsRevealSlot?: number;
    startEpoch?: number;
    eventStartDate?: Date;
    eventEndDate?: Date;
    proposalsRevealDate?: Date;
    snapshotTime?: Date;
    endEpoch?: number;
    snapshotEpoch?: number;
    proposalsRevealEpoch?: number;
    categories: CategoryPresentation[];
    tallies: TallyPresentation[];
    active: boolean;
    notStarted: boolean;
    proposalsReveal: boolean;
    commitmentsWindowOpen: boolean;
    allowVoteChanging: boolean;
    finished: boolean;
    highLevelEventResultsWhileVoting: boolean;
    highLevelCategoryResultsWhileVoting: boolean;
    categoryResultsWhileVoting: boolean;
    started: boolean;
}

export interface EventPresentationBuilder {
}

export interface HydraTallyConfigPresentation {
    contractName: string;
    compiledScript: string;
    compiledScriptHash: string;
    contractVersion: string;
    compilerName: string;
    compilerVersion: string;
    plutusVersion: string;
    verificationKeys: string[];
}

export interface HydraTallyConfigPresentationBuilder {
}

export interface ProposalPresentation {
    id: string;
    name?: string;
}

export interface ProposalPresentationBuilder {
}

export interface TallyPresentation {
    name: string;
    description?: string;
    type: TallyType;
    config?: HydraTallyConfigPresentation;
}

export interface TallyPresentationBuilder {
}

export interface CategoryRegistrationEnvelope {
    type: OnChainEventType;
    id: string;
    event: string;
    schemaVersion: SchemaVersion;
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
    organisers: string;
    schemaVersion: SchemaVersion;
    creationSlot: number;
    allowVoteChanging: boolean;
    highLevelEventResultsWhileVoting: boolean;
    highLevelCategoryResultsWhileVoting: boolean;
    categoryResultsWhileVoting: boolean;
    votingEventType: VotingEventType;
    votingPowerAsset?: VotingPowerAsset;
    startSlot?: number;
    endSlot?: number;
    proposalsRevealSlot?: number;
    startEpoch?: number;
    endEpoch?: number;
    snapshotEpoch?: number;
    proposalsRevealEpoch?: number;
    tallies: TallyRegistrationEnvelope[];
}

export interface EventRegistrationEnvelopeBuilder {
}

export interface HydraTallyRegistrationEnvelope {
    contractName: string;
    contractDesc: string;
    contractVersion: string;
    compiledScript: string;
    compiledScriptHash: string;
    compilerName: string;
    compilerVersion: string;
    plutusVersion: string;
    verificationKeys: string[];
}

export interface HydraTallyRegistrationEnvelopeBuilder {
}

export interface ProposalEnvelope {
    id: string;
    name: string;
}

export interface ProposalEnvelopeBuilder {
}

export interface TallyRegistrationEnvelope {
    name: string;
    description: string;
    type: TallyType;
    config: any;
}

export interface TallyRegistrationEnvelopeBuilder {
}

export interface AccountService {
}

export interface DefaultAccountService extends AccountService {
}

export interface DefaultAccountService__BeanDefinitions {
}

export interface BlockchainDataChainTipService {
    chainTip: Either<Problem, ChainTip>;
}

export interface BlockchainDataStakePoolService {
}

export interface BlockchainDataTransactionDetailsService {
}

export interface BlockchainDataUtxoStateReader {
}

export interface FixedBlockchainDataStakePoolService extends BlockchainDataStakePoolService {
}

export interface BackendServiceBlockchainDataChainTipService extends BlockchainDataChainTipService {
}

export interface BackendServiceBlockchainDataCurrentStakePoolService extends BlockchainDataStakePoolService {
}

export interface BackendServiceBlockchainDataStakePoolService extends BlockchainDataStakePoolService {
}

export interface BackendServiceBlockchainDataTransactionDetailsService extends BlockchainDataTransactionDetailsService {
}

export interface CborService {
}

export interface CborService__Autowiring {
}

export interface CborService__BeanDefinitions {
}

export interface ChainSyncService {
}

export interface Noop extends ChainSyncService {
}

export interface DefaultChainSyncService extends ChainSyncService {
}

export interface SyncStatus {
    isSynced: boolean;
    diff?: number;
    ex?: Exception;
}

export interface CustomEpochService {
}

export interface CustomEpochService__BeanDefinitions {
}

export interface CustomEraService {
}

export interface CustomEraService__Autowiring {
}

export interface CustomEraService__BeanDefinitions {
}

export interface EventAdditionalInfoService {
}

export interface EventAdditionalInfoService__Autowiring {
}

export interface EventAdditionalInfoService__BeanDefinitions {
}

export interface YaciStoreTipHealthIndicator extends HealthIndicator {
}

export interface YaciStoreTipHealthIndicator__BeanDefinitions {
}

export interface CustomMetadataProcessor {
}

export interface CustomMetadataProcessor__Autowiring {
}

export interface CustomMetadataProcessor__BeanDefinitions {
}

export interface PlutusScriptLoader {
}

export interface PlutusScriptLoader__BeanDefinitions {
}

export interface ReferenceDataService {
}

export interface ReferenceDataService__BeanDefinitions {
}

export interface ReferencePresentationService {
}

export interface ReferencePresentationService__BeanDefinitions {
}

export interface VotingTallyService {
}

export interface VotingTallyService__BeanDefinitions {
}

export interface RollbackHandler {
}

export interface EventResultsUtxoDataService {
}

export interface EventResultsUtxoDataService__BeanDefinitions {
}

export interface MerkleRootHashService {
}

export interface MerkleRootHashService__BeanDefinitions {
}

export interface VotingPowerService {
}

export interface VotingPowerService__BeanDefinitions {
}

export interface YaciCustomMetadataStorage extends TxMetadataStorage {
}

export interface YaciCustomMetadataStorage__BeanDefinitions {
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

export interface BasePlutusDataConverter {
}

export interface Exception extends Throwable {
}

export interface HealthIndicator extends HealthContributor {
}

export interface TxMetadataStorage {
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

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type OnChainEventType = "COMMITMENTS" | "EVENT_REGISTRATION" | "CATEGORY_REGISTRATION";

export type SchemaVersion = "V1" | "V11";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type TallyType = "HYDRA";

export type Era = "Byron" | "Shelley" | "Allegra" | "Mary" | "Alonzo" | "Babbage";
