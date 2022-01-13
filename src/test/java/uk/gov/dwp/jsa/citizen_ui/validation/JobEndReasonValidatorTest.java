package uk.gov.dwp.jsa.citizen_ui.validation;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason;

import javax.validation.ConstraintValidatorContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobEndReasonValidatorTest {

    private JobEndReasonValidator jobEndReasonValidator = new JobEndReasonValidator();
    private WhyJobEndQuestion whyJobEndQuestion = new WhyJobEndQuestion();
    @Mock
    private ConstraintValidatorContext mockContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext mockNodeContext;

    @Before
    public void setUp() throws Exception {
        when(mockContext.buildConstraintViolationWithTemplate(any())).thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(any())).thenReturn(mockNodeContext);
    }

    @Test
    public void whyJobEndedIsNull_IsValid_ReturnsFalse() {
        whyJobEndQuestion.setWhyJobEndedReason(null);

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(false));
    }

    @Test
    public void whyJobEndIsOtherAndReasonIsEmpty_IsValid_ReturnsFalse() {
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.OTHER);
        whyJobEndQuestion.setDetailedReason("");

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(false));
    }

    @Test
    public void whyJobEndIsOtherAndReasonIsNull_IsValid_ReturnsFalse() {
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.OTHER);
        whyJobEndQuestion.setDetailedReason(null);

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(false));
    }

    @Test
    public void whyJobEndIsOtherAndReasonHasSpecialChars_IsValid_ReturnsTrue() {
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.OTHER);
        whyJobEndQuestion.setDetailedReason("£$%^^");

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(true));
    }

    @Test
    public void whyJobEndIsOtherAndReasonIs201CharsLong_IsValid_ReturnsFalse() {
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.OTHER);
        whyJobEndQuestion.setDetailedReason(StringUtils.repeat("d", 201));

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(false));
    }

    @Test
    public void whyJobEndIsRedundancyAndReasonIsBlank_IsValid_ReturnsTrue() {
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.REDUNDANCY);
        whyJobEndQuestion.setDetailedReason(null);

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(true));
    }

    @Test
    public void whyJobEndIsLongSickness_IsValid_ReturnsTrue() {
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.LONG_SICKNESS);

        boolean valid = jobEndReasonValidator.isValid(whyJobEndQuestion, mockContext);

        assertThat(valid, is(true));
    }

}
