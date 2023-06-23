package org.cardano.foundation.voting.service.verification;

import io.micrometer.core.annotation.Timed;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationService {

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Timed(value = "service.verify.verifyEvent", percentiles = { 0.3, 0.5, 0.95 })
    @Transactional
    public boolean verifyEvent(Event event) {
        if (!event.getTeam().equals("CF & IOG")) {
            return false;
        }

        return true;

//        var maybeTransactionDetails = blockchainDataTransactionDetailsService.getTransactionDetails(event.getL1TransactionHash());
//
//        if (maybeTransactionDetails.isEmpty()) {
//            return false;
//        }
//        var transactionDetails = maybeTransactionDetails.orElseThrow();
//
//        // TODO is this clever or we should lower this requirement, finality = 1,5 days = 36 hours
//        if (!(transactionDetails.getFinalityScore().getScore() < TransactionDetails.FinalityScore.FINAL.getScore())) {
//            return false;
//        }
//
//        // TODO verify CF witness signature, it needs to be from our address
//
//        return true;
    }


    @Timed(value = "service.verify.verifyCategory", percentiles = { 0.3, 0.5, 0.95 })
    @Transactional
    public boolean verifyCategory(Category category) {
        var maybeTransactionDetails = blockchainDataTransactionDetailsService.getTransactionDetails(category.getL1TransactionHash());

        if (maybeTransactionDetails.isEmpty()) {
            return false;
        }
        var transactionDetails = maybeTransactionDetails.orElseThrow();

        // TODO is this clever or we should lower this requirement, finality = 1,5 days = 36 hours
        if (!(transactionDetails.getFinalityScore().getScore() < TransactionDetails.FinalityScore.FINAL.getScore())) {
            return false;
        }

        // TODO verify CF witness signature, it needs to be from our address

        return true;
    }

}
