// mati@Mateuszs-MBP scripts % amm check_address.sc --mnemonic "solution ..."  

import $ivy.`com.bloxbean.cardano:cardano-client-lib:0.5.0`

import $ivy.`org.slf4j:slf4j-simple:2.0.9`

import com.bloxbean.cardano.client.common.model.Networks
import com.bloxbean.cardano.client.account.Account

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

val logger = LoggerFactory.getLogger(getClass());

@main
def main(mnemonic: String) = {
    val account = new Account(Networks.testnet(), mnemonic)
    val stakeAddress = account.stakeAddress()

    println(stakeAddress)
}

// curl -H "project_id: password"     https://cardano-preprod.blockfrost.io/api/v0/accounts/stake_test1uzw478du6llchakgyety622kngsg8jcgs5jl72qnadm5y3skdkp5u
// {"stake_address":"stake_test1uzw478du6llchakgyety622kngsg8jcgs5jl72qnadm5y3skdkp5u","active":true,"active_epoch":96,"controlled_amount":"2997489065","rewards_sum":"0","withdrawals_sum":"0","reserves_sum":"0","treasury_sum":"0","withdrawable_amount":"0","pool_id":"pool1xafhfcvhyk2cyr7lprt4luxjcxgh8z87z6fke7d6grvvzljga3l"}%
