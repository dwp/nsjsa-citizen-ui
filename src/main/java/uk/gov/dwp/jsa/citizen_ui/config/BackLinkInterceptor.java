package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;


public class BackLinkInterceptor extends HandlerInterceptorAdapter {

    private RoutingService routingService;

    private static HashMap<String, HashMap<String, String>> backLinkOverrides =
            new HashMap<String, HashMap<String, String>>();

    static {
        HashMap<String, String> fromTo = new HashMap<>();
        fromTo.put("/form/claim-start", "/form/default-claim-start");

        backLinkOverrides.put("/form/nino", fromTo);
    }

    @Autowired
    public void setRoutingService(final RoutingService routingService) {
        this.routingService = routingService;
    }

    @Override
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response, final Object handler,
                           final ModelAndView modelAndView) {

        if (handler instanceof HandlerMethod) {
                request.setAttribute(COOKIE_CLAIM_ID, null);
            Cookie claimIdCookie = WebUtils.getCookie(request, COOKIE_CLAIM_ID);

            if (hasRequiredDependencies(modelAndView, claimIdCookie)
                    && isRequestToProcess(request)
                    && !isRedirect(modelAndView)
                    // Appease findbugs
                    && routingService != null) {
                String backRef = overrideBackLink(
                        request.getRequestURI(), routingService.getBackRef(claimIdCookie.getValue())
                );
                modelAndView.getModelMap().addAttribute("backUrl", backRef);
            }
        }
    }

    protected boolean hasRequiredDependencies(final ModelAndView modelAndView, final Cookie claimIdCookie) {
        return claimIdCookie != null
                    && routingService != null
                    && modelAndView != null;
    }

    protected boolean isRequestToProcess(final HttpServletRequest request) {
        return request.getMethod() != null
                    && request.getRequestURI().contains("/form/")
                    && !request.getRequestURI().contains("/summary");
    }

    protected boolean isRedirect(final ModelAndView modelAndView) {
        String viewName = modelAndView != null ? modelAndView.getViewName() : null;
        viewName = viewName != null ? viewName : "";

        return viewName.startsWith("redirect");
    }

    protected String overrideBackLink(final String requestUrl, final String backLink) {
        Optional<String> matchedKey = backLinkOverrides.keySet().stream()
                .filter(key -> requestUrl.contains(key)).findFirst();
        if (matchedKey.isPresent()) {
            HashMap<String, String> linkAndOverride = backLinkOverrides.get(matchedKey.get());
            if (linkAndOverride.containsKey(backLink)) {
                return linkAndOverride.get(backLink);
            }
        }
        return backLink;
    }

}
