package uk.gov.dwp.jsa.citizen_ui.util.date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(value = Parameterized.class)
public class I8NDateDayOfWeekTest {


    private int dayOfMonth;
    private String expectedDayOfWeekText;

    public I8NDateDayOfWeekTest(final int dayOfMonth, final String expectedDayOfWeekText) {
        this.dayOfMonth = dayOfMonth;
        this.expectedDayOfWeekText = expectedDayOfWeekText;
    }

    @Parameterized.Parameters(name = "{index}: testDayOfMonth({0}) = {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {10, "Monday"},
                {11, "Tuesday"},
                {12, "Wednesday"},
                {13, "Thursday"},
                {14, "Friday"},
                {15, "Saturday"},
                {16, "Sunday"},
         });
    }

    @Test
    public void getsCorrectTextForMonth() {
        assertThat(new I8NDate(LocalDate.of(2018, 9, dayOfMonth), Locale.getDefault()).getDayOfWeek(), is(expectedDayOfWeekText));
    }

}


