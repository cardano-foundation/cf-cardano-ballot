export const PATHS = {
  createGovernanceAction: "/create_governance_action",
  dashboard: "/dashboard",
  dashboardGovernanceActions: "/connected/governance_actions",
  dashboardGovernanceActionsAction: "/connected/governance_actions/:proposalId",
  dashboardGovernanceActionsCategory:
    "/connected/governance_actions/category/:category",
  dashboardDRepDirectory: "/connected/drep_directory",
  dashboardDRepDirectoryDRep: "/connected/drep_directory/:dRepId",
  dRepDirectory: "/drep_directory",
  dRepDirectoryDRep: "/drep_directory/:dRepId",
  editDrepMetadata: "/edit_drep",
  error: "/error",
  faqs: "/faqs",
  governanceActions: "/governance_actions",
  governanceActionsAction: "/governance_actions/:proposalId",
  governanceActionsCategory: "/governance_actions/category/:category",
  governanceActionsCategoryAction:
    "/governance_actions/category/:category/:proposalId",
  guides: "/guides",
  home: "/",
  registerAsdRep: "/register_drep",
  registerAsDirectVoter: "/register_direct_voter",
  retireAsDrep: "/retire_drep",
  retireAsDirectVoter: "/retire_direct_voter",
  stakeKeys: "/stake_keys",
};

export const PDF_PATHS = {
  proposalDiscussion: "/proposal_discussion",
  proposalDiscussionProposal: "/proposal_discussion/:id",
  proposalDiscussionPropose: "/proposal_discussion/propose",
};

export const BUDGET_DISCUSSION_PATHS = {
  budgetDiscussion: "/budget_discussion",
  budgetDiscussionProposal: "/budget_discussion/:id",
  budgetDiscussionPropose: "/budget_discussion/propose",
  budgetDiscussionAction: "/budget_discussion/:proposalId",
  budgetDiscussionCategory: "/budget_discussion/category/:category",
  budgetDiscussionCategoryAction:
    "/budget_discussion/category/:category/:proposalId",
};

export const USER_PATHS = {
  governanceActionsVotedByMe: "/my/votes_and_favorites",
};

export const OUTCOMES_PATHS = {
  governanceActionsOutcomes: "/outcomes",
  governanceActionOutcomes: "/outcomes/governance_actions/:id",
  governanceActionsLiveVoting: "/connected/governance_actions",
};
