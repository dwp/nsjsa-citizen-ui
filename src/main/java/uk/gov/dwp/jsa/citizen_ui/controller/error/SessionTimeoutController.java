package uk.gov.dwp.jsa.citizen_ui.controller.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionTimeoutController {

    public static final String IDENTIFIER = "/error/session-timeout";

    @GetMapping(IDENTIFIER)
    public String getCookiePolicy() {
        return "error/session-timeout";
    }

    @GetMapping("/session-timeout")
    public String getSessionTimedout() {
        return "session-timeout";
    }

}
