package uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details;

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
import uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.common.MultipleOptionsForm.MULTIPLE_OPTIONS_VIEW_NAME;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/form/current-work/details")
public class WorkPaidOrVoluntaryController extends
        CounterFormController<MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork>> {

    private static final TypeOfWork TRUE_CONDITION_VALUE = TypeOfWork.PAID;
    public static final String IDENTIFIER = "form/current-work/details/is-work-paid";
    public static final String IDENTIFIER_TEMPLATE = "form/current-work/details/%s/is-work-paid";

    public WorkPaidOrVoluntaryController(final ClaimRepository claimRepository, final RoutingService routingService) {

        super(claimRepository,
                MULTIPLE_OPTIONS_VIEW_NAME,
                FORM_NAME,
                routingService,
              IDENTIFIER,
                "/form/current-work/details/%s/choose-payment",
                "/form/current-work/details/%s/how-often-paid",
                Section.CURRENT_WORK);
    }

    @Override
    public MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork>  getForm() {
        MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form =
                new MultipleOptionsForm<>(new TypeOfWorkQuestion(), TRUE_CONDITION_VALUE);
        return form;
    }

    @Override
    public MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form, final String claimId) {
        form.setTranslationKey("current.work.is.paid.");
        form.setInline(true);
        form.setDefaultOption(TypeOfWork.PAID);
        form.setOptions(Arrays.asList(TypeOfWork.values()));
        super.setFormAttrs(form, claimId);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form) {
        boolean isPaid = resolve(() -> claimDB
                .getCircumstances()
                .getCurrentWork()
                .get(form.getCount() - 1)
                .isPaid())
                .orElse(false);
        boolean isVoluntary = resolve(() -> claimDB
                .getCircumstances()
                .getCurrentWork()
                .get(form.getCount() - 1)
                .isVoluntary())
                .orElse(false);

        if (isPaid) {
            form.setQuestion(new TypeOfWorkQuestion(TypeOfWork.PAID));
        } else if (isVoluntary) {
            form.setQuestion(new TypeOfWorkQuestion(TypeOfWork.VOLUNTARY));
        }
    }

    @GetMapping(path = "/{count:[1-4]+}/is-work-paid")
    public final String getIsWorkPaid(@PathVariable final Integer count,
                                      final Model model,
                                      @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                      final HttpServletRequest request) {
        enableHint(count, model);
        return get(model, claimId, request, count);
    }

    @PostMapping(path = "/{count:[1-4]+}/is-work-paid")
    public final String postIsWorkPaid(
            @ModelAttribute(FORM_NAME) @Valid final MultipleOptionsForm<TypeOfWorkQuestion, TypeOfWork> form,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final Model model) {

        enableHint(form.getCount(), model);
        return post(claimId, form, bindingResult, response, model);
    }

    public String postUrlTemplate() {
        return IDENTIFIER_TEMPLATE;
    }

    private void enableHint(final Integer count, final Model model) {
        Boolean enableHint = count < 4 ? Boolean.TRUE : Boolean.FALSE;
        model.addAttribute("enableHint", enableHint);
    }
}
