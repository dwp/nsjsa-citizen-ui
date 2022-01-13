package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.AvailabilitySelectionConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validates when a least one of the days has been selected.
 */
@Component
public class AvailabilitySelectionValidator
        implements ConstraintValidator<AvailabilitySelectionConstraint, List<Day>>, Validator {

    @Override
    public boolean isValid(final List<Day> days, final ConstraintValidatorContext context) {
        return days.stream().anyMatch(day -> day.getMorning().isSelected() || day.getAfternoon().isSelected());
    }

    @Override
    public void initialize(final AvailabilitySelectionConstraint constraintAnnotation) {
        // Nothing to initialize
    }
}
