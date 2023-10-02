package org.cardano.foundation.voting.domain;

import java.util.Arrays;
import java.util.Optional;

public enum SchemaVersion {

    V1("1.0.0");

    private final String version;

    SchemaVersion(String version) {
        this.version = version;
    }

    public String getSemVer() {
        return version;
    }

    public static Optional<SchemaVersion> fromText(String text) {
        return Arrays.asList(values()).stream()
                    .filter(schemaVersion -> schemaVersion.getSemVer().equals(text))
                    .findFirst();
    }

}
