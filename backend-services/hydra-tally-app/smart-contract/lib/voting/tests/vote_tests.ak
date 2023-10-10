use acca/collections/hashed_list
use aiken/builtin
use aiken/hash.{sha2_256}
use voting/vote.{Vote}

test vote_hash1() {
  let v0 =
    Vote(
      #"68fb74df64af1bb4559ed5b1f777515ad9b259923fa1618f354992bac12fd14a",
      9631517,
      192691745,
      1426925,
      1,
    )

  let v1 =
    Vote(
      #"8254cca30347062475b28b3669da2a5e4b93c164ed81d5341cf229f6b6f09168",
      1418415,
      422503982,
      5026403,
      2,
    )

  let v2 =
    Vote(
      #"c616dbb35b70ac7b3d365d4676225c5f9d9b6201cbb141249691f3f81448ff3c",
      7859940,
      223135486,
      2376972,
      2,
    )

  let items =
    [v0, v1, v2]

  let hash_fn =
    fn(v: Vote) { sha2_256(builtin.serialise_data(v)) }

  let hashed =
    hashed_list.from(items, hash_fn)

  #"9c89699e1d025419538376b61a2fdc866f623a28d45d63e895b77f3745110b94" == hashed.hash
}
