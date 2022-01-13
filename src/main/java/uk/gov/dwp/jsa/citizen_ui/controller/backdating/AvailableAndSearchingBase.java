package uk.gov.dwp.jsa.citizen_ui.controller.backdating;
/**
 * class used to extend functionality between questions that use {@link uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion} class
 * Example that extend this class include {@link uk.gov.dwp.jsa.citizen_ui.controller.backdating.WereYouAvailableForWorkController}
 * @author David Powell
 */

import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

public abstract class AvailableAndSearchingBase extends BaseFormController<BooleanForm<BooleanQuestion>> {
    public AvailableAndSearchingBase(final ClaimRepository claimRepository,
                                     final String viewName,
                                     final String modelName,
                                     final RoutingService routingService,
                                     final String identifier,
                                     final String nextStepIdentifier,
                                     final DateFormatterUtils dateFormatterUtils,
                                     final CookieLocaleResolver cookieLocaleResolver) {
        super(claimRepository,
                viewName,
                modelName,
                routingService,
                identifier,
                nextStepIdentifier,
                Constants.NO_ALTERNATIVE_IDENTIFIER,
                Section.BACK_DATING,
                dateFormatterUtils,
                cookieLocaleResolver);
    }

    @Override
    public BooleanForm<BooleanQuestion> getForm() {
        return new BooleanForm(new BooleanQuestion());
    }

    @Override
    public BooleanForm<BooleanQuestion> getTypedForm() {
        return getForm();
    }

    @Override
    public void setFormAttrs(final BooleanForm<BooleanQuestion> form, final String claimId) {
        super.setFormAttrs(form, claimId);
    }

}
