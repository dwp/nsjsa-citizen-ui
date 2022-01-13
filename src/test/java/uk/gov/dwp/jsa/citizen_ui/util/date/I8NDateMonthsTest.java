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
public class I8NDateMonthsTest {


    private int month;
    private String expectedMonthText;

    public I8NDateMonthsTest(final int month, final String expectedMonthText) {
        this.month = month;
        this.expectedMonthText = expectedMonthText;
    }

    @Parameterized.Parameters(name = "{index}: testMonthNumber({0}) = {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1, "January"},
                {2, "February"},
                {3, "March"},
                {4, "April"},
                {5, "May"},
                {6, "June"},
                {7, "July"},
                {8, "August"},
                {9, "September"},
                {10, "October"},
                {11, "November"},
                {12, "December"}
        });
    }

    @Test
    public void getsCorrectTextForMonth() {
        assertThat(new I8NDate(LocalDate.of(1971, month, 1), Locale.getDefault()).getMonth(), is(expectedMonthText));
    }

}

