/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-09-12 02:01:41.

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

export interface LoginResult {
    accessToken: string;
    expiresAt: Date;
}

export interface ProtocolMagic {
    magic: number;
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

export interface LoginEnvelope {
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
}

export interface VoteEnvelopeBuilder {
}

export interface DefaultLoginService extends LoginService {
}

export interface DefaultLoginService__Autowiring {
}

export interface DefaultLoginService__BeanDefinitions {
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

export interface JwtPrincipal extends Principal, AuthenticatedPrincipal {
    signedJWT: SignedJWT;
}

export interface JwtService {
}

export interface JwtService__Autowiring {
}

export interface JwtService__BeanDefinitions {
}

export interface LoginService {
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

export interface MerkleElement<T> {
    empty: boolean;
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
    initParameterNames: Enumeration<string>;
    servletContext: ServletContext;
    filterName: string;
}

export interface ServletContext {
    classLoader: ClassLoader;
    majorVersion: number;
    minorVersion: number;
    attributeNames: Enumeration<string>;
    responseCharacterEncoding: string;
    requestCharacterEncoding: string;
    defaultSessionTrackingModes: SessionTrackingMode[];
    effectiveSessionTrackingModes: SessionTrackingMode[];
    initParameterNames: Enumeration<string>;
    virtualServerName: string;
    servletContextName: string;
    sessionCookieConfig: SessionCookieConfig;
    jspConfigDescriptor: JspConfigDescriptor;
    servletRegistrations: { [index: string]: ServletRegistration };
    filterRegistrations: { [index: string]: FilterRegistration };
    serverInfo: string;
    /**
     * @deprecated
     */
    servletNames: Enumeration<string>;
    /**
     * @deprecated
     */
    servlets: Enumeration<Servlet>;
    contextPath: string;
    effectiveMajorVersion: number;
    effectiveMinorVersion: number;
    sessionTimeout: number;
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
    async: boolean;
    lazy: boolean;
    singleValued: boolean;
}

export interface Authentication extends Principal, Serializable {
    details: any;
    credentials: any;
    authenticated: boolean;
    principal: any;
    authorities: GrantedAuthority[];
}

export interface CredentialsContainer {
}

export interface PropertyResolver {
}

export interface Enumeration<E> {
}

export interface ClassLoader {
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

export interface JspConfigDescriptor {
    jspPropertyGroups: JspPropertyGroupDescriptor[];
    taglibs: TaglibDescriptor[];
}

export interface ServletRegistration extends Registration {
    runAsRole: string;
    mappings: string[];
}

export interface FilterRegistration extends Registration {
    urlPatternMappings: string[];
    servletNameMappings: string[];
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
    audience: string[];
    issueTime: Date;
    notBeforeTime: Date;
    subject: string;
    jwtid: string;
    issuer: string;
    expirationTime: Date;
}

export interface JWSObject extends JOSEObject {
    header: JWSHeader;
    signature: Base64URL;
    state: State;
    signingInput: any;
}

export interface JWT extends Serializable {
    header: Header;
    parsedString: string;
    parsedParts: Base64URL[];
    jwtclaimsSet: JWTClaimsSet;
}

export interface JspPropertyGroupDescriptor {
    buffer: string;
    urlPatterns: string[];
    pageEncoding: string;
    includePreludes: string[];
    defaultContentType: string;
    elIgnored: string;
    includeCodas: string[];
    scriptingInvalid: string;
    trimDirectiveWhitespaces: string;
    deferredSyntaxAllowedAsLiteral: string;
    errorOnUndeclaredNamespace: string;
    isXml: string;
}

export interface TaglibDescriptor {
    taglibLocation: string;
    taglibURI: string;
}

export interface Registration {
    name: string;
    className: string;
    initParameters: { [index: string]: string };
}

export interface ServletConfig {
    servletName: string;
    initParameterNames: Enumeration<string>;
    servletContext: ServletContext;
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
    /**
     * @deprecated
     */
    x509CertThumbprint: Base64URL;
    issueTime: Date;
    x509CertChain: Base64[];
    notBeforeTime: Date;
    keyOperations: KeyOperation[];
    x509CertURL: URI;
    requiredParams: { [index: string]: any };
    parsedX509CertChain: X509Certificate[];
    x509CertSHA256Thumbprint: Base64URL;
    keyType: KeyType;
    keyUse: KeyUse;
    keyID: string;
    algorithm: Algorithm;
    expirationTime: Date;
}

export interface JWSAlgorithm extends Algorithm {
}

export interface JOSEObjectType extends Serializable {
    type: string;
}

export interface CommonSEHeader extends Header {
    jwk: JWK;
    /**
     * @deprecated
     */
    x509CertThumbprint: Base64URL;
    x509CertChain: Base64[];
    x509CertURL: URI;
    x509CertSHA256Thumbprint: Base64URL;
    keyID: string;
    jwkurl: URI;
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
    criticalParams: string[];
    includedParams: string[];
    algorithm: Algorithm;
    contentType: string;
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
    subjectX500Principal: X500Principal;
    issuerX500Principal: X500Principal;
    signature: any;
    basicConstraints: number;
    version: number;
    subjectAlternativeNames: any[][];
    keyUsage: boolean[];
    sigAlgName: string;
    serialNumber: number;
    /**
     * @deprecated since 16
     */
    subjectDN: Principal;
    /**
     * @deprecated since 16
     */
    issuerDN: Principal;
    notBefore: Date;
    notAfter: Date;
    sigAlgParams: any;
    extendedKeyUsage: string[];
    tbscertificate: any;
    sigAlgOID: string;
    issuerUniqueID: boolean[];
    subjectUniqueID: boolean[];
    issuerAlternativeNames: any[][];
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

export type Web3Action = "CAST_VOTE" | "VIEW_VOTE_RECEIPT" | "LOGIN";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type SessionTrackingMode = "COOKIE" | "URL" | "SSL";

export type State = "UNSIGNED" | "SIGNED" | "VERIFIED";

export type Origin = "JSON" | "STRING" | "BYTE_ARRAY" | "BASE64URL" | "JWS_OBJECT" | "SIGNED_JWT";

export type KeyOperation = "SIGN" | "VERIFY" | "ENCRYPT" | "DECRYPT" | "WRAP_KEY" | "UNWRAP_KEY" | "DERIVE_KEY" | "DERIVE_BITS";

export type Requirement = "REQUIRED" | "RECOMMENDED" | "OPTIONAL";
