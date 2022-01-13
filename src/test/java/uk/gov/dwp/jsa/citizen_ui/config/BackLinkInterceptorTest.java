package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

@RunWith(MockitoJUnitRunner.Silent.class)
public class BackLinkInterceptorTest {

    private BackLinkInterceptor sut;
    private HandlerMethod handlerMethod;
    private Cookie[] cookies;
    private final String BACK_REF = "BACK_REF";

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private ModelAndView mockModelAndView;
    @Mock
    private Cookie mockCookie;
    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private ModelMap mockModelMap;

    @Before
    public void setUp () throws NoSuchMethodException {
        sut = new BackLinkInterceptor();
        sut.setRoutingService(mockRoutingService);
        cookies = new Cookie[] { mockCookie };
        // Just need a 'HandlerMethod' any will do
        handlerMethod = new HandlerMethod(sut, BackLinkInterceptorTest.class.getMethod("setUp"));

        when(mockRequest.getCookies()).thenReturn(cookies);
        when(mockCookie.getName()).thenReturn(COOKIE_CLAIM_ID);
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getRequestURI()).thenReturn("/form/");
        when(mockModelAndView.getModelMap()).thenReturn(mockModelMap);
        when(mockModelAndView.getViewName()).thenReturn("view_name");
        when(mockRoutingService.getBackRef(any())).thenReturn(BACK_REF);
    }

    @Test
    public void postHandleTest_LandingPage_WithoutLink() {
        when(mockRequest.getRequestURI()).thenReturn("/");

        sut.postHandle(mockRequest, mockResponse, handlerMethod, mockModelAndView);

        verify(mockModelMap, never()).addAttribute("backUrl", BACK_REF);
    }

    @Test
    public void postHandleTest_AllFormPages_WithLink() {
        sut.postHandle(mockRequest, mockResponse, handlerMethod, mockModelAndView);

        verify(mockModelMap).addAttribute("backUrl", BACK_REF);
    }

    @Test
    public void postHandleTest_AllFormPages_FromRedirect() {
        when(mockModelAndView.getViewName()).thenReturn("redirect:view_name");

        sut.postHandle(mockRequest, mockResponse, handlerMethod, mockModelAndView);

        verify(mockModelMap, never()).addAttribute("backUrl", BACK_REF);
    }

    @Test
    public void postHandleTest_SummaryPage_WithoutLink() {
        when(mockRequest.getRequestURI()).thenReturn("/");

        sut.postHandle(mockRequest, mockResponse, handlerMethod, mockModelAndView);

        verify(mockModelMap, never()).addAttribute("backUrl", BACK_REF);
    }

    @Test
    public void postHandleTest_NinoPage_MatchedOverride() {
        when(mockRequest.getRequestURI()).thenReturn("/form/nino");
        when(mockRoutingService.getBackRef(any())).thenReturn("/form/claim-start");

        sut.postHandle(mockRequest, mockResponse, handlerMethod, mockModelAndView);

        verify(mockModelMap).addAttribute("backUrl", "/form/default-claim-start");
    }

    @Test
    public void postHandleTest_NinoPage_NotMatchedOverride() {
        when(mockRequest.getRequestURI()).thenReturn("/form/nino");
        when(mockRoutingService.getBackRef(any())).thenReturn("/form/backdating/have-you-been-in-full-time-education");

        sut.postHandle(mockRequest, mockResponse, handlerMethod, mockModelAndView);

        verify(mockModelMap).addAttribute("backUrl", "/form/backdating/have-you-been-in-full-time-education");
    }
}
