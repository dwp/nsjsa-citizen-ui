package uk.gov.dwp.jsa.citizen_ui.validation;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SortCodeValidatorTest {

    private static final String BLANK_SORT_CODE_MSG = "bankaccount.sortcode.blank";
    private static final String INVALID_SORT_CODE_MSG = "bankaccount.sortcode.invalid";

    @Mock
    private ConstraintValidatorContext mockContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;

    private final SortCodeValidator testSubject = new SortCodeValidator();

    @Before
    public void beforeEach() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mockBuilder);
    }

    @Test
    public void testIsValidReturnsFalseAndBlankMessageWhenCodeEmpty() {
        //Arrange
        final SortCode sortCode = new SortCode();

        //Act
        final boolean actual = testSubject.isValid(sortCode, mockContext);

        //Assert
        assertThat(actual).isFalse();
        verify(mockContext).buildConstraintViolationWithTemplate(BLANK_SORT_CODE_MSG);
    }

    @Test
    public void testIsValidReturnsFalseWhenCodeDoesNotMatchPattern() {
        //Arrange
        final SortCode sortCode = new SortCode("BadCode");

        //Act
        final boolean actual = testSubject.isValid(sortCode, mockContext);

        //Assert
        assertThat(actual).isFalse();
        verify(mockContext).buildConstraintViolationWithTemplate(INVALID_SORT_CODE_MSG);
    }

    @Test
    public void testIsValidReturnsTrueWhenCodeMatchesSanitisedFormat() {
        //Arrange
        final SortCode sortCode = new SortCode("112233");

        //Act
        final boolean actual = testSubject.isValid(sortCode, mockContext);

        //Assert
        assertThat(actual).isTrue();
    }

    @Test
    public void testIsValidReturnsTrueWhenCodeMatchesFormattedFormat() {
        //Arrange
        final SortCode sortCode = new SortCode("11-22-33");

        //Act
        final boolean actual = testSubject.isValid(sortCode, mockContext);

        //Assert
        assertThat(actual).isTrue();
    }

    @Test
    public void testIsValidReturnsFalseWhenValidFormattedCodeWithinSubstring() {
        //Arrange
        final SortCode sortCode = new SortCode("Valid 11-22-33 code substring");

        //Act
        final boolean actual = testSubject.isValid(sortCode, mockContext);

        //Assert
        assertThat(actual).isFalse();
        verify(mockContext).buildConstraintViolationWithTemplate(INVALID_SORT_CODE_MSG);
    }

    @Test
    public void testIsValidReturnsFalseWhenValidSanitisedCodeWithinSubstring() {
        //Arrange
        final SortCode sortCode = new SortCode("Valid 112233 code substring");

        //Act
        final boolean actual = testSubject.isValid(sortCode, mockContext);

        //Assert
        assertThat(actual).isFalse();
        verify(mockContext).buildConstraintViolationWithTemplate(INVALID_SORT_CODE_MSG);
    }
}
