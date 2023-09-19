// brew install amm
// amm cose_debug.sc

import $ivy.`org.cardanofoundation:cip30-data-signature-parser:0.0.10`

import $ivy.`org.slf4j:slf4j-simple:2.0.9`

import org.cardanofoundation.cip30.CIP30Verifier

import java.util.Optional

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

val logger = LoggerFactory.getLogger(getClass());

@main
def main() = {
    val coseSig = "84582aa201276761646472657373581de0ab2a3abab6339252c9fdb9a51ae58099654e2391ce07616f0e1b734ba166686173686564f4584b393964663265613931346364636664353130363933363531353861303037373830393637333137643538653063376539633666393663396337313663613666367c3170373737667935637658404e9b80972aa07ca8dbe91a17641e3962c68812345c598a0e572a500f4788e304e51dff0ccd67d9f38622ff34e202264b67078d71390669840ec6515132f07707"
    val cosePubKey = "a4010103272006215820752c7e93380225fb5dd8563ffc95dd84fe0ff689dc51f14c51315bb2fff053c1"

    val cip30Verifier = new CIP30Verifier(coseSig, Optional.of(cosePubKey));
    val verificationResult = cip30Verifier.verify();

    var text = verificationResult.getMessage(org.cardanofoundation.cip30.MessageFormat.TEXT)

    println(text);
}
