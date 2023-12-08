// brew install amm
// amm cip_30_sign.sc

import $ivy.`com.bloxbean.cardano:cardano-client-lib:0.5.0-beta2`
import $ivy.`com.bloxbean.cardano:cardano-client-cip30:0.5.0-beta2`

import $ivy.`com.lihaoyi:requests_3:0.8.0`
import $ivy.`com.fasterxml.jackson.core:jackson-core:2.15.2`

import $ivy.`org.slf4j:slf4j-simple:2.0.9`

import com.bloxbean.cardano.client.account._
import com.bloxbean.cardano.client.common.model._

import com.bloxbean.cardano.client.cip.cip30._

import com.bloxbean.cardano.client.address._

import com.fasterxml.jackson.databind._

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

val mapper = new ObjectMapper()

val orgMnemonic = "ENTER WALLET MNEMO HERE"

val organiserAccount = new Account(Networks.testnet(), orgMnemonic)

val logger = LoggerFactory.getLogger(getClass());

def signDiscordCIP30(): Unit = {
    val stakeAddress = organiserAccount.stakeAddress()
    val stakeAddressAccount = new Address(stakeAddress)

    val input = s"938c2cc0dcc05f2b68c4287040cfcf72|dupa.jasiu"

    println(input)

    val cip30Result = CIP30DataSigner.INSTANCE.signData(
        stakeAddressAccount.getBytes(),
        input.getBytes("UTF-8"),
        organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
        organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
    );

        val output = s"""
        Signature: ${cip30Result.signature()}
        Public-Key: ${cip30Result.key()}
""".stripMargin

    println(output)
}

@main
def main() = {
    signDiscordCIP30()
}
