package uk.gov.dwp.jsa.citizen_ui.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.error.SessionTimeoutController;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import static groovy.lang.GString.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.JWT_TOKEN;
import static uk.gov.dwp.jsa.security.JWTFilter.AUTHORIZATION_HEADER;
import static uk.gov.dwp.jsa.security.JWTFilter.AUTH_PREFIX;
import static uk.gov.dwp.jsa.security.JWTFilter.TOKEN_PAYLOAD_HEADER;

@RunWith(MockitoJUnitRunner.class)
public class JWTGeneratorFilterTest {

    private static final String NON_STARTING_PATH = "anyPath";

    private static final String STARTING_PATH = "/";

    private static final boolean ENABLE_SECURITY = true;

    private static final boolean DISABLE_SECURITY = false;

    @Mock
    private PrivateKeyProvider privateKeyProviderMock;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    private JWTGeneratorFilter testSubject;

    @Before
    public void setUp() throws JsonProcessingException {
        testSubject = new JWTGeneratorFilter(privateKeyProviderMock, DISABLE_SECURITY);
        when(requestMock.getServletPath()).thenReturn(NON_STARTING_PATH);
    }

    @Test
    public void test_FilterChainDoFilter_ShouldBeCalledWithOriginalRequestIfRequestNotHttpServletRequest()
            throws IOException, ServletException {
        ServletRequest nonHttpServletRequest = mock(ServletRequest.class);
        testSubject.doFilter(nonHttpServletRequest, responseMock, filterChain);
        ArgumentCaptor<ServletRequest> argument = ArgumentCaptor.forClass(ServletRequest.class);
        verify(filterChain).doFilter(argument.capture(), eq(responseMock));
        assertEquals(nonHttpServletRequest, argument.getValue());
    }

    @Test
    public void test_ShouldAddTokenPayloadIfNotSecureInNonStartingPage()
            throws IOException, ServletException {
        testSubject.doFilter(requestMock, responseMock, filterChain);
        ArgumentCaptor<MutableHttpServletRequest> argument = ArgumentCaptor.forClass(MutableHttpServletRequest.class);
        verify(filterChain).doFilter(argument.capture(), eq(responseMock));
        MutableHttpServletRequest request = argument.getValue();
        assertNotNull(request.getHeader(TOKEN_PAYLOAD_HEADER));
    }

    @Test
    public void test_ShouldAddTokenPayloadIfNotSecureInStartingPage()
            throws IOException, ServletException {

        when(requestMock.getServletPath()).thenReturn(STARTING_PATH);

        testSubject.doFilter(requestMock, responseMock, filterChain);
        ArgumentCaptor<MutableHttpServletRequest> argument = ArgumentCaptor.forClass(MutableHttpServletRequest.class);
        verify(filterChain).doFilter(argument.capture(), eq(responseMock));
        MutableHttpServletRequest request = argument.getValue();
        assertNotNull(request.getHeader(TOKEN_PAYLOAD_HEADER));
    }

    @Test
    public void test_ShouldRedirectToExpiredPage_IfSecureAndNoTokenAndNotStartingPage()
            throws IOException, ServletException {
        testSubject = new JWTGeneratorFilter(privateKeyProviderMock, ENABLE_SECURITY);

        testSubject.doFilter(requestMock, responseMock, filterChain);
        verify(responseMock).sendRedirect(SessionTimeoutController.IDENTIFIER);
    }

    @Test
    public void test_ShouldSetTokenInCookie_IfSecureAndStartingPage()
            throws IOException, ServletException, NoSuchAlgorithmException {
        testSubject = new JWTGeneratorFilter(privateKeyProviderMock, ENABLE_SECURITY);
        PublicKey publicKey = setUpKeyPairAndReturnPublicKey();
        when(requestMock.getServletPath()).thenReturn(STARTING_PATH);

        testSubject.doFilter(requestMock, responseMock, filterChain);

        ArgumentCaptor<MutableHttpServletRequest> argument = ArgumentCaptor.forClass(MutableHttpServletRequest.class);
        verify(filterChain).doFilter(argument.capture(), eq(responseMock));
        MutableHttpServletRequest request = argument.getValue();

        assertNotNull(request.getHeader(AUTHORIZATION_HEADER));
        String authorisationHeader = request.getHeader(AUTHORIZATION_HEADER).replace(AUTH_PREFIX + " ", EMPTY);
        Jwt jwt =  Jwts.parser().setSigningKey(publicKey).parse(authorisationHeader);
        assertEquals("citizen", jwt.getHeader().get(JwsHeader.KEY_ID));
        assertEquals("JWT", jwt.getHeader().get(JwsHeader.TYPE));


        ArgumentCaptor<Cookie> argumentCookie = ArgumentCaptor.forClass(Cookie.class);
        verify(responseMock).addCookie(argumentCookie.capture());
        assertEquals(JWT_TOKEN, argumentCookie.getValue().getName());
        assertEquals(authorisationHeader, argumentCookie.getValue().getValue());
    }

    @Test
    public void test_ShouldGetTokenFromCookie_IfSecureAndNotStartingPage()
            throws IOException, ServletException {
        testSubject = new JWTGeneratorFilter(privateKeyProviderMock, ENABLE_SECURITY);
        when(requestMock.getServletPath()).thenReturn(NON_STARTING_PATH);
        when(requestMock.getCookies()).thenReturn(new Cookie[]{new Cookie(JWT_TOKEN, "mytoken")});

        testSubject.doFilter(requestMock, responseMock, filterChain);

        ArgumentCaptor<MutableHttpServletRequest> argument = ArgumentCaptor.forClass(MutableHttpServletRequest.class);
        verify(filterChain).doFilter(argument.capture(), eq(responseMock));
        MutableHttpServletRequest request = argument.getValue();

        assertEquals(AUTH_PREFIX + " " + "mytoken", request.getHeader(AUTHORIZATION_HEADER));
    }

    private PublicKey setUpKeyPairAndReturnPublicKey() throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        final KeyPair keyPair = generator.generateKeyPair();

        // write private key
        final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        when(privateKeyProviderMock.getPrivateKey()).thenReturn(privateKey);

        return keyPair.getPublic();
    }
}

