package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class EmailSanitiser {
    /**
     * This function sanitises argument email by removing
     * spaces, hyphens and brackets.
     * @param email to be sanitised
     * @return sanitised email
     */
    public String sanitise(final String email) {
        if (isEmpty(email)) {
            return email;
        }
        return email
                .replaceAll(" *", "");
    }
}
