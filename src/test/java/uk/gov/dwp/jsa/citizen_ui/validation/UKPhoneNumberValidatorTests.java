package uk.gov.dwp.jsa.citizen_ui.validation;

import  junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.services.PhoneSanitiser;

import javax.validation.ConstraintValidatorContext;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class UKPhoneNumberValidatorTests {

    private PhoneSanitiser sanitiser = new PhoneSanitiser();
    private UKPhoneNumberValidator validator = new UKPhoneNumberValidator(sanitiser);
    private ConstraintValidatorContext mockContext = Mockito.mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    @Test
    @Parameters({"07715009000, true",
            "0771 5009000, true",
            "(077) 1500(9000), true",
            "07-715-009–000,true",
            "07 (715) 00-9000,true",
            "0771 5009000,true",
            "0771 500 9000-,true",
            ", false"}) //no phone number provided}
    public void GivenValidPhoneNumber_ReturnTrue(String phoneNumber, Boolean hasProvidedPhoneNumber) {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        PhoneQuestion phone = new PhoneQuestion(phoneNumber,hasProvidedPhoneNumber);
        boolean actual = validator.isValid(phone, mockContext);
        assertThat(actual, is(true));
    }



    @Test
    @Parameters({
            "7715009000, true", // doesn't start with 0
            "0771500900, true", //  mobile must be 11 digits
            "077 150009000, true ", // > mobile must be 11 digits
            "012345678, true",
            "0771^5009000%,true", // special char
            ",true", //phone number mandatory if hasProvided is true
    })
    public void GivenInValidPhoneNumber_ReturnFalse(String phoneNumber, Boolean hasProvidedPhoneNumber) {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        PhoneQuestion phone = new PhoneQuestion(phoneNumber,hasProvidedPhoneNumber);
        boolean actual = validator.isValid(phone, mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void GivenNull_ReturnFalse() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        PhoneQuestion phone = new PhoneQuestion("",null);
        boolean actual = validator.isValid(phone, mockContext);
        assertThat(actual, is(false));
    }
}
