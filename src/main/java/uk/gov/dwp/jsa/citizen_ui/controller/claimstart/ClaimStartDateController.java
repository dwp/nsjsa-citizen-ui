package uk.gov.dwp.jsa.citizen_ui.controller.claimstart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.backdating.WereYouAvailableForWorkController;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.SDT;
import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.NO_ALTERNATIVE_IDENTIFIER;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Controller
@RequestMapping("/" + ClaimStartDateController.IDENTIFIER)
public class ClaimStartDateController extends BaseFormController<ClaimStartDateForm> {

    public static final String IDENTIFIER = "form/claim-start";
    private static final int WEEKS_BEFORE_ALLOWED = 13;

    @Autowired
    public ClaimStartDateController(final ClaimRepository claimRepository, final RoutingService routingService) {
        super(claimRepository,
                IDENTIFIER,
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/" + WereYouAvailableForWorkController.IDENTIFIER,
                NO_ALTERNATIVE_IDENTIFIER,
                Section.BACK_DATING);
    }

    /**
     * Renders the Claim Start Date part of the form.
     *
     * @param request HttpServletRequest
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return Name of view
     */
    @GetMapping
    public final String claimStartDate(final Model model,
                                       @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                       final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Captures submission of the Claim Start Date part of the form.
     *
     * @param part Claim start date form
     * @param bindingResult Spring MVC Binding Result
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @return View
     */
    @PostMapping
    public final String claimStartDate(
            @Validated(ValidationSequence.class) @ModelAttribute(FORM_NAME) final ClaimStartDateForm part,
            final BindingResult bindingResult,
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final HttpServletResponse response,
            final HttpServletRequest request,
            final Model model) {
        if (bindingResult.hasFieldErrors(ClaimStartDateForm.HONEYPOT_FIELD)) {
            removeCookie(request, response, Constants.JSESSIONID);
            removeCookie(request, response, Constants.COOKIE_CLAIM_ID);
            return "redirect:/error/404.html";
        }
        if (!bindingResult.hasFieldErrors() && part.getQuestion().getFormattedDate().equals(LocalDate.now().format(SDT))) {
            super.changeNextStep("/form/nino",
                    NO_ALTERNATIVE_IDENTIFIER, Section.BACK_DATING, false);
        } else if (!bindingResult.hasFieldErrors() && isBeforeToday(part)) {
            super.changeNextStep("/" + WereYouAvailableForWorkController.IDENTIFIER,
                    NO_ALTERNATIVE_IDENTIFIER, Section.BACK_DATING, false);
        }
        model.addAttribute("exampleStartDate",
                LocalDate.now().minusWeeks(WEEKS_BEFORE_ALLOWED).minusDays(1).format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return post(claimId, part, bindingResult, response, model);
    }

    private static boolean isBeforeToday(final ClaimStartDateForm form) {
        boolean isBefore;
        LocalDate dateToCompare = LocalDate.of(
                form.getQuestion().getYear(), form.getQuestion().getMonth(), form.getQuestion().getDay());
        if (dateToCompare.isBefore(LocalDate.now())) {
            isBefore = true;
        } else {
            isBefore = false;
        }
        return isBefore;
    }

    @Override
    public ClaimStartDateForm getForm() {
        ClaimStartDateForm claimStartDateForm = new ClaimStartDateForm();
        claimStartDateForm.setClaimStartDateQuestion(new ClaimStartDateQuestion());

        return claimStartDateForm;
    }

    @Override
    public ClaimStartDateForm getTypedForm() {
        return getForm();
    }


    @Override
    public void loadForm(final ClaimDB claimDB, final ClaimStartDateForm form) {
        resolve(() -> claimDB.getCircumstances().getClaimStartDate())
                .ifPresent(localDate -> form.setClaimStartDateQuestion(new ClaimStartDateQuestion(localDate)));
    }

    private Optional<Cookie> getCookie(final HttpServletRequest req, final String cookieName) {
        if (req.getCookies() != null) {
            return Arrays.stream(req.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private void removeCookie(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String cookieName
    ) {
        getCookie(request, cookieName).ifPresent(cookie -> {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
        });

        final HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

    }
}
