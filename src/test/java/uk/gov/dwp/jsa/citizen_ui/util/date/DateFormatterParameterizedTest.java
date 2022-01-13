package uk.gov.dwp.jsa.citizen_ui.util.date;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class DateFormatterParameterizedTest {

    @RunWith(Parameterized.class)
    public static class EnglishTestCases {
        private static final String EN_LOCALE = "en";

        private TestCase testCase;
        private DateFormatterUtils dateFormatterUtils;

        @Mock
        private HttpServletRequest mockRequest;

        @Mock
        private CookieLocaleResolver mockResolver;

        @Before
        public void setUp() {
            this.mockResolver = mock(CookieLocaleResolver.class);
            dateFormatterUtils = new DateFormatterUtils();
        }

        public EnglishTestCases(TestCase testCase) {
            this.testCase = testCase;
        }

        @Test
        public void formatDate_englishDate_returnsCorrectlyFormattedDate() {
            when(mockResolver.resolveLocale(any())).thenReturn(new Locale(EN_LOCALE));
            String response = dateFormatterUtils.formatDate(mockRequest, mockResolver, testCase.getInputDate());
            assertEquals(testCase.getExpectedDateFormat(), response);
        }

        @Parameterized.Parameters
        public static List<TestCase> getEnglishTestCases() {
            int year = LocalDate.now().getYear();
            return Arrays.asList(
                    new TestCase(LocalDate.of(year, 1, 1), String.format("1 January %s", year)),
                    new TestCase(LocalDate.of(year, 2, 1), String.format("1 February %s", year)),
                    new TestCase(LocalDate.of(year, 3, 1), String.format("1 March %s", year)),
                    new TestCase(LocalDate.of(year, 4, 1), String.format("1 April %s", year)),
                    new TestCase(LocalDate.of(year, 5, 1), String.format("1 May %s", year)),
                    new TestCase(LocalDate.of(year, 6, 1), String.format("1 June %s", year)),
                    new TestCase(LocalDate.of(year, 7, 1), String.format("1 July %s", year)),
                    new TestCase(LocalDate.of(year, 8, 1), String.format("1 August %s", year)),
                    new TestCase(LocalDate.of(year, 9, 1), String.format("1 September %s", year)),
                    new TestCase(LocalDate.of(year, 10, 1), String.format("1 October %s", year)),
                    new TestCase(LocalDate.of(year, 11, 1), String.format("1 November %s", year)),
                    new TestCase(LocalDate.of(year, 12, 1), String.format("1 December %s", year))
            );
        }
    }

    @RunWith(Parameterized.class)
    public static class WelshTestCases {
        private static final String CY_LOCALE = "cy";

        private TestCase testCase;
        private DateFormatterUtils dateFormatterUtils;

        @Mock
        private HttpServletRequest mockRequest;

        @Mock
        private CookieLocaleResolver mockResolver;

        @Before
        public void setUp() {
            this.mockResolver = mock(CookieLocaleResolver.class);
            dateFormatterUtils = new DateFormatterUtils();
        }

        public WelshTestCases(TestCase testCase) {
            this.testCase = testCase;
        }

        @Test
        public void formatDate_englishDate_returnsCorrectlyFormattedDate() {
            when(mockResolver.resolveLocale(any())).thenReturn(new Locale(CY_LOCALE));
            String response = dateFormatterUtils.formatDate(mockRequest, mockResolver, testCase.getInputDate());
            assertEquals(testCase.getExpectedDateFormat(), response);
        }

        @Parameterized.Parameters
        public static List<TestCase> getEnglishTestCases() {
            int year = LocalDate.now().getYear();
            return Arrays.asList(
                    new TestCase(LocalDate.of(year, 1, 1), String.format("1 Ionawr %s", year)),
                    new TestCase(LocalDate.of(year, 2, 1), String.format("1 Chwefror %s", year)),
                    new TestCase(LocalDate.of(year, 3, 1), String.format("1 Mawrth %s", year)),
                    new TestCase(LocalDate.of(year, 4, 1), String.format("1 Ebrill %s", year)),
                    new TestCase(LocalDate.of(year, 5, 1), String.format("1 Mai %s", year)),
                    new TestCase(LocalDate.of(year, 6, 1), String.format("1 Mehefin %s", year)),
                    new TestCase(LocalDate.of(year, 7, 1), String.format("1 Gorffennaf %s", year)),
                    new TestCase(LocalDate.of(year, 8, 1), String.format("1 Awst %s", year)),
                    new TestCase(LocalDate.of(year, 9, 1), String.format("1 Medi %s", year)),
                    new TestCase(LocalDate.of(year, 10, 1), String.format("1 Hydref %s", year)),
                    new TestCase(LocalDate.of(year, 11, 1), String.format("1 Tachwedd %s", year)),
                    new TestCase(LocalDate.of(year, 12, 1), String.format("1 Rhagfyr %s", year))
            );
        }
    }

    private static class TestCase {
        private LocalDate inputDate;
        private String expectedDateFormat;

        public TestCase(LocalDate inputDate, String expectedDateFormat) {
            this.inputDate = inputDate;
            this.expectedDateFormat = expectedDateFormat;
        }

        public LocalDate getInputDate() {
            return inputDate;
        }

        public String getExpectedDateFormat() {
            return expectedDateFormat;
        }
    }
}
