package uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;

import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/current-work/details")
public class HoursController extends CounterFormController<HoursForm> {
    public static final String IDENTIFIER = "form/current-work/details/hours";
    public static final String IDENTIFIER_TEMPLATE = "form/current-work/details/%s/hours";
    private static final String PAID_NEXT_STEP = "/form/current-work/details/%s/self-employed-confirmation";
    private static final String VOLUNTEER_NEXT_STEP = "/form/current-work/%s/has-another-job";
    private ClaimRepository claimRepository = getClaimRepository();

    public HoursController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository, "form/current-work/hours",
                FORM_NAME,
                routingService,
                IDENTIFIER,
                PAID_NEXT_STEP,
                NO_ALTERNATIVE_IDENTIFIER,
                Section.CURRENT_WORK);
    }

    @GetMapping("/{count:[1-4]+}/hours")
    public final String getView(final Model model,
                                @PathVariable final Integer count,
                                @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                final HttpServletRequest request) {
      return get(model, claimId, request, count);
    }

    @PostMapping("/{count:[1-4]+}/hours")
    public String submitHours(@ModelAttribute(FORM_NAME) @Valid final HoursForm form,
                              final BindingResult bindingResult,
                              @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                              @RequestParam(name = "count") final Integer count,
                              final HttpServletResponse response,
                              final Model model) {
        Claim claim = claimRepository.findById(claimId).get();
        Optional<TypeOfWork> typeOfWork = super.getTypeOfWork(claim, count);
        String nextStepIdentifier;
        if (typeOfWork.isPresent() && typeOfWork.get() != TypeOfWork.PAID) {
            nextStepIdentifier = VOLUNTEER_NEXT_STEP;
        } else {
            nextStepIdentifier = PAID_NEXT_STEP;
        }
        changeNextStep(nextStepIdentifier, NO_ALTERNATIVE_IDENTIFIER, Section.CURRENT_WORK, false);
        return post(claimId, form, bindingResult, response, model);
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    @Override
    public String getFormPostUrl() {
        return "/form/current-work/details/hours";
    }

    @Override
    public HoursForm getForm() {
        HoursForm form = new HoursForm();
        form.setHoursQuestion(new HoursQuestion());
        return form;
    }

    @Override
    public HoursForm getTypedForm() {
        return getForm();
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final HoursForm form) {
        resolve(() -> claimDB.getCircumstances().getCurrentWork().get(form.getCount() - 1).getHoursPerWeek())
                .ifPresent(hours -> form.getQuestion().setHours(hours));
    }
}
