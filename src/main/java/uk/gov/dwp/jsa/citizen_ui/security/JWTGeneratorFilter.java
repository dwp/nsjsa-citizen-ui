package uk.gov.dwp.jsa.citizen_ui.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import uk.gov.dwp.jsa.citizen_ui.controller.error.SessionTimeoutController;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Locale.ENGLISH;
import static uk.gov.dwp.jsa.citizen_ui.Constants.JWT_TOKEN;
import static uk.gov.dwp.jsa.security.JWTFilter.AUTHORIZATION_HEADER;
import static uk.gov.dwp.jsa.security.JWTFilter.AUTH_PREFIX;
import static uk.gov.dwp.jsa.security.JWTFilter.TOKEN_PAYLOAD_HEADER;

public class JWTGeneratorFilter extends GenericFilterBean {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final List<String> STARTING_PAGE_PATH =
            Arrays.asList("/form/eligibility/residence", "/form/default-claim-start", "/");

    private static final Map<String, Object> CITIZEN_JWT =
            new ImmutableMap.Builder<String, Object>()
                    .put("sid", UUID.randomUUID().toString())
                    .put("iat", String.valueOf(Instant.now().toEpochMilli()))
                    .put("sub", UUID.randomUUID().toString())
                    .put("iss", "citizen-ui")
                    .put("aud", "")
                    .put("username", "")
                    .build();

    private final String tokenPayload;

    private boolean isSecure;

    private final PrivateKeyProvider privateKeyProvider;

    public JWTGeneratorFilter(final PrivateKeyProvider pPrivateKeyProvider,
                              final boolean pIsSecure) throws JsonProcessingException {
        this.privateKeyProvider = pPrivateKeyProvider;
        this.isSecure = pIsSecure;
        tokenPayload = Base64.getEncoder().encodeToString(MAPPER.writeValueAsString(CITIZEN_JWT).getBytes(UTF8));
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain)
            throws IOException, ServletException {

        ServletRequest servReq = servletRequest;
        if (servletRequest instanceof HttpServletRequest
                && servletResponse instanceof HttpServletResponse) {
            final HttpServletRequest req = (HttpServletRequest) servletRequest;
            final HttpServletResponse res = (HttpServletResponse) servletResponse;
            MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(req);
            String jwtToken = null;

            servReq = mutableRequest;

            if (STARTING_PAGE_PATH.contains(req.getServletPath().toLowerCase(ENGLISH)) && isSecure) {
                JwtBuilder builder = Jwts.builder()
                        .setHeaderParam(JwsHeader.KEY_ID, "citizen")
                        .setHeaderParam(JwsHeader.TYPE, "JWT")
                        .setClaims(CITIZEN_JWT)
                        .signWith(this.privateKeyProvider.getPrivateKey());

                jwtToken = builder.compact();
                final Cookie cookie = new Cookie(JWT_TOKEN, jwtToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                res.addCookie(cookie);
            } else {
                Optional<Cookie> cookieToken = requestHasToken(req);
                if (cookieToken.isPresent()) {
                    jwtToken = cookieToken.get().getValue();
                } else if (isSecure) {
                    sendToSessionExpiredPage(res);
                    return;
                }
            }
            mutableRequest.putHeader(TOKEN_PAYLOAD_HEADER, tokenPayload);
            if (StringUtils.isNotEmpty(jwtToken)) {
                mutableRequest.putHeader(AUTHORIZATION_HEADER, AUTH_PREFIX + " " + jwtToken);
            }
        }
        filterChain.doFilter(servReq, servletResponse);
    }

    private Optional<Cookie> requestHasToken(final HttpServletRequest req) {
        if (req.getCookies() != null) {
            return Arrays.stream(req.getCookies())
                    .filter(cookie -> JWT_TOKEN.equals(cookie.getName()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private void sendToSessionExpiredPage(
            final HttpServletResponse response
    ) throws IOException {
        response.sendRedirect(SessionTimeoutController.IDENTIFIER);
    }


}
