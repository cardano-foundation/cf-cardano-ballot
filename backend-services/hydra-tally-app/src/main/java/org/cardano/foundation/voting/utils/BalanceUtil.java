package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.function.TxBuilder;
import com.bloxbean.cardano.client.function.helper.ChangeOutputAdjustments;
import com.bloxbean.cardano.client.function.helper.CollateralBuilders;
import com.bloxbean.cardano.client.function.helper.FeeCalculators;

//TODO -- This fix will be moved to cardano-client-lib after testing. Order of invocation for totalCollateral calculation and ChageOutputAdjustment
//changed.
public class BalanceUtil {

    public static TxBuilder balanceTx(String changeAddress, int nSigners) {
        return (context, txn) -> {
            FeeCalculators.feeCalculator(changeAddress, nSigners).apply(context, txn);

            ChangeOutputAdjustments.adjustChangeOutput(changeAddress, nSigners).apply(context, txn);

            if (txn.getBody().getCollateralReturn() != null) {
                CollateralBuilders.balanceCollateralOutputs().apply(context, txn);
            }
        };
    }
}
