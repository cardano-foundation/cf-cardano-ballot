enum WalletIdentifierType {
  CARDANO = "CARDANO",
  KERI = "KERI",
}

const resolveWalletIdentifierType = (
  walletIdentifier: string,
): WalletIdentifierType => {
  const regex = /^stake_[a-zA-Z0-9]+$/;

  if (regex.test(walletIdentifier)) {
    return WalletIdentifierType.CARDANO;
  } else {
    return WalletIdentifierType.KERI;
  }
};

export { resolveWalletIdentifierType, WalletIdentifierType };