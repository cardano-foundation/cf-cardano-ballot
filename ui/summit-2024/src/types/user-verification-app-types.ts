/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-09-22 11:08:20.

export interface Either<L, R> extends Value<R>, Serializable {
  left: L;
  right: boolean;
  orNull: R;
}

export interface IsVerifiedRequest {
  eventId: string;
  stakeAddress: string;
}

export interface IsVerifiedRequestBuilder {}

export interface IsVerifiedResponse {
  verified: boolean;
}

export interface SaltHolder {
  salt: string;
}

export interface DiscordCheckVerificationRequest {
  eventId: string;
  stakeAddress: string;
  secret: string;
  coseSignature: string;
  cosePublicKey?: string;
}

export interface DiscordCheckVerificationRequestBuilder {}

export interface DiscordStartVerificationRequest {
  discordIdHash: string;
  secret: string;
}

export interface DiscordStartVerificationRequestBuilder {}

export interface DiscordStartVerificationResponse {
  eventId: string;
  discordIdHash: string;
  status: VerificationStatus;
}

export interface DiscordStartVerificationResponseBuilder {}

export interface AbstractTimestampEntity {
  createdAt: Date;
  updatedAt: Date;
}

// @ts-ignore
export interface AbstractTimestampEntityBuilder<C, B> {}

export interface DiscordUserVerification extends AbstractTimestampEntity {
  discordIdHash: string;
  eventId: string;
  stakeAddress?: string;
  secretCode: string;
  status: VerificationStatus;
  expiresAt: Date;
}

export interface DiscordUserVerificationBuilder<C, B>
  extends AbstractTimestampEntityBuilder<C, B> {}

export interface DiscordUserVerificationBuilderImpl
  extends DiscordUserVerificationBuilder<
    DiscordUserVerification,
    DiscordUserVerificationBuilderImpl
  > {}

export interface SMSUserVerification extends AbstractTimestampEntity {
  id: string;
  stakeAddress: string;
  eventId: string;
  verificationCode: string;
  requestId: string;
  phoneNumberHash: string;
  status: VerificationStatus;
  expiresAt: Date;
}

export interface SMSUserVerificationBuilder<C, B>
  extends AbstractTimestampEntityBuilder<C, B> {}

export interface SMSUserVerificationBuilderImpl
  extends SMSUserVerificationBuilder<
    SMSUserVerification,
    SMSUserVerificationBuilderImpl
  > {}

export interface SMSCheckVerificationRequest {
  eventId: string;
  requestId: string;
  stakeAddress: string;
  verificationCode: string;
  locale?: Locale;
}

export interface SMSCheckVerificationRequestBuilder {}

export interface SMSStartVerificationRequest {
  eventId: string;
  stakeAddress: string;
  phoneNumber: string;
  locale?: Locale;
}

export interface SMSStartVerificationRequestBuilder {}

export interface SMSStartVerificationResponse {
  eventId: string;
  stakeAddress: string;
  requestId: string;
  createdAt: Date;
  expiresAt: Date;
}

export interface DefaultUserVerificationService
  extends UserVerificationService {}

export interface DefaultUserVerificationService__BeanDefinitions {}

export interface UserVerificationService {}

export interface DefaultDiscordUserVerificationService
  extends DiscordUserVerificationService {}

export interface DefaultDiscordUserVerificationService__Autowiring {}

export interface DefaultDiscordUserVerificationService__BeanDefinitions {}

export interface DiscordUserVerificationService {}

export interface CodeGenService {}

export interface CodeGenService__BeanDefinitions {}

export interface AWSSNSService extends SMSService {}

export interface DefaultSMSSMSUserVerificationService
  extends SMSUserVerificationService {}

export interface DefaultSMSSMSUserVerificationService__Autowiring {}

export interface DefaultSMSSMSUserVerificationService__BeanDefinitions {}

export interface SMSService {}

export interface SMSUserVerificationService {}

export interface SMSVerificationResponse {
  requestId: string;
}

export interface Problem {
  instance: URI;
  type: URI;
  parameters: { [index: string]: any };
  detail: string;
  status: StatusType;
  title: string;
}

export interface Serializable {}

export interface Locale extends Cloneable, Serializable {}

export interface URI extends Comparable<URI>, Serializable {}

export interface StatusType {
  statusCode: number;
  reasonPhrase: string;
}

export interface Value<T> extends Iterable<T> {
  empty: boolean;
  orNull: T;
  singleValued: boolean;
  async: boolean;
  lazy: boolean;
}

export interface Cloneable {}
// @ts-ignore
export interface Comparable<T> {}
// @ts-ignore
export interface Iterable<T> {}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type VerificationStatus = "PENDING" | "VERIFIED";
