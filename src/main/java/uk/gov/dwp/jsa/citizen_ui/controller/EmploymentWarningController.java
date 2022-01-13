package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;
import static uk.gov.dwp.jsa.citizen_ui.controller.pensions.PensionWarningController.REMOVE_VIEW;

@Controller
@RequestMapping("/form/employment/warning")
public class EmploymentWarningController {

    private static final String QUESTION = "common.work.warning.question";
    private static final String PREVIOUS_EMPLOYMENT_REMOVE_URL = "/form/previous-employment/%s/remove-work";
    private static final String CURRENT_EMPLOYMENT_REMOVE_URL = "/form/current-work/%s/remove-work";
    private static final String PREVIOUS = "previous";
    private static final String CURRENT = "current";

    @GetMapping("/{type}/{count}/remove")
    public final String remove(
            @PathVariable("type") final String type,
            @PathVariable("count") final Integer count,
            final Model model) {
        addAttributes(type, count, model);
        return REMOVE_VIEW;
    }

    private void addAttributes(final String type,
                               final Integer count,
                               final Model model) {
        model.addAttribute("count", count);
        model.addAttribute("question", QUESTION);
        if (PREVIOUS.equals(type)) {
            model.addAttribute("hrefForRemove", format(PREVIOUS_EMPLOYMENT_REMOVE_URL, count));
        } else if (CURRENT.equals(type)) {
            model.addAttribute("hrefForRemove", format(CURRENT_EMPLOYMENT_REMOVE_URL, count));
        }
        model.addAttribute("backRef", "/form/summary");
    }
}
