import $ivy.`com.bloxbean.cardano:cardano-client-lib:0.5.0`

import $ivy.`org.slf4j:slf4j-simple:2.0.9`

import com.bloxbean.cardano.client.crypto.VerificationKey
import com.bloxbean.cardano.client.util.HexUtil
import com.bloxbean.cardano.client.crypto.KeyGenUtil

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

val logger = LoggerFactory.getLogger(getClass());

@main
def main(vkCbor: String) = {
    val vk = new VerificationKey(vkCbor);

    val key = HexUtil.encodeHexString(vk.getBytes());
    val keyHash = KeyGenUtil.getKeyHash(vk)

    println(s"key: ${key}")
    println(s"keyHash (blake 224): ${keyHash}")
}
