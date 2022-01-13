package uk.gov.dwp.jsa.citizen_ui;

import java.time.format.DateTimeFormatter;

/**
 * Class that keeps all the constant values used by the app.
 */
public final class Constants {

    public static final String COMMON_FORM_ERROR_SUMMARY_TITLE = "common.form.error.summary.title";
    public static final String SLASH = "/";
    public static final String AGENT_UI_CLAIM_CREATED_URL = "/claim/created";
    public static final String AGENT_UI_CLAIM_UPDATED_URL = "/claim/updated?claimantId=%s";

    private Constants() {
    }

    public static final class Environment {

        private Environment() {
        }

        public static final String AWS = "aws";

        public static final String LOCAL = "local";

        public static final String DEV = "dev";

        public static final String TEST = "local_test";

        public static final String STAGE = "stage";

        public static final String PROD = "prod";
    }

    /**
     * Default alternate identifier for routing service.
     */
    public static final String NO_ALTERNATIVE_IDENTIFIER = null;

    /**
     * The name of the cookie holding the claim_id unit we replace it with JWT.
     */
    public static final String COOKIE_CLAIM_ID = "claim_id";
    public static final String JSESSIONID = "JSESSIONID";
    public static final String JWT_SESSION = "JWT-SESSION";
    public static final String JWT_TOKEN = "token";
    public static final String XSRF_TOKEN = "XSRF-TOKEN";

    /**
     * Cookie name to store locale.
     */
    public static final String LANG_COOKIE_ID = "jsa_lang";

    /**
     * Set cookie to be expired and refreshed when reload the page.
     */
    public static final int LANG_COOKIE_EXPIRY_SECONDS = -1;

    /**
     * Parameter used to switch locale.
     */
    public static final String LANG_PARAM_NAME = "lang";

    /**
     * Url allowed to switch local.
     */
    public static final String ALLOWED_LANG_CHANGE_URL = "/";

    /**
     * The Cookie to determine whether to show Cookie Policy to User.
     * It expires after a month, when we re show the message to user.
     */
    public static final String SEEN_COOKIE_MESSAGE = "seen_cookie_message";

    /**
     * The value of SEEN COOKIE MESSAGE.
     */
    public static final String SEEN_COOKIE_MESSAGE_VALUE = "TRUE";


    /**
     * Minimum year for a date field.
     * Set to 1000 so we can validate that year has at least 4 digits.
     */
    public static final int MIN_YEAR = 1000;

    /**
     * Date format for example dates (12 03 2020).
     */
    public static final String EXAMPLE_DATE_FORMAT = "dd MM yyyy";

    /**
     * Date format for example dates (12 3 2020).
     */
    public static final String EXAMPLE_DATE_FORMAT_EXCLUDING_ZERO_PREFIXES = "d M yyyy";

    /**
     * Date format for example dates (03 2020).
     */
    public static final String EXAMPLE_MONTH_YEAR_DATE_FORMAT = "MM yyyy";

    /**
     * Date format for summary dates.
     */
    public static final String SUMMARY_DATE_FORMAT = "dd/MM/yyyy";

    /**
     * Date format for interview availability.
     */
    public static final String ATTEND_INTERVIEW_DATE_FORMAT = "EEEE d MMMM";

    public static final DateTimeFormatter SDT = DateTimeFormatter.ofPattern(SUMMARY_DATE_FORMAT);

    /**
     * Maximum allowed jobs.
     */
    public static final int MAX_JOBS_ALLOWED = 4;
    /**
     * Maximum allowed pensions.
     */
    public static final int MAX_PENSIONS_ALLOWED = 9;

    /**
     * Basic string field regex.
     */
    public static final String STRING_FIELD_REGEX = "^([\\p{IsLatin}[0-9]. ,'&-]?)+$";

    /**
     * Basic string free field regex.
     */
    public static final String STRING_FREE_FIELD_REGEX = "^(.|\\R)*$";

    public static final String STRING_DECIMAL_NUMBER_REGEX = "^(\\d{0,2}(\\.?\\d{0,1}))$";

    /**
     * Data version Key.
     */
    public static final String DATA_VERSION_KEY = "citizen-ui.app.version";

    /**
     * Property if application is deployed in agent mode.
     */
    public static final String AGENT_MODE = "agent.mode";

    /**
     * New claim identifier.
     */
    public static final String NEW_CLAIM_STATUS = "NEW_CLAIM";

    /**
     * Session timeout key.
     */
    public static final String SESSION_TIMEOUT = "citizen.session.timeout.seconds";

    /**
     * Shared template variable name for title prefix.
     */
    public static final String TITLE_PREFIX = "titlePrefix";

    /**
     * Time to Live value for Redis Cache.
     */
    public static final long REDIS_TTL = 28800;

    public static final String WELSH_LOCALE = "cy";

}
