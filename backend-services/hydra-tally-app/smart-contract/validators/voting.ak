use acca/collections/hashed_list
use acca/datums as adatums
use acca/validators
use aiken/builtin
use aiken/dict.{Dict}
use aiken/hash.{Hash, Sha2_256, sha2_256}
use aiken/list
use aiken/transaction.{InlineDatum, Input, Output, ScriptContext, Spend}
use voting/challenge_proposal.{Challenge, ChallengeProposal, Proposal}
use voting/result.{Result}
use voting/results_batch_datum.{ResultsBatchDatum}
use voting/vote.{FlatVote, Vote}

// respective pubkey hashes for authorised parties
type VotingDatum {
}

type Redeemer {
  CreateVoteBatch { off_chain_batch_hash: Hash<Sha2_256, Vote> }
  ReduceResults { off_chain_batch_hash: Hash<Sha2_256, ResultsBatchDatum> }
}

// ApproveReduceResults { batch_hash } + contains all signatures of all head operators

validator {
  fn voting(
    _voting_datum: VotingDatum,
    redeemer: Redeemer,
    sc: ScriptContext,
  ) -> Bool {
    // On L2 (Hydra) one will typically lock votes with 0 ADA but on L1 of course each eUTxO is guarded by ca. 1,7 ADA
    expect Spend(output_reference) =
      sc.purpose

    let inputs: List<Input> =
      sc.transaction.inputs

    let outputs: List<Output> =
      sc.transaction.outputs
    when redeemer is {
      CreateVoteBatch { off_chain_batch_hash } -> {
        let votes: List<Vote> =
          get_votes_from_inputs(inputs)
        let results: Dict<ChallengeProposal, Result> =
          count_votes(votes)
        let on_chain_hash =
          get_votes_hash(votes)
        let on_chain_results_with_hash =
          ResultsBatchDatum { results, batch_hash: on_chain_hash }
        let hashes_match =
          (off_chain_batch_hash == on_chain_hash)?
        list.and(
          [
            hashes_match,
            validators.any_output_contains_own_validator_address(
              inputs,
              outputs,
              output_reference,
            )?,
            output_contains_final_result(
              sc.transaction.outputs,
              on_chain_results_with_hash,
            )?,
          ],
        )
      }
      ReduceResults { off_chain_batch_hash } -> {
        // collect results from all inputs (fail if there are still votes in smart contract)
        // take existing results and compress them, into one
        // check if resulting output datum contains votes counted up properly and that they send result back to the same address
        let on_chain_results: List<ResultsBatchDatum> =
          get_vote_results_from_inputs(inputs)

        let counted_on_chain_results: Dict<ChallengeProposal, Result> =
          count_vote_results(on_chain_results)

        // this is not a mistake that we are passing here off-chain hash
        // this is onchain data structure but hash verification is in a differrent step
        // for this we have validate_results function
        let on_chain_results_hash =
          get_vote_results_hash(on_chain_results)

        let on_chain_results_with_hash =
          ResultsBatchDatum {
            results: counted_on_chain_results,
            batch_hash: on_chain_results_hash,
          }
        let hashes_match =
          (off_chain_batch_hash == on_chain_results_hash)?

        list.and(
          [
            validators.any_output_contains_own_validator_address(
              inputs,
              outputs,
              output_reference,
            )?,
            hashes_match?,
            output_contains_final_result(
              sc.transaction.outputs,
              on_chain_results_with_hash,
            )?,
          ],
        )
      }

      _ ->
        False
    }
  }
}

// checks that one of the outputs contains
// computed on chain results
fn output_contains_final_result(
  outputs: List<Output>,
  on_chain_results: ResultsBatchDatum,
) -> Bool {
  let result_checker =
    fn(r: ResultsBatchDatum) { r == on_chain_results }

  let mapper =
    fn(output) {
      if adatums.is_inline_datum(output) {
        expect InlineDatum(off_chain_results_data) =
          output.datum

        expect off_chain_results: ResultsBatchDatum =
          off_chain_results_data

        Some(off_chain_results)
      } else {
        None
      }
    }

  list.filter_map(outputs, mapper)
    |> list.any(result_checker)
}

fn get_votes_hash(on_chain_votes: List<Vote>) -> Hash<Sha2_256, Vote> {
  let hash_fn =
    fn(v: Vote) { sha2_256(builtin.serialise_data(v)) }

  let hashed_list =
    hashed_list.from(on_chain_votes, hash_fn)

  hashed_list.hash
}

fn get_vote_results_hash(
  on_chain_results: List<ResultsBatchDatum>,
) -> Hash<sha2_256, ResultsBatchDatum> {
  let hash_fn =
    fn(r: ResultsBatchDatum) { sha2_256(builtin.serialise_data(r)) }

  let hashed_list =
    hashed_list.from(on_chain_results, hash_fn)

  hashed_list.hash
}

fn get_votes_from_inputs(inputs: List<Input>) -> List<Vote> {
  list.filter_map(
    inputs,
    fn(input) {
      if adatums.is_inline_datum(input.output) {
        expect InlineDatum(vote_data) =
          input.output.datum
        expect vote: Vote =
          vote_data

        Some(vote)
      } else {
        None
      }
    },
  )
}

fn get_vote_results_from_inputs(inputs: List<Input>) -> List<ResultsBatchDatum> {
  list.filter_map(
    inputs,
    fn(input) {
      if adatums.is_inline_datum(input.output) {
        expect InlineDatum(vote_results_data) =
          input.output.datum
        expect results_data_datum: ResultsBatchDatum =
          vote_results_data

        Some(results_data_datum)
      } else {
        None
      }
    },
  )
}

fn count_vote_results(
  on_chain_results: List<ResultsBatchDatum>,
) -> Dict<ChallengeProposal, Result> {
  let merging_fn =
    fn(r1: Dict<ChallengeProposal, Result>, r2: Dict<ChallengeProposal, Result>) {
      dict.union_with(
        left: r1,
        right: r2,
        with: results_conflict_resolver,
        compare: challenge_proposal.compare,
      )
    }

  let dict_list_with_results: List<Dict<ChallengeProposal, Result>> =
    list.map(on_chain_results, fn(rbd) { rbd.results })

  list.reduce(dict_list_with_results, merging_fn, dict.new())
}

fn do_count_votes(
  votes: List<Vote>,
  acc: Dict<ChallengeProposal, Result>,
) -> Dict<ChallengeProposal, Result> {
  when votes is {
    [] ->
      acc
    [h, ..t] -> {
      let fv: FlatVote =
        vote.to_flat_vote(h)
      let c: Challenge =
        h.challenge
      let p: Proposal =
        h.proposal
      dict.union_with(
        left: do_count_votes(t, acc),
        right: dict.insert(
          self: dict.new(),
          key: ChallengeProposal { challenge: c, proposal: p },
          value: Result { yes: fv.yes, no: fv.no, abstain: fv.abstain },
          compare: challenge_proposal.compare,
        ),
        with: results_conflict_resolver,
        compare: challenge_proposal.compare,
      )
    }
  }
}

fn results_conflict_resolver(
  _key: ChallengeProposal,
  left: Result,
  right: Result,
) -> Option<Result> {
  Some(
    Result {
      yes: left.yes + right.yes,
      no: left.no + right.no,
      abstain: left.abstain + right.abstain,
    },
  )
}

fn count_votes(votes: List<Vote>) -> Dict<ChallengeProposal, Result> {
  let empty =
    dict.new()
  do_count_votes(votes, empty)
}
