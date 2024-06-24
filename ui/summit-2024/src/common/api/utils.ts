
enum WalletIdentifierType {
    Cardano = "Cardano",
    Keri = "Keri"
}

const resolveWalletIdentifierType = (walletIdentifier: string): WalletIdentifierType => {

    const regex = /^stake_[a-zA-Z0-9]+$/;

    if (regex.test(walletIdentifier)){
        return WalletIdentifierType.Cardano;
    } else {
        return WalletIdentifierType.Keri
    }
}

export {
    resolveWalletIdentifierType,
    WalletIdentifierType
}