package uk.gov.dwp.jsa.citizen_ui.util;

import org.springframework.util.StringUtils;

import java.util.UUID;

public final class Uuid {
    private Uuid() { }

    public static String sanitiseUuid(final String uuid) {
        String sanitisedUuid = null;
        if (StringUtils.isEmpty(uuid)) {
            return uuid;
        }

        try {
            sanitisedUuid = UUID.fromString(uuid).toString();
        } catch (IllegalArgumentException ex) {
            sanitisedUuid = null;
        }

        return sanitisedUuid;
    }
}
