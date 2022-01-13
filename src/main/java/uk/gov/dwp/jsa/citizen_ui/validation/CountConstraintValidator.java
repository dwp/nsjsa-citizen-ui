package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.CountConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class CountConstraintValidator implements ConstraintValidator<CountConstraint, AbstractCounterForm> {

    private int min;

    @Override
    public void initialize(final CountConstraint constraint) {
        min = constraint.min();
        Assert.isTrue(min > 0, "Min must be greater than 0");

    }

    public boolean isValid(final AbstractCounterForm form, final ConstraintValidatorContext context) {
        Integer count = form.getCount();
        return Objects.nonNull(count) && count >= min && count <= form.getMaxCount();
    }

    public int getMin() {
        return min;
    }
}
