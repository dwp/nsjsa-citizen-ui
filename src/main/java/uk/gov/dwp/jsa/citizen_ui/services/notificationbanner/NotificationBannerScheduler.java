package uk.gov.dwp.jsa.citizen_ui.services.notificationbanner;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static java.lang.Boolean.parseBoolean;
import static uk.gov.dwp.jsa.citizen_ui.Constants.Environment.AWS;
import static uk.gov.dwp.jsa.citizen_ui.Constants.Environment.LOCAL;


@Profile({AWS, LOCAL})
@Configuration
@EnableScheduling
public class NotificationBannerScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationBannerScheduler.class);
    private static final String LOG_MSG_PREFIX = "BANNER_ERROR";

    private final NotificationBannerState notificationBannerState;

    private final AWSSimpleSystemsManagement ssmClient;

    @Value("${banner.ssm.prefix}")
    private String bannerSsmPrefix;

    @Value("${banner.research-volunteer.enabled.key}")
    private String researchVolunteerEnabledKey;

    private Long paramVersion;

    public NotificationBannerScheduler(final NotificationBannerState notificationBannerState,
                                       final AWSSimpleSystemsManagement ssmClient) {
        this.notificationBannerState = notificationBannerState;
        this.ssmClient = ssmClient;
    }

    @Scheduled(initialDelay = 1000, fixedDelayString = "${banner.refresh.seconds}000")
    public void checkIfBannerShouldBeDisplayed() {
        Parameter isDisplayEnabled = getParamFor("banner.display.enabled");
        if (!isDisplayEnabled.getVersion().equals(paramVersion)) {
            if (parseBoolean(isDisplayEnabled.getValue())) {
                notificationBannerState.setBannerBorderColour(getParamFor("banner.border.colour").getValue());
                notificationBannerState.setBannerHeaderMsg(getParamFor("banner.header.message").getValue());
                notificationBannerState.setBannerHeaderMsgWelsh(getParamFor("banner.header.message.welsh").getValue());
                notificationBannerState.setBannerBodyMsg(getParamFor("banner.body.message").getValue());
                notificationBannerState.setBannerBodyMsgWelsh(getParamFor("banner.body.message.welsh").getValue());
            }
            notificationBannerState.setBannerEnabled(parseBoolean(isDisplayEnabled.getValue()));
            paramVersion = isDisplayEnabled.getVersion();
        }
    }

    /**
     * Responsible for refreshing the state of the user research volunteer banner.
     */
    @Scheduled(initialDelay = 1000, fixedDelayString = "${banner.refresh.seconds}000")
    public void refreshResearchVolunteerBanner() {
        LOGGER.debug("Refreshing research volunteer banner state");
        final Parameter parameter = getParamFor(researchVolunteerEnabledKey);
        notificationBannerState.setResearchVolunteerBannerEnabled(Boolean.parseBoolean(parameter.getValue()));
    }

    private Parameter getParamFor(final String subKey) {
        String keyFullPath = null;

        try {
            keyFullPath = getFullPathForSubKey(subKey);
            LOGGER.debug("Attempting to get AWS SSM param {}", keyFullPath);
            final Parameter parameter = ssmClient.getParameter(new GetParameterRequest().withName(keyFullPath))
                    .getParameter();
            LOGGER.debug("Successfully got parameter {} from SSM with value {}", parameter.getName(),
                    parameter.getValue());
            return parameter;
        } catch (Exception e) {
            LOGGER.error("{}: Failed to read the SSM key {}", LOG_MSG_PREFIX, keyFullPath, e);
        }

        return new Parameter();
    }

    private String getFullPathForSubKey(final String key) {
        return bannerSsmPrefix + "/" + key;
    }
}
