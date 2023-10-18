use acca/datums as adatums
use acca/dict as adict
use acca/hash.{PubKeyHash} as ahash
use acca/string as astring
use acca/validators
use aiken/builtin
use aiken/bytearray
use aiken/cbor
use aiken/dict.{Dict}
use aiken/list
use aiken/string
use aiken/transaction.{InlineDatum, Input, Output, ScriptContext, Spend}

type VoteId =
  ByteArray

type Category =
  ByteArray

type Proposal =
  ByteArray

type Redeemer {
  CreateVoteBatch
  ReduceVoteBatches
}

type CategoryResultsDatum {
  category_id: ByteArray,
  results: Dict<Proposal, Int>,
}

type Vote {
  vote_id: ByteArray,
  voter_key: ByteArray,
  category: ByteArray,
  proposal: ByteArray,
}

fn compare_proposal(left: Proposal, right: Proposal) -> Ordering {
  bytearray.compare(builtin.serialise_data(left), builtin.serialise_data(right))
}

validator(event_id: ByteArray, contract_category_id: ByteArray) {
  fn voting(_voting_datum: Void, redeemer: Redeemer, sc: ScriptContext) -> Bool {
    // On L2 (Hydra) one will typically lock votes with 0 ADA but on L1 of course each eUTxO is guarded by ca. 1,7 ADA
    expect Spend(output_reference) = sc.purpose

    let inputs: List<Input> = sc.transaction.inputs
    let outputs: List<Output> = sc.transaction.outputs

    when redeemer is {
      CreateVoteBatch -> {
        let votes: List<Vote> =
          get_votes_from_inputs(contract_category_id, inputs)

        //let results: Dict<Proposal, Int> = count_votes(votes)
        // let sorted = adict.sort(results, astring.compare)
        // let on_chain_results =
        //   CategoryResultsDatum { category_id: contract_category_id, results: sorted }
        // trace @"on_chain_results:"
        // trace cbor.diagnostic(on_chain_results)
        and {
          validators.any_output_contains_own_validator_address(
            inputs,
            outputs,
            output_reference,
          )?,
          //(dict.size(results) > 0)?,
          (list.length(votes) > 1)?,
        }
      }
      //(list.length(inputs) == list.length(votes))?,
      //output_contains_final_result(sc.transaction.outputs, on_chain_results)?,
      ReduceVoteBatches -> True
    }
    // ReduceResults { off_chain_batch_hash } -> {
    //   // collect results from all inputs (fail if there are still votes in smart contract)
    //   // take existing results and compress them, into one
    //   // check if resulting output datum contains votes counted up properly and that they send result back to the same address
    //   let on_chain_results: List<ResultsBatchDatum> =
    //     get_vote_results_from_inputs(inputs)
    //   let counted_on_chain_results: Dict<ChallengeProposal, Result> =
    //     count_vote_results(on_chain_results)
    //   // this is not a mistake that we are passing here off-chain hash
    //   // this is onchain data structure but hash verification is in a differrent step
    //   // for this we have validate_results function
    //   let on_chain_results_hash = get_vote_results_hash(on_chain_results)
    //   let on_chain_results_with_hash =
    //     ResultsBatchDatum {
    //       results: counted_on_chain_results,
    //       batch_hash: on_chain_results_hash,
    //     }
    //   let hashes_match = (off_chain_batch_hash == on_chain_results_hash)?
    //   and {
    //     validators.any_output_contains_own_validator_address(
    //       inputs,
    //       outputs,
    //       output_reference,
    //     )?,
    //     hashes_match?,
    //     output_contains_final_result(
    //       sc.transaction.outputs,
    //       on_chain_results_with_hash,
    //     )?,
    //   }
    // }
  }
}

// checks that one of the outputs contains
// computed on chain results
fn output_contains_final_result(
  outputs: List<Output>,
  on_chain_results: CategoryResultsDatum,
) -> Bool {
  let result_checker =
    fn(r: CategoryResultsDatum) { r == on_chain_results }

  let mapper =
    fn(output) {
      if adatums.is_inline_datum(output) {
        expect InlineDatum(off_chain_results_data) = output.datum

        expect off_chain_results: CategoryResultsDatum = off_chain_results_data

        Some(off_chain_results)
      } else {
        None
      }
    }

  list.filter_map(outputs, mapper)
    |> list.any(result_checker)
}

fn get_votes_from_inputs(
  contract_category_id: ByteArray,
  inputs: List<Input>,
) -> List<Vote> {
  list.filter_map(
    inputs,
    fn(input) {
      if adatums.is_inline_datum(input.output) {
        expect InlineDatum(vote_data) = input.output.datum
        expect vote: Vote = vote_data

        if bytearray.compare(contract_category_id, vote.category) == Equal {
          Some(vote)
        } else {
          None
        }
      } else {
        None
      }
    },
  )
}

// fn get_vote_results_from_inputs(inputs: List<Input>) -> List<ResultsBatchDatum> {
//   list.filter_map(
//     inputs,
//     fn(input) {
//       if adatums.is_inline_datum(input.output) {
//         expect InlineDatum(vote_results_data) = input.output.datum
//         expect results_data_datum: ResultsBatchDatum = vote_results_data

//         Some(results_data_datum)
//       } else {
//         None
//       }
//     },
//   )
// }

// fn count_vote_results(
//   on_chain_results: List<ResultsBatchDatum>,
// ) -> Dict<ChallengeProposal, Result> {
//   let merging_fn =
//     fn(r1: Dict<ChallengeProposal, Result>, r2: Dict<ChallengeProposal, Result>) {
//       dict.union_with(
//         left: r1,
//         right: r2,
//         with: results_conflict_resolver,
//         compare: challenge_proposal.compare,
//       )
//     }

//   let dict_list_with_results: List<Dict<ChallengeProposal, Result>> =
//     list.map(on_chain_results, fn(rbd) { rbd.results })

//   list.reduce(dict_list_with_results, merging_fn, dict.new())
// }

fn count_votes(votes: List<Vote>) -> Dict<Proposal, Int> {
  let empty = dict.new()

  do_count_votes(votes, empty)
}

fn do_count_votes(
  votes: List<Vote>,
  acc: Dict<Proposal, Int>,
) -> Dict<Proposal, Int> {
  when votes is {
    [] -> acc
    [h, ..t] -> {
      let p: Proposal = h.proposal
      dict.union_with(
        left: do_count_votes(t, acc),
        right: dict.insert(
          self: dict.new(),
          key: p,
          value: 1,
          compare: compare_proposal,
        ),
        with: fn(_key, a, b) { Some(a + b) },
        compare: compare_proposal,
      )
    }
  }
}
