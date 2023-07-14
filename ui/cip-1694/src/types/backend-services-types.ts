/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-07-13 01:33:28.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface Account {
    stakeAddress: string;
    votingPower: string;
    votingPowerAsset: VotingPowerAsset;
}

export interface AccountBuilder {
}

export interface ChainTip {
    absoluteSlot: number;
    epochNo: number;
    cardanoNetwork: CardanoNetwork;
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
    votes: Votes;
}

export interface ByEventBuilder {
}

export interface LeaderboardBuilder {
}

export interface Votes {
    votes: number;
    votingPower: number;
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
    proposalText: string;
    votingPower: string;
    voterStakingAddress: string;
    coseSignature: string;
    cosePublicKey: string;
    cardanoNetwork: CardanoNetwork;
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
    valid: boolean;
}

export interface CategoryBuilder {
}

export interface Event extends AbstractTimestampEntity {
    id: string;
    team: string;
    votingEventType: VotingEventType;
    votingPowerAsset: VotingPowerAsset;
    allowVoteChanging: boolean;
    categoryResultsWhileVoting: boolean;
    startEpoch: number;
    endEpoch: number;
    startSlot: number;
    endSlot: number;
    snapshotEpoch: number;
    version: SchemaVersion;
    categories: Category[];
    valid: boolean;
}

export interface EventBuilder {
}

export interface Proposal extends AbstractTimestampEntity {
    id: string;
    name: string;
    category: Category;
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
    cosePublicKey: string;
    votingPower: number;
    network: CardanoNetwork;
    votedAtSlot: number;
}

