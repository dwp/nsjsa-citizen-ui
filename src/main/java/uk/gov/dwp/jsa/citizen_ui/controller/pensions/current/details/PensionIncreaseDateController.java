package uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMonthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm.MULTIPLE_OPTIONS_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months.APRIL;
import static uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months.valueOf;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/pensions/current/details")
public class PensionIncreaseDateController extends
        CounterFormController<PensionIncreaseMultipleOptionsForm> {

    public static final String IDENTIFIER = "form/pensions/current/details/increase-date";
    public static final String IDENTIFIER_TEMPLATE = "form/pensions/current/details/%s/increase-date";
    public static final String TRANSLATION_KEY = "pensions.current.month.";

    public PensionIncreaseDateController(final ClaimRepository claimRepository, final RoutingService routingService) {

        super(claimRepository,
                MULTIPLE_OPTIONS_VIEW_NAME,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/pensions/current/%s/has-another-pension",
                NO_ALTERNATIVE_IDENTIFIER,
                Section.CURRENT_PENSIONS);
    }

    @Override
    public PensionIncreaseMultipleOptionsForm getForm() {
        PensionIncreaseMultipleOptionsForm form =
                new PensionIncreaseMultipleOptionsForm(new PensionIncreaseMonthQuestion(), APRIL);
        return form;
    }

    @Override
    public PensionIncreaseMultipleOptionsForm getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final PensionIncreaseMultipleOptionsForm form, final String claimId) {
        form.setTranslationKey(TRANSLATION_KEY);
        form.setInline(false);
        form.setDefaultOption(valueOf(LocalDate.now().getMonth().name()));
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final PensionIncreaseMultipleOptionsForm form) {
        resolve(() -> claimDB
                .getCircumstances()
                .getPensions()
                .getCurrent()
                .get(form.getCount() - 1)
                .getPensionIncreaseMonth())
                .ifPresent(month -> form.getQuestion().setUserSelectionValue(valueOf(month)));


    }

    @GetMapping(path = "/{count:[1-9]+}/increase-date")
    public final String getPensionIncreaseMonth(@PathVariable final Integer count,
                                        final Model model,
                                        @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                        final HttpServletRequest request) {
        return get(model, claimId, request, count);
    }

    @PostMapping(path = "/{count:[1-9]+}/increase-date")
    public final String postIsWorkPaid(
            @ModelAttribute(FORM_NAME) @Valid final PensionIncreaseMultipleOptionsForm form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {

        return post(claimId, form, bindingResult, response, model);
    }
    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }
}
