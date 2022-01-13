package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceDatesController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.claimstart.JuryServiceDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JuryDatesResolverTest {

    private static final DateQuestion ANSWER_START_DATE = new DateQuestion(1, Month.JANUARY.getValue(), 2018);
    private static final DateQuestion ANSWER_END_DATE = new DateQuestion(1, Month.APRIL.getValue(), 2018);
    private static final LocalDate EXPECTED_START_DATE = LocalDate.of(2018, Month.JANUARY.getValue(), 1);
    private static final LocalDate EXPECTED_END_DATE = LocalDate.of(2018, Month.APRIL.getValue(), 1);
    public static final Question JURY_DATE_QUESTION = new JuryServiceDurationQuestion(ANSWER_START_DATE, ANSWER_END_DATE);

    @Mock
    private Claim mockClaim = new Claim();

    private JuryDatesResolver sut;

    @Before
    public void setUp() {
        sut = new JuryDatesResolver();
        when(mockClaim.get(JuryServiceDatesController.IDENTIFIER)).thenReturn(Optional.of(JURY_DATE_QUESTION));
    }

    @Test
    public void juryDateAreResolvedFromClaimSuccessfully() {
        when(mockClaim.get(JuryServiceConfirmationController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(true)));

        Circumstances result = new Circumstances();
        sut.resolve(mockClaim, result);
        assertEquals(EXPECTED_START_DATE, result.getJuryService().getStartDate());
        assertEquals(EXPECTED_END_DATE, result.getJuryService().getEndDate());
    }

    @Test
    public void juryDateAreNullWhenJuryConfirmationIsFalse() {
        when(mockClaim.get(JuryServiceConfirmationController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(false)));

        Circumstances result = new Circumstances();
        sut.resolve(mockClaim, result);
        assertNull(result.getJuryService());
    }
}
