//package org.cardano.foundation.voting.utils;
//
//import com.bloxbean.cardano.client.util.JsonUtil;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.jupiter.api.Test;
//
//class MetadataParserTest {
//
//    @Test
//    void parseArrayStringMetadata() throws JsonProcessingException {
//        var json = "{" +
//                "        \"signature\" : [ \"845890a301270458407374616b655f7465737431757177637a30373534777770\", \"75686d36786864706461367539656e796168616a35796e6c63396179356c346d\", \"6c6d73347079717967676164647265737358407374616b655f74657374317571\", \"77637a3037353477777075686d36786864706461367539656e796168616a3579\", \"6e6c63396179356c346d6c6d73347079717967a166686173686564f45888a764\", \"6e616d65781e4349502d313639345f5072655f526174696669636174696f6e5f\", \"44423034647465616d684346202620494f476474797065724556454e545f5245\", \"47495354524154494f4e68656e6445706f6368185a6a737461727445706f6368\", \"18466d736368656d6156657273696f6e65312e302e306d736e617073686f7445\", \"706f6368184b5840531f847ca34e414669ded9c0727dd2f2998005d5d54b3199\", \"ef66ec7b8d6c4df3e3a4d987fb1272a3eab5d02b09863ff90e4bb663c490358e\", \"bfe208531d727d0c\" ],\n" +
//                "        \"type\" : \"EVENT_REGISTRATION\",\n" +
//                "        \"key\" : [ \"a501010258407374616b655f7465737431757177637a3037353477777075686d\", \"36786864706461367539656e796168616a35796e6c63396179356c346d6c6d73\", \"3470797179670327200621582071fa3a7188a0076f54f90445e572aada05626b\", \"eda5067e6dcc5afd0ecd7bb3b3\" ]\n" +
//                "        }";
//
//        var jsonNode = JsonUtil.parseJson(json);
//
//        String signature = ChunkedMetadataParser.parseArrayStringMetadata(jsonNode.get("signature"));
//
//        System.out.println(signature);
//    }
//}
//
////2023-06-28T10:33:28.502+02:00  INFO 15176 --- [           main] o.c.f.v.s.t.L1TransactionCreator         : Metadata envelope:{
////        "signature" : [ "845890a301270458407374616b655f7465737431757177637a30373534777770", "75686d36786864706461367539656e796168616a35796e6c63396179356c346d", "6c6d73347079717967676164647265737358407374616b655f74657374317571", "77637a3037353477777075686d36786864706461367539656e796168616a3579", "6e6c63396179356c346d6c6d73347079717967a166686173686564f45888a764", "6e616d65781e4349502d313639345f5072655f526174696669636174696f6e5f", "44423034647465616d684346202620494f476474797065724556454e545f5245", "47495354524154494f4e68656e6445706f6368185a6a737461727445706f6368", "18466d736368656d6156657273696f6e65312e302e306d736e617073686f7445", "706f6368184b5840531f847ca34e414669ded9c0727dd2f2998005d5d54b3199", "ef66ec7b8d6c4df3e3a4d987fb1272a3eab5d02b09863ff90e4bb663c490358e", "bfe208531d727d0c" ],
////        "type" : "EVENT_REGISTRATION",
////        "key" : [ "a501010258407374616b655f7465737431757177637a3037353477777075686d", "36786864706461367539656e796168616a35796e6c63396179356c346d6c6d73", "3470797179670327200621582071fa3a7188a0076f54f90445e572aada05626b", "eda5067e6dcc5afd0ecd7bb3b3" ]
////        }