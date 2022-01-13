package uk.gov.dwp.jsa.citizen_ui.util.date;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.getWelshMonth;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.isCurrentRequestInWelsh;

@Service
public class DateFormatterUtils {
    private static final String FORMATTED_PATTERN    = "d MMMM yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATTED_PATTERN, Locale.UK);

    public DateFormatterUtils() { }

    public String formatDate(final HttpServletRequest request, final CookieLocaleResolver cookieLocaleResolver,
                             final LocalDate date) {
        return format(request, cookieLocaleResolver, date);
    }

    public LocalDate getTodayDate() {
        LocalDate now = LocalDate.now();
        return LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth());
    }

    private String format(final HttpServletRequest request, final CookieLocaleResolver cookieLocaleResolver,
                          final LocalDate date) {
        String formattedDateEnglish = FORMATTER.format(date);
        if (isCurrentRequestInWelsh(cookieLocaleResolver, request)) {
            return formatDateInWelsh(formattedDateEnglish);
        }
        return formattedDateEnglish;
    }

    private String formatDateInWelsh(final String formattedDateEnglish) {
        String englishMonth = formattedDateEnglish.replaceAll("[0-9]", "").trim();
        return formattedDateEnglish.replaceAll(englishMonth, getWelshMonth(englishMonth));
    }
}
