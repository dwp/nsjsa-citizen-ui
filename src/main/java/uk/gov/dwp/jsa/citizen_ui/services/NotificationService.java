package uk.gov.dwp.jsa.citizen_ui.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.NotificationServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.citizen_ui.model.Email;
import uk.gov.dwp.jsa.citizen_ui.model.PhoneNumber;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.ClaimantService;

import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationServiceAdaptor notificationServiceAdaptor;
    private final ClaimantService claimantService;

    public NotificationService(final NotificationServiceAdaptor notificationServiceAdaptor,
            final ClaimantService claimantService) {
        this.notificationServiceAdaptor = notificationServiceAdaptor;
        this.claimantService = claimantService;
    }

    public void notifyClaimant(final UUID claimId, final UUID claimantId) {
        Optional<Claimant> dataFromClaimOptional = claimantService.getDataFromClaim(claimId);
        if (dataFromClaimOptional.isPresent()) {
            final Claimant claimant = dataFromClaimOptional.get();
            notifyClaimantViaSMS(claimant, claimantId);
            notifyClaimantViaEmail(claimant, claimantId);
        }
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("REC_CATCH_EXCEPTION")
    private void notifyClaimantViaEmail(final Claimant claimant, final UUID claimantId) {
        try {
            if (new Email(claimant.getContactDetails().getEmail()).isValid()) {
                notificationServiceAdaptor.sendEmail(claimantId).get();
            }
        } catch (final Exception x) {
            // we can allow normal operation to continue in this scenario.
            LOG.error("Problem communicating with Notification Service via email.", x);
        }
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("REC_CATCH_EXCEPTION")
    private void notifyClaimantViaSMS(final Claimant claimant, final UUID claimantId) {
        try {
            if (new PhoneNumber(claimant.getContactDetails().getNumber()).isMobile()) {
                notificationServiceAdaptor.sendSMS(claimantId).get();
            }
        } catch (final Exception x) {
            // we can allow normal operation to continue in this scenario.
            LOG.error("Problem communicating with Notification Service via SMS", x);
        }
    }

}
