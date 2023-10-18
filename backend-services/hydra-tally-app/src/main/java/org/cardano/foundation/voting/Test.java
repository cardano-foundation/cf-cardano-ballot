package org.cardano.foundation.voting;

import org.cardano.foundation.voting.domain.VoteDatum;
import org.cardano.foundation.voting.domain.VoteDatumConverter;

public class Test {


    public static void main(String[] args) {
        System.out.println("Hello, world!");
        var t = "d8799f4a414d4241535341444f52a6582430323939643933652d393366322d346263382d396234302d36646430393334336334343301582430623735356561662d613538382d343431662d613964642d35306334616134373861393001582432633934636432652d326164392d343432352d616630312d32373231306166636131653301582436333132336537662d646663332d343831652d626239642d66656431643966366539623901582465376434646634612d383330352d346564382d396534322d36663637343432643739366501582466643437376661632d616431362d346432612d393161342d30613432383864336437616101ff";

        var newDatum = new VoteDatum();
        newDatum.setCategoryId(new byte[] { 0, 1, 3});
        newDatum.setProposalId(new byte[] { 2, 3, 4 });
        newDatum.setVoteId(new byte[] { 5, 6, 7 });
        newDatum.setVoterKey(new byte[] { 8, 9, 10 });

        var serialized = new VoteDatumConverter().serialize(newDatum);

        System.out.println(serialized);

        var datum = new VoteDatumConverter().deserialize(serialized);

        System.out.println(datum);
    }

}
