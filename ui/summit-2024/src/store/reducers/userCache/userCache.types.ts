interface VerificationStarted {
  eventId: string;
  stakeAddress: string;
  requestId: string;
  createdAt: string;
  expiresAt: string;
}

interface UserVotes {
  categoryId: string;
  proposalId: string;
}

interface UserCacheProps {
  stakeAddress: string;
  verificationStarted: VerificationStarted;
  userVotes: UserVotes[];
  isVerified: boolean;
}

export type { VerificationStarted, UserVotes, UserCacheProps };
