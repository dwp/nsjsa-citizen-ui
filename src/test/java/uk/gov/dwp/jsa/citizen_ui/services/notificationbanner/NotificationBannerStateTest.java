package uk.gov.dwp.jsa.citizen_ui.services.notificationbanner;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class NotificationBannerStateTest {
    private final CookieLocaleResolver mockCookieLocaleResolver = Mockito.mock(CookieLocaleResolver.class);
    private final HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);

    @Before
    public void initialise() {
        Mockito.when(mockCookieLocaleResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.UK);
    }

    @Test
    public void whenSetValuesOnBannerStateShouldReturnExpectedEnglishValues() {
        NotificationBannerState notificationBannerState = new NotificationBannerState(mockCookieLocaleResolver, mockRequest);

        boolean expectedBannerEnabled = false;
        String expectedBorderColour = "green";
        String expectedHeaderMsg = "headah";
        String notExpectedHeaderMsgWelsh = "header welsh";
        String expectedBodyMsg = "bodyyhh";
        String notExpectedBodyMsgWelsh = "body welsh";

        notificationBannerState.setBannerEnabled(expectedBannerEnabled);
        notificationBannerState.setBannerBorderColour(expectedBorderColour);
        notificationBannerState.setBannerHeaderMsg(expectedHeaderMsg);
        notificationBannerState.setBannerHeaderMsgWelsh(notExpectedHeaderMsgWelsh);
        notificationBannerState.setBannerBodyMsg(expectedBodyMsg);
        notificationBannerState.setBannerBodyMsgWelsh(notExpectedBodyMsgWelsh);

        Assert.assertEquals(expectedBannerEnabled, notificationBannerState.isBannerEnabled());
        Assert.assertEquals(expectedBorderColour, notificationBannerState.getBannerBorderColour());
        Assert.assertEquals(expectedHeaderMsg, notificationBannerState.getBannerHeaderMsg());
        Assert.assertEquals(expectedBodyMsg, notificationBannerState.getBannerBodyMsg());
    }

    @Test
    public void whenSetValuesOnBannerStateShouldReturnExpectedWelshValues() {
        Mockito.when(mockCookieLocaleResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(new Locale("cy"));
        NotificationBannerState notificationBannerState = new NotificationBannerState(mockCookieLocaleResolver, mockRequest);

        boolean expectedBannerEnabled = true;
        String expectedBorderColour = "green";
        String notExpectedHeaderMsg = "headah";
        String expectedHeaderMsgWelsh = "header welsh";
        String notExpectedBodyMsg = "bodyyhh";
        String expectedBodyMsgWelsh = "body welsh";

        notificationBannerState.setBannerEnabled(expectedBannerEnabled);
        notificationBannerState.setBannerBorderColour(expectedBorderColour);
        notificationBannerState.setBannerHeaderMsg(notExpectedHeaderMsg);
        notificationBannerState.setBannerHeaderMsgWelsh(expectedHeaderMsgWelsh);
        notificationBannerState.setBannerBodyMsg(notExpectedBodyMsg);
        notificationBannerState.setBannerBodyMsgWelsh(expectedBodyMsgWelsh);

        Assert.assertEquals(expectedBannerEnabled, notificationBannerState.isBannerEnabled());
        Assert.assertEquals(expectedBorderColour, notificationBannerState.getBannerBorderColour());
        Assert.assertEquals(expectedHeaderMsgWelsh, notificationBannerState.getBannerHeaderMsg());
        Assert.assertEquals(expectedBodyMsgWelsh, notificationBannerState.getBannerBodyMsg());
    }

    @Test
    public void whenBannerStateInitialisesWithoutAnythingSet_should_initialiseDisabled() {
        NotificationBannerState notificationBannerState = new NotificationBannerState(mockCookieLocaleResolver, mockRequest);

        notificationBannerState.init();

        Assert.assertFalse(notificationBannerState.isBannerEnabled());
        Assert.assertEquals("blue", notificationBannerState.getBannerBorderColour());
        Assert.assertEquals("", notificationBannerState.getBannerHeaderMsg());
        Assert.assertEquals("", notificationBannerState.getBannerBodyMsg());
    }

    @Test
    public void testIsResearchVolunteerBannerEnabled() {
        //Arrange
        final boolean expected = true;
        final NotificationBannerState testSubject = new NotificationBannerState(mockCookieLocaleResolver, mockRequest);
        testSubject.setResearchVolunteerBannerEnabled(expected);

        //Act and Assert
        assertThat(testSubject.isResearchVolunteerBannerEnabled()).isTrue();
    }
}
