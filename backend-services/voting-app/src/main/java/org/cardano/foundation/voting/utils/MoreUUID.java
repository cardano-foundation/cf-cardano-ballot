package org.cardano.foundation.voting.utils;

import java.util.regex.Pattern;

public final class MoreUUID {

    public static final String UUID_V4_STRING =
            "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}";

    public static final Pattern UUID_V4 = Pattern.compile(UUID_V4_STRING);

    public static boolean isUUIDv4(String uuid) {
        return UUID_V4.matcher(uuid).matches();
    }

}
