import {Category, EventCacheProps, Proposal} from "./eventCache.types";

const emptyProposal: Proposal = { id: "", name: null };
const emptyCategory: Category = {
    id: "",
    gdprProtection: false,
    proposals: [emptyProposal],
    version: 0,
    event: "",
    absoluteSlot: 0,
    valid: false
};
const initialStateData: EventCacheProps = {
    id: "",
    organisers: "",
    votingEventType: "",
    startSlot: 0,
    endSlot: 0,
    proposalsRevealSlot: 0,
    startEpoch: null,
    endEpoch: null,
    snapshotEpoch: null,
    proposalsRevealEpoch: null,
    eventStartDate: "",
    eventEndDate: "",
    proposalsRevealDate: "",
    snapshotTime: null,
    categories: [emptyCategory],
    tallies: [],
    active: false,
    started: false,
    finished: false,
    proposalsReveal: false,
    commitmentsWindowOpen: false,
    notStarted: false,
    allowVoteChanging: false,
    highLevelEventResultsWhileVoting: false,
    highLevelCategoryResultsWhileVoting: false,
    categoryResultsWhileVoting: false,
};


export {
    initialStateData
}