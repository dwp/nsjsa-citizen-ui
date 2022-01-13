package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class PhoneSanitiser {

    /**
     * This function sanitises argument phone number by removing
     * spaces, hyphens, brackets and spaces.
     * @param phoneNumber phone number to be sanitised
     * @return sanitised phone number
     */
    public String sanitise(final String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        return phoneNumber
                .replaceAll("\\s", EMPTY)
                .replaceAll("-", EMPTY)
                .replaceAll("–", EMPTY)
                .replaceAll("\\(", EMPTY)
                .replaceAll("\\)", EMPTY);
    }
}
