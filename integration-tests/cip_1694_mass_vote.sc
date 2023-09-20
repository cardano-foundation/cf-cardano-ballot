import $ivy.`com.bloxbean.cardano:cardano-client-lib:0.5.0-alpha.4`
import $ivy.`com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0-alpha.4`
import $ivy.`com.bloxbean.cardano:cardano-client-cip30:0.5.0-alpha.4`

import $ivy.`com.lihaoyi:requests_3:0.8.0`
import $ivy.`com.fasterxml.jackson.core:jackson-core:2.15.2`

import com.bloxbean.cardano.client.api.model.Amount
import com.bloxbean.cardano.client.crypto._
import com.bloxbean.cardano.client.account._
import com.bloxbean.cardano.client.common.model._

import com.bloxbean.cardano.client.cip.cip30._

import com.bloxbean.cardano.client.address._

import com.bloxbean.cardano.client.crypto.Blake2bUtil._
import com.bloxbean.cardano.client.common.cbor._
import com.bloxbean.cardano.client.metadata._

import com.bloxbean.cardano.client.common._

import java.math._
import java.util.Random

import com.fasterxml.jackson.databind._

import com.bloxbean.cardano.client.util.HexUtil

import com.bloxbean.cardano.client.quicktx._

import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService

import com.bloxbean.cardano.client.function.helper.SignerProviders

case class CreateEventCommand(id: String,
                              team: String,
                              allowVoteChanging: Boolean,
                              highLevelEventResultsWhileVoting: Boolean,
                              highLevelCategoryResultsWhileVoting: Boolean, 
                              categoryResultsWhileVoting: Boolean, 
                              votingEventType: String,
                              votingPowerAsset: Option[String] = None,
                              startEpoch: Option[Int] = None,
                              endEpoch: Option[Int] = None,
                              startSlot: Option[Long] = None,
                              endSlot: Option[Long] = None,
                              snapshotEpoch: Option[Int] = None,
                              schemaVersion: String,
                              proposalsRevealEpoch: Option[Int] = None,
                              proposalsRevealSlot: Option[Long] = None
                              )

case class Proposal(id: String, name: String)

case class CreateCategoryCommand(id: String,
                                 event: String,
                                 gdprProtection: Boolean,
                                 proposals: List[Proposal],
                                 schemaVersion: String
                              )

val blockfrostUrl = "http://localhost:8080/api/v1/"
val blockfrostApiKey = ""
val backendService = new BFBackendService(blockfrostUrl, blockfrostApiKey)

val metadataLabel = 11113
val amountAda: Int = 1000

val eventName = "CIP-1694_Pre_Ratification_YACI"
val orgMnemonic = "ocean sad mixture disease faith once celery mind clay hidden brush brown you sponsor dawn good claim gloom market world online twist laptop thrive"

val organiserAccount = new Account(Networks.testnet(), orgMnemonic)
val mapper = new ObjectMapper()

val endEpoch = Some(10)

val cip1694Event = CreateEventCommand(
    id = eventName,
    team = "CF",
    allowVoteChanging = false,
    highLevelEventResultsWhileVoting = false,
    highLevelCategoryResultsWhileVoting = false,
    categoryResultsWhileVoting = false,
    votingEventType = "STAKE_BASED",
    votingPowerAsset = Some("ADA"),
    startEpoch = Some(0),
    endEpoch = endEpoch,
    snapshotEpoch = Some(0),
    proposalsRevealEpoch = endEpoch.map(e => e + 10),
    schemaVersion = "1.0.0"
)

val yesProposal = new Proposal(
    id = "e42f820f-5852-4c03-9d42-8cf4a4044a51",
    name = "YES"
)

val noProposal = new Proposal(
    id = "3b40644b-3f6f-4c91-945e-4d612fa4f6cf",
    name = "NO"
)

val abstainProposal = new Proposal(
    id = "a8f60f84-58bf-47b3-9582-5272fbdc6ff6",
    name = "ABSTAIN"
)

