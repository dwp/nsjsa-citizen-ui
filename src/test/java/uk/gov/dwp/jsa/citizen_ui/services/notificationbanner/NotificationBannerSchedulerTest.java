package uk.gov.dwp.jsa.citizen_ui.services.notificationbanner;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class NotificationBannerSchedulerTest {
    private static final String AWS_ENV = "testing";
    private static final String PARAM_STORE_PREFIX = "/nsjsa/citizen/testing/";
    private static final String BANNER_DISPLAY_ENABLE_SUBKEY = "banner.display.enabled";
    private static final String BANNER_BORDER_COLOUR_SUBKEY = "banner.border.colour";
    private static final String BANNER_HEADER_MSG_SUBKEY = "banner.header.message";
    private static final String BANNER_HEADER_MSG_WELSH_SUBKEY = "banner.header.message.welsh";
    private static final String BANNER_BODY_MSG_SUBKEY = "banner.body.message";
    private static final String BANNER_BODY_MSG_WELSH_SUBKEY = "banner.body.message.welsh";
    private static final String BANNER_RESEARCH_VOLUNTEER_KEY = "banner.research-volunteer.enabled";

    @Mock
    private NotificationBannerState mockNotificationBannerState;

    @Mock
    private AWSSimpleSystemsManagement mockSsmClient;

    @InjectMocks
    private NotificationBannerScheduler notificationBannerScheduler;

    @Before
    public void initialise() {
        mockBannerState(true, "blue", "head", "body", "welsh head", "welsh body");

        // because final inside does not get injected
        ReflectionTestUtils.setField(notificationBannerScheduler, "notificationBannerState", mockNotificationBannerState);
        ReflectionTestUtils.setField(notificationBannerScheduler, "bannerSsmPrefix", PARAM_STORE_PREFIX);
        ReflectionTestUtils.setField(notificationBannerScheduler, "researchVolunteerEnabledKey", BANNER_RESEARCH_VOLUNTEER_KEY);
    }

    @Test
    public void whenSchedulerJobTriggered_shouldSetBannerStatesWithExpectedValues() {
        boolean expectedBannerEnabled = true;
        String expectedBorderColour = "red";
        String expectedHeaderMsg = "mahh headahh";
        String expectedBodyMsg = "mahh bodddyyy";
        String expectedHeaderMsgWelsh = "welsh header";
        String expectedBodyMsgWelsh = "welsh body";

        mockBannerState(expectedBannerEnabled, expectedBorderColour,
                        expectedHeaderMsg, expectedBodyMsg, expectedHeaderMsgWelsh, expectedBodyMsgWelsh);

        notificationBannerScheduler.checkIfBannerShouldBeDisplayed();

        verify(mockNotificationBannerState, times(1)).setBannerEnabled(expectedBannerEnabled);
        verify(mockNotificationBannerState, times(1)).setBannerBorderColour(expectedBorderColour);
        verify(mockNotificationBannerState, times(1)).setBannerHeaderMsg(expectedHeaderMsg);
        verify(mockNotificationBannerState, times(1)).setBannerHeaderMsgWelsh(expectedHeaderMsgWelsh);
        verify(mockNotificationBannerState, times(1)).setBannerBodyMsg(expectedBodyMsg);
        verify(mockNotificationBannerState, times(1)).setBannerBodyMsgWelsh(expectedBodyMsgWelsh);
    }

    @Test
    public void bannerDetailsShouldBeUpdatedAfterEnabled() {
        boolean defaultBannerEnabled = false;
        String defaultBorderColour = "red";
        String defaultHeaderMsg = "default mahh headahh";
        String defaultBodyMsg = "default mahh bodddyyy";
        String defaultHeaderMsgWelsh = "default welsh header";
        String defaultBodyMsgWelsh = "default welsh body";

        mockBannerState(defaultBannerEnabled, defaultBorderColour,
                defaultHeaderMsg, defaultBodyMsg, defaultHeaderMsgWelsh, defaultBodyMsgWelsh);

        mockGetParamBannerEnabledValue("false", 1L);
        notificationBannerScheduler.checkIfBannerShouldBeDisplayed();

        boolean newBannerEnabled = true;
        String newBorderColour = "blue";
        String newHeaderMsg = "new mahh headahh";
        String newBodyMsg = "new mahh bodddyyy";
        String newHeaderMsgWelsh = "new welsh header";
        String newBodyMsgWelsh = "new welsh body";

        mockBannerState(newBannerEnabled, newBorderColour,
                newHeaderMsg, newBodyMsg, newHeaderMsgWelsh, newBodyMsgWelsh);

        mockGetParamBannerEnabledValue("true", 2L);
        notificationBannerScheduler.checkIfBannerShouldBeDisplayed();

        verify(mockNotificationBannerState, times(1)).setBannerEnabled(newBannerEnabled);
        verify(mockNotificationBannerState, times(1)).setBannerBorderColour(newBorderColour);
        verify(mockNotificationBannerState, times(1)).setBannerHeaderMsg(newHeaderMsg);
        verify(mockNotificationBannerState, times(1)).setBannerHeaderMsgWelsh(newHeaderMsgWelsh);
        verify(mockNotificationBannerState, times(1)).setBannerBodyMsg(newBodyMsg);
        verify(mockNotificationBannerState, times(1)).setBannerBodyMsgWelsh(newBodyMsgWelsh);
    }

    @Test
    public void whenSchedulerJobTriggered_should_notUpdateBanner_whenBannerIs_false() {
        mockBannerState(false, "blue", "head", "body", "welsh head", "welsh body");
        mockGetParamBannerEnabledValue("false", 1L);
        notificationBannerScheduler.checkIfBannerShouldBeDisplayed();

        verify(mockNotificationBannerState, times(1)).setBannerEnabled(false);
        verify(mockNotificationBannerState, times(0)).setBannerBorderColour(anyString());
        verify(mockNotificationBannerState, times(0)).setBannerHeaderMsg(anyString());
        verify(mockNotificationBannerState, times(0)).setBannerHeaderMsgWelsh(anyString());
        verify(mockNotificationBannerState, times(0)).setBannerBodyMsg(anyString());
        verify(mockNotificationBannerState, times(0)).setBannerBodyMsgWelsh(anyString());

        verify(mockSsmClient, times(0)).putParameter(any(PutParameterRequest.class));
    }

    @Test
    public void testRefreshResearchVolunteerBannerTrue() {
        //Arrange
        final boolean expected = true;
        doReturn(new GetParameterResult().withParameter(new Parameter().withValue(Boolean.toString(expected))))
                .when(mockSsmClient).getParameter(eq(new GetParameterRequest().withName(getFullPathForSubKey(BANNER_RESEARCH_VOLUNTEER_KEY))));

        //Act
        notificationBannerScheduler.refreshResearchVolunteerBanner();

        //Assert
        verify(mockNotificationBannerState).setResearchVolunteerBannerEnabled(expected);
    }

    @Test
    public void testRefreshResearchVolunteerBannerFalse() {
        //Arrange
        final boolean expected = false;
        doReturn(new GetParameterResult().withParameter(new Parameter().withValue(Boolean.toString(expected))))
                .when(mockSsmClient).getParameter(eq(new GetParameterRequest().withName(getFullPathForSubKey(BANNER_RESEARCH_VOLUNTEER_KEY))));

        //Act
        notificationBannerScheduler.refreshResearchVolunteerBanner();

        //Assert
        verify(mockNotificationBannerState).setResearchVolunteerBannerEnabled(expected);
    }

    private String getFullPathForSubKey(final String key) {
        return PARAM_STORE_PREFIX + "/" + key;
    }

    private void mockBannerState(final boolean bannerEnabled, final String borderColour,
                                 final String headerMsg, final String bodyMsg, final String headerMsgWelsh,
                                 final String bodyMsgWelsh) {

        mockGetParamBannerEnabledValue(String.valueOf(bannerEnabled), 1L);
        mockGetParamBannerBorderColourValue(borderColour);
        mockGetParamBannerHeaderMsgValue(headerMsg);
        mockGetParamBannerHeaderMsgWelshValue(headerMsgWelsh);
        mockGetParamBannerBodyMsgValue(bodyMsg);
        mockGetParamBannerBodyMsgWelshValue(bodyMsgWelsh);
    }

    private void mockGetParamBannerEnabledValue(final String value, Long version) {
        Mockito.doReturn(new GetParameterResult().withParameter(new Parameter().withValue(value).withVersion(version)))
                .when(mockSsmClient).getParameter(
                argThat(getParameterRequest -> getParameterRequest.getName().equals(getFullPathForSubKey(BANNER_DISPLAY_ENABLE_SUBKEY))));

        Mockito.when(mockNotificationBannerState.isBannerEnabled()).thenReturn(Boolean.parseBoolean(value));
    }

    private void mockGetParamBannerBorderColourValue(final String value) {
        Mockito.doReturn(new GetParameterResult().withParameter(new Parameter().withValue(value)))
                .when(mockSsmClient).getParameter(
                argThat(getParameterRequest -> getParameterRequest.getName().equals(getFullPathForSubKey(BANNER_BORDER_COLOUR_SUBKEY))));

        Mockito.when(mockNotificationBannerState.getBannerBorderColour()).thenReturn(value);
    }

    private void mockGetParamBannerHeaderMsgValue(final String value) {
        Mockito.doReturn(new GetParameterResult().withParameter(new Parameter().withValue(value)))
                .when(mockSsmClient).getParameter(
                argThat(getParameterRequest -> getParameterRequest.getName().equals(getFullPathForSubKey(BANNER_HEADER_MSG_SUBKEY))));

        Mockito.when(mockNotificationBannerState.getBannerHeaderMsg()).thenReturn(value);
    }

    private void mockGetParamBannerHeaderMsgWelshValue(final String value) {
        Mockito.doReturn(new GetParameterResult().withParameter(new Parameter().withValue(value)))
                .when(mockSsmClient).getParameter(
                argThat(getParameterRequest -> getParameterRequest.getName().equals(getFullPathForSubKey(BANNER_HEADER_MSG_WELSH_SUBKEY))));

        Mockito.when(mockNotificationBannerState.getBannerHeaderMsg()).thenReturn(value);
    }

    private void mockGetParamBannerBodyMsgValue(final String value) {
        Mockito.doReturn(new GetParameterResult().withParameter(new Parameter().withValue(value)))
                .when(mockSsmClient).getParameter(
                argThat(getParameterRequest -> getParameterRequest.getName().equals(getFullPathForSubKey(BANNER_BODY_MSG_SUBKEY))));

        Mockito.when(mockNotificationBannerState.getBannerBodyMsg()).thenReturn(value);
    }

    private void mockGetParamBannerBodyMsgWelshValue(final String value) {
        Mockito.doReturn(new GetParameterResult().withParameter(new Parameter().withValue(value)))
                .when(mockSsmClient).getParameter(
                argThat(getParameterRequest -> getParameterRequest.getName().equals(getFullPathForSubKey(BANNER_BODY_MSG_WELSH_SUBKEY))));

        Mockito.when(mockNotificationBannerState.getBannerBodyMsg()).thenReturn(value);
    }
}
