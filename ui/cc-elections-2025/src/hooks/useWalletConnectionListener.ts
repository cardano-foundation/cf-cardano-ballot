import { useEffect } from "react";
import { useCardano } from "@context";
import { WALLET_LS_KEY } from "@utils";

export const useWalletConnectionListener = () => {
  const { disconnectWallet } = useCardano();

  useEffect(() => {
    const handleStorageChange = (event: StorageEvent) => {
      if (event.key === `${WALLET_LS_KEY}_name` && event.newValue === null) {
        disconnectWallet();
      }
    };

    window.addEventListener("storage", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);
};
