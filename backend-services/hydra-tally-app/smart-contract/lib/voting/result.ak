use voting/vote.{VotingPower}

pub type Result {
  yes: VotingPower,
  no: VotingPower,
  abstain: VotingPower,
}
