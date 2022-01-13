package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Component that is used for the interview availability.
 */
@Component
public class InterviewAvailability {

    private static final int MAX_NUMBER_OF_DAYS = 5;

    /**
     * @return Returns a list of 5 working days(which are empty) from tomorrow.
     *
     *
     */
    public List<Day> createWorkingDays() {
        List<Day> days = new ArrayList<>();
        int i = 0;
        while (MAX_NUMBER_OF_DAYS > days.size()) {
            Day day = createEmptyDay(LocalDate.now().plusDays(++i));
            if (!Day.isWeekendDay(day.getDate())) {
                days.add(day);
            }
        }
        return days;
    }

    private Day createEmptyDay(final LocalDate date) {
        return new Day(date,
                new Reason(false),
                new Reason(false));
    }
}
