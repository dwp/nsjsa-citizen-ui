package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.Country;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EnumConstraint;

import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class EnumValueValidatorTest {

    EnumConstraint enumAnnotation = new EnumConstraint() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }

        @Override
        public String message() {
            return null;
        }

        @Override
        public Class<?>[] groups() {
            return new Class[0];
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return new Class[0];
        }

        @Override
        public Class<? extends java.lang.Enum<?>> enumClass() {
            return Country.class;
        }
    };

    EnumValueValidator sut;

    @Before
    public void setUp() {
        sut = new EnumValueValidator();
        sut.initialize(enumAnnotation);
    }

    @Test
    public void initialize() {
        assertEquals(enumAnnotation, sut.getAnnotation());
    }

    @Test
    @Parameters(method = "invalidValues")
    public void invalid(String country) {
        boolean result = sut.isValid(country, null);
        assertFalse(result);
    }

    @Test
    @Parameters({"ENGLAND", "SCOTLAND", "WALES"})
    public void valid(String country) {
        boolean result = sut.isValid(country, null);
        assertTrue(result);
    }

    private Object[] invalidValues() {
        return new Object[]{null, "", "GREECE"};
    }
}
