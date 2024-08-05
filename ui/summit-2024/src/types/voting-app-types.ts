/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-11-30 16:17:07.

export interface Either<L, R> extends Value<R>, Serializable {
  left: L;
  right: boolean;
  orNull: R;
}

export interface Leaderboard {}

export interface ByCategoryStats {
  id: string;
  votes: number;
  votingPower: string;
}

export interface ByCategoryStatsBuilder {}

export interface ByEventStats {
  event: string;
  totalVotesCount: number;
  totalVotingPower: string;
  categories: ByCategoryStats[];
}

export interface ByEventStatsBuilder {}

export interface ByProposalsInCategoryStats {
  category: string;
  proposals: { [index: string]: Votes };
}

export interface ByProposalsInCategoryStatsBuilder {}

export interface LeaderboardBuilder {}

export interface Votes {
  votes: number;
  votingPower: string;
}

export interface VotesBuilder {}

export interface LoginResult {
  accessToken: string;
  expiresAt: Date;
}

export interface UserVotes {
  categoryId: string;
  proposalId: string;
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

export interface MerkleProofBuilder {}

export interface MerkleProofItem {
  type: MerkleProofType;
  hash: string;
}

export interface MerkleProofItemBuilder {}

export interface VoteReceiptBuilder {}

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

export interface VoteBuilder {}

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

export interface VoteMerkleProofBuilder {}

export interface CIP93Envelope<T> {
  uri: string;
  action: string;
  actionText: string;
  slot: string;
  data: T;
  slotAsLong: number;
  actionAsEnum?: Web3Action;
}

// @ts-ignore
export interface CIP93EnvelopeBuilder<T> {}

export interface JwtLoginEnvelope {
  event: string;
  address: string;
  network: string;
  role: string;
}

export interface SignedKeriRequest {
  signature: string;
  payload: string;
  oobi: string;
}

export interface SignedWeb3Request {
  signature: string;
  publicKey?: string;
}

export interface SignedWeb3RequestBuilder {}

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

export interface VoteEnvelopeBuilder {}

export interface DefaultLoginService extends LoginService {}

export interface DefaultLoginService__BeanDefinitions {}

export interface DefaultLoginService__TestContext001_BeanDefinitions {}

export interface LoginService {}

export interface LoginSystemDetector {}

export interface LoginSystemDetector__BeanDefinitions {}

export interface LoginSystemDetector__TestContext001_BeanDefinitions {}

export interface JwtAuthenticationToken extends AbstractAuthenticationToken {
  stakeAddress: string;
}

export interface JwtFilter extends OncePerRequestFilter {
  beanName: string;
  servletContext: ServletContext;
}

export interface JwtFilter__BeanDefinitions {}

export interface JwtFilter__TestContext001_BeanDefinitions {}

export interface JwtPrincipal extends Principal, AuthenticatedPrincipal {
  signedJWT: SignedJWT;
}

export interface JwtService {}

export interface JwtService__Autowiring {}

export interface JwtService__BeanDefinitions {}

export interface JwtService__TestContext001_Autowiring {}

export interface JwtService__TestContext001_BeanDefinitions {}

export interface Web3AuthenticationToken extends AbstractAuthenticationToken {
  details: Web3Details;
}

export interface Web3Details {
  stakeAddress: string;
  event: EventDetailsResponse;
  action: Web3Action;
  cip30VerificationResult: Cip30VerificationResult;
  envelope: CIP93Envelope<{ [index: string]: any }>;
  signedWeb3Request: SignedWeb3Request;
  chainTip: ChainTipResponse;
  network: CardanoNetwork;
}

export interface Web3DetailsBuilder {}

export interface Web3Filter extends OncePerRequestFilter {
  beanName: string;
  servletContext: ServletContext;
}

export interface Web3Filter__BeanDefinitions {}

export interface Web3Filter__TestContext001_BeanDefinitions {}

export interface ExpirationService {}

export interface ExpirationService__Autowiring {}

export interface ExpirationService__BeanDefinitions {}

export interface ExpirationService__TestContext001_Autowiring {}

export interface ExpirationService__TestContext001_BeanDefinitions {}

export interface JsonService {}

export interface JsonService__Autowiring {}

export interface JsonService__BeanDefinitions {}

export interface JsonService__TestContext001_Autowiring {}

export interface JsonService__TestContext001_BeanDefinitions {}

export interface AbstractWinnersService {}

export interface DBHighLevelLeaderBoardService
  extends HighLevelLeaderBoardService {}

export interface DBHighLevelLeaderBoardService__BeanDefinitions {}

export interface DBHighLevelLeaderBoardService__TestContext001_BeanDefinitions {}

export interface DBLeaderboardWinnersService
  extends AbstractWinnersService,
    LeaderboardWinnersService {}

export interface DBLeaderboardWinnersService__Autowiring {}

export interface DBLeaderboardWinnersService__BeanDefinitions {}

export interface DBLeaderboardWinnersService__TestContext001_Autowiring {}

export interface DBLeaderboardWinnersService__TestContext001_BeanDefinitions {}

export interface HighLevelLeaderBoardService {}

export interface L1LeaderboardWinnersService
  extends AbstractWinnersService,
    LeaderboardWinnersService {}

export interface L1LeaderboardWinnersService__Autowiring {}

export interface L1LeaderboardWinnersService__BeanDefinitions {}

export interface L1LeaderboardWinnersService__TestContext001_Autowiring {}

export interface L1LeaderboardWinnersService__TestContext001_BeanDefinitions {}

export interface LeaderboardWinnersProvider {}

export interface LeaderboardWinnersProvider__Autowiring {}

export interface LeaderboardWinnersProvider__BeanDefinitions {}

export interface LeaderboardWinnersProvider__TestContext001_Autowiring {}

export interface LeaderboardWinnersProvider__TestContext001_BeanDefinitions {}

export interface LeaderboardWinnersService {}

export interface MerkleProofSerdeService {}

export interface MerkleProofSerdeService__BeanDefinitions {}

export interface MerkleProofSerdeService__TestContext001_BeanDefinitions {}

export interface VoteMerkleProofService {}

export interface VoteMerkleProofService__BeanDefinitions {}

export interface VoteMerkleProofService__TestContext001_BeanDefinitions {}

export interface DefaultVoteService extends VoteService {}

export interface DefaultVoteService__BeanDefinitions {}

export interface DefaultVoteService__TestContext001_BeanDefinitions {}

export interface VoteService {}

export interface Problem {
  type: URI;
  title: string;
  status: StatusType;
  detail: string;
  instance: URI;
  parameters: { [index: string]: any };
}

export interface Serializable {}

export interface GrantedAuthority extends Serializable {
  authority: string;
}

export interface AbstractAuthenticationToken
  extends Authentication,
    CredentialsContainer {}

export interface Environment extends PropertyResolver {
  activeProfiles: string[];
  defaultProfiles: string[];
}

export interface FilterConfig {
  filterName: string;
  servletContext: ServletContext;
  initParameterNames: Enumeration<string>;
}

export interface ServletContext {
  contextPath: string;
  majorVersion: number;
  minorVersion: number;
  effectiveMajorVersion: number;
  effectiveMinorVersion: number;
  /**
   * @deprecated
   */
  servlets: Enumeration<Servlet>;
  /**
   * @deprecated
   */
  servletNames: Enumeration<string>;
  serverInfo: string;
  initParameterNames: Enumeration<string>;
  attributeNames: Enumeration<string>;
  servletContextName: string;
  servletRegistrations: { [index: string]: ServletRegistration };
  filterRegistrations: { [index: string]: FilterRegistration };
  sessionCookieConfig: SessionCookieConfig;
  defaultSessionTrackingModes: SessionTrackingMode[];
  effectiveSessionTrackingModes: SessionTrackingMode[];
  jspConfigDescriptor: JspConfigDescriptor;
  classLoader: ClassLoader;
  virtualServerName: string;
  sessionTimeout: number;
  requestCharacterEncoding: string;
  responseCharacterEncoding: string;
}

export interface OncePerRequestFilter extends GenericFilterBean {}

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
  organisers: string;
  finished: boolean;
  notStarted: boolean;
  isStarted: boolean;
  active: boolean;
  proposalsReveal: boolean;
  commitmentsWindowOpen: boolean;
  allowVoteChanging: boolean;
  highLevelEventResultsWhileVoting: boolean;
  highLevelCategoryResultsWhileVoting: boolean;
  categoryResultsWhileVoting: boolean;
  votingEventType: VotingEventType;
  categories: CategoryDetailsResponse[];
  tallies: Tally[];
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

export interface ChainTipResponse {
  hash: string;
  epochNo: number;
  absoluteSlot: number;
  synced: boolean;
  network: CardanoNetwork;
  notSynced: boolean;
}

export interface URI extends Comparable<URI>, Serializable {}

export interface StatusType {
  statusCode: number;
  reasonPhrase: string;
}

export interface Value<T> extends Iterable<T> {
  orNull: T;
  async: boolean;
  empty: boolean;
  lazy: boolean;
  singleValued: boolean;
}

export interface Authentication extends Principal, Serializable {
  authorities: GrantedAuthority[];
  credentials: any;
  details: any;
  principal: any;
  authenticated: boolean;
}

export interface CredentialsContainer {}

export interface PropertyResolver {}

// @ts-ignore
export interface Enumeration<E> {}

export interface Servlet {
  servletConfig: ServletConfig;
  servletInfo: string;
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
  name: string;
  domain: string;
  path: string;
  comment: string;
  httpOnly: boolean;
  secure: boolean;
  maxAge: number;
}

export interface JspConfigDescriptor {
  taglibs: TaglibDescriptor[];
  jspPropertyGroups: JspPropertyGroupDescriptor[];
}

export interface ClassLoader {}

export interface GenericFilterBean
  extends Filter,
    BeanNameAware,
    EnvironmentAware,
    EnvironmentCapable,
    ServletContextAware,
    InitializingBean,
    DisposableBean {
  filterConfig: FilterConfig;
}

export interface Payload extends Serializable {
  origin: Origin;
}

export interface Base64URL extends Base64 {}

export interface JWSHeader extends CommonSEHeader {
  algorithm: JWSAlgorithm;
  base64URLEncodePayload: boolean;
}

export interface JWTClaimsSet extends Serializable {
  claims: { [index: string]: any };
  issuer: string;
  subject: string;
  audience: string[];
  expirationTime: Date;
  notBeforeTime: Date;
  issueTime: Date;
  jwtid: string;
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

export interface CategoryDetailsResponse {
  id: string;
  gdprProtection: boolean;
  proposals: ProposalDetailsResponse[];
}

export interface Tally {
  name: string;
  type: TallyType;
}

export interface ServletConfig {
  servletName: string;
  servletContext: ServletContext;
  initParameterNames: Enumeration<string>;
}

export interface Registration {
  name: string;
  className: string;
  initParameters: { [index: string]: string };
}

export interface TaglibDescriptor {
  taglibURI: string;
  taglibLocation: string;
}

export interface JspPropertyGroupDescriptor {
  urlPatterns: string[];
  elIgnored: string;
  pageEncoding: string;
  scriptingInvalid: string;
  isXml: string;
  includePreludes: string[];
  includeCodas: string[];
  deferredSyntaxAllowedAsLiteral: string;
  trimDirectiveWhitespaces: string;
  defaultContentType: string;
  buffer: string;
  errorOnUndeclaredNamespace: string;
}

export interface Filter {}

export interface BeanNameAware extends Aware {}

export interface EnvironmentAware extends Aware {}

export interface EnvironmentCapable {
  environment: Environment;
}

export interface ServletContextAware extends Aware {}

export interface InitializingBean {}

export interface DisposableBean {}

export interface Base64 extends Serializable {}

export interface JWK extends Serializable {
  keyStore: KeyStore;
  keyType: KeyType;
  keyUse: KeyUse;
  keyOperations: KeyOperation[];
  algorithm: Algorithm;
  keyID: string;
  x509CertURL: URI;
  /**
   * @deprecated
   */
  x509CertThumbprint: Base64URL;
  x509CertSHA256Thumbprint: Base64URL;
  x509CertChain: Base64[];
  parsedX509CertChain: X509Certificate[];
  expirationTime: Date;
  notBeforeTime: Date;
  issueTime: Date;
  requiredParams: { [index: string]: any };
  private: boolean;
}

export interface JWSAlgorithm extends Algorithm {}

export interface JOSEObjectType extends Serializable {
  type: string;
}

export interface CommonSEHeader extends Header {
  jwk: JWK;
  jwkurl: URI;
  x509CertURL: URI;
  /**
   * @deprecated
   */
  x509CertThumbprint: Base64URL;
  x509CertSHA256Thumbprint: Base64URL;
  x509CertChain: Base64[];
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
  type: JOSEObjectType;
  contentType: string;
  criticalParams: string[];
  includedParams: string[];
}

export interface ProposalDetailsResponse {
  id: string;
  name: string;
}

// @ts-ignore
export interface Comparable<T> {}

// @ts-ignore
export interface Iterable<T> {}

export interface Aware {}

export interface KeyStore {
  type: string;
  provider: { [index: string]: any };
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

export interface X509Certificate extends Certificate, X509Extension {
  version: number;
  serialNumber: number;
  /**
   * @deprecated since 16
   */
  issuerDN: Principal;
  issuerX500Principal: X500Principal;
  /**
   * @deprecated since 16
   */
  subjectDN: Principal;
  subjectX500Principal: X500Principal;
  notBefore: Date;
  notAfter: Date;
  tbscertificate: any;
  signature: any;
  sigAlgName: string;
  sigAlgOID: string;
  sigAlgParams: any;
  issuerUniqueID: boolean[];
  subjectUniqueID: boolean[];
  keyUsage: boolean[];
  extendedKeyUsage: string[];
  basicConstraints: number;
  subjectAlternativeNames: any[][];
  issuerAlternativeNames: any[][];
}

export interface X500Principal extends Principal, Serializable {
  encoded: any;
}

export interface PublicKey extends Key {}

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
  format: string;
  encoded: any;
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type Role = "VOTER";

export type SchemaVersion = "V1";

export type TallyType = "HYDRA";

export type MerkleProofType = "L" | "R";

export type Status = "BASIC" | "PARTIAL" | "ROLLBACK" | "FULL";

export type VotingEventType = "USER_BASED" | "STAKE_BASED" | "BALANCE_BASED";

export type VotingPowerAsset = "ADA";

export type WinnerLeaderboardSource = "l1" | "db";

export type Web3Action =
  | "CAST_VOTE"
  | "VIEW_VOTE_RECEIPT"
  | "LOGIN"
  | "IS_VOTE_CASTING_ALLOWED"
  | "IS_VOTE_CHANGING_ALLOWED"
  | "VOTES";

export type LoginSystem = "JWT" | "CIP93";

export type FinalityScore = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH" | "FINAL";

export type SessionTrackingMode = "COOKIE" | "URL" | "SSL";

export type State = "UNSIGNED" | "SIGNED" | "VERIFIED";

export type ValidationError = "UNKNOWN" | "CIP8_FORMAT_ERROR" | "NO_PUBLIC_KEY";

export type Origin =
  | "JSON"
  | "STRING"
  | "BYTE_ARRAY"
  | "BASE64URL"
  | "JWS_OBJECT"
  | "SIGNED_JWT";

export type KeyOperation =
  | "SIGN"
  | "VERIFY"
  | "ENCRYPT"
  | "DECRYPT"
  | "WRAP_KEY"
  | "UNWRAP_KEY"
  | "DERIVE_KEY"
  | "DERIVE_BITS";

export type Requirement = "REQUIRED" | "RECOMMENDED" | "OPTIONAL";
