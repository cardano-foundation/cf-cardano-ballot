// brew install amm
// amm cip30_debug.sc

import $ivy.`org.cardanofoundation:cip30-data-signature-parser:0.0.10`

import $ivy.`org.slf4j:slf4j-simple:2.0.9`

import org.cardanofoundation.cip30.CIP30Verifier

import java.util.Optional

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cardanofoundation.cip30.AddressFormat
import org.cardanofoundation.cip30.MessageFormat

val logger = LoggerFactory.getLogger(getClass());

@main
def main(sig: String, key: String) = {
    val cip30Verifier = new CIP30Verifier(sig, Optional.of(key));
    val verificationResult = cip30Verifier.verify();

    println(s"valid: ${verificationResult.isValid()}");

    var text = verificationResult.getMessage(MessageFormat.TEXT)

    verificationResult.getAddress(AddressFormat.TEXT).map(bech32 => println(s"Address: $bech32"))
    println(s"Message: $text");
}
