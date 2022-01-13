package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason;

@Component
public class ReasonTransformer {
    public Reason transform(final uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Reason reason) {
        if (reason == null) {
            final Reason citizenReason = new Reason();
            citizenReason.setSelected(false);
            return citizenReason;
        }
        final Reason citizenReason = new Reason();
        citizenReason.setSelected(reason.getSelected());
        return citizenReason;
    }
}

