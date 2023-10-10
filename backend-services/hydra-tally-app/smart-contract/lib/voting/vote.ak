use acca/hash.{PubKeyHash} as ahash
use aiken/list
use aiken/option
use voting/challenge_proposal.{Challenge, ChallengeProposal, Proposal}

pub type VotingPower =
  Int

pub type Choice =
  Int

pub type Vote {
  voter_key: PubKeyHash,
  voting_power: VotingPower,
  challenge: Challenge,
  proposal: Proposal,
}

pub type FlatVote {
  key: ChallengeProposal
}

pub fn voting_power_for_choice(v: Vote, c: Choice) -> Option<VotingPower> {
  list.find(
    [0, 1, 2],
    fn(choice: Choice) { list.and([v.choice == choice, c == choice]) },
  )
    |> option.map(fn(_) { v.voting_power })
}

pub fn to_flat_vote(v: Vote) -> FlatVote {
  let cp =
    ChallengeProposal { challenge: v.challenge, proposal: v.proposal }
  let y =
    voting_power_for_choice(v, 2)
      |> option.or_else(0)
  let n =
    voting_power_for_choice(v, 1)
      |> option.or_else(0)
  let a =
    voting_power_for_choice(v, 0)
      |> option.or_else(0)

  FlatVote { key: cp, yes: y, no: n, abstain: a }
}

test to_flat_vote_1() {
  let v1 =
    Vote {
      voter_key: #"01",
      voting_power: 10,
      challenge: 1,
      proposal: 1,
      choice: 2,
    }

  to_flat_vote(v1) == FlatVote {
    key: ChallengeProposal(1, 1),
    yes: 10,
    no: 0,
    abstain: 0,
  }
}

test to_flat_vote_2() {
  let v1 =
    Vote {
      voter_key: #"01",
      voting_power: 10,
      challenge: 1,
      proposal: 1,
      choice: 1,
    }

  to_flat_vote(v1) == FlatVote {
    key: ChallengeProposal(1, 1),
    yes: 0,
    no: 10,
    abstain: 0,
  }
}

test to_flat_vote_3() {
  let v1 =
    Vote {
      voter_key: #"01",
      voting_power: 10,
      challenge: 1,
      proposal: 1,
      choice: 0,
    }

  to_flat_vote(v1) == FlatVote {
    key: ChallengeProposal(1, 1),
    yes: 0,
    no: 0,
    abstain: 10,
  }
}
