package uk.gov.dwp.jsa.citizen_ui.util.date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateFormatterUtilsTest {
    private static final String EN_LOCALE                 = "en";
    private static final String CY_LOCALE                 = "cy";

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    private DateFormatterUtils sut;

    @Before
    public void setUp() {
        sut = new DateFormatterUtils();
    }

    @Test
    public void givenDate_isEnglishRequest_returnsCorrectlyFormattedDate() {
        int day = 6;
        int month = 7;
        int year = LocalDate.now().getYear();
        String monthAlpha = "July";
        LocalDate dateArgument = LocalDate.of(year, month, day);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale(EN_LOCALE));

        String actual = sut.formatDate(mockRequest, mockCookieLocaleResolver, dateArgument);

        String expected = String.format("%s %s %s", day, monthAlpha, year);
        assertEquals(expected, actual);
    }

    @Test
    public void givenDate_isWelshRequest_returnsCorrectlyFormattedDate() {
        int day = 6;
        int month = 7;
        int year = LocalDate.now().getYear();
        String monthAlpha = "Gorffennaf";
        LocalDate dateArgument = LocalDate.of(year, month, day);
        when(mockCookieLocaleResolver.resolveLocale(any())).thenReturn(new Locale(CY_LOCALE));

        String actual = sut.formatDate(mockRequest, mockCookieLocaleResolver, dateArgument);

        String expected = String.format("%s %s %s", day, monthAlpha, year);
        assertEquals(expected, actual);
    }
}
