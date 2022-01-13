package uk.gov.dwp.jsa.citizen_ui.model.form.pensions.current;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMonthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMultipleOptionsForm;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months.valueOf;

public class PensionIncreaseMultipleOptionsFormTest {

    PensionIncreaseMultipleOptionsForm sut = new PensionIncreaseMultipleOptionsForm(new PensionIncreaseMonthQuestion(),
            Months.APRIL);

    @Test
    public void getOptionsReturnsNext6Months() {
        List<Months> options = sut.getOptions();

        assertThat(options.get(0), is(valueOf(now().getMonth().name())));
        assertThat(options.get(1), is(valueOf(now().plusMonths(1).getMonth().name())));
        assertThat(options.get(2), is(valueOf(now().plusMonths(2).getMonth().name())));
        assertThat(options.get(3), is(valueOf(now().plusMonths(3).getMonth().name())));
        assertThat(options.get(4), is(valueOf(now().plusMonths(4).getMonth().name())));
        assertThat(options.get(5), is(valueOf(now().plusMonths(5).getMonth().name())));
    }

    @Test
    public void getOptionsReturnsNext6MonthsAfterCurrentWhenClaimDateIsLastDayOfMonth() {
        LocalDate claimStartDate = LocalDate.of(now().getYear(), now().getMonth(), now().getMonth().length(now().isLeapYear()));

        sut = new PensionIncreaseMultipleOptionsForm(new PensionIncreaseMonthQuestion(),
                Months.APRIL);
        sut.setClaimStartDate(claimStartDate);

        List<Months> options = sut.getOptions();

        assertThat(options.get(0), is(valueOf(now().plusMonths(1).getMonth().name())));
        assertThat(options.get(1), is(valueOf(now().plusMonths(2).getMonth().name())));
        assertThat(options.get(2), is(valueOf(now().plusMonths(3).getMonth().name())));
        assertThat(options.get(3), is(valueOf(now().plusMonths(4).getMonth().name())));
        assertThat(options.get(4), is(valueOf(now().plusMonths(5).getMonth().name())));
        assertThat(options.get(5), is(valueOf(now().plusMonths(6).getMonth().name())));
    }

}
