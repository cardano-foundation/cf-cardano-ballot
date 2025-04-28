export {};
interface SentryEventDataLayer {
  event: string;
  sentryEventId: string;
  sentryErrorMessage?: JSONValue;
}

declare global {
  interface Window {
    dataLayer: SentryEventDataLayer[];
  }

  type VoteType = "yes" | "no" | "abstain" | "notVoted";

  type ActionTypeFromAPI = {
    id: string;
    txHash: string;
    type: string;
    details: string;
    expiryDate: string;
    url: string;
    metadataHash: string;
  };

  type ActionDetailsType = {
    [key: string]: JSONValue;
  };

  interface ActionVotedOnType extends ActionTypeToDsiplay {
    vote: VoteType;
  }

  type VotedOnDataType = {
    title: string;
    actions: ActionVotedOnType[];
  }[];

  type ToVoteDataType = {
    title: string;
    actions: ProposalData[];
  }[];

  type NestedKeys<T> = T extends Record<string, unknown>
    ? {
        [K in keyof T]: T[K] extends Record<string, unknown>
          ? `${string & K}.${NestedKeys<T[K]>}`
          : string & K;
      }[keyof T]
    : never;

  type JSONValue =
    | string
    | number
    | boolean
    | null
    | { [property: string]: JSONValue }
    | JSONValue[];

  type ArrayElement<ArrayType extends readonly unknown[]> =
    ArrayType extends readonly (infer ElementType)[] ? ElementType : never;

  type Reference = {
    "@type": string;
    label: string;
    uri: string;
  };

  interface NavItem {
    dataTestId: string;
    label: string;
    navTo: string;
    activeIcon: JSX.Element | string;
    icon: JSX.Element | string;
    newTabLink: string | null;
    childNavItems?: NavItem[];
  }
}
