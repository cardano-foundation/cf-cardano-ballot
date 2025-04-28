export const PENDING_TRANSACTION_KEY = "pending_transaction";
export const PROTOCOL_PARAMS_KEY = "protocol_params";
export const NETWORK_METRICS_KEY = "network_metrics";
export const NETWORK_INFO_KEY = "network_info";
export const NETWORK_TOTAL_STAKE_KEY = "network_total_stake";

export const WALLET_LS_KEY = "wallet_data";

export function getItemFromLocalStorage(key: string) {
  const item = window.localStorage.getItem(key);
  return item ? JSON.parse(item) : null;
}

export function setItemToLocalStorage(key: string, data: JSONValue) {
  window.localStorage.setItem(key, JSON.stringify(data));
}

export function removeItemFromLocalStorage(key: string) {
  window.localStorage.removeItem(key);
}
