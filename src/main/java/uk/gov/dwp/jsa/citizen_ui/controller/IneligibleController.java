package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for rendering not eligible pages for claimant.
 */
@Controller
public class IneligibleController {
    private static final String ELIGIBILITY_SECTION_PREFIX_URL = "/form/eligibility/";
    private static final String INELIGIBLE_GENERIC_TEMPLATE = "form/eligibility/ineligible-generic";
    private static final String INELIGIBLE_REASON = "ineligibleReason";
    private static final String PAGE_TITLE = "title";

    private static final Map<String, String> ROUTING = new HashMap<>();

    IneligibleController() {
        ROUTING.put("residence", "/form/eligibility/working");
        ROUTING.put("working", "/form/eligibility/ineligible/apply");
        ROUTING.put("working-over", "/form/eligibility/ineligible/apply");
    }

    /**
     * Renders the Generic Ineligible Page.
     *
     * @param reason Ineligibility reason
     * @param model  Spring MVC Model
     * @return String
     */
    @GetMapping("/form/eligibility/{reason:[a-z-]+}/ineligible")
    public final String getGeneralIneligible(@PathVariable final String reason, final Model model) {
        return getIneligibleFromSource(reason, reason, model);
    }

    /**
     * Renders the Generic Ineligible Page.
     *
     * @param reason     Ineligibility reason
     * @param sourcePage Trigger page
     * @param model      Spring MVC Model
     * @return String
     */
    @GetMapping("/form/eligibility/{reason:[a-z-]+}/{sourcePage:[a-z-]+}/ineligible")
    public final String getIneligibleFromSource(@PathVariable final String reason, @PathVariable final String sourcePage, final Model model) {
        setReasonAndTitle(reason, model);
        setBackUrl(sourcePage, model);
        setNextUrl(sourcePage, model);
        return INELIGIBLE_GENERIC_TEMPLATE;
    }

    private void setBackUrl(final String referralUrl, final Model model) {
        model.addAttribute("backUrl", ELIGIBILITY_SECTION_PREFIX_URL + referralUrl);
    }

    private void setNextUrl(final String referralUrl, final Model model) {
        model.addAttribute("nextUrl", ROUTING.get(referralUrl));
    }

    private void setReasonAndTitle(final String referralUrl, final Model model) {
        model.addAttribute(INELIGIBLE_REASON, referralUrl);
        model.addAttribute(PAGE_TITLE, "ineligible.general.heading1.text");
    }

    /**
     * Renders the page showing that claimant is ineligible due to date of birth.
     *EmployersNameController.java
     * @param model Spring MVC Model
     * @return String
     */
    @GetMapping("/form/personal-details/under18")
    public final String getIneligibleUnder18(final Model model) {
        String page = "form/ineligible/under18";
        return page;
    }

}
