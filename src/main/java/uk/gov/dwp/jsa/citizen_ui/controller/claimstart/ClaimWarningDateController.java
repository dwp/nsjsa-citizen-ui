package uk.gov.dwp.jsa.citizen_ui.controller.claimstart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.util.StringUtils.isEmpty;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;

@Controller
public class ClaimWarningDateController extends BaseFormController<ClaimStartDateForm> {

    public static final String IDENTIFIER = "form/warning/claim-start-date";

    @Autowired
    public ClaimWarningDateController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                null,
                NO_ALTERNATIVE_IDENTIFIER,
                Section.NONE);
    }

    /**
     * Renders the Claim Start Date warning.
     *
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping("/" + ClaimWarningDateController.IDENTIFIER)
    public final String claimWarning(final Model model,
                                     @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                     final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    @GetMapping("/form/warning/claim-start-date/reset")
    public final String claimReset(@CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                   final HttpServletResponse response) {
        Assert.isTrue(!isEmpty(claimId), "Claim id should not be empty");
        getClaimRepository().deleteById(claimId);
        getRoutingService().clearHistory(claimId);
        addClaimIdCookie(this.getClaimRepository().save(new Claim()), response);
        return "redirect:/" + ClaimStartDateController.IDENTIFIER;
    }

    @Override
    public void setFormAttrs(final ClaimStartDateForm form, final String claimId) {
        form.setBackRef("/form/summary");
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final ClaimStartDateForm form) {
        //Nothing to load;
    }

    @Override
    public ClaimStartDateForm getForm() {
        return new ClaimStartDateForm();
    }

    @Override
    public ClaimStartDateForm getTypedForm() {
        return getForm();
    }
}