val cip1694Category = CreateCategoryCommand(
        id = "CIP-1694_Pre_Ratification",
        event = eventName,
        gdprProtection = false,
        schemaVersion = "1.0.0",
        proposals = List(yesProposal, noProposal, abstainProposal)
)

def toBigInteger(value: Boolean): BigInteger = if (value) { BigInteger.ONE } else { BigInteger.ZERO }

def topUpAccount(newAcc: Account, amount: Int): Boolean = {
    val jsonTopUpPayload = s""" {
        "address": \"${newAcc.baseAddress()}\",
        "adaAmount": $amount
    }
    """.stripMargin

    val r = requests.post(
        "http://localhost:10000/local-cluster/api/addresses/topup", 
        headers = Map("Content-Type" -> "application/json"),
        data = jsonTopUpPayload
    )
    
    var res = if (r.statusCode == 200) {
        println(s"Topup with ${amount} for addr:${newAcc.baseAddress} success!")

        true
    } else {
        println(s"Topup with ${amount} for addr:${newAcc.baseAddress} failed!")

        false
    }

    println("sleeping for 1 sec")
    Thread.sleep(1000)

    res
}

def randomProposal(): Proposal = {
    val r = new Random()
    val proposals = List(yesProposal, noProposal, abstainProposal)
    val index = r.nextInt(proposals.length)

    proposals(index)
}

def castVote(acc: Account, amountAda: Int): Boolean = {
    val lastSlot = latestAbsoluteSlot(mapper)
    println(s"slot:${lastSlot}")

    println("Casting random vote for account...");

    val voteId = java.util.UUID.randomUUID().toString()
    val stakeAddress = acc.stakeAddress()

    println("stakeAddress:" + stakeAddress)

    val randomP: Proposal = randomProposal()
    val stakeAddressAccount = new Address(stakeAddress);

    val voteJson = s"""
    {
        "uri": "https://evoting.cardano.org/voltaire",
        "action": "CAST_VOTE",
        "actionText": "Cast Vote",
        "slot": "${lastSlot}",
        "data": {
            "id": "${voteId}",
            "address": "${stakeAddress}",
            "event": "CIP-1694_Pre_Ratification_YACI",
            "category": "CIP-1694_Pre_Ratification",
            "proposal": "${randomP.name}",
            "network": "DEV",
            "votedAt": "${lastSlot}",
            "votingPower": "${ADAConversionUtil.adaToLovelace(amountAda)}"
        }
}
""".stripMargin

    println(s"${voteJson}")

    val castCoteCIP30Result = CIP30DataSigner.INSTANCE.signData(
        stakeAddressAccount.getBytes(),
        voteJson.getBytes("UTF-8"),
        acc.stakeHdKeyPair().getPrivateKey().getKeyData(),
        acc.stakeHdKeyPair().getPublicKey().getKeyData()
    );

    val r = requests.post(
        "http://localhost:9091/api/vote/cast", 
        headers = Map(
         "Content-Type" -> "application/json",
         "X-CIP93-Signature" -> castCoteCIP30Result.signature(),
         "X-CIP93-Public-Key" -> castCoteCIP30Result.key()
    ),
    data = "{ }"
    )

    if (r.statusCode == 200) {
        println(s"VoteId: ${voteId} cast successfuly!")

        true
    } else {
        println(s"VoteId: ${voteId} cast failed!")

        false
    }
}

