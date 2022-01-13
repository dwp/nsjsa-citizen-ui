package uk.gov.dwp.jsa.citizen_ui.services.notificationbanner;

import org.junit.Assert;
import org.junit.Test;

import static uk.gov.dwp.jsa.citizen_ui.services.notificationbanner.BannerFormattingUtil.formatMsgAsSafeHtml;
import static uk.gov.dwp.jsa.citizen_ui.services.notificationbanner.BannerFormattingUtil.sanitizeBorderColour;

public class BannerFormattingUtilTest {
    private final static String EXPECTED_COLOUR_BLUE = "blue";
    private final static String EXPECTED_COLOUR_GREEN = "green";
    private final static String EXPECTED_COLOUR_ORANGE = "orange";
    private final static String EXPECTED_COLOUR_RED = "red";

    @Test
    public void whenSanitizeBorderColour_blue_shouldReturn_blue() {
        Assert.assertEquals(EXPECTED_COLOUR_BLUE, sanitizeBorderColour("blue"));
    }

    @Test
    public void whenSanitizeBorderColour_green_shouldReturn_green() {
        Assert.assertEquals(EXPECTED_COLOUR_GREEN, sanitizeBorderColour("green"));
    }

    @Test
    public void whenSanitizeBorderColour_orange_shouldReturn_orange() {
        Assert.assertEquals(EXPECTED_COLOUR_ORANGE, sanitizeBorderColour("orange"));
    }

    @Test
    public void whenSanitizeBorderColour_red_shouldReturn_red() {
        Assert.assertEquals(EXPECTED_COLOUR_RED, sanitizeBorderColour("red"));
    }

    @Test
    public void whenSanitizeBorderColourUnsupported_purple_shouldReturnSanitizedDefault_blue() {
        Assert.assertEquals(EXPECTED_COLOUR_BLUE, sanitizeBorderColour("purple"));
    }

    @Test
    public void whenSanitizeBorderColourRandomCase_bLuE_shouldReturnSanitizedLowerCase_blue() {
        Assert.assertEquals(EXPECTED_COLOUR_BLUE, sanitizeBorderColour("bLuE"));
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withNoHtml_shouldReturn_sameMessage() {
        Assert.assertEquals("No html message.", formatMsgAsSafeHtml("No html message."));
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withHtml_shouldReturn_sanitizedEscapedHtmlMessage() {
        Assert.assertEquals(
                "Here is some html &lt;script&gt;alert(&#39;haxxx&#39;)&lt;/script&gt;.",
                formatMsgAsSafeHtml("Here is some html <script>alert('haxxx')</script>.")
        );
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withMarkdownSingleLink_shouldReturn_messageWithHtml_singleHyperlink() {
        Assert.assertEquals(
                "Here is single html " +
                        "<a href=\"https://google.com\" class=\"govuk-link\" target=\"_blank\">click here</a>" +
                        " hyperlink.",
                formatMsgAsSafeHtml("Here is single html [click here](https://google.com) hyperlink.")
        );
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withMarkdownThreeLinks_shouldReturn_messageWithHtml_threeHyperlinks() {
        Assert.assertEquals(
                "First " +
                        "<a href=\"https://google.com\" class=\"govuk-link\" target=\"_blank\">click here</a>" +
                        " hyperlink, then " +
                        "<a href=\"https://bing.com\" class=\"govuk-link\" target=\"_blank\">click here</a>" +
                        ", finally " +
                        "<a href=\"https://duckduckgo.com\" class=\"govuk-link\" target=\"_blank\">maybe this too</a>" +
                        " and this is the end.",

                formatMsgAsSafeHtml("First [click here](https://google.com) hyperlink, then [click here](https://bing.com), " +
                        "finally [maybe this too](https://duckduckgo.com) and this is the end.")
        );
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withMarkdownSingleParagraph_shouldReturn_messageWithHtml_singleParagraph() {
        Assert.assertEquals("First line and <p>this will be second line.", formatMsgAsSafeHtml("First line and |p|this will be second line."));
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withMarkdownFourParagraphs_shouldReturn_messageWithHtml_fourParagraphs() {
        Assert.assertEquals("First <p>second <p>third <p>fourth.", formatMsgAsSafeHtml("First |p|second |p|third |p|fourth."));
    }

    @Test
    public void whenFormatMsgAsSafeHtml_withMarkdownOneLinkAndOneParagraph_shouldReturn_messageWithHtml_hyperlinkAndParagraph() {
        Assert.assertEquals("First line <p>second line <a href=\"https://yahoo.com\" class=\"govuk-link\" target=\"_blank\">press me</a> to find out more.",
                formatMsgAsSafeHtml("First line |p|second line [press me](https://yahoo.com) to find out more."));
    }
}
