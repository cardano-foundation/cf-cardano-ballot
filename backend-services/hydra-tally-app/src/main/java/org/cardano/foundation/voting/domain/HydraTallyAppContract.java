package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Blueprint;

@Blueprint(fileInRessources = "blueprint/plutus.json", packageName = "org.cardano.foundation.voting.domain.blueprint.plutus.model")
public class HydraTallyAppContract {
}
