package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.junit.Test;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Reason;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReasonTransformerTest {
    private static final String DETAIL = "DETAIL";
    private static final Boolean SELECTED = Boolean.TRUE;
    private static final Boolean NOT_SELECTED = Boolean.FALSE;

    private ReasonTransformer reasonTransformer;
    private Reason commonReason;
    private uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason citizenReason;

    @Test
    public void transformsReason() {
        givenATransformer();
        andGivenACommonReason();
        whenICallTransform();
        thenTheReasonIsTransformed();
    }

    @Test
    public void nullReasonReturnsNull() {
        givenATransformer();
        andGivenANullReason();
        whenICallTransform();
        thenTheReasonIsEmpty();
    }

    private void givenATransformer() {
        reasonTransformer = new ReasonTransformer();
    }

    private void andGivenACommonReason() {
        commonReason = new Reason();
        commonReason.setSelected(SELECTED);
    }

    private void andGivenANullReason() {
        commonReason = null;
    }

    private void whenICallTransform() {
        citizenReason = reasonTransformer.transform(commonReason);
    }

    private void thenTheReasonIsTransformed() {
        assertThat(citizenReason.isSelected(), is(SELECTED));
    }

    private void thenTheReasonIsEmpty() {
        assertThat(citizenReason.isSelected(), is(NOT_SELECTED));
    }


}
