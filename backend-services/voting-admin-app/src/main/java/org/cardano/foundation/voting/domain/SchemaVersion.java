package org.cardano.foundation.voting.domain;

public enum SchemaVersion {

    V1("1.0.0");

    private final String version;

    SchemaVersion(String version) {
        this.version = version;
    }

    public String getSemVer() {
        return version;
    }

}
