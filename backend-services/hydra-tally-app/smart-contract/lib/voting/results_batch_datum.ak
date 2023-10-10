use aiken/dict.{Dict}
use voting/challenge_proposal.{ChallengeProposal}
use voting/result.{Result}

pub type ResultsBatchDatum {
  results: Dict<ChallengeProposal, Result>,
  batch_hash: ByteArray,
}
