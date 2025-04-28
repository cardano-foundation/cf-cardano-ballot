import { Buffer } from "buffer";
import blake from "blakejs";

import { CardanoApiWallet } from "@models";

export const getPubDRepID = async (walletApi: CardanoApiWallet) => {
  try {
    // From wallet get pub DRep key
    const raw = await walletApi.cip95.getPubDRepKey();
    const dRepKey = raw;
    // From wallet's DRep key hash to get DRep ID
    const dRepKeyBytes = Buffer.from(dRepKey, "hex");
    const dRepID = blake.blake2bHex(dRepKeyBytes, undefined, 28);
    // into bech32

    return {
      dRepKey,
      dRepID,
    };
  } catch (err) {
    console.error(err);
    return {
      dRepKey: undefined,
      dRepID: undefined,
    };
  }
};
