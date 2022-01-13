package uk.gov.dwp.jsa.citizen_ui.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, ValidationSequence.BusinessValidationGroup.class})
public interface ValidationSequence {

    interface BusinessValidationGroup {
    }
}
