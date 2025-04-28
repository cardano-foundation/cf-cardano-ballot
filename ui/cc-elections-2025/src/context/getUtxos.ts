import { CardanoApiWallet } from "@models";
import * as Sentry from "@sentry/react";
import {
  TransactionUnspentOutput,
  MultiAsset,
} from "@emurgo/cardano-serialization-lib-asmjs";
import { Buffer } from "buffer";

type Utxos = {
  txid: string;
  txindx: number;
  amount: string;
  str: string;
  multiAssetStr: string;
  TransactionUnspentOutput: TransactionUnspentOutput;
}[];

const parseMultiAsset = (multiasset?: MultiAsset): string => {
  if (!multiasset) return "";

  return Array.from({ length: multiasset.keys().len() }, (_, i) => {
    const policyId = multiasset.keys().get(i);
    const policyIdHex = policyId.to_hex();
    const assets = multiasset.get(policyId);

    return assets
      ? Array.from({ length: assets.keys().len() }, (_i, j) => {
          const assetName = assets.keys().get(j);
          const assetNameHex = Buffer.from(
            assetName.name().toString(),
            "utf8",
          ).toString("hex");
          const assetNameString = assetName.name().toString();
          const multiassetAmt = multiasset.get_asset(policyId, assetName);
          return `+ ${multiassetAmt.to_str()} + ${policyIdHex}.${assetNameHex} (${assetNameString})`;
        }).join(" ")
      : "";
  }).join(" ");
};

export const getUtxos = async (
  enabledApi: CardanoApiWallet,
): Promise<Utxos | undefined> => {
  try {
    const rawUtxos = await enabledApi.getUtxos();
    return rawUtxos.map((rawUtxo: string) => {
      const utxo = TransactionUnspentOutput.from_bytes(
        Buffer.from(rawUtxo, "hex"),
      );
      const input = utxo.input();
      const output = utxo.output();

      return {
        txid: input.transaction_id().to_hex(),
        txindx: input.index(),
        amount: output.amount().coin().to_str(),
        multiAssetStr: parseMultiAsset(output.amount().multiasset()),
        str: `${input.transaction_id().to_hex()} #${input.index()} = ${output
          .amount()
          .coin()
          .to_str()}`,
        TransactionUnspentOutput: utxo,
      };
    });
  } catch (err) {
    Sentry.setTag("util", "getUtxos");
    Sentry.captureException(err);
    console.error(err);
  }
};
