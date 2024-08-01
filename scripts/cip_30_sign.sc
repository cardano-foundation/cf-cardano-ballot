// brew install amm
// amm cip_30_sign.sc

import $ivy.`com.bloxbean.cardano:cardano-client-lib:0.5.0`
import $ivy.`com.bloxbean.cardano:cardano-client-cip30:0.5.0`

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

val orgMnemonic = "test test test test test test test test test test test test test test test test test test test test test test test sauce"

val organiserAccount = new Account(Networks.preprod(), orgMnemonic)

val logger = LoggerFactory.getLogger(getClass());

def signCIP30LoginEnvelope(): Unit = {
    //val lastSlot = latestAbsoluteSlot(mapper)
    val lastSlot = 40162406;

    if (lastSlot == -1) {
        logger.error("lastSlot error")
        return
    }

    val stakeAddress = organiserAccount.stakeAddress()
    println(s"address stake: ${stakeAddress}")

    val stakeAddressAccount = new Address(stakeAddress)

    val inputJSON = s"""
            {
              "action": "LOGIN",
              "actionText": "Login",
              "slot": "40262406",
              "data": {
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType: "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "network": "PREPROD",
                "role": "VOTER"
              }
            }
""".stripMargin

    println(inputJSON)

    val cip30Result = CIP30DataSigner.INSTANCE.signData(
        stakeAddressAccount.getBytes(),
        inputJSON.getBytes("UTF-8"),
        organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
        organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
    );

    val output = s"""
        X-Login-Signature: ${cip30Result.signature()}
        X-Login-Public-Key: ${cip30Result.key()}
        X-Wallet-Type: "CARDANO"
""".stripMargin

    println(output)
}

def signCIP30VoteCastEnvelope(): Unit = {
    val lastSlot = latestAbsoluteSlot(mapper)

    if (lastSlot == -1) {
        logger.error("lastSlot error")
        return
    }

    val stakeAddress = organiserAccount.stakeAddress()
    val stakeAddressAccount = new Address(stakeAddress)

    val voteId = java.util.UUID.randomUUID().toString()

    val inputJSON = s"""
            {
              "action": "CAST_VOTE",
              "actionText": "Cast Vote",
              "data": {
                "id": "2658fb7d-cd12-48c3-bc95-23e73616b79f",
                "walletId": "stake_test1uruw6wswag80sd0l57alehj47llf6tx96402vt8vks46k0q0e2ne6",
                "walletType": "CARDANO",
                "event": "CF_TEST_EVENT_01",
                "category": "CHANGE_SOMETHING",
                "proposal": "YES",
                "network": "PREPROD",
                "votedAt": "40262406",
                "votingPower": "10444555666"
              },
              "slot": "40262406",
              "uri": "https://evoting.cardano.org/voltaire"
            }
""".stripMargin

    println(inputJSON)

    val cip30Result = CIP30DataSigner.INSTANCE.signData(
        stakeAddressAccount.getBytes(),
        inputJSON.getBytes("UTF-8"),
        organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
        organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
    );

    val output = s"""
        X-CIP93-Signature: ${cip30Result.signature()}
        X-CIP93-Public-Key: ${cip30Result.key()}
""".stripMargin

    println(output)
}

def signCIP30ViewVoteReceiptEnvelope(): Unit = {
    val lastSlot = latestAbsoluteSlot(mapper)

    if (lastSlot == -1) {
        logger.error("lastSlot error")
        return
    }

    val stakeAddress = organiserAccount.stakeAddress()
    val stakeAddressAccount = new Address(stakeAddress)

    val voteId = java.util.UUID.randomUUID().toString()

    val inputJSON = s"""
    {
        "uri": "https://evoting.cardano.org/voltaire",
        "action": "VIEW_VOTE_RECEIPT",
        "actionText": "Vote",
        "slot": "${lastSlot}",
        "data": {
            "id": "${voteId}",
            "address": "${stakeAddress}",
            "event": "CIP-1694_Pre_Ratification_3316",
            "category": "CHANGE_GOV_STRUCTURE",
            "network": "PREPROD"
        }
   }
""".stripMargin

    println(inputJSON)

    val cip30Result = CIP30DataSigner.INSTANCE.signData(
        stakeAddressAccount.getBytes(),
        inputJSON.getBytes("UTF-8"),
        organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
        organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
    );

    val output = s"""
        X-CIP93-Signature: ${cip30Result.signature()}
        X-CIP93-Public-Key: ${cip30Result.key()}
""".stripMargin

    println(output)
}

def latestAbsoluteSlot(mapper: ObjectMapper): Long = {
    val r = requests.get(
        "https://cardano-preprod.blockfrost.io/api/v0/blocks/latest",
        headers = Map
            (
            "Content-Type" -> "application/json",
            "project_id" -> "preprodTkCORBc752YRMKxBPw83zybOuRNelcP7"
            )
    )

    if (r.statusCode == 200) {
        val body = r.text()
        val tree = mapper.readTree(body);

        val slot = tree.get("slot").asLong()

        slot
    } else {
        -1
    }
}

@main
def main() = {
    //signCIP30LoginEnvelope()
    signCIP30VoteCastEnvelope()
    //signCIP30ViewVoteReceiptEnvelope()
}
