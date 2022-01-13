package uk.gov.dwp.jsa.citizen_ui.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class DateValidationUtils {
    private static final String DATE_DAY_FIELD_IDENTIFY_POSTFIX = ".day";
    private static final String DATE_MONTH_FIELD_IDENTIFY_POSTFIX = ".month";

    public ParsableDateQuestion getUsersSubmittedDateQuestionFieldsBeforeParsed(final String dateRangeFieldsIdentify) {
        Map<String, String[]> requestBodyMap = getRequestBodyMap();

        ParsableDateQuestion parsableDateQuestion = new ParsableDateQuestion();
        for (Map.Entry<String, String[]> entry : requestBodyMap.entrySet()) {
            if (!entry.getKey().contains(dateRangeFieldsIdentify)) {
                continue;
            }

            for (String fieldValue : entry.getValue()) {
                if (entry.getKey().equals(dateRangeFieldsIdentify + DATE_DAY_FIELD_IDENTIFY_POSTFIX)) {
                    parsableDateQuestion.setDay(fieldValue);
                } else if (entry.getKey().equals(dateRangeFieldsIdentify + DATE_MONTH_FIELD_IDENTIFY_POSTFIX)) {
                    parsableDateQuestion.setMonth(fieldValue);
                } else {
                    parsableDateQuestion.setYear(fieldValue);
                }
            }
        }
        return parsableDateQuestion;
    }

    private Map<String, String[]> getRequestBodyMap() {
        return getCurrentHttpRequest().getParameterMap();
    }

    // suppressing because we think it's a false positive
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        return servletRequest;
    }

    public static class ParsableDateQuestion {
        private String day;
        private String month;
        private String year;

        public ParsableDateQuestion(final String day, final String month, final String year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public ParsableDateQuestion() { }

        public String getDay() {
            return day;
        }

        public String getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }

        public void setDay(final String day) {
            this.day = day;
        }

        public void setMonth(final String month) {
            this.month = month;
        }

        public void setYear(final String year) {
            this.year = year;
        }
    }
}
