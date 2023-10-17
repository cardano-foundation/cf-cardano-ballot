import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet';

export const resolveCardanoNetwork = (network: string): NetworkType => {
  if (['MAINNET', 'MAIN'].includes(network.toUpperCase())) {
    return NetworkType.MAINNET;
  } else {
    return NetworkType.TESTNET;
  }
};