def latestAbsoluteSlot(mapper: ObjectMapper): Long = {
    val r = requests.get(
        "http://localhost:8080/api/v1/blocks/latest",
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

def registerEventAndCategories(organiserAccount: Account, mapper: ObjectMapper, eventName: String): Boolean = {
    var latestSlot: Long = latestAbsoluteSlot(mapper)

    println(s"Latest absolute slot:${latestSlot}")

    if (latestSlot == -1) {
        return false
    }
    val eventMetadataMap: MetadataMap = createEvent(cip1694Event, latestSlot)

    val eventMetadata: Metadata = serialiseCIP30(organiserAccount, eventMetadataMap, "EVENT_REGISTRATION")

    var eventTxData: Array[Byte] = serialiseTx(organiserAccount, eventMetadata)
    var eventTxId: String = submitTx(eventTxData)

    println("Event txId:" + eventTxId)

    if (eventTxId != null) {
        latestSlot = latestAbsoluteSlot(mapper)

        println(s"Latest absolute slot:${latestSlot}")

        val categoryMetadataMap: MetadataMap = createCategory(cip1694Category, latestSlot)

        val categoryMetadata: Metadata = serialiseCIP30(organiserAccount, categoryMetadataMap, "CATEGORY_REGISTRATION")

        var categoryTxData: Array[Byte] = serialiseTx(organiserAccount, categoryMetadata)
        var categoryTxId: String = submitTx(categoryTxData)

        println("Category txId:" + categoryTxId)

        return categoryTxId != null
    }

    return false
}

def submitTx(txData: Array[Byte]): String = {
    var result = backendService.getTransactionService().submitTransaction(txData)

    var res = if (result.isSuccessful()) {
        result.getValue()
    } else {
        println(s"Tx failure, reason: ${result.getResponse()}")
        null
    }

    println("Sleeping for 1 sec.")

    Thread.sleep(1000L)

    res
}

def serialiseTx(organiserAccount: Account, metadata: Metadata) = {
    val quickTxBuilder = new QuickTxBuilder(backendService)

    val tx = new Tx()
            .payToAddress(organiserAccount.baseAddress(), Amount.ada(2.0))
            .attachMetadata(metadata)
            .from(organiserAccount.baseAddress())

    quickTxBuilder.compose(tx)
            .withSigner(SignerProviders.signerFrom(organiserAccount))
            .buildAndSign()
            .serialize()
}

def serialiseCIP30(organiserAccount: Account, childMetadata: MetadataMap, onChainEventType: String): Metadata = {
    val stakeAddress = organiserAccount.stakeAddress()
    println("stakeAddress:" + stakeAddress)
    val stakeAddressAccount = new Address(stakeAddress)

    val data = CborSerializationUtil.serialize(childMetadata.getMap())
    val hashedData = blake2bHash224(data)

    val dataSignature = CIP30DataSigner.INSTANCE.signData(
        stakeAddressAccount.getBytes(),
        hashedData,
        organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
        organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
    );

    val envelope = MetadataBuilder.createMap()
    envelope.put("type", onChainEventType)
    envelope.put("signature", dataSignature.signature())
    envelope.put("key", dataSignature.key())
    envelope.put("signatureType", "HASH_ONLY")
    envelope.put("hashType", "BLAKE2B_224")

    envelope.put("payload", childMetadata)

    envelope.put("format", "CIP-30")
    envelope.put("subFormat", "CBOR")

    val metadata = MetadataBuilder.createMetadata()
    metadata.put(metadataLabel, envelope)

    metadata
}

def createEvent(createEventCommand: CreateEventCommand, slot: Long): MetadataMap = {
    var map = MetadataBuilder.createMap()

    map.put("type", "EVENT_REGISTRATION")

    map.put("id", createEventCommand.id)
    map.put("organisers", createEventCommand.team)
    map.put("votingEventType", createEventCommand.votingEventType)
    map.put("schemaVersion", createEventCommand.schemaVersion)
    map.put("creationSlot", BigInteger.valueOf(slot))

    if (List("STAKE_BASED", "BALANCE_BASED").contains(createEventCommand.votingEventType)) {
        map.put("startEpoch", BigInteger.valueOf(createEventCommand.startEpoch.get))
        map.put("endEpoch", BigInteger.valueOf(createEventCommand.endEpoch.get))
        map.put("snapshotEpoch", BigInteger.valueOf(createEventCommand.snapshotEpoch.get))
        map.put("snapshotEpoch", BigInteger.valueOf(createEventCommand.snapshotEpoch.get))
        map.put("proposalsRevealEpoch", BigInteger.valueOf(createEventCommand.endEpoch.get))
        map.put("votingPowerAsset", createEventCommand.votingPowerAsset.get)
    }
    if (createEventCommand.votingEventType.equals("USER_BASED")) {
        map.put("startSlot", BigInteger.valueOf(createEventCommand.startSlot.get))
        map.put("endSlot", BigInteger.valueOf(createEventCommand.endSlot.get))
    }

    map.put("options", createEventOptions(createEventCommand))

    map
}

def createEventOptions(createEventCommand: CreateEventCommand): MetadataMap = {
    val optionsMap = MetadataBuilder.createMap()
    optionsMap.put("allowVoteChanging", toBigInteger(createEventCommand.allowVoteChanging))
    optionsMap.put("highLevelEventResultsWhileVoting", toBigInteger(createEventCommand.highLevelEventResultsWhileVoting))
    optionsMap.put("highLevelCategoryResultsWhileVoting", toBigInteger(createEventCommand.highLevelCategoryResultsWhileVoting))
    optionsMap.put("categoryResultsWhileVoting", toBigInteger(createEventCommand.categoryResultsWhileVoting))

    optionsMap
}

def createCategory(createCategoryCommand: CreateCategoryCommand, slot: Long): MetadataMap = {
    var map = MetadataBuilder.createMap()

    map.put("type", "CATEGORY_REGISTRATION")

    map.put("id", createCategoryCommand.id)
    map.put("event", createCategoryCommand.event)

    map.put("schemaVersion", createCategoryCommand.schemaVersion)
    map.put("creationSlot", BigInteger.valueOf(slot))
    map.put("options", createCategoryOptions(createCategoryCommand))

    map.put("proposals", createProposals(createCategoryCommand))

    map
}

def createProposals(createCategoryCommand: CreateCategoryCommand): MetadataList = {
    val proposalsList = MetadataBuilder.createList()

    for (proposal <- createCategoryCommand.proposals) {
        val proposalMap = MetadataBuilder.createMap()

        if (createCategoryCommand.gdprProtection) {
            proposalMap.put("id", proposal.id);
        } else {
            proposalMap.put("id", proposal.id);
            proposalMap.put("name", proposal.name);
        }

        proposalsList.add(proposalMap);
    }

    proposalsList
}

def createCategoryOptions(createCategoryCommand: CreateCategoryCommand): MetadataMap = {
    var optionsMap = MetadataBuilder.createMap()
    optionsMap.put("gdprProtection", toBigInteger(createCategoryCommand.gdprProtection));

    optionsMap
}

@main
def main(isAlreadyRegistered: Boolean = false, organiserAlreadyToppedUp: Boolean = false) = {
    val isOrganiserAccountLoaded = if (organiserAlreadyToppedUp) { true } else { topUpAccount(organiserAccount, 10000) }

    if (isOrganiserAccountLoaded) {
        val isRegistered = if (isAlreadyRegistered) { true } else { registerEventAndCategories(organiserAccount, mapper, eventName) }

        if (isRegistered) {
            println("Onchain event and categories registration success!")

            println("sleep for 5 seconds")

            Thread.sleep(5 * 1000L) // lets wait until voting-app and verification app ingest event and category data

            println("woke up...")

            for (i <- 1 to 50001) {
                try {
                    val account = Account(Networks.testnet())
                    //val isPreloaded = topUpAccount(account, amountAda)
                    val isPreloaded = true

                    if (isPreloaded) {
                        castVote(account, amountAda)
                    }
                } catch {
                    case e: Exception => println("vote cast error, error:" + e.getMessage())
                }
            }
            
        }
    }

}
