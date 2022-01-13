package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleQuestion;

import javax.validation.ConstraintValidatorContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class PersonalDetailsValidatorTest {

    private static final TitleEnum VALID_TILE = TitleEnum.MR;
    private static final String VALID_NAME = "Name";
    private static final String INVALID_NAME = "Name'";
    PersonalDetailsValidator validator = new PersonalDetailsValidator();
    ConstraintValidatorContext mockContext = mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    @Test
    public void GivenAllFieldsBlank_returnInvalid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        boolean actual = validator.isValid(new PersonalDetailsQuestion(
                new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion()
        ), mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void GivenAllFieldsFilledCorrectly_returnValid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
        PersonalDetailsQuestion question = new PersonalDetailsQuestion(new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion());
        question.getTitleQuestion().setUserSelectionValue(VALID_TILE);
        question.getFirstNameQuestion().setValue(VALID_NAME);
        question.getLastNameQuestion().setValue(VALID_NAME);
        boolean actual = validator.isValid(question, mockContext);
        assertThat(actual, is(true));
    }

    @Test
    public void GivenInvalidFirstName_returnInvalid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
        PersonalDetailsQuestion question = new PersonalDetailsQuestion(new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion());
        question.getTitleQuestion().setUserSelectionValue(VALID_TILE);
        question.getFirstNameQuestion().setValue(INVALID_NAME);
        question.getLastNameQuestion().setValue(VALID_NAME);
        boolean actual = validator.isValid(question, mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void GivenInvalidLastName_returnInvalid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
        PersonalDetailsQuestion question = new PersonalDetailsQuestion(new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion());
        question.getTitleQuestion().setUserSelectionValue(VALID_TILE);
        question.getFirstNameQuestion().setValue(VALID_NAME);
        question.getLastNameQuestion().setValue(INVALID_NAME);
        boolean actual = validator.isValid(question, mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void GivenNoTitleSelected_returnInvalid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
        PersonalDetailsQuestion question = new PersonalDetailsQuestion(new TitleQuestion(),
                new NameStringTruncatedQuestion(),
                new NameStringTruncatedQuestion());
        question.getFirstNameQuestion().setValue(VALID_NAME);
        question.getLastNameQuestion().setValue(VALID_NAME);
        boolean actual = validator.isValid(question, mockContext);
        assertThat(actual, is(false));
    }

}
