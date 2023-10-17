package org.cardano.foundation.voting.utils;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.*;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.crypto.SecretKey;
import com.bloxbean.cardano.client.crypto.VerificationKey;
import com.bloxbean.cardano.client.crypto.api.SigningProvider;
import com.bloxbean.cardano.client.crypto.bip32.HdKeyGenerator;
import com.bloxbean.cardano.client.crypto.config.CryptoConfiguration;
import com.bloxbean.cardano.client.exception.CborRuntimeException;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.util.List;

public final class TransactionSigningUtil {

    @SneakyThrows
    public static byte[] sign(byte[] transactionBytes, SecretKey secretKey) {
        byte[] txnBody = extractTransactionBody(transactionBytes);
        byte[] txnBodyHash = Blake2bUtil.blake2bHash256(txnBody);

        SigningProvider signingProvider = CryptoConfiguration.INSTANCE.getSigningProvider();
        VerificationKey verificationKey;
        byte[] signature;

        if (secretKey.getBytes().length == 64) { //extended pvt key (most prob for regular account)
            //check for public key
            byte[] vBytes = HdKeyGenerator.getPublicKey(secretKey.getBytes());
            signature = signingProvider.signExtended(txnBodyHash, secretKey.getBytes(), vBytes);

            try {
                verificationKey = VerificationKey.create(vBytes);
            } catch (CborSerializationException e) {
                throw new CborRuntimeException("Unable to get verification key from secret key", e);
            }
        } else {
            signature = signingProvider.sign(txnBodyHash, secretKey.getBytes());
            try {
                verificationKey = KeyGenUtil.getPublicKeyFromPrivateKey(secretKey);
            } catch (CborSerializationException e) {
                throw new CborRuntimeException("Unable to get verification key from SecretKey", e);
            }
        }

        return addWitnessToTransaction(transactionBytes, verificationKey.getBytes(), signature);
    }

    private static byte[] extractTransactionBody(byte[] txBytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(txBytes);
        CborDecoder decoder = new CborDecoder(bais);
        try {
            Array txArray = (Array)decoder.decodeNext();
            DataItem txBodyDI = txArray.getDataItems().get(0);
            return CborSerializationUtil.serialize(txBodyDI, false);
        } catch (CborException e) {
            throw new CborRuntimeException(e);
        } finally {
            try {
                bais.close();
            } catch (Exception e) {}
        }
    }

    @SneakyThrows
    private static byte[] addWitnessToTransaction(byte[] txBytes, byte[] vkey, byte[] signature) {
        Array txDIArray = (Array)CborSerializationUtil.deserialize(txBytes);
        List<DataItem> txDIList = txDIArray.getDataItems();

        try {
            DataItem witnessSetDI = txDIList.get(1);
            Map witnessSetMap = (Map) witnessSetDI;

            DataItem vkWitnessArrayDI = witnessSetMap.get(new UnsignedInteger(0));
            Array vkWitnessArray;
            if (vkWitnessArrayDI != null) {
                vkWitnessArray = (Array) vkWitnessArrayDI;
            } else {
                vkWitnessArray = new Array();
                witnessSetMap.put(new UnsignedInteger(0), vkWitnessArray);
            }

            //Add witness
            Array vkeyWitness = new Array();
            vkeyWitness.add(new ByteString(vkey));
            vkeyWitness.add(new ByteString(signature));

            vkWitnessArray.add(vkeyWitness);

            return CborSerializationUtil.serialize(txDIArray, false);
        } catch (CborException e) {
            throw new CborRuntimeException(e);
        }
    }

}
