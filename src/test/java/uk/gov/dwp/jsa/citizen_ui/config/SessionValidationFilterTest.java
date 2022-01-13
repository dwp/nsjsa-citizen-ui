package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.error.SessionTimeoutController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionValidationFilterTest {

    private static final LocalDateTime VALID_SESSION_TIME = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime EXPIRED_SESSION_TIME = LocalDateTime.now().minusDays(1);
    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Mock
    private HttpServletResponse mockHttpResponse;

    @Mock
    private Claim mockClaim;

    private SessionValidationFilter sut;

    private static UUID GIVEN_CLAIM_ID = UUID.randomUUID();

    private Cookie CLAIM_COOKIE = new Cookie(Constants.COOKIE_CLAIM_ID, GIVEN_CLAIM_ID.toString());

    private int SESSION_TIMEOUT = 1200;

    @Before
    public void setUp() {
        sut = new SessionValidationFilter(claimRepository, SESSION_TIMEOUT);
    }

    @Test
    public void givenClaimIdCookieIsPresent_InterceptorSearchForClaimIdInRepo() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenSearchForClaim();
    }

    @Test
    public void givenClaimIdCookieIsPresentAndExistsInRepo_InterceptorChecksIfItIsExpired() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();
        whenClaimIsFound();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenCheckForClaimantLatestActivity();
    }

    @Test
    public void givenSessionIsExpired_InterceptorRemovesExpectedCookies() throws IOException, ServletException {
        final Cookie jSession = buildJSessionIdCookie();
        final Cookie seen = buildSeenCookie();
        when(mockHttpRequest.getCookies()).thenReturn(new Cookie[]{CLAIM_COOKIE, jSession, seen});
        whenClaimIsFound();
        whenSessionIsExpired();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenCookieIsNotRemoved(seen);
        thenCookieIsRemoved(jSession);
        thenCookieIsRemoved(CLAIM_COOKIE);

    }

    @Test
    public void givenSessionIsExpired_InterceptorRedirectsToSessionExpiredPage() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();
        whenClaimIsFound();
        whenSessionIsExpired();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenRedirectToSessionExpirePage();
    }


    @Test
    public void givenSessionIsExpired_InterceptorDeletesOldClaimData() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();
        whenClaimIsFound();
        whenSessionIsExpired();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenOldClaimDataIsDeleted();
    }


    @Test
    public void givenClaimIdCookieIsNotPresent_InterceptorRemovesExpectedCookies() throws IOException, ServletException {
        final Cookie jSession = buildJSessionIdCookie();
        final Cookie seen = buildSeenCookie();
        when(mockHttpRequest.getCookies()).thenReturn(new Cookie[]{jSession, seen});

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenCookieIsRemoved(jSession);
        thenCookieIsNotRemoved(seen);
    }

    @Test
    public void givenUrlDoesNotStartWithForm_FilterDoesNotExecute() throws IOException, ServletException {
        when(mockHttpRequest.getRequestURI()).thenReturn("/any");
        assertTrue(sut.shouldNotFilter(mockHttpRequest));
    }

    @Test
    public void givenUrlDoesStartWithForm_FilterDoesExecute() throws IOException, ServletException {
        when(mockHttpRequest.getRequestURI()).thenReturn("/form/any");
        assertFalse(sut.shouldNotFilter(mockHttpRequest));
    }


    @Test
    public void setGivenClaimIdIsNotPresent_InterceptorSendsRedirectToSessionExpiredPage() throws IOException, ServletException {
        whenNoCookieIsGiven();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenRedirectToSessionExpirePage();
    }


    @Test
    public void givenClaimIdCookieIsNotPresent_InterceptorDoesNotSearchForClaimIdInRepo() throws IOException, ServletException {
        whenNoCookieIsGiven();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenDoNotSearchForMatchingClaim();
    }


    @Test
    public void givenClaimIdCookieIsNotPresent_InterceptorDoesNotSaveClaimInRepo() throws IOException, ServletException {
        whenNoCookieIsGiven();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        verify(claimRepository, never()).save(any());
    }


    @Test
    public void givenClaimIsNotFound_InterceptorRemovesExpectedCookies() throws IOException, ServletException {
        final Cookie jSession = buildJSessionIdCookie();
        final Cookie seen = buildSeenCookie();
        when(mockHttpRequest.getCookies()).thenReturn(new Cookie[]{CLAIM_COOKIE, jSession, seen});

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenCookieIsNotRemoved(seen);
        thenCookieIsRemoved(jSession);
        thenCookieIsRemoved(CLAIM_COOKIE);

    }

    @Test
    public void givenClaimIsNotFound_InterceptorRedirectsToSessionExpiredPage() throws IOException, ServletException {
        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenRedirectToSessionExpirePage();
    }

    @Test
    public void givenValidSession_InterceptorUpdatesLatestClaimantActivity() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();
        whenClaimIsFound();
        whenSessionIsNotExpired();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenClaimIsUpdatedWithLatestActivity();
    }


    @Test
    public void givenValidSession_InterceptorSavesClaim() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();
        whenClaimIsFound();
        whenSessionIsNotExpired();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenClaimIsSaved();
    }


    @Test
    public void givenValidSession_InterceptorProceedsWithFilterChainExecution() throws IOException, ServletException {
        whenClaimIdCookieIsGiven();
        whenClaimIsFound();
        whenSessionIsNotExpired();

        sut.doFilterInternal(mockHttpRequest, mockHttpResponse, filterChain);

        thenAllowToProceed();

    }


    private void whenNoCookieIsGiven() {
        when(mockHttpRequest.getCookies()).thenReturn(new Cookie[]{});
    }

    private void whenClaimIsFound() {
        when(claimRepository.findById(GIVEN_CLAIM_ID.toString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.getClaimantLatestActivity()).thenReturn(LocalDateTime.now());
    }

    private void whenClaimIsNotFound() {
        when(claimRepository.findById(GIVEN_CLAIM_ID.toString())).thenReturn(Optional.empty());
    }

    private void whenClaimIdCookieIsGiven() {
        when(mockHttpRequest.getCookies()).thenReturn(new Cookie[]{CLAIM_COOKIE});
    }

    private void whenSessionIsExpired() {
        when(mockClaim.getClaimantLatestActivity()).thenReturn(EXPIRED_SESSION_TIME);
    }

    private void thenCookieIsNotRemoved(final Cookie cookie) {
        assertNotEquals(cookie.getMaxAge(), 0);
        assertNotNull(cookie.getValue());
        verify(mockHttpResponse, never()).addCookie(cookie);
    }

    private void thenCheckForClaimantLatestActivity() {
        verify(mockClaim, times(1)).getClaimantLatestActivity();
    }

    private Optional<Claim> thenSearchForClaim() {
        return verify(claimRepository, times(1)).findById(GIVEN_CLAIM_ID.toString());
    }

    private void thenCookieIsRemoved(final Cookie cookie) {
        assertEquals(cookie.getMaxAge(), 0);
        assertNull(cookie.getValue());
        assertEquals(cookie.getPath(), "/");
        verify(mockHttpResponse, times(1)).addCookie(cookie);
    }

    private void thenRedirectToSessionExpirePage() throws IOException {
        verify(mockHttpResponse, times(1)).sendRedirect(SessionTimeoutController.IDENTIFIER);
    }

    private void thenDoNotSearchForMatchingClaim() {
        verify(claimRepository, never()).findById(GIVEN_CLAIM_ID.toString());
    }

    private void thenOldClaimDataIsDeleted() {
        verify(claimRepository, times(1)).deleteById(any());
    }

    private void thenAllowToProceed() throws IOException, ServletException {
        verify(filterChain, times(1)).doFilter(mockHttpRequest, mockHttpResponse);
    }

    private void whenSessionIsNotExpired() {
        when(mockClaim.getClaimantLatestActivity()).thenReturn(VALID_SESSION_TIME);
    }

    private void thenClaimIsUpdatedWithLatestActivity() {
        verify(mockClaim, times(1)).setClaimantLatestActivity(any());
    }

    private void thenClaimIsSaved() {
        verify(claimRepository, times(1)).save(mockClaim);
    }

    private Cookie buildJSessionIdCookie() {
        return new Cookie(Constants.JSESSIONID, UUID.randomUUID().toString());
    }

    private Cookie buildSeenCookie() {
        return new Cookie(Constants.SEEN_COOKIE_MESSAGE, Boolean.TRUE.toString());
    }
}
