use aiken/builtin
use aiken/bytearray

pub type Challenge =
  Int

pub type Proposal =
  Int

pub type ChallengeProposal {
  challenge: Challenge,
  proposal: Proposal,
}

pub fn compare(left: ChallengeProposal, right: ChallengeProposal) -> Ordering {
  bytearray.compare(builtin.serialise_data(left), builtin.serialise_data(right))
}
