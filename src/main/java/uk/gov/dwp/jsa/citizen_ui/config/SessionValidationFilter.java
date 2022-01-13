package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.error.SessionTimeoutController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


public class SessionValidationFilter extends OncePerRequestFilter {
    private final ClaimRepository claimRepository;
    private final int sessionTimeout;

    private static final String FORM_PATH = "/form/";

    public SessionValidationFilter(final ClaimRepository claimRepository, final int sessionTimeout) {
        this.claimRepository = claimRepository;
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI == null ? true  : !requestURI.contains(FORM_PATH);

    }



    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final Optional<Cookie> cookie = requestHasCookie(httpServletRequest, Constants.COOKIE_CLAIM_ID);
        if (!cookie.isPresent()) {
            sendToSessionExpiredPage(httpServletRequest, httpServletResponse);
            return;
        }

        final String claimId = cookie.get().getValue();
        final Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (!optionalClaim.isPresent()) {
            sendToSessionExpiredPage(httpServletRequest, httpServletResponse);
            return;
        }
        redirectIfSessionIsExpired(httpServletRequest, httpServletResponse, filterChain, claimId, optionalClaim.get());
    }

    private void redirectIfSessionIsExpired(
            final HttpServletRequest req,
            final HttpServletResponse res,
            final FilterChain filterChain,
            final String claimId,
            final Claim claim
    ) throws IOException, ServletException {
        if (isClaimExpired(claim)) {
            sendToSessionExpiredPage(req, res, claimId);

        } else {
            claim.setClaimantLatestActivity(LocalDateTime.now());
            claimRepository.save(claim);
            filterChain.doFilter(req, res);
        }
    }

    private boolean sendToSessionExpiredPage(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws IOException {
        removeCookie(request, response, Constants.JSESSIONID);
        removeCookie(request, response, Constants.COOKIE_CLAIM_ID);
        response.sendRedirect(SessionTimeoutController.IDENTIFIER);
        return true;
    }

    private void sendToSessionExpiredPage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String claimId
    ) throws IOException {
        claimRepository.deleteById(claimId);
        sendToSessionExpiredPage(request, response);
    }

    private boolean isClaimExpired(final Claim claim) {
        final LocalDateTime latestActivity = claim.getClaimantLatestActivity();
        return latestActivity.plusSeconds(sessionTimeout).isBefore(LocalDateTime.now());
    }

    private Optional<Cookie> requestHasCookie(final HttpServletRequest req, final String cookieName) {
        if (req.getCookies() != null) {
            return Arrays.stream(req.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private void removeCookie(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String cookieName
    ) {
        final Optional<Cookie> optionalCookie = requestHasCookie(request, cookieName);
        if (optionalCookie.isPresent()) {
            final Cookie cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setValue(null);
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);
        }

        final HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

    }

}
