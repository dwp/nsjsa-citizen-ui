package uk.gov.dwp.jsa.citizen_ui.services.notificationbanner;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BannerFormattingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(BannerFormattingUtil.class);
    private static final String ERROR_LOG_PREFIX = "BANNER_ERROR: %s";

    private static final Pattern MARKDOWN_HYPERLINK_PATTERN = Pattern.compile("(\\[(.+?)])(\\((.+?)\\))");
    private static final int REGEX_GROUP_LINK_ONLY = 4;
    private static final int REGEX_GROUP_TEXT_ONLY = 2;

    private static final String MARKDOWN_PARAGRAPH_REGEX = "\\|p\\|";

    private static final PolicyFactory SANITIZATION_POLICY = new HtmlPolicyBuilder().toFactory();

    private static final String HTML_HYPERLINK_TEMPLATE = "<a href=\"%s\" class=\"govuk-link\" target=\"_blank\">%s</a>";
    private static final String HTML_PARAGRAPH_TAG = "<p>";

    private static final List<String> SUPPORTED_BORDER_COLOURS = Arrays.asList("blue", "green", "orange", "red");

    private BannerFormattingUtil() {
    }

    static String sanitizeBorderColour(final String requestedColour) {
        String actualColour = requestedColour != null ? requestedColour.toLowerCase(Locale.UK) : null;
        if (actualColour == null || !SUPPORTED_BORDER_COLOURS.contains(actualColour)) {
            actualColour = SUPPORTED_BORDER_COLOURS.get(0); // default to blue
            LOGGER.error(String.format(ERROR_LOG_PREFIX, "colour {} not supported, defaulting to {}."), requestedColour, actualColour);
        }
        return actualColour;
    }

    static String formatMsgAsSafeHtml(final String msg) {
        return replaceMarkdownWithHtml(sanitizeAnyHtml(msg));
    }

    private static String replaceMarkdownWithHtml(final String msg) {
        String formattedMsg = msg;
        Matcher m = MARKDOWN_HYPERLINK_PATTERN.matcher(formattedMsg);

        while (m.find()) {
            String htmlTagged = String.format(HTML_HYPERLINK_TEMPLATE, m.group(REGEX_GROUP_LINK_ONLY), m.group(REGEX_GROUP_TEXT_ONLY));
            formattedMsg = m.replaceFirst(htmlTagged);
            m = MARKDOWN_HYPERLINK_PATTERN.matcher(formattedMsg);
        }

        return formattedMsg.replaceAll(MARKDOWN_PARAGRAPH_REGEX, HTML_PARAGRAPH_TAG);
    }

    private static String sanitizeAnyHtml(final String msg) {
        return SANITIZATION_POLICY.sanitize(StringEscapeUtils.escapeHtml4(msg));
    }
}