export interface VoteMerkleProof extends AbstractTimestampEntity {
    voteId: string;
    eventId: string;
    rootHash: string;
    l1TransactionHash: string;
    proofItemsJson: string;
    invalidated: boolean;
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

export interface LoginEnvelope {
    event: string;
    network: string;
    role: string;
}

export interface LoginEnvelopeBuilder {
}

export interface ProposalEnvelope {
    id: string;
    name: string;
}

export interface ProposalEnvelopeBuilder {
}

export interface SignedWeb3Request {
    coseSignature: string;
    cosePublicKey: string;
}

export interface SignedWeb3RequestBuilder {
}

export interface VoteEnvelope {
    id: string;
    address: string;
    event: string;
    category: string;
    proposal: string;
    proposalText: string;
    network: string;
    votedAt: string;
    votingPower: string;
}

export interface VoteEnvelopeBuilder {
}

export interface RollbackHandler {
}

export interface RollbackHandler__Autowiring {
}

export interface RollbackHandler__BeanDefinitions {
}

export interface RollbackHandler__TestContext001_Autowiring {
}

export interface RollbackHandler__TestContext001_BeanDefinitions {
}

export interface AccountService {
}

export interface DefaultAccountService extends AccountService {
}

export interface DefaultAccountService__Autowiring {
}

export interface DefaultAccountService__BeanDefinitions {
}

export interface DefaultAccountService__TestContext001_Autowiring {
}

export interface DefaultAccountService__TestContext001_BeanDefinitions {
}

export interface StakeAddressVerificationService {
}

export interface StakeAddressVerificationService__Autowiring {
}

export interface StakeAddressVerificationService__BeanDefinitions {
}

export interface StakeAddressVerificationService__TestContext001_Autowiring {
}

export interface StakeAddressVerificationService__TestContext001_BeanDefinitions {
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

export interface CardanoSubmitApiBlockchainTransactionSubmissionService__TestContext001_Autowiring {
}

export interface CardanoSubmitApiBlockchainTransactionSubmissionService__TestContext001_BeanDefinitions {
}

export interface CborService {
}

export interface CborService__BeanDefinitions {
}

export interface CborService__TestContext001_BeanDefinitions {
}

export interface ExpirationService {
}

export interface ExpirationService__Autowiring {
}

export interface ExpirationService__BeanDefinitions {
}

export interface ExpirationService__TestContext001_Autowiring {
}

export interface ExpirationService__TestContext001_BeanDefinitions {
}

export interface LocalisationService {
}

export interface LocalisationService__BeanDefinitions {
}

export interface LocalisationService__TestContext001_BeanDefinitions {
}

export interface JsonService {
}

export interface JsonService__Autowiring {
}

export interface JsonService__BeanDefinitions {
}

export interface JsonService__TestContext001_Autowiring {
}

export interface JsonService__TestContext001_BeanDefinitions {
}

export interface DefaultLeaderBoardService extends LeaderBoardService {
}

export interface DefaultLeaderBoardService__Autowiring {
}

export interface DefaultLeaderBoardService__BeanDefinitions {
}

export interface DefaultLeaderBoardService__TestContext001_Autowiring {
}

export interface DefaultLeaderBoardService__TestContext001_BeanDefinitions {
}

export interface LeaderBoardService {
}

export interface MerkleProofSerdeService {
}

export interface MerkleProofSerdeService__Autowiring {
}

export interface MerkleProofSerdeService__BeanDefinitions {
}

export interface MerkleProofSerdeService__TestContext001_Autowiring {
}

export interface MerkleProofSerdeService__TestContext001_BeanDefinitions {
}

export interface MerkleTreeService {
}

export interface MerkleTreeService__Autowiring {
}

export interface MerkleTreeService__BeanDefinitions {
}

export interface MerkleTreeService__TestContext001_Autowiring {
}

export interface MerkleTreeService__TestContext001_BeanDefinitions {
}

export interface VoteCommitmentService {
}

export interface VoteCommitmentService__Autowiring {
}

export interface VoteCommitmentService__BeanDefinitions {
}

export interface VoteCommitmentService__TestContext001_Autowiring {
}

export interface VoteCommitmentService__TestContext001_BeanDefinitions {
}

export interface VoteMerkleProofService {
}

export interface VoteMerkleProofService__Autowiring {
}

export interface VoteMerkleProofService__BeanDefinitions {
}

export interface VoteMerkleProofService__TestContext001_Autowiring {
}

export interface VoteMerkleProofService__TestContext001_BeanDefinitions {
}

export interface MetadataProcessor {
}

export interface MetadataProcessor__Autowiring {
}

export interface MetadataProcessor__BeanDefinitions {
}

export interface MetadataProcessor__TestContext001_Autowiring {
}

export interface MetadataProcessor__TestContext001_BeanDefinitions {
}

export interface MetadataService {
}

export interface MetadataService__Autowiring {
}

export interface MetadataService__BeanDefinitions {
}

export interface MetadataService__TestContext001_Autowiring {
}

export interface MetadataService__TestContext001_BeanDefinitions {
}

export interface ReferenceDataService {
}

export interface ReferenceDataService__Autowiring {
}

export interface ReferenceDataService__BeanDefinitions {
}

export interface ReferenceDataService__TestContext001_Autowiring {
}

export interface ReferenceDataService__TestContext001_BeanDefinitions {
}

export interface ReferencePresentationService {
}

export interface ReferencePresentationService__Autowiring {
}

export interface ReferencePresentationService__BeanDefinitions {
}

export interface ReferencePresentationService__TestContext001_Autowiring {
}

export interface ReferencePresentationService__TestContext001_BeanDefinitions {
}

export interface DefaultLoginService extends LoginService {
}

export interface DefaultLoginService__Autowiring {
}

export interface DefaultLoginService__BeanDefinitions {
}

export interface DefaultLoginService__TestContext001_Autowiring {
}

export interface DefaultLoginService__TestContext001_BeanDefinitions {
}

export interface JwtAuthenticationEntryPoint extends AuthenticationEntryPoint, Serializable {
}

export interface JwtAuthenticationEntryPoint__BeanDefinitions {
}

export interface JwtAuthenticationEntryPoint__TestContext001_BeanDefinitions {
}

export interface JwtAuthenticationToken extends AbstractAuthenticationToken {
}

export interface JwtFilter extends OncePerRequestFilter {
    beanName: string;
    servletContext: ServletContext;
}

export interface JwtFilter__Autowiring {
}

export interface JwtFilter__BeanDefinitions {
}

export interface JwtFilter__TestContext001_Autowiring {
}

export interface JwtFilter__TestContext001_BeanDefinitions {
}

export interface JwtPrincipal extends Principal, AuthenticatedPrincipal {
    signedJWT: SignedJWT;
}

export interface JwtService {
}

export interface JwtService__Autowiring {
}

export interface JwtService__BeanDefinitions {
}

export interface JwtService__TestContext001_Autowiring {
}

export interface JwtService__TestContext001_BeanDefinitions {
}

export interface LoginService {
}

export interface RoleService {
}

export interface RoleService__Autowiring {
}

export interface RoleService__BeanDefinitions {
}

export interface RoleService__TestContext001_Autowiring {
}

export interface RoleService__TestContext001_BeanDefinitions {
}

export interface L1SubmissionService {
}

export interface L1SubmissionService__Autowiring {
}

export interface L1SubmissionService__BeanDefinitions {
}

export interface L1SubmissionService__TestContext001_Autowiring {
}

export interface L1SubmissionService__TestContext001_BeanDefinitions {
}

export interface L1TransactionCreator {
}

export interface L1TransactionCreator__Autowiring {
}

export interface L1TransactionCreator__BeanDefinitions {
}

export interface L1TransactionCreator__TestContext001_Autowiring {
}

export interface L1TransactionCreator__TestContext001_BeanDefinitions {
}

export interface MetadataSerialiser {
}

export interface MetadataSerialiser__BeanDefinitions {
}

export interface MetadataSerialiser__TestContext001_BeanDefinitions {
}

export interface DefaultVoteService extends VoteService {
}

export interface DefaultVoteService__Autowiring {
}

export interface DefaultVoteService__BeanDefinitions {
}

export interface DefaultVoteService__TestContext001_Autowiring {
}

export interface DefaultVoteService__TestContext001_BeanDefinitions {
}

export interface VoteService {
}

export interface VotingPowerService {
}

export interface VotingPowerService__Autowiring {
}

export interface VotingPowerService__BeanDefinitions {
}

export interface VotingPowerService__TestContext001_Autowiring {
}

export interface VotingPowerService__TestContext001_BeanDefinitions {
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

export interface AuthenticationEntryPoint {
}

export interface GrantedAuthority extends Serializable {
    authority: string;
}

export interface AbstractAuthenticationToken extends Authentication, CredentialsContainer {
}

export interface Environment extends PropertyResolver {
    activeProfiles: string[];
    defaultProfiles: string[];
}

export interface FilterConfig {
    servletContext: ServletContext;
    filterName: string;
    initParameterNames: Enumeration<string>;
}

export interface ServletContext {
    classLoader: ClassLoader;
    majorVersion: number;
    minorVersion: number;
    defaultSessionTrackingModes: SessionTrackingMode[];
    effectiveSessionTrackingModes: SessionTrackingMode[];
    requestCharacterEncoding: string;
    responseCharacterEncoding: string;
    attributeNames: Enumeration<string>;
    contextPath: string;
    servletRegistrations: { [index: string]: ServletRegistration };
    /**
     * @deprecated
     */
    servletNames: Enumeration<string>;
    sessionTimeout: number;
    filterRegistrations: { [index: string]: FilterRegistration };
    servletContextName: string;
    sessionCookieConfig: SessionCookieConfig;
    jspConfigDescriptor: JspConfigDescriptor;
    effectiveMinorVersion: number;
    serverInfo: string;
    /**
     * @deprecated
     */
    servlets: Enumeration<Servlet>;
    virtualServerName: string;
    effectiveMajorVersion: number;
    initParameterNames: Enumeration<string>;
}

export interface OncePerRequestFilter extends GenericFilterBean {
}

export interface SignedJWT extends JWSObject, JWT {
    header: JWSHeader;
}

export interface Principal {
    name: string;
}

export interface AuthenticatedPrincipal {
    name: string;
}

export interface URI extends Comparable<URI>, Serializable {
}

export interface StatusType {
    statusCode: number;
    reasonPhrase: string;
}

export interface Value<T> extends Iterable<T> {
    empty: boolean;
    orNull: T;
    singleValued: boolean;
    lazy: boolean;
    async: boolean;
}

export interface Authentication extends Principal, Serializable {
    details: any;
    authenticated: boolean;
    authorities: GrantedAuthority[];
    credentials: any;
    principal: any;
}

export interface CredentialsContainer {
}

export interface PropertyResolver {
}

export interface Enumeration<E> {
}

export interface ClassLoader {
}

export interface ServletRegistration extends Registration {
    mappings: string[];
    runAsRole: string;
}

export interface FilterRegistration extends Registration {
    servletNameMappings: string[];
    urlPatternMappings: string[];
}

export interface SessionCookieConfig {
    domain: string;
    name: string;
    path: string;
    comment: string;
    httpOnly: boolean;
    secure: boolean;
    maxAge: number;
}

export interface JspConfigDescriptor {
    jspPropertyGroups: JspPropertyGroupDescriptor[];
    taglibs: TaglibDescriptor[];
}

export interface Servlet {
    servletConfig: ServletConfig;
    servletInfo: string;
}

export interface GenericFilterBean extends Filter, BeanNameAware, EnvironmentAware, EnvironmentCapable, ServletContextAware, InitializingBean, DisposableBean {
    filterConfig: FilterConfig;
}

export interface Payload extends Serializable {
    origin: Origin;
}

export interface Base64URL extends Base64 {
}

export interface JWSHeader extends CommonSEHeader {
    algorithm: JWSAlgorithm;
    base64URLEncodePayload: boolean;
}

export interface JWTClaimsSet extends Serializable {
    claims: { [index: string]: any };
    expirationTime: Date;
    notBeforeTime: Date;
    issueTime: Date;
    audience: string[];
    subject: string;
    jwtid: string;
    issuer: string;
}

export interface JWSObject extends JOSEObject {
    header: JWSHeader;
    signature: Base64URL;
    state: State;
    signingInput: any;
}

export interface JWT extends Serializable {
    header: Header;
    jwtclaimsSet: JWTClaimsSet;
    parsedParts: Base64URL[];
    parsedString: string;
}

export interface Registration {
    name: string;
    className: string;
    initParameters: { [index: string]: string };
}

export interface JspPropertyGroupDescriptor {
    buffer: string;
    deferredSyntaxAllowedAsLiteral: string;
    trimDirectiveWhitespaces: string;
    errorOnUndeclaredNamespace: string;
    urlPatterns: string[];
    elIgnored: string;
    pageEncoding: string;
    scriptingInvalid: string;
    includePreludes: string[];
    includeCodas: string[];
    defaultContentType: string;
    isXml: string;
}

export interface TaglibDescriptor {
    taglibURI: string;
    taglibLocation: string;
}

export interface ServletConfig {
    servletContext: ServletContext;
    servletName: string;
    initParameterNames: Enumeration<string>;
}

export interface Filter {
}

export interface BeanNameAware extends Aware {
}

export interface EnvironmentAware extends Aware {
}

export interface EnvironmentCapable {
    environment: Environment;
}

export interface ServletContextAware extends Aware {
}

export interface InitializingBean {
}

export interface DisposableBean {
}

export interface Base64 extends Serializable {
}

export interface JWK extends Serializable {
    keyStore: KeyStore;
    algorithm: Algorithm;
    private: boolean;
    x509CertSHA256Thumbprint: Base64URL;
    requiredParams: { [index: string]: any };
    keyOperations: KeyOperation[];
    x509CertURL: URI;
    /**
     * @deprecated
     */
    x509CertThumbprint: Base64URL;
    x509CertChain: Base64[];
    expirationTime: Date;
    notBeforeTime: Date;
    issueTime: Date;
    parsedX509CertChain: X509Certificate[];
    keyUse: KeyUse;
    keyID: string;
    keyType: KeyType;
}

export interface JWSAlgorithm extends Algorithm {
}

export interface JOSEObjectType extends Serializable {
    type: string;
}

export interface CommonSEHeader extends Header {
    jwk: JWK;
    x509CertSHA256Thumbprint: Base64URL;
    x509CertURL: URI;
    /**
     * @deprecated
     */
    x509CertThumbprint: Base64URL;
    x509CertChain: Base64[];
    jwkurl: URI;
    keyID: string;
}

export interface JOSEObject extends Serializable {
    payload: Payload;
    parsedParts: Base64URL[];
    header: Header;
    parsedString: string;
}

export interface Header extends Serializable {
    customParams: { [index: string]: any };
    parsedBase64URL: Base64URL;
    algorithm: Algorithm;
    contentType: string;
    type: JOSEObjectType;
    criticalParams: string[];
    includedParams: string[];
}

export interface Comparable<T> {
}

export interface Iterable<T> {
}

export interface Aware {
}

export interface KeyStore {
    type: string;
    provider: { [index: string]: any };
}

export interface Algorithm extends Serializable {
    name: string;
    requirement: Requirement;
}

export interface X509Certificate extends Certificate, X509Extension {
    subjectX500Principal: X500Principal;
    issuerX500Principal: X500Principal;
    extendedKeyUsage: string[];
    issuerAlternativeNames: any[][];
    sigAlgName: string;
    sigAlgParams: any;
    tbscertificate: any;
    /**
     * @deprecated since 16
     */
    issuerDN: Principal;
    /**
     * @deprecated since 16
     */
    subjectDN: Principal;
    sigAlgOID: string;
    issuerUniqueID: boolean[];
    subjectUniqueID: boolean[];
    signature: any;
    basicConstraints: number;
    version: number;
    subjectAlternativeNames: any[][];
    serialNumber: number;
    notAfter: Date;
    notBefore: Date;
    keyUsage: boolean[];
}

export interface KeyUse extends Serializable {
    value: string;
}

export interface KeyType extends Serializable {
    value: string;
    requirement: Requirement;
}

export interface X500Principal extends Principal, Serializable {
    encoded: any;
}

export interface PublicKey extends Key {
}

export interface Certificate extends Serializable {
    type: string;
    encoded: any;
    publicKey: PublicKey;
}

export interface X509Extension {
    criticalExtensionOIDs: string[];
    nonCriticalExtensionOIDs: string[];
}

export interface Key extends Serializable {
    algorithm: string;
    encoded: any;
    format: string;
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type OnChainEventType = "COMMITMENTS" | "EVENT_REGISTRATION" | "CATEGORY_REGISTRATION";

export type Role = "VOTER" | "ORGANISER";

export type SchemaVersion = "V1";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type MerkleProofType = "Left" | "Right";

export type Status = "BASIC" | "PARTIAL" | "ROLLBACK" | "FULL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type Web3Action = "LOGIN" | "CAST_VOTE";

export type SessionTrackingMode = "COOKIE" | "URL" | "SSL";

export type State = "UNSIGNED" | "SIGNED" | "VERIFIED";

export type Origin = "JSON" | "STRING" | "BYTE_ARRAY" | "BASE64URL" | "JWS_OBJECT" | "SIGNED_JWT";

export type KeyOperation = "SIGN" | "VERIFY" | "ENCRYPT" | "DECRYPT" | "WRAP_KEY" | "UNWRAP_KEY" | "DERIVE_KEY" | "DERIVE_BITS";

export type Requirement = "REQUIRED" | "RECOMMENDED" | "OPTIONAL";
