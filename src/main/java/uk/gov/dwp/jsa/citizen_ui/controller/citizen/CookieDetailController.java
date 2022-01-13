package uk.gov.dwp.jsa.citizen_ui.controller.citizen;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;


/**
 * Controller for rendering Cookie details.
 */
@Controller
public class CookieDetailController {

    @GetMapping("/cookies-details")
    public String getCookiePolicy(final HttpServletRequest request, final Model model) {
        model.addAttribute("redirectUrl", getReferrer(request));
        return "citizen/cookies-details";
    }


    private String getReferrer(final HttpServletRequest request) {
        return request.getHeader("referer");
    }

}
