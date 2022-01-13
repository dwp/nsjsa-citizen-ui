package uk.gov.dwp.jsa.citizen_ui.model;


import org.apache.commons.lang3.StringUtils;

public class PhoneNumber {
    public static final String MOBILE_PREFIX = "07";
    private String value;

    public PhoneNumber(final String value) {
        this.value = value;
    }

    public boolean isValid() {
        return StringUtils.isNoneBlank(value);
    }

    public boolean isMobile() {
        return isValid() && value.startsWith(MOBILE_PREFIX);
    }
}
