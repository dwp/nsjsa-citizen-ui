package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class AvailabilitySelectionValidatorTests {

    private AvailabilitySelectionValidator validator = new AvailabilitySelectionValidator();
    private ConstraintValidatorContext mockContext = Mockito.mock(ConstraintValidatorContext.class);

    @Test
    public void GivenWeHaveASelectedAvailability_ReturnTrue() {
        List<Day> days = createDaysWithSelectedIs(true);
        boolean actual = validator.isValid(days, mockContext);
        assertThat(actual, is(true));
    }

    @Test
    public void GivenWeDontSelectAnyAvailability_ReturnFalse() {
        List<Day> days = createDaysWithSelectedIs(false);
        boolean actual = validator.isValid(days, mockContext);
        assertThat(actual, is(false));
    }

    private List<Day> createDaysWithSelectedIs(boolean selected) {
        return Arrays.asList(new Day(null,
                new Reason(selected),
                new Reason(false)));
    }
}
