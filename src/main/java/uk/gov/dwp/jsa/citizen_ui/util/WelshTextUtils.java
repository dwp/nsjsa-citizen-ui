package uk.gov.dwp.jsa.citizen_ui.util;

import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class WelshTextUtils {
    private static final String WELSH_LOCALE_IDENTIFY                      = "cy";
    private static final String IS_WELSH_ALTERNATIVE_TEXT_REQUIRED_YES     = "alternativeWelshTextYES";
    private static final String IS_WELSH_ALTERNATIVE_TEXT_REQUIRED_NO      = "alternativeWelshTextNO";
    private static final Map<String, AlternativeBooleanPageLocalesHolder> ALTERNATIVE_WELSH_TEXTS = new HashMap<>();


    private WelshTextUtils() { }

    public static void useAlternativeWelshTextBooleanPage(final CookieLocaleResolver resolver,
                                                          final HttpServletRequest request,
                                                          final Model model) {
        if (isCurrentRequestInWelsh(resolver, request) && isPageWithAlternativeWelshText(request)) {
            AlternativeBooleanPageLocalesHolder message = getAlternativeWelshText(request);
            model.addAttribute(IS_WELSH_ALTERNATIVE_TEXT_REQUIRED_YES, message.getYesLocale());
            model.addAttribute(IS_WELSH_ALTERNATIVE_TEXT_REQUIRED_NO, message.getNoLocale());
        }
    }

    public static boolean isCurrentRequestInWelsh(final CookieLocaleResolver resolver,
                                                   final HttpServletRequest request) {
        return getLocaleLanguageIdentify(resolver, request).equalsIgnoreCase(WELSH_LOCALE_IDENTIFY);
    }

    private static String getLocaleLanguageIdentify(final CookieLocaleResolver resolver,
                                                    final HttpServletRequest request) {
        return resolver.resolveLocale(request).getLanguage();
    }

    private static boolean isPageWithAlternativeWelshText(final HttpServletRequest request) {
        return ALTERNATIVE_WELSH_TEXTS.containsKey(sanitiseServletPath(request));
    }

    private static AlternativeBooleanPageLocalesHolder getAlternativeWelshText(final HttpServletRequest request) {
        return ALTERNATIVE_WELSH_TEXTS.get(sanitiseServletPath(request));
    }

    private static String sanitiseServletPath(final HttpServletRequest request) {
        return request.getServletPath().replaceAll("[0-9]/", "");
    }

    static {
        ALTERNATIVE_WELSH_TEXTS.put("/form/other-benefits/have-you-applied",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/personal-details/address-is-it-postal",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/outside-work/has-outside-work",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/claim-start/jury-service/have-you-been",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/previous-employment/employer-details/expect-payment",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/education/have-you-been",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/current-work/details/get-paid",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/current-work/details/self-employed-confirmation",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/current-work/details/choose-payment",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/previous-employment/add-work",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/eligibility/working",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/eligibility/residence",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/pensions/current/has-pension",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/backdating/have-you-asked-for-advice",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/backdating/have-you-been-in-full-time-education",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/backdating/have-you-been-in-paid-work-since",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw",
                        "common.question.yesno.choice.false.alternative.na"));
        ALTERNATIVE_WELSH_TEXTS.put("/form/current-work/has-another-job",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.oes", null));
        ALTERNATIVE_WELSH_TEXTS.put("/form/personal-details/contact/email-confirmation",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.oes", null));
        ALTERNATIVE_WELSH_TEXTS.put("/form/previous-employment/employer-details/status",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.Oeddwn", null));
        ALTERNATIVE_WELSH_TEXTS.put("/form/pensions/current/has-another-pension",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.oes", null));
        ALTERNATIVE_WELSH_TEXTS.put("/form/availability/available-for-interview",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.oes", null));
        ALTERNATIVE_WELSH_TEXTS.put("/form/previous-employment/has-previous-work",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw", null));
        ALTERNATIVE_WELSH_TEXTS.put("/form/current-work/are-you-working",
                new AlternativeBooleanPageLocalesHolder(
                        "common.question.yesno.choice.true.alternative.ydw", null));
    }

    private static final Map<String, String> WELSH_MONTHS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        WELSH_MONTHS.put("January", "Ionawr");
        WELSH_MONTHS.put("February", "Chwefror");
        WELSH_MONTHS.put("March", "Mawrth");
        WELSH_MONTHS.put("April", "Ebrill");
        WELSH_MONTHS.put("May", "Mai");
        WELSH_MONTHS.put("June", "Mehefin");
        WELSH_MONTHS.put("July", "Gorffennaf");
        WELSH_MONTHS.put("August", "Awst");
        WELSH_MONTHS.put("September", "Medi");
        WELSH_MONTHS.put("October", "Hydref");
        WELSH_MONTHS.put("November", "Tachwedd");
        WELSH_MONTHS.put("December", "Rhagfyr");
    }

    public static String getWelshMonth(final String englishMonth) {
        return WELSH_MONTHS.get(englishMonth);
    }

    protected  static final class AlternativeBooleanPageLocalesHolder {
        private String yesLocale;
        private String noLocale;

        public AlternativeBooleanPageLocalesHolder(final String yesLocale, final String noLocale) {
            this.yesLocale = yesLocale;
            this.noLocale = noLocale;
        }

        public String getYesLocale() {
            return yesLocale;
        }

        public String getNoLocale() {
            return noLocale;
        }
    }
}
