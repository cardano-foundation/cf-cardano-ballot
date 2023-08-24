package org.cardano.foundation.voting.service.i18n;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class LocalisationService {

    // TODO make it generic, e.g. using java resource files

    public String translate(String event, String path, Optional<String> name, Locale locale) {
        if (path.equals("e42f820f-5852-4c03-9d42-8cf4a4044a51")) {
            return "Yes";
        }

        if (path.equals("3b40644b-3f6f-4c91-945e-4d612fa4f6cf")) {
            return "No";
        }

        if (path.equals("a8f60f84-58bf-47b3-9582-5272fbdc6ff6")) {
            return "Abstain";
        }

        // TODO should we introduce a class type for this?
        if (path.startsWith("CIP-1694_Pre_Ratification")) {
            return "CIP-1694 Pre-Ratification";
        }

        if (path.startsWith("CF_SUMMIT_2023")) {
            return "CF Summit 2023";
        }

        if (path.equals("be79ce1f-3cf1-4335-bd07-98f6f24f0f12")) {
            return "Sundae Swap";
        }

        return name
                .map(n -> n.replaceAll("_", " "))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .orElse("TODO::LOCALISE");
    }

}
