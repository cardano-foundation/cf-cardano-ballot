import { MemberBody } from "@models";
import { SelectChangeEvent } from "@mui/material";

export type ConsortiumMemberFormData = {
  name: string;
  country: string;
  bio: string;
  socialX: string;
  socialLinkedin: string;
  socialDiscord: string;
  socialTelegram: string;
  socialWebsite: string;
  socialOther: string;
  liveliness: string;
  conflictOfInterest: string;
  drepId: string;
  stakeId: string;
  xverification: string;
}

export type RegisterFormData = {
  termsOfUse: boolean;
  coldCredential: string;
  governanceActionRationale: string;
  name: string;
  registrationNumber: string;
  keyContactPerson: string;
  socialWebsite: string;
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
  members: ConsortiumMemberFormData[];
  membersAmount: number;
}

export type ErrObject = {
  members?: { name?: boolean, bio?: boolean, socialLinkedin?: boolean, socialX?: boolean, socialDiscord?: boolean, socialTelegram?: boolean, socialOther?: boolean, socialWebsite?: boolean }[];
  termsOfUse?: boolean;
  name?: boolean;
  email?: boolean;
  about?: boolean;
  bio?: boolean;
  videoPresentationLink? : boolean;
  socialLinkedin?: boolean;
  socialX?: boolean;
  socialDiscord?: boolean;
  socialTelegram?: boolean;
  socialOther?: boolean;
  socialWebsite?: boolean;
  publicContact?: boolean;
  candidateType?: boolean;
}

export type FormContextType<T> = {
  candidateType: "individual" | "company" | "consortium" | null;
  data: T;
  error: ErrObject;
  handleChange: (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<unknown>,
  ) => void;
  handleMemberChange?: (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<unknown>,
    index: number,
  ) => void;
  memberInit: MemberBody;
  page: number;
  req: string[][];
  setCandidateType: React.Dispatch<React.SetStateAction<"individual" | "company" | "consortium" | null>>;
  setData: React.Dispatch<React.SetStateAction<T>>;
  setError: React.Dispatch<React.SetStateAction<ErrObject>>;
  setPage: React.Dispatch<React.SetStateAction<number>>;
  title: string[];
}

export type TitlesType = {
  noContext: string[];
  individual: string[];
  company: string[];
  consortium: string[];
}
