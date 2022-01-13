package uk.gov.dwp.jsa.citizen_ui.services;

import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {App.class})
public class InterviewAvailabilityTests {

    final InterviewAvailability interviewAvailability = new InterviewAvailability();

    @Test
    public void ensureICanCreateAListOfFiveDays() {
        List<Day> daysNotToAttend = interviewAvailability.createWorkingDays();
        assertThat(daysNotToAttend.size(), is(5));
    }

    @Test
    public void ensureICanCreateAListOfWorkingDaysWithTheCorrectFirstNextWorkingDay() {
        Day expectedNextDay = createNextWorkingDay();
        Day nextDay = interviewAvailability.createWorkingDays().get(0);
        assertThat(nextDay, is(expectedNextDay));
    }

    @Test
    public void ensureICanCreateAListOfWorkingDaysThatDoesntContainAWeekendDay() {
        List<Day> daysNotToAttend = interviewAvailability.createWorkingDays();
        assertThatWeDontHaveWeekendDays(daysNotToAttend);
    }

    private void assertThatWeDontHaveWeekendDays(final List<Day> daysNotToAttend) {
        for(Day d: daysNotToAttend) {
            assertFalse(isWeekendDay(d.getDate()));
        }
    }

    private Day createNextWorkingDay() {
        Day day = createEmptyDay();
        for(int i=1;i<4;i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            if(!isWeekendDay(date)) {
                day.setDate(date);
                return day;
            }
        }
        return day;
    }

    private boolean isWeekendDay(final LocalDate date) {
        return (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    private Day createEmptyDay() {
        return new Day(null,
                new Reason(false),
                new Reason(false));
    }
}
