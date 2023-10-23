use acca/datums as adatums
use acca/hash.{PubKeyHash} as ahash
use acca/string as astring
use acca/validators
use aiken/dict.{Dict}
use aiken/list
use aiken/transaction.{InlineDatum, Input, Output, ScriptContext, Spend}

type VoteId =
  String

type Category =
  String

type Proposal =
  String

type Redeemer {
  CreateVoteBatch
  ReduceVoteBatches
}

type CategoryResultsDatum {
  event_id: String,
  organiser: String,
  category_id: Category,
  results: Dict<Proposal, Int>,
}

type Vote {
  event_id: String,
  organiser: String,
  vote_id: VoteId,
  voter_key: PubKeyHash,
  category_id: Category,
  proposal_id: Proposal,
}

fn compare_proposal(left: Proposal, right: Proposal) -> Ordering {
  astring.compare(left, right)
}

validator(
  organiser_keys: List<PubKeyHash>,
  contract_event_id: String,
  contract_organiser: String,
  contract_category_id: String,
) {
  fn voting(_voting_datum: Void, redeemer: Redeemer, sc: ScriptContext) -> Bool {
    expect Spend(output_reference) = sc.purpose

    let inputs: List<Input> = sc.transaction.inputs
    let outputs: List<Output> = sc.transaction.outputs

    when redeemer is {
      CreateVoteBatch -> {
        let votes: List<Vote> =
          get_votes_from_inputs(
            contract_event_id,
            contract_organiser,
            contract_category_id,
            inputs,
          )

        let results: Dict<Proposal, Int> = count_votes(votes)

        let on_chain_results =
          CategoryResultsDatum {
            event_id: contract_event_id,
            organiser: contract_organiser,
            category_id: contract_category_id,
            results,
          }

        let must_be_signed =
          list.any(
            sc.transaction.extra_signatories,
            fn(key) { list.has(organiser_keys, key) },
          )

        and {
          validators.any_output_contains_own_validator_address(
            inputs,
            outputs,
            output_reference,
          )?,
          must_be_signed?,
          output_contains_final_result(sc.transaction.outputs, on_chain_results)?,
        }
      }
      ReduceVoteBatches -> {
        let on_chain_results: List<CategoryResultsDatum> =
          get_vote_results_from_inputs(
            contract_event_id,
            contract_organiser,
            contract_category_id,
            inputs,
          )

        let counted_on_chain_results: Dict<Proposal, Int> =
          count_vote_results(on_chain_results)

        let must_be_signed =
          list.any(
            sc.transaction.extra_signatories,
            fn(key) { list.has(organiser_keys, key) },
          )

        let final_result =
          CategoryResultsDatum {
            event_id: contract_event_id,
            organiser: contract_organiser,
            category_id: contract_category_id,
            results: counted_on_chain_results,
          }

        and {
          validators.any_output_contains_own_validator_address(
            inputs,
            outputs,
            output_reference,
          )?,
          must_be_signed?,
          output_contains_final_result(sc.transaction.outputs, final_result)?,
        }
      }
    }
  }
}

fn output_contains_final_result(
  outputs: List<Output>,
  on_chain_results: CategoryResultsDatum,
) -> Bool {
  let result_checker =
    fn(r: CategoryResultsDatum) { r == on_chain_results }

  let mapper =
    fn(output) {
      if adatums.is_inline_datum(output) {
        expect InlineDatum(results_data) = output.datum

        expect off_chain_results: CategoryResultsDatum = results_data

        Some(off_chain_results)
      } else {
        None
      }
    }

  list.filter_map(outputs, mapper)
    |> list.any(result_checker)
}

fn get_vote_results_from_inputs(
  contract_event_id: String,
  contract_organiser: String,
  contract_category_id: String,
  inputs: List<Input>,
) -> List<CategoryResultsDatum> {
  list.filter_map(
    inputs,
    fn(input) {
      if adatums.is_inline_datum(input.output) {
        expect InlineDatum(category_data) = input.output.datum
        expect categoryResultsDatum: CategoryResultsDatum = category_data

        let conditionsFullfilled = and {
            contract_event_id == categoryResultsDatum.event_id,
            contract_organiser == categoryResultsDatum.organiser,
            contract_category_id == categoryResultsDatum.category_id,
          }

        if conditionsFullfilled {
          Some(categoryResultsDatum)
        } else {
          None
        }
      } else {
        None
      }
    },
  )
}

fn get_votes_from_inputs(
  contract_event_id: String,
  contract_organiser: String,
  contract_category_id: String,
  inputs: List<Input>,
) -> List<Vote> {
  list.filter_map(
    inputs,
    fn(input) {
      if adatums.is_inline_datum(input.output) {
        expect InlineDatum(vote_data) = input.output.datum
        expect vote: Vote = vote_data

        let conditionsFullfilled = and {
            contract_event_id == vote.event_id,
            contract_category_id == vote.category_id,
            contract_organiser == vote.organiser,
          }

        if conditionsFullfilled {
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

fn count_vote_results(
  on_chain_results: List<CategoryResultsDatum>,
) -> Dict<Proposal, Int> {
  let merging_fn =
    fn(r1: Dict<Proposal, Int>, r2: Dict<Proposal, Int>) {
      dict.union_with(
        left: r1,
        right: r2,
        with: fn(_key, a, b) { Some(a + b) },
        compare: compare_proposal,
      )
    }

  let dict_list_with_results: List<Dict<Proposal, Int>> =
    list.map(on_chain_results, fn(rbd) { rbd.results })

  let empty = dict.new()
  list.reduce(dict_list_with_results, empty, merging_fn)
}

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
      let p: Proposal = h.proposal_id
      dict.union_with(
        left: do_count_votes(t, acc),
        right: dict.insert(
          self: dict.new(),
          key: p,
          value: 1,
          compare: compare_proposal,
        ),
        with: fn(_key, left, right) { Some(left + right) },
        compare: compare_proposal,
      )
    }
  }
}
