package org.cardano.foundation.voting.service.results;

import com.bloxbean.cardano.client.common.model.Network;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataUtxoStateReader;
import org.cardano.foundation.voting.service.plutus.PlutusScriptLoader;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@AllArgsConstructor
@Slf4j
public class VotingResultsService {

    private final ReferenceDataService referenceDataService;

    private final CategoryResultsDatumConverter categoryResultsDatumConverter;

    private final BlockchainDataUtxoStateReader blockchainDataUtxoStateReader;

    private final Network network;

    public Either<Problem, Optional<CategoryResultsDatum>> getVoteResults(String eventId,
                                                                          String categoryId) {
        val eventAdditionalInfoM = referenceDataService
                .findValidEventByName(eventId);

        if (eventAdditionalInfoM.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + eventId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var eventDetails = eventAdditionalInfoM.orElseThrow();

        // for now we only support user based voting via Hydra and L1 Leaderboard winners service
        // contract needs to be modified to support vote count and voting power based voting
        if (eventDetails.getVotingEventType() != USER_BASED) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_EVENT_TYPE_NOT_SUPPORTED")
                    .withDetail("Voting event type not supported, event:" + eventDetails + "," +
                            " votingEventType:" + eventDetails.getVotingEventType())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var isValidCategory = eventDetails.getCategories()
                .stream()
                .anyMatch(category -> category.getId().equals(categoryId));

        if (!isValidCategory) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + categoryId)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        // TODO if Hydra Tally object is not available in the event, it won't be available
        // and we need to return some error response
        // e.g. L1 results are not available for this event

        // TODO hard-code this by reading this from on chain event (effectively from the db)
        var verificationKeys = List.of("582071fa3a7188a0076f54f90445e572aada05626beda5067e6dcc5afd0ecd7bb3b3");
        var compiledContractTemplate = "59080601000032323232323232323223223223223222232533300f323253330113370e90011808000899191919191919191919191919191919191919299981219b87480000344c8c8c8c94ccc0a0ccc02c05004805854ccc0a00044cc030dd618099813180a981300d8010a5014a06600e6eb0c020c094c050c0940688cc028090004cdd2a4000660546ea4dcc010198151ba9373003c660546ea4dcc00e198151ba60014bd7019198008008011129998150008a5eb7bdb1804c8c8c8cccc024cc014014008c8cc0040052f5bded8c044a66605e00226606066ec0dd49b980034c010101004bd6f7b630099191919299981819baf3300d0070024c0103d8798000133034337606ea4dcc003a61010100005153330303372e00e00426606866ec0dd49b980074c1010100003133034337606ea4dcc0011ba800133006006003375a60620066e64dd718178011819801181880091119ba548000cc0c4dd419b800020014bd700039b99375c605c605e605e605e605e605e604e004605c0046058002660140204a66604a601a602c6046002264a66604c66e1d20043025001132323232533302a00113374a900019817001a5eb80530103d87a800053330293372e0466e64dd7180b18138010a99981499b9701f37326eb8c0b8c0bcc0bcc0bcc0bcc09c0084cdcb8109b99375c6034604e004294052819299981499b87480000044c8c8c8c8c8c8c8c8c8c8c8c94ccc0e0c0ec0085261637326eb8c0e4004c0e4008dcc9bae30370013037002375c606a002606a0046e64dd7181980098198011b99375c606200260620046e64dd7181780098138010b1813800981600098120008b18081811980b18118008a60103d87a8000132323232533302833300b0140120161533302800213300c37586026604c602a604c036002294052819ba548000cc0acdd49b980213302b37526e6007ccc0acdd49b9801d3302b374c00497ae0330063758600e60486026604803246601204600266644464666002002008006444a66605c00420022666006006606200466446600c0020046eacc0c0008004c8cc004004008894ccc0a800452f5c02660566e98dd5981618169816981698129816000998010011816800a5eb7bdb18088cccc018008004888cdd2a40006605c6ea0cdc0001000a5eb80010cc02804094ccc094c034c058c08c0044c94ccc098cdc3a4008604a002264646464a666054002266e9520003302e0034bd700a60103d87a800053330293372e0466e64dd7180b18138010a99981499b9702137326eb8c068c09c0084cdcb80f9b99375c6028604e00429405281807800981600098120008b18081811980b18118008a6103d87a8000223322533302733720004002298103d8798000153330273371e0040022980103d87a800014c103d87b8000300300230030012373000244446466600200200a008444a6660560042002264666008008605e00666664444646600200200e44a66606400226606666ec0dd49b98006375000a97adef6c6013232323253330333375e6600e014004980103d8798000133037337606ea4dcc0051ba8009005153330333372e01400426464a66606a66e1d2000001133039337606ea4dcc006181d18198010028802981980099980400500480089981b99bb037526e60008dd4000998030030019bad303400337326eb8c0c8008c0d8008c0d0004dcc9bae302a001375a605600200c00a605a00444646600200200644a66604e00229404c8c94ccc098c01400852889980200200098158011bae3029001230253026302630263026302630263026302600122323300100100322533302500114a026464a66604866e3c00801452889980200200098148011bae302700122232323300700123253330243370e90011811800899b8f375c605260440020082c6020604260206042002646600200200844a66604c002297ae0132325333025323253330273370e90010008a5114a0604a0026024604660246046004266052004660080080022660080080026054004605000264a66604266e1d20003020001132323253330243370e9001181180089bae30293022001163010302130103021301430210013027001301f00116323300100100422533302500114c0103d87a80001323253330243375e6022604400400a266e952000330280024bd70099802002000981480118138009119801998020011299980f9803800899299981019b8748010c07c0044c8c8c8cdd2a40006604e00497ae030090013026001301e00116300a301d00114c103d87a800023375e00200444646600200200644a66604400229404c8c94ccc084c014008528899802002000981300118120009119198008008019129998108008a5eb804c8c8c8c94ccc088cdc3a400400226600c00c00626604c604e60400046600c00c0066040002600a004604a0046046002464a66603666e1d20000011323232323232323253330263029002132498c8cc004004008894ccc0a000452613233003003302c0023232375a60520046e64dd7181380098150008b1bab3027001302700237326eb8c094004c094008dcc9bae3023001302300237326eb8c084004c06400858c0640048c8c94ccc06ccdc3a40080022944528180c8009802180b800980b0059bac30013014300330140092301b301c301c0013758600260246002602400e46032002602e002601e0022c6002601c0064602a602c00229309b2b19299980799b874800000454ccc048c03400c526161533300f3370e90010008a99980918068018a4c2c2c601a0046e64dd70009b99375c0026e64dd70009bac001230053754002460066ea80055cd2ab9d5573caae7d5d02ba15745";
        var contractHash = "58710f36229f7e7124d7b429e54930dcec19ef4fa042f3f534cff8bf";

        var scriptLoader = PlutusScriptLoader.builder()
                .eventId(eventDetails.getId())
                .categoryId(categoryId)
                .organiser(eventDetails.getOrganisers())
                .contractHash(contractHash)
                .verificationKeys(verificationKeys)
                .parametrisedCompiledTemplate(compiledContractTemplate)
                .build();

        var compiledScript = scriptLoader.getCompiledScript();

        var contractAddress = scriptLoader.getContractAddress(compiledScript, network);

        var uTxOsE = blockchainDataUtxoStateReader.getUTxOs(contractAddress);

        if (uTxOsE.isEmpty()) {
            return Either.left(uTxOsE.getLeft());
        }

        var uTxOs = uTxOsE.get();

        if (uTxOs.isEmpty()) {
            return Either.right(Optional.empty());
        }

        var categoryResultsDatumM = uTxOs.stream()
                .filter(utxo -> utxo.getInlineDatum().isPresent())
                .map(utxo -> utxo.getInlineDatum().get())
                .map(inlineDatum -> {
                    try {
                        return Optional.of(categoryResultsDatumConverter.deserialize(inlineDatum));
                    } catch (Exception e) {
                        log.warn("Unable to deserialize inline datum to CategoryResultsDatum", e);
                        return Optional.<CategoryResultsDatum>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        return Either.right(categoryResultsDatumM);
    }

}
