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

val orgMnemonic = "ENTER WALLET MNEMO HERE"

val organiserAccount = new Account(Networks.mainnet(), orgMnemonic)

val logger = LoggerFactory.getLogger(getClass());

def signCIP30LoginEnvelope(): Unit = {
    val lastSlot = latestAbsoluteSlot(mapper)

    if (lastSlot == -1) {
        logger.error("lastSlot error")
        return
    }

    val stakeAddress = organiserAccount.stakeAddress()
    val stakeAddressAccount = new Address(stakeAddress)

    val inputJSON = s"""
    {
        "uri": "https://evoting.cardano.org/voltaire",
        "action": "LOGIN",
        "actionText": "Login",
        "slot": "${lastSlot}",
        "data": {
            "address": "${stakeAddress}",
            "event": "CF_SUMMIT_2023_TEST2",
            "network": "MAIN",
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
        X-CIP93-Signature: ${cip30Result.signature()}
        X-CIP93-Public-Key: ${cip30Result.key()}
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
        "uri": "https://evoting.cardano.org/voltaire",
        "action": "CAST_VOTE",
        "actionText": "Cast Vote",
        "slot": "${lastSlot}",
        "data": {
            "id": "${voteId}",
            "address": "${stakeAddress}",
            "event": "CIP-1694_Pre_Ratification_3316",
            "category": "MIN_VIABLE_GOV_STRUCTURE",
            "proposal": "ABSTAIN",
            "votingPower": "9980230980",
            "network": "PREPROD",
            "votedAt": "${lastSlot}"
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
        "https://follower-api.pro.cf-summit-2023-mainnet.eu-west-1.voting.summit.cardano.org/yaci-api/blocks/latest",
        headers = Map("Content-Type" -> "application/json")
    )

    if (r.statusCode == 200) {
        val body = r.text
        val tree = mapper.readTree(body);

        val slot = tree.get("slot").asLong()

        slot
    } else {
        -1
    }
}

@main
def main() = {
    signCIP30LoginEnvelope()
    //signCIP30VoteCastEnvelope()
    //signCIP30ViewVoteReceiptEnvelope()
}
