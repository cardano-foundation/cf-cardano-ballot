import {
  Credential,
  Address,
  PublicKey,
  RewardAddress,
} from "@emurgo/cardano-serialization-lib-asmjs";
import { Buffer } from "buffer";
import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
} from "react";
import { CardanoApiWallet } from "@/models";
import {
  checkIsMaintenanceOn,
  getItemFromLocalStorage,
  WALLET_LS_KEY,
  setItemToLocalStorage,
  removeItemFromLocalStorage,
  getPubDRepID,
  NETWORK_INFO_KEY,
} from "@/utils";
import { useModal } from "@context";

interface Props {
  children: React.ReactNode;
}

interface EnableResponse {
  status: string;
  stakeKey?: boolean;
  error?: string;
}

interface CardanoContextType {
  address?: string;
  disconnectWallet: () => Promise<void>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  enable: (walletName: string) => Promise<EnableResponse>;
  isEnableLoading: string | null;
  error?: string;
  isEnabled: boolean;
  pubDRepKey: string;
  dRepID: string;
  isMainnet: boolean;
  stakeKey?: string;
  setStakeKey: (key: string) => void;
  stakeKeys: string[];
  walletApi?: CardanoApiWallet;
  registeredStakeKeysListState: string[];
}

const CardanoContext = createContext<CardanoContextType>(
  {} as CardanoContextType,
);
CardanoContext.displayName = "CardanoContext";

const NETWORK = +import.meta.env.VITE_NETWORK_FLAG; // 1 for mainnet, 0 for testnet

