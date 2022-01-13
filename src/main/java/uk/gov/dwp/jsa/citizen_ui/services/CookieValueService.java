package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieValueService {
    @Autowired
    private HttpServletRequest request;

    public String get(final String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().compareTo(cookieName) == 0) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
