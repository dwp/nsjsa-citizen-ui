package uk.gov.dwp.jsa.citizen_ui.util.date;

import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class I8NDate {
    private LocalDate localDate;
    private Locale locale;

    public I8NDate(final LocalDate localDate, final Locale locale) {
        this.localDate = localDate;
        this.locale = locale;
    }

    public LocalDate getDate() {
        return localDate;
    }

    public int getDayOfMonth() {
        return localDate.getDayOfMonth();
    }

    public int getYear() {
        return localDate.getYear();
    }

    public String getMonth() {
        ResourceBundle rb = ResourceBundle.getBundle("i18n.dates", locale);
        switch (localDate.getMonth()) {
            case JANUARY:
                return rb.getString("date.month.january");
            case FEBRUARY:
                return rb.getString("date.month.february");
            case MARCH:
                return rb.getString("date.month.march");
            case APRIL:
                return rb.getString("date.month.april");
            case MAY:
                return rb.getString("date.month.may");
            case JUNE:
                return rb.getString("date.month.june");
            case JULY:
                return rb.getString("date.month.july");
            case AUGUST:
                return rb.getString("date.month.august");
            case SEPTEMBER:
                return rb.getString("date.month.september");
            case OCTOBER:
                return rb.getString("date.month.october");
            case NOVEMBER:
                return rb.getString("date.month.november");
            case DECEMBER:
                return rb.getString("date.month.december");
            default:
                return rb.getString("date.month.january");
        }
    }

    public String getDayOfWeek() {
        ResourceBundle rb = ResourceBundle.getBundle("i18n.dates", locale);
        switch (localDate.getDayOfWeek()) {
            case MONDAY:
                return rb.getString("date.day.monday");
            case TUESDAY:
                return rb.getString("date.day.tuesday");
            case WEDNESDAY:
                return rb.getString("date.day.wednesday");
            case THURSDAY:
                return rb.getString("date.day.thursday");
            case FRIDAY:
                return rb.getString("date.day.friday");
            case SATURDAY:
                return rb.getString("date.day.saturday");
            case SUNDAY:
                return rb.getString("date.day.sunday");
            default:
                return rb.getString("date.day.monday");
        }
    }
}
