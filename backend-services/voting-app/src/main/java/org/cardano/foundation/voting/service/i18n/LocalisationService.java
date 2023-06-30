package org.cardano.foundation.voting.service.i18n;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocalisationService {


    // TODO make it generic, e.g. using java resource files

    public String translate(String event, String name, Locale locale) {
        if (name.equals("YES")) {
            return "Yes";
        }

        if (name.equals("NO")) {
            return "No";
        }

        if (name.equals("ABSTAIN")) {
            return "Abstain";
        }

        if (name.equals("CIP-1694_Pre_Ratification")) {
            return "CIP-1694 Pre-Ratification";
        }

        return "";
    }

}
