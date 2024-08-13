interface Proposal {
  id: string;
  name: string | null;
}

interface Category {
  id: string;
  gdprProtection: boolean;
  proposals: Proposal[];
}

interface EventCacheProps {
  id: string;
  organisers: string;
  votingEventType: string;
  startSlot: number;
  endSlot: number;
  proposalsRevealSlot: number;
  startEpoch: null;
  endEpoch: null;
  snapshotEpoch: null;
  proposalsRevealEpoch: null;
  eventStartDate: string;
  eventEndDate: string;
  proposalsRevealDate: string;
  snapshotTime: null;
  categories: Category[];
  tallies: any[];
  active: boolean;
  started: boolean;
  finished: boolean;
  proposalsReveal: boolean;
  commitmentsWindowOpen: boolean;
  notStarted: boolean;
  allowVoteChanging: boolean;
  highLevelEventResultsWhileVoting: boolean;
  highLevelCategoryResultsWhileVoting: boolean;
  categoryResultsWhileVoting: boolean;
}

export type { EventCacheProps, Proposal, Category };