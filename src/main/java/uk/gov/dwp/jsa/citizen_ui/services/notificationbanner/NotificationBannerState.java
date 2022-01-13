package uk.gov.dwp.jsa.citizen_ui.services.notificationbanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static uk.gov.dwp.jsa.citizen_ui.services.notificationbanner.BannerFormattingUtil.formatMsgAsSafeHtml;
import static uk.gov.dwp.jsa.citizen_ui.services.notificationbanner.BannerFormattingUtil.sanitizeBorderColour;
import static uk.gov.dwp.jsa.citizen_ui.util.WelshTextUtils.isCurrentRequestInWelsh;

@Component
public class NotificationBannerState {
    @Value("${banner.display.enabled}")
    private boolean bannerEnabled;

    @Value("${banner.border.colour}")
    private String bannerBorderColour;

    @Value("${banner.header.message}")
    private String bannerHeaderMsg;

    @Value("${banner.body.message}")
    private String bannerBodyMsg;

    @Value("${banner.header.message.welsh}")
    private String bannerHeaderMsgWelsh;

    @Value("${banner.body.message.welsh}")
    private String bannerBodyMsgWelsh;

    /**
     * Obtained from SSM.
     */
    private boolean researchVolunteerBannerEnabled;

    private final CookieLocaleResolver cookieLocaleResolver;
    private final HttpServletRequest request;

    public NotificationBannerState(final CookieLocaleResolver cookieLocaleResolver, final HttpServletRequest request) {
        this.cookieLocaleResolver = cookieLocaleResolver;
        this.request = request;
    }

    @PostConstruct
    public void init() {
        this.bannerBorderColour = sanitizeBorderColour(bannerBorderColour);
        this.bannerHeaderMsg = formatMsgAsSafeHtml(bannerHeaderMsg);
        this.bannerBodyMsg = formatMsgAsSafeHtml(bannerBodyMsg);
        this.bannerHeaderMsgWelsh = formatMsgAsSafeHtml(bannerHeaderMsgWelsh);
        this.bannerBodyMsgWelsh = formatMsgAsSafeHtml(bannerBodyMsgWelsh);
    }

    public String getBannerHeaderMsg() {
        return isCurrentRequestInWelsh(cookieLocaleResolver, request) ? bannerHeaderMsgWelsh : bannerHeaderMsg;
    }

    public String getBannerBodyMsg() {
        return isCurrentRequestInWelsh(cookieLocaleResolver, request) ? bannerBodyMsgWelsh : bannerBodyMsg;
    }

    void setBannerBorderColour(final String bannerBorderColour) {
        this.bannerBorderColour = sanitizeBorderColour(bannerBorderColour);
    }

    void setBannerHeaderMsg(final String bannerHeaderMsg) {
        this.bannerHeaderMsg = formatMsgAsSafeHtml(bannerHeaderMsg);
    }

    void setBannerBodyMsg(final String bannerBodyMsg) {
        this.bannerBodyMsg = formatMsgAsSafeHtml(bannerBodyMsg);
    }

    public void setBannerHeaderMsgWelsh(final String bannerHeaderMsgWelsh) {
        this.bannerHeaderMsgWelsh = formatMsgAsSafeHtml(bannerHeaderMsgWelsh);
    }

    public void setBannerBodyMsgWelsh(final String bannerBodyMsgWelsh) {
        this.bannerBodyMsgWelsh = formatMsgAsSafeHtml(bannerBodyMsgWelsh);
    }

    public boolean isBannerEnabled() {
        return bannerEnabled;
    }

    public String getBannerBorderColour() {
        return this.bannerBorderColour;
    }

    void setBannerEnabled(final boolean bannerEnabled) {
        this.bannerEnabled = bannerEnabled;
    }

    public boolean isResearchVolunteerBannerEnabled() {
        return researchVolunteerBannerEnabled;
    }

    public void setResearchVolunteerBannerEnabled(final boolean researchVolunteerBannerEnabled) {
        this.researchVolunteerBannerEnabled = researchVolunteerBannerEnabled;
    }
}
