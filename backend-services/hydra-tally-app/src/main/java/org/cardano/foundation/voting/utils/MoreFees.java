package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.Value;
import lombok.val;

import java.math.BigInteger;
import java.util.List;

public class MoreFees {

    public static void changeTransactionCost(Transaction preparedTransaction) {
        val fee = preparedTransaction.getBody().getFee();
        preparedTransaction.getBody().setFee(BigInteger.ZERO);

        val firstOutput = preparedTransaction.getBody().getOutputs().get(0);

        val newOutputValue = firstOutput.getValue()
                .plus(new Value(firstOutput.getValue().getCoin().add(fee), List.of()));

        firstOutput.setValue(newOutputValue);

        preparedTransaction.getBody().getOutputs().set(0, firstOutput);
    }

}
