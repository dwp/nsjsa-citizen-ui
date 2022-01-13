package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.availability.AttendInterviewController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityResolverTest {

    private AvailabilityResolver availabilityResolver;

    @Mock
    private Claim mockClaim;

    @Before
    public void setUp() {
        availabilityResolver = new AvailabilityResolver();
    }

    @Test
    public void availabilityIsResolvedCorrectlyFromClaim() {
        givenAvailabilityisSet();

        Circumstances circumstances = new Circumstances();

        availabilityResolver.resolve(mockClaim, circumstances);

        assertThat(circumstances.getAvailableForInterview().getDaysNotAvailable().get(0).getDate(),
                is(LocalDate.now()));

    }

    private void givenAvailabilityisSet() {
        AttendInterviewQuestion attendInterviewQuestion = new AttendInterviewQuestion();
        List<Day> daysNotToAttend = new ArrayList<>();
        daysNotToAttend.add(new Day(now(),
                new Reason(true), new Reason()));
        attendInterviewQuestion.setDaysNotToAttend(daysNotToAttend);
        when(mockClaim.get(AttendInterviewController.IDENTIFIER)).thenReturn(Optional.of(attendInterviewQuestion));
    }
}
