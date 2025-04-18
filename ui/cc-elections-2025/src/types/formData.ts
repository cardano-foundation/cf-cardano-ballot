export type IndividualFormData = {
  termsAndCondition: boolean;
  coldCredential: string;
  rationale: string;
  name: string;
  email: string;
  country: string;
  socialX: string;
  socialLinkedin: string;
  socialDiscord: string;
  socialTelegram: string;
  socialOther: string;
  publicContact: string;
  about: string;
  bio: string;
  additionalInfo: string;
  videoPresentationLink: string;
  reasonToServe: string;
  governanceExperience: string;
  communicationStrategy: string;
  ecosystemContributions: string;
  legalExpertise: string;
  weeklyCommitmentHours: string;
  liveliness: string;
  conflictOfInterest: string;
  drepId: string;
  stakeId: string;
  xverification: string;
}

export type CompanyFormData = {
  termsAndCondition: boolean;
  coldCredential: string;
  rationale: string;
  name: string;
  registrationNumber: string;
  keyContactPerson: string;
  xtwitter: string;
  linkedin: string;
  discord: string;
  telegram: string;
  website: string;
  other: string;
  email: string;
  country: string;
  socialX: string;
  socialLinkedin: string;
  socialDiscord: string;
  socialTelegram: string;
  socialOther: string;
  publicContact: string;
  about: string;
  bio: string;
  additionalInfo: string;
  videoPresentationLink: string;
  reasonToServe: string;
  governanceExperience: string;
  communicationStrategy: string;
  ecosystemContributions: string;
  legalExpertise: string;
  weeklyCommitmentHours: string;
  liveliness: string;
  conflictOfInterest: string;
  drepId: string;
  stakeId: string;
  xverification: string;
}

export type ConsortumFormData = {
  termsAndCondition: boolean;
  coldCredential: string;
  rationale: string;
  name: string;
  email: string;
  country: string;
  socialX: string;
  socialLinkedin: string;
  socialDiscord: string;
  socialTelegram: string;
  socialOther: string;
  publicContact: string;
  about: string;
  bio: string;
  additionalInfo: string;
  videoPresentationLink: string;
  reasonToServe: string;
  governanceExperience: string;
  communicationStrategy: string;
  ecosystemContributions: string;
  legalExpertise: string;
  members: ConsortumMemberFormData[];
}

export type ConsortumMemberFormData = {
  name: string;
  country: string;
  bio: string;
  socialX: string;
  socialLinkedin: string;
  socialDiscord: string;
  socialTelegram: string;
  socialOther: string;
  liveliness: string;
  conflictOfInterest: string;
  drepId: string;
  stakeId: string;
  xverification: string;
}

export type ErrObject = {
  [key: string]: boolean;
}

export type FormContextType<T> = {
  title: string[];
  page: number;
  setPage: React.Dispatch<React.SetStateAction<number>>;
  data: T;
  setData: React.Dispatch<React.SetStateAction<T>>;
  handleChange: (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  error: ErrObject;
  setError: React.Dispatch<React.SetStateAction<ErrObject>>;
  req: string[][];
}
