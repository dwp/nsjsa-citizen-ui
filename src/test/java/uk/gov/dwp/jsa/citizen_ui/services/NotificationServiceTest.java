package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.jsa.adaptors.NotificationServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.ClaimantService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NotificationServiceTest {

    private static final UUID CLAIM_ID = UUID.randomUUID();
    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    private static final String VALID_EMAIL = "dav@dav.com";
    private static final String INVALID_EMAIL = "dav@.com";
    private static final String MOBILE_NUMBER = "077";
    private static final String NON_MOBILE_NUMBER = "012";
    private static final Throwable EXCEPTION = new RuntimeException("");

    @Mock
    private ClaimantService claimantService;
    @Mock
    private NotificationServiceAdaptor notificationServiceAdaptor;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claimant claimant;

    private NotificationService service;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void notifysClaimantViaSmsAndEmail() {
        givenAService();
        givenAClaimantWithEmail(VALID_EMAIL);
        givenAClaimantWithNumber(MOBILE_NUMBER);
        whenINotifyClaimant();
        thenAnEmailIsSent();
        thenAnSmsIsSent();

    }

    @Test
    public void notifysClaimantViaSmsAndEmailAndIgnoresExceptions() {
        givenAService();
        givenAClientThatThrowsAnException();
        givenAClaimantWithEmail(VALID_EMAIL);
        givenAClaimantWithNumber(MOBILE_NUMBER);
        whenINotifyClaimant();
        thenAnEmailIsSent();
        thenAnSmsIsSent();

    }

    @Test
    public void doesNotNotifyClaimant() {
        givenAService();
        givenAClaimantWithEmail(INVALID_EMAIL);
        givenAClaimantWithNumber(NON_MOBILE_NUMBER);
        whenINotifyClaimant();
        thenAnEmailIsNotSent();
        thenAnSmsIsNotSent();
    }

    @Test
    public void notifysClaimantViaSmsAndNotEmail() {
        givenAService();
        givenAClaimantWithEmail(INVALID_EMAIL);
        givenAClaimantWithNumber(MOBILE_NUMBER);
        whenINotifyClaimant();
        thenAnEmailIsNotSent();
        thenAnSmsIsSent();

    }

    @Test
    public void notifysClaimantViaEmailAndNotSms() {
        givenAService();
        givenAClaimantWithEmail(VALID_EMAIL);
        givenAClaimantWithNumber(NON_MOBILE_NUMBER);
        whenINotifyClaimant();
        thenAnEmailIsSent();
        thenAnSmsIsNotSent();

    }

    private void givenAService() {
        service = new NotificationService(notificationServiceAdaptor, claimantService);
        when(claimantService.getDataFromClaim(CLAIM_ID)).thenReturn(Optional.of(claimant));
        when(notificationServiceAdaptor.sendSMS(CLAIMANT_ID))
                .thenReturn(CompletableFuture.completedFuture(HttpStatus.OK.name()));
        when(notificationServiceAdaptor.sendEmail(CLAIMANT_ID))
                .thenReturn(CompletableFuture.completedFuture(HttpStatus.OK.name()));
    }

    private void givenAClientThatThrowsAnException() {
        when(notificationServiceAdaptor.sendSMS(CLAIMANT_ID)).thenThrow(EXCEPTION);
        when(notificationServiceAdaptor.sendEmail(CLAIMANT_ID)).thenThrow(EXCEPTION);
    }

    private void givenAClaimantWithNumber(final String number) {
        when(claimant.getContactDetails().getNumber()).thenReturn(number);
    }

    private void givenAClaimantWithEmail(final String email) {
        when(claimant.getContactDetails().getEmail()).thenReturn(email);
    }

    private void whenINotifyClaimant() {
        service.notifyClaimant(CLAIM_ID, CLAIMANT_ID);
    }

    private void thenAnEmailIsSent() {
        verify(notificationServiceAdaptor).sendEmail(CLAIMANT_ID);
    }

    private void thenAnSmsIsNotSent() {
        verify(notificationServiceAdaptor, never()).sendSMS(CLAIMANT_ID);
    }

    private void thenAnEmailIsNotSent() {
        verify(notificationServiceAdaptor, never()).sendEmail(CLAIMANT_ID);
    }

    private void thenAnSmsIsSent() {
        verify(notificationServiceAdaptor).sendSMS(CLAIMANT_ID);
    }

}
