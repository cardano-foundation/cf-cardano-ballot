/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2023-08-31 16:25:22.

export interface Either<L, R> extends Value<R>, Serializable {
    left: L;
    right: boolean;
    orNull: R;
}

export interface CheckVerificationRequest {
    eventId: string;
    requestId: string;
    stakeAddress: string;
    verificationCode: string;
    locale?: Locale;
}

export interface CheckVerificationRequestBuilder {
}

export interface IsVerifiedRequest {
    stakeAddress: string;
    eventId: string;
}

export interface IsVerifiedRequestBuilder {
}

export interface IsVerifiedResponse {
    verified: boolean;
}

export interface StartVerificationRequest {
    eventId: string;
    stakeAddress: string;
    phoneNumber: string;
    locale?: Locale;
}

export interface StartVerificationRequestBuilder {
}

export interface StartVerificationResponse {
    eventId: string;
    stakeAddress: string;
    requestId: string;
    createdAt: Date;
    expiresAt: Date;
}

export interface AbstractTimestampEntity {
    createdAt: Date;
    updatedAt: Date;
}

export interface AbstractTimestampEntityBuilder<C, B> {
}

export interface UserVerification extends AbstractTimestampEntity {
    id: string;
    stakeAddress: string;
    eventId: string;
    verificationCode: string;
    requestId: string;
    phoneNumberHash?: string;
    status: Status;
    provider: Provider;
    channel: Channel;
    expiresAt: Date;
}

export interface UserVerificationBuilder<C, B> extends AbstractTimestampEntityBuilder<C, B> {
}

export interface UserVerificationBuilderImpl extends UserVerificationBuilder<UserVerification, UserVerificationBuilderImpl> {
}

export interface StakeAddressVerificationService {
}

export interface StakeAddressVerificationService__Autowiring {
}

export interface StakeAddressVerificationService__BeanDefinitions {
}

export interface AWSSNSService extends SMSService {
}

export interface SMSService {
}

export interface SMSVerificationResponse {
    requestId: string;
}

export interface SimpleUserVerificationService extends UserVerificationService {
}

export interface SimpleUserVerificationService__Autowiring {
}

export interface SimpleUserVerificationService__BeanDefinitions {
}

export interface UserVerificationService {
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

export interface Locale extends Cloneable, Serializable {
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

export interface Cloneable {
}

export interface Comparable<T> {
}

export interface Iterable<T> {
}

export type CardanoNetwork = "MAIN" | "PREPROD" | "PREVIEW" | "DEV";

export type Channel = "SMS";

export type Provider = "TWILIO" | "AWS_SNS";

export type Status = "NOT_REQUESTED" | "PENDING" | "VERIFIED";
