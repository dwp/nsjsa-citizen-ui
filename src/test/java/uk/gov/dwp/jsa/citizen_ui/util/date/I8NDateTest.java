package uk.gov.dwp.jsa.citizen_ui.util.date;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class I8NDateTest {

    public static final int DAY_OF_MONTH = 10;
    public static final int MONTH = 9;
    public static final int YEAR = 2018;
    private final static LocalDate DATE =  LocalDate.of (YEAR, MONTH, DAY_OF_MONTH);
    private final static Locale LOCALE = Locale.getDefault();

    @Test
    public void getsDate() {
        I8NDate i8nDte = new I8NDate(DATE, LOCALE);
        assertThat(i8nDte.getDate(), is(DATE));
    }

    @Test
    public void getsDayOfMonth() {
        I8NDate i8nDte = new I8NDate(DATE, LOCALE);
        assertThat(i8nDte.getDayOfMonth(), is(DAY_OF_MONTH));
    }

    @Test
    public void getsYear() {
        I8NDate i8nDte = new I8NDate(DATE, LOCALE);
        assertThat(i8nDte.getYear(), is(YEAR));
    }


}
