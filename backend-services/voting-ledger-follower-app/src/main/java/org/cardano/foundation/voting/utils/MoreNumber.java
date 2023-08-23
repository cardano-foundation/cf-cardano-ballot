package org.cardano.foundation.voting.utils;

import java.util.regex.Pattern;

public final class MoreNumber {

    private final static Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }

        return NUMBER_PATTERN.matcher(strNum).matches();
    }

}
