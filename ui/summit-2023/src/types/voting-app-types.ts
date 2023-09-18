/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-09-18 21:28:15.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface L1MerkleCommitment {
    signedVotes: CompactVote[];
    root: MerkleElement<CompactVote>;
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

export interface ByCategoryStats {
    id: string;
    votes: number;
    votingPower: string;
}

export interface ByCategoryStatsBuilder {
}

export interface ByEventStats {
    event: string;
    totalVotesCount: number;
    totalVotingPower: string;
    categories: ByCategoryStats[];
}

export interface ByEventStatsBuilder {
}

export interface ByProposalsInCategoryStats {
    category: string;
    proposals: { [index: string]: Votes };
}

export interface ByProposalsInCategoryStatsBuilder {
}

export interface LeaderboardBuilder {
}

export interface Votes {
    votes: number;
    votingPower: string;
}

export interface VotesBuilder {
}

export interface LoginResult {
    accessToken: string;
    expiresAt: Date;
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

export interface VoteSerialisations {
}

export interface WellKnownPointWithProtocolMagic {
    wellKnownPointForNetwork?: Point;
    protocolMagic: number;
}

export interface AbstractTimestampEntity {
    createdAt: Date;
    updatedAt: Date;
}

export interface Vote extends AbstractTimestampEntity {
    id: string;
    idNumericHash: number;
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
    voteIdNumericHash: number;
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
    slotAsLong: number;
    actionAsEnum?: Web3Action;
}

export interface CIP93EnvelopeBuilder<T> {
}

export interface JwtLoginEnvelope {
    event: string;
    address: string;
    network: string;
    role: string;
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
    votedAtSlot: number;
}

export interface VoteEnvelopeBuilder {
}

export interface DefaultLoginService extends LoginService {
}

export interface DefaultLoginService__BeanDefinitions {
}

export interface LoginService {
}

export interface LoginSystemDetector {
}

export interface LoginSystemDetector__BeanDefinitions {
}

export interface JwtAuthenticationToken extends AbstractAuthenticationToken {
    stakeAddress: string;
}

export interface JwtFilter extends OncePerRequestFilter {
    beanName: string;
    servletContext: ServletContext;
}

export interface JwtFilter__BeanDefinitions {
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

export interface Web3AuthenticationToken extends AbstractAuthenticationToken {
    details: Web3Details;
}

export interface Web3Details {
    stakeAddress: string;
    event: EventDetailsResponse;
    action: Web3Action;
    network: CardanoNetwork;
    cip30VerificationResult: Cip30VerificationResult;
    envelope: CIP93Envelope<{ [index: string]: any }>;
    signedWeb3Request: SignedWeb3Request;
}

export interface Web3DetailsBuilder {
}

export interface Web3Filter extends OncePerRequestFilter {
    beanName: string;
    servletContext: ServletContext;
}

export interface Web3Filter__BeanDefinitions {
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

export interface VoteCommitmentService__BeanDefinitions {
}

export interface VoteMerkleProofService {
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

export interface DefaultVoteService__BeanDefinitions {
}

export interface VoteService {
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
    detail: string;
    title: string;
}

export interface Serializable {
}

export interface CompactVote {
    coseSignature: string;
    cosePublicKey?: string;
}

export interface MerkleElement<T> {
    empty: boolean;
}

export interface Point {
    slot: number;
    hash: string;
}

export interface GrantedAuthority extends Serializable {
    authority: string;
}

export interface AbstractAuthenticationToken extends Authentication, CredentialsContainer {
}

export interface Environment extends PropertyResolver {
    defaultProfiles: string[];
    activeProfiles: string[];
}

export interface FilterConfig {
    filterName: string;
    servletContext: ServletContext;
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
    contextPath: string;
    sessionTimeout: number;
    virtualServerName: string;
    jspConfigDescriptor: JspConfigDescriptor;
    sessionCookieConfig: SessionCookieConfig;
    filterRegistrations: { [index: string]: FilterRegistration };
    servletRegistrations: { [index: string]: ServletRegistration };
    servletContextName: string;
    initParameterNames: Enumeration<string>;
    serverInfo: string;
    /**
     * @deprecated
     */
    servletNames: Enumeration<string>;
    /**
     * @deprecated
     */
    servlets: Enumeration<Servlet>;
    effectiveMinorVersion: number;
    effectiveMajorVersion: number;
    attributeNames: Enumeration<string>;
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

export interface EventDetailsResponse {
    id: string;
    finished: boolean;
    notStarted: boolean;
    active: boolean;
    proposalsReveal: boolean;
    allowVoteChanging: boolean;
    highLevelEventResultsWhileVoting: boolean;
    highLevelCategoryResultsWhileVoting: boolean;
    categoryResultsWhileVoting: boolean;
    votingEventType: VotingEventType;
    categories: CategoryDetailsResponse[];
    eventInactive: boolean;
}

export interface Cip30VerificationResult {
    validationError?: ValidationError;
    address?: any;
    ed25519PublicKey: any;
    ed25519Signature: any;
    message: any;
    cosePayload: any;
    valid: boolean;
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

export interface Authentication extends Principal, Serializable {
    authenticated: boolean;
    authorities: GrantedAuthority[];
    principal: any;
    credentials: any;
    details: any;
}

export interface CredentialsContainer {
}

export interface PropertyResolver {
}

export interface Enumeration<E> {
}

export interface ClassLoader {
}

export interface JspConfigDescriptor {
    jspPropertyGroups: JspPropertyGroupDescriptor[];
    taglibs: TaglibDescriptor[];
}

export interface SessionCookieConfig {
    name: string;
    path: string;
    comment: string;
    httpOnly: boolean;
    secure: boolean;
    domain: string;
    maxAge: number;
}

export interface FilterRegistration extends Registration {
    urlPatternMappings: string[];
    servletNameMappings: string[];
}

export interface ServletRegistration extends Registration {
    mappings: string[];
    runAsRole: string;
}

export interface Servlet {
    servletInfo: string;
    servletConfig: ServletConfig;
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
    base64URLEncodePayload: boolean;
    algorithm: JWSAlgorithm;
}

export interface JWTClaimsSet extends Serializable {
    claims: { [index: string]: any };
    issueTime: Date;
    notBeforeTime: Date;
    expirationTime: Date;
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
    parsedString: string;
    parsedParts: Base64URL[];
}

export interface CategoryDetailsResponse {
    id: string;
    gdprProtection: boolean;
    proposals: ProposalDetailsResponse[];
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

export interface Registration {
    name: string;
    className: string;
    initParameters: { [index: string]: string };
}

export interface ServletConfig {
    servletContext: ServletContext;
    initParameterNames: Enumeration<string>;
    servletName: string;
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
    private: boolean;
    x509CertSHA256Thumbprint: Base64URL;
    requiredParams: { [index: string]: any };
    issueTime: Date;
    notBeforeTime: Date;
    expirationTime: Date;
    x509CertChain: Base64[];
    /**
     * @deprecated
     */
    x509CertThumbprint: Base64URL;
    x509CertURL: URI;
    keyOperations: KeyOperation[];
    parsedX509CertChain: X509Certificate[];
    keyType: KeyType;
    keyUse: KeyUse;
    keyID: string;
    algorithm: Algorithm;
}

export interface JWSAlgorithm extends Algorithm {
}

export interface JOSEObjectType extends Serializable {
    type: string;
}

export interface CommonSEHeader extends Header {
    jwk: JWK;
    x509CertSHA256Thumbprint: Base64URL;
    x509CertChain: Base64[];
    /**
     * @deprecated
     */
    x509CertThumbprint: Base64URL;
    x509CertURL: URI;
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
    type: JOSEObjectType;
    includedParams: string[];
    criticalParams: string[];
    algorithm: Algorithm;
    contentType: string;
}

export interface ProposalDetailsResponse {
    id: string;
    name: string;
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

export interface X509Certificate extends Certificate, X509Extension {
    signature: any;
    basicConstraints: number;
    version: number;
    issuerX500Principal: X500Principal;
    subjectX500Principal: X500Principal;
    keyUsage: boolean[];
    subjectAlternativeNames: any[][];
    notBefore: Date;
    serialNumber: number;
    notAfter: Date;
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
}

export interface KeyType extends Serializable {
    value: string;
    requirement: Requirement;
}

export interface KeyUse extends Serializable {
    value: string;
}

export interface Algorithm extends Serializable {
    name: string;
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
    encoded: any;
    format: string;
    algorithm: string;
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type OnChainEventType = "COMMITMENTS" | "EVENT_REGISTRATION" | "CATEGORY_REGISTRATION";

export type Role = "VOTER";

export type SchemaVersion = "V1";

export type MerkleProofType = "Left" | "Right";

export type Status = "BASIC" | "PARTIAL" | "ROLLBACK" | "FULL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type Web3Action = "CAST_VOTE" | "VIEW_VOTE_RECEIPT" | "LOGIN" | "IS_VOTE_CASTING_ALLOWED" | "IS_VOTE_CHANGING_ALLOWED" | "VOTED_ON";

export type LoginSystem = "JWT" | "CIP93";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type SessionTrackingMode = "COOKIE" | "URL" | "SSL";

export type State = "UNSIGNED" | "SIGNED" | "VERIFIED";

export type ValidationError = "UNKNOWN" | "CIP8_FORMAT_ERROR" | "NO_PUBLIC_KEY";

export type Origin = "JSON" | "STRING" | "BYTE_ARRAY" | "BASE64URL" | "JWS_OBJECT" | "SIGNED_JWT";

export type KeyOperation = "SIGN" | "VERIFY" | "ENCRYPT" | "DECRYPT" | "WRAP_KEY" | "UNWRAP_KEY" | "DERIVE_KEY" | "DERIVE_BITS";

export type Requirement = "REQUIRED" | "RECOMMENDED" | "OPTIONAL";
