package uk.gov.dwp.jsa.citizen_ui.util;

import org.apache.commons.lang3.StringUtils;

public final class PostalAddressFormatter {

    private static final String ADDRESS_LINE_FORMAT_1 = "%s";
    private static final String ADDRESS_LINE_FORMAT_2 = ", %s";
    private static final String TOWN_OR_CITY_FORMAT = ", %s";
    private static final String POSTAL_CODE_FORMAT = ", %s";
    private static final String EMPTY = "";

    private PostalAddressFormatter() {
    }

    public static String format(
            final String addressLine1,
            final String addressLine2,
            final String townOrCity,
            final String postalCode
    ) {
        return applyFormat(ADDRESS_LINE_FORMAT_1, addressLine1)
                + applyFormat(ADDRESS_LINE_FORMAT_2, addressLine2)
                + applyFormat(TOWN_OR_CITY_FORMAT, townOrCity)
                + applyFormat(POSTAL_CODE_FORMAT, postalCode);
    }

    private static String applyFormat(final String format, final String text) {
        if (isValid(text)) {
            return String.format(format, text);
        } else {
            return EMPTY;
        }
    }

    private static boolean isValid(final String text) {
        return !StringUtils.isBlank(text);
    }
}
