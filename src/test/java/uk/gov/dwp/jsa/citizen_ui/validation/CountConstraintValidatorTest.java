package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.CountConstraint;

import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class CountConstraintValidatorTest {

    CountConstraintValidator sut;


    @Parameters(method = "validLimits")
    @Test
    public void shouldInitialize(int min) {

        sut = new CountConstraintValidator();
        sut.initialize(getConstraint(min));

        assertEquals(min, sut.getMin());
    }

    @Parameters(method = "invalidLimits")
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInitialize(int min) {

        sut = new CountConstraintValidator();
        sut.initialize(getConstraint(min));

        assertEquals(min, sut.getMin());
    }


    @Parameters(method = "validCounts")
    @Test
    public void isValid(Integer count) {
        sut = new CountConstraintValidator();
        sut.initialize(getConstraint(1));

        TestFrom form = new TestFrom();
        form.setCount(count);
        form.setMaxCount(5);

        assertTrue(sut.isValid(form, null));
    }

    @Parameters(method = "invalidCounts")
    @Test
    public void isNotValid(Integer count) {
        sut = new CountConstraintValidator();
        sut.initialize(getConstraint(1));

        TestFrom form = new TestFrom();
        form.setMaxCount(5);
        form.setCount(count);

        assertFalse(sut.isValid(form, null));
    }


    private Integer[] invalidCounts() {
        return new Integer[]{
                null, -1, 0, 6, Integer.MAX_VALUE + 1
        };
    }


    private Integer[][] validLimits() {
        return new Integer[][]{
                new Integer[]{1},
                new Integer[]{1},
                new Integer[]{Integer.MAX_VALUE}
        };
    }

    private Integer[] validCounts() {
        return new Integer[]{
                1, 2, 3, 4, 5
        };
    }

    private Integer[][] invalidLimits() {
        return new Integer[][]{
                new Integer[]{0},
                new Integer[]{-1},
        };
    }

    private CountConstraint getConstraint(int min) {
        return new CountConstraint() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public int min() {
                return min;
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
        };
    }

    class TestFrom extends AbstractCounterForm {
        @Override
        public Question getQuestion() {
            return null; // not needed here
        }

        @Override
        public void setQuestion(Question question) {
            // not needed here
        }
    }
}
