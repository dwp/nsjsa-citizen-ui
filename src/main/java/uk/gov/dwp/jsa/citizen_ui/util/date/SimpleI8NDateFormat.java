package uk.gov.dwp.jsa.citizen_ui.util.date;

import java.time.LocalDate;
import java.util.Locale;

public class SimpleI8NDateFormat implements I8NDateFormat {
    private static final String DATE_FORMAT = "%s %s %s";
    private Locale locale;

    public SimpleI8NDateFormat(final Locale locale) {
        this.locale = locale;
    }

    @Override
    public String format(final LocalDate localDate) {
        final I8NDate i8nDate = new I8NDate(localDate, locale);
        return String.format(DATE_FORMAT, i8nDate.getDayOfWeek(), i8nDate.getDayOfMonth(), i8nDate.getMonth());
    }
}
