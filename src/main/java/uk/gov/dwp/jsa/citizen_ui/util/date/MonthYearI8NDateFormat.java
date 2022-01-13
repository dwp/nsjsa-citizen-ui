package uk.gov.dwp.jsa.citizen_ui.util.date;

import java.time.LocalDate;
import java.util.Locale;

public class MonthYearI8NDateFormat implements I8NDateFormat {
    private static final String DATE_FORMAT = "%s %s";
    private Locale locale;

    public MonthYearI8NDateFormat(final Locale locale) {
        this.locale = locale;
    }

    @Override
    public String format(final LocalDate localDate) {
        final I8NDate i8nDate = new I8NDate(localDate, locale);
        return String.format(DATE_FORMAT, i8nDate.getMonth(), i8nDate.getYear());
    }
}
