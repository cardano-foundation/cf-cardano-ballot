package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.Value;
import lombok.val;

import java.math.BigInteger;
import java.util.List;

public class MoreFees {

    public static void changeTransactionCost(Transaction transaction) {
//        val fee = transaction.getBody().getFee();
//        transaction.getBody().setFee(BigInteger.ZERO);
//
//        val firstOutput = transaction.getBody().getOutputs().get(0);
//
//        val newOutputValue = firstOutput.getValue()
//                .plus(new Value(firstOutput.getValue().getCoin().add(fee), List.of()));
//
//        firstOutput.setValue(newOutputValue);
//
//        transaction.getBody().getOutputs().set(0, firstOutput);
    }

}
