package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Day;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Reason;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DayTransformerTest {

    private static final Reason REASON = new Reason();
    private static final uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason CITIZEN_REASON = new uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason();
    private static final LocalDate DATE = LocalDate.now();
    @Mock
    private ReasonTransformer reasonTransformer;

    private DayTransformer dayTransformer;
    private uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day citizenDay;
    private Day commonDay;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void transformsDay() {
        givenATransformer();
        andGivenACommonDay();
        whenICallTransform();
        thenTheDayIsTransformed();
    }

    private void givenATransformer() {
        dayTransformer = new DayTransformer(reasonTransformer);
        when(reasonTransformer.transform(REASON)).thenReturn(CITIZEN_REASON);
    }

    private void andGivenACommonDay() {
        commonDay = new Day();
        commonDay.setDate(DATE);
        commonDay.setMorning(REASON);
        commonDay.setAfternoon(REASON);
    }

    private void whenICallTransform() {
        citizenDay = dayTransformer.transform(commonDay);
    }

    private void thenTheDayIsTransformed() {
        assertThat(citizenDay.getDate(), is(DATE));
        assertThat(citizenDay.getMorning(), is(CITIZEN_REASON));
        assertThat(citizenDay.getAfternoon(), is(CITIZEN_REASON));
    }
}
