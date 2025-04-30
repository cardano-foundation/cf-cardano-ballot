export type CandidateBody = {
  candidate: {
    candidateType: "individual" | "company" | "consortium";
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
    weeklyCommitmentHours: number;
    conflictOfInterest: string;
    drepId: string;
    stakeId: string;
    walletAddress: string;
    xverification: string;
  };
  registrationNumber?: string;
  keyContactPerson?: string;
  socialWebsite?: string;
  members?: MemberBody[];
}

export type MemberBody = {
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

export type Candidate = {
  candidate: {
    id: number;
    candidateType: "individual" | "company" | "consortium";
    name: string;
    email: string;
    country: string;
    coldCredential: string;
    governanceActionRationale: string;
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
    conflictOfInterest: string;
    drepId: string;
    stakeId: string;
    walletAddress: string;
    verified: boolean;
    createdAt: string;
    updatedAt: string;
    xverification: string;
  };
  registrationNumber?: string;
  keyContactPerson?: string;
  socialWebsite?: string;
  members?: Member[];
}

export type Member = {
  id: number;
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
