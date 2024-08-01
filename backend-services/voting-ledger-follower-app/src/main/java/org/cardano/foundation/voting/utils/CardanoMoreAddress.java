package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.common.model.Networks;

public final class CardanoMoreAddress {

    public static boolean isMainnet(String stakeAddress) {
        var addr = new Address(stakeAddress);

        return addr.getNetwork().equals(Networks.mainnet());
    }

    public static boolean isTestnet(String stakeAddress) {
        return !isMainnet(stakeAddress);
    }

}