const CardanoProvider = (props: Props) => {
  const [isEnabled, setIsEnabled] = useState(false);
  const [isEnableLoading, setIsEnableLoading] = useState<string | null>(null);
  const [walletApi, setWalletApi] = useState<CardanoApiWallet | undefined>(
    undefined,
  );

  const [address, setAddress] = useState<string | undefined>(undefined);
  const [pubDRepKey, setPubDRepKey] = useState<string>("");
  const [dRepID, setDRepID] = useState<string>("");
  const [stakeKey, setStakeKey] = useState<string | undefined>(undefined);
  const [stakeKeys, setStakeKeys] = useState<string[]>([]);
  const [isMainnet, setIsMainnet] = useState<boolean>(false);
  const [registeredStakeKeysListState, setRegisteredPubStakeKeysState] =
    useState<string[]>([]);
  const [error, setError] = useState<string | undefined>(undefined);
  const [walletState, setWalletState] = useState<{
    changeAddress: undefined | string;
    usedAddress: undefined | string;
  }>({
    changeAddress: undefined,
    usedAddress: undefined,
  });

  const getChangeAddress = useCallback(async (enabledApi: CardanoApiWallet) => {
    try {
      const raw = await enabledApi.getChangeAddress();
      const changeAddress = Address.from_bytes(
        Buffer.from(raw, "hex"),
      ).to_bech32();
      setWalletState((prev) => ({ ...prev, changeAddress }));

      return changeAddress;
    } catch (err) {
      console.error(err);
    }
  }, []);

  const getUsedAddresses = async (enabledApi: CardanoApiWallet) => {
    try {
      const raw = await enabledApi.getUsedAddresses();
      const rawFirst = raw[0];
      if (!rawFirst) return [];
      const usedAddress = Address.from_bytes(
        Buffer.from(rawFirst, "hex"),
      ).to_bech32();
      setWalletState((prev) => ({ ...prev, usedAddress }));
    } catch (err) {
      console.error(err);
    }
  };

  /**
   * Checks if there are any registered stake keys.
   * @returns {boolean} True if there are registered stake keys, false otherwise.
   */
  const isStakeKeyRegistered = () => !!registeredStakeKeysListState.length;

  const enable = useCallback(
    async (walletName: string) => {
      setIsEnableLoading(walletName);
      await checkIsMaintenanceOn();

      // todo: use .getSupportedExtensions() to check if wallet supports CIP-95
      if (!isEnabled && walletName) {
        try {
          // Check that this wallet supports CIP-95 connection
          if (!window.cardano[walletName].supportedExtensions) {
            throw new Error("Your wallet does not support CIP-30 extensions.");
          } else if (
            !window.cardano[walletName].supportedExtensions.some(
              (item) => item.cip === 95,
            )
          ) {
            throw new Error("Your wallet does not support the required CIP-30 extension, CIP-95.");
          }
          // Enable wallet connection
          const enabledApi: CardanoApiWallet = await window.cardano[walletName]
            .enable({
              extensions: [{ cip: 95 }],
            })
            .then((enabledWalletApi) => enabledWalletApi)
            .catch((e) => {
              throw e.info;
            });
          await getChangeAddress(enabledApi);
          await getUsedAddresses(enabledApi);

          setIsEnabled(true);
          setWalletApi(enabledApi);
          // Check if wallet has enabled the CIP-95 extension
          const enabledExtensions = await enabledApi.getExtensions();
          if (!enabledExtensions.some((item) => item.cip === 95)) {
            throw new Error("Your wallet did not enable the needed CIP-95 functions during connection.");
          }
          const network = await enabledApi.getNetworkId();

          if (network !== NETWORK) {
            throw new Error(
              `You are trying to connect with a wallet connected to ${network === 1 ? "mainnet" : "testnet"}. Please adjust your wallet settings to connect to ${network !== 1 ? "mainnet" : "testnet"} or select a different wallet.`
            );
          }
          setIsMainnet(network === 1);
          // Check and set wallet address
          const usedAddresses = await enabledApi.getUsedAddresses();
          const unusedAddresses = await enabledApi.getUnusedAddresses();
          if (!usedAddresses.length && !unusedAddresses.length) {
            throw new Error("No addresses found");
          }
          if (!usedAddresses.length) {
            setAddress(unusedAddresses[0]);
          } else {
            setAddress(usedAddresses[0]);
          }

          const registeredStakeKeysList =
            await enabledApi.cip95.getRegisteredPubStakeKeys();
          setRegisteredPubStakeKeysState(registeredStakeKeysList);

          const unregisteredStakeKeysList =
            await enabledApi.cip95.getUnregisteredPubStakeKeys();

          let stakeKeysList;
          if (registeredStakeKeysList.length > 0) {
            stakeKeysList = registeredStakeKeysList.map((key) => {
              const stakeKeyHash = PublicKey.from_hex(key).hash();
              const stakeCredential = Credential.from_keyhash(stakeKeyHash);
              if (network === 1) {
                return RewardAddress.new(1, stakeCredential)
                  .to_address()
                  .to_hex();
              }
              return RewardAddress.new(0, stakeCredential)
                .to_address()
                .to_hex();
            });
          } else {
            stakeKeysList = unregisteredStakeKeysList.map((key) => {
              const stakeKeyHash = PublicKey.from_hex(key).hash();
              const stakeCredential = Credential.from_keyhash(stakeKeyHash);
              if (network === 1) {
                return RewardAddress.new(1, stakeCredential)
                  .to_address()
                  .to_hex();
              }
              return RewardAddress.new(0, stakeCredential)
                .to_address()
                .to_hex();
            });
          }

          setStakeKeys(stakeKeysList);

          let stakeKeySet = false;
          const savedStakeKey = getItemFromLocalStorage(
            `${WALLET_LS_KEY}_stake_key`,
          );
          if (savedStakeKey && stakeKeysList.includes(savedStakeKey)) {
            setStakeKey(savedStakeKey);
            stakeKeySet = true;
          } else if (stakeKeysList.length === 1) {
            setStakeKey(stakeKeysList[0]);

            setItemToLocalStorage(
              `${WALLET_LS_KEY}_stake_key`,
              stakeKeysList[0],
            );
            stakeKeySet = true;
          }
          const dRepIDs = await getPubDRepID(enabledApi);
          setPubDRepKey(dRepIDs?.dRepKey || "");
          setDRepID(dRepIDs?.dRepID || "");
          setItemToLocalStorage(`${WALLET_LS_KEY}_name`, walletName);

          return { status: "Ok", stakeKey: stakeKeySet };
        } catch (e) {
          console.error({ e });
          console.error(e);
          setError(`${e}`);
          setAddress(undefined);
          setWalletApi(undefined);
          setPubDRepKey("");
          setStakeKey(undefined);
          setIsEnabled(false);
          // eslint-disable-next-line no-throw-literal
          throw {
            status: "ERROR",
            error: e,
          };
        } finally {
          setIsEnableLoading(null);
        }
      }
      // eslint-disable-next-line no-throw-literal
      throw { status: "ERROR" };
    },
    [getChangeAddress, isEnabled],
  );

  const disconnectWallet = useCallback(async () => {
    removeItemFromLocalStorage(`${WALLET_LS_KEY}_name`);
    removeItemFromLocalStorage(`${WALLET_LS_KEY}_stake_key`);
    setWalletApi(undefined);
    setAddress(undefined);
    setStakeKey(undefined);
    setIsEnabled(false);
  }, []);

  const value = useMemo(
    () => ({
      isEnabled,
      isEnableLoading,
      enable,
      disconnectWallet,
      walletApi,
      address,
      pubDRepKey,
      dRepID,
      stakeKey,
      stakeKeys,
      setStakeKey,
      isMainnet,
      registeredStakeKeysListState,
      isStakeKeyRegistered,
      error,
      setError,
      walletState,
    }),
    [
      isEnabled,
      isEnableLoading,
      enable,
      disconnectWallet,
      walletApi,
      address,
      pubDRepKey,
      dRepID,
      stakeKey,
      stakeKeys,
      setStakeKey,
      isMainnet,
      registeredStakeKeysListState,
      isStakeKeyRegistered,
      error,
      setError,
      walletState,
    ],
  );

  return <CardanoContext.Provider value={value} {...props} />;
};

function useCardano() {
  const context = useContext(CardanoContext);
  const { closeModal } = useModal();

  if (context === undefined) {
    throw new Error("useCardano must be used within a CardanoProvider");
  }

  const enable = useCallback(
    async (walletName: string) => {
      try {
        const result = await context.enable(walletName);
        if (!result.error) {
          closeModal();
          setItemToLocalStorage(`${NETWORK_INFO_KEY}_${walletName}`, true);
          return result;
        }
      } catch (e) {
        await context.disconnectWallet();
        throw e;
      }
    },
    [context],
  );

  const disconnectWallet = useCallback(async () => {
    await context.disconnectWallet();
  }, [context]);

  return { ...context, enable, disconnectWallet };
}

export { CardanoProvider, useCardano };
