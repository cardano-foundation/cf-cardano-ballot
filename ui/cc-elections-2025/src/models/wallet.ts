// TODO: Replace any with proper types
/* eslint-disable @typescript-eslint/no-explicit-any */
interface Extension {
  cip: number;
}

export interface EnableExtensionPayload {
  extensions: Extension[];
}

export interface Protocol {
  block_id: number;
  coins_per_utxo_size: number;
  collateral_percent: number;
  committee_max_term_length: number;
  committee_min_size: number;
  cost_model_id: number;
  decentralisation: number;
  drep_activity: number;
  drep_deposit: number;
  dvt_committee_no_confidence: number;
  dvt_committee_normal: number;
  dvt_hard_fork_initiation: number;
  dvt_motion_no_confidence: number;
  dvt_p_p_economic_group: number;
  dvt_p_p_gov_group: number;
  dvt_p_p_network_group: number;
  dvt_p_p_technical_group: number;
  dvt_treasury_withdrawal: number;
  dvt_update_to_constitution: number;
  epoch_no: number;
  extra_entropy: any;
  gov_action_deposit: number;
  gov_action_lifetime: number;
  id: number;
  influence: number;
  key_deposit: number;
  max_bh_size: number;
  max_block_ex_mem: number;
  max_block_ex_steps: number;
  max_block_size: number;
  max_collateral_inputs: number;
  max_epoch: number;
  max_tx_ex_mem: number;
  max_tx_ex_steps: number;
  max_tx_size: number;
  max_val_size: number;
  min_fee_a: number;
  min_fee_b: number;
  min_pool_cost: number;
  min_utxo_value: number;
  monetary_expand_rate: number;
  nonce: string;
  optimal_pool_count: number;
  pool_deposit: number;
  price_mem: number;
  price_step: number;
  protocol_major: number;
  protocol_minor: number;
  pvt_committee_no_confidence: number;
  pvt_committee_normal: number;
  pvt_hard_fork_initiation: number;
  pvt_motion_no_confidence: number;
  treasury_growth_rate: number;
}

export interface CardanoApiWallet {
  experimental: any;
  cip95: {
    getPubDRepKey(): Promise<string>;
    getRegisteredPubStakeKeys(): Promise<string[]>;
    getUnregisteredPubStakeKeys(): Promise<string[]>;
    signData(): Promise<any>;
  };
  isEnabled(): Promise<boolean>;
  getBalance(): Promise<string>;
  getUtxos(): Promise<string[]>;
  getCollateral?(): Promise<string[]>;
  getUsedAddresses(): Promise<string[]>;
  getUnusedAddresses(): Promise<string[]>;
  getChangeAddress(): Promise<string>;
  getRewardAddresses(): Promise<string[]>;
  getNetworkId(): Promise<number>;
  signData(arg0: any, arg1?: any): Promise<any>;
  signTx(arg0: any, arg1?: any): Promise<any>;
  submitTx(arg0: any): Promise<any>;
  onAccountChange(arg0: (addresses: string) => void): Promise<void>;
  onNetworkChange(arg0: (network: number) => void): Promise<void>;
  getActivePubStakeKeys(): Promise<string[]>;
  getExtensions(): Promise<Extension[]>;
}

export interface CardanoBrowserWallet {
  apiVersion: string;
  enable(extensions?: EnableExtensionPayload): Promise<CardanoApiWallet>;
  icon: string;
  isEnabled(): Promise<boolean>;
  name: string;
  supportedExtensions: Extension[];
}

declare global {
  interface Window {
    cardano: {
      [key: string]: CardanoBrowserWallet;
    };
  }
}
