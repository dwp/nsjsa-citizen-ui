package uk.gov.dwp.jsa.citizen_ui.controller.personaldetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.util.WarningLogger;
import uk.gov.dwp.jsa.citizen_ui.validation.PensionAgeRuleOne;
import uk.gov.dwp.jsa.citizen_ui.validation.PensionAgeRuleTwo;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Optional;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;
import static uk.gov.dwp.jsa.citizen_ui.validation.DateOfBirthValidator.EIGHTEEN;
import static uk.gov.dwp.jsa.citizen_ui.validation.DateOfBirthValidator.ONE;
import static uk.gov.dwp.jsa.citizen_ui.validation.DateOfBirthValidator.SIXTEEN;

/**
 * Form controller. Handles to rendering and submission of each part of the JSA
 * form
 */
@Controller
public class DateOfBirthFormController extends BaseFormController<DateOfBirthForm> {

    public static final String IDENTIFIER = "form/date-of-birth";
    public static final int MAX_AGE = 100;
    private final WarningLogger warningLogger;

    public DateOfBirthFormController(final ClaimRepository claimRepository,
                                     final WarningLogger warningLogger,
                                     final RoutingService routingService) {
        super(claimRepository,
                "form/personal-details/date-of-birth",
                FORM_NAME,
                routingService,
                IDENTIFIER,
                "/form/personal-details/address",
                "/form/personal-details/under18",
                Section.PERSONAL_DETAILS);
        this.warningLogger = warningLogger;
    }

    /**
     * Renders the Date of Birth part of the form.
     *
     * @param model   the Spring Boot MVC model
     * @param claimId Claim's id in cache
     * @param request HttpServletRequest
     * @return Name of view
     */
    @GetMapping(path = "/form/date-of-birth")
    public final String getDateOfBirth(final Model model,
                                       @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
                                       final HttpServletRequest request) {
        return get(model, claimId, request);
    }

    /**
     * Captures submission of the Date Of Birth part of the form.
     *
     * @param dateOfBirthForm Model capturing the response of the date of birth
     *                        step
     * @param bindingResult   Spring Boot MVC binding result
     * @param model SpringBoot MVC model
     * @param claimId Claimaint Id
     * @param response HttpServletResponse
     * @return Name of view
     */
    @PostMapping("/form/date-of-birth")
    public final String submitDateOfBirth(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            @Validated(ValidationSequence.class)
            @ModelAttribute(FORM_NAME) final DateOfBirthForm dateOfBirthForm,
            final BindingResult bindingResult,
            final HttpServletResponse response,
            final Model model) {
        if (!bindingResult.hasErrors()) {
            Integer year = dateOfBirthForm.getQuestion().getYear();
            if (String.valueOf(year).length() == 1 || String.valueOf(year).length() == 2) {
                Integer parsedYear = parseTwoDigitYear(year);
                dateOfBirthForm.getQuestion().setYear(parsedYear);
            }
            checkForMaxDateValidation(claimId, dateOfBirthForm, bindingResult);
        }
        return post(claimId, dateOfBirthForm, bindingResult, response, model);
    }

    private Integer parseTwoDigitYear(final Integer dateToParse) {
        String result = dateToParse.toString();
        if (result.length() == 1) {
            result =  String.format("%02d", dateToParse);
        }
        DateTimeFormatter twoYearFormat = new DateTimeFormatterBuilder()
                .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 2,
                        LocalDate.now().minusYears(MAX_AGE))
                .toFormatter();
        Year parsedYear = twoYearFormat.parse(result, Year::from);
        return parsedYear.getValue();
    }

    private void checkForMaxDateValidation(final String claimId, final DateOfBirthForm dateOfBirthForm,
                                           final BindingResult bindingResult) {
        if (dateOfBirthAndClaimIdIsNotNull(claimId, dateOfBirthForm)) {
            DateOfBirthQuestion dateOfBirthQuestion = dateOfBirthForm.getDateOfBirthQuestion();
            LocalDate dateOfBirth = LocalDate.of(dateOfBirthQuestion.getYear(),
                    dateOfBirthQuestion.getMonth(), dateOfBirthQuestion.getDay());
            Optional<Claim> optionalClaim = getClaimRepository().findById(claimId);

            if (optionalClaim.isPresent()) {
                addMaxAgeValidationErrorIfApplicable(bindingResult, dateOfBirth, optionalClaim.get());
            }
        }
    }

    private void addMaxAgeValidationErrorIfApplicable(
            final BindingResult bindingResult,
            final LocalDate dateOfBirth,
            final Claim claim) {

        Optional<ClaimStartDateQuestion> claimStartDate =
                claim.get(ClaimStartDateController.IDENTIFIER, ClaimStartDateQuestion.class);

        if (claimStartDate.isPresent()) {
            ClaimStartDateQuestion claimStartDateQuestion = claimStartDate.get();
            LocalDate dateOfClaim = LocalDate.of(
                    claimStartDateQuestion.getYear(),
                    claimStartDateQuestion.getMonth(),
                    claimStartDateQuestion.getDay());

            if (!(PensionAgeRuleOne.isUnderPensionAge(dateOfBirth, dateOfClaim)
                    || PensionAgeRuleTwo.isUnderPensionAge(dateOfBirth, dateOfClaim))) {
                FieldError fieldError = new FieldError(
                        FORM_NAME,
                        "dateOfBirthQuestion",
                        "",
                        false,
                        new String[]{},
                        new Object[]{},
                        "dateofbirth.error.above.pension.age.text"
                );
                bindingResult.addError(fieldError);
            }
        }
    }

    private boolean dateOfBirthAndClaimIdIsNotNull(final String claimId, final DateOfBirthForm dateOfBirthForm) {
        return dateOfBirthForm != null && claimId != null && dateOfBirthForm.getDateOfBirthQuestion() != null;
    }

    @Override
    public void setErrorFieldsOnModel(final BindingResult bindingResult,
                                      final Model model, final DateOfBirthForm form) {
        warningLogger.logErrorIfTypeMismatch(bindingResult);
    }

    @Override
    public void loadForm(final ClaimDB claimDB, final DateOfBirthForm form) {
        resolve(() -> claimDB.getClaimant().getDateOfBirth()).ifPresent(birthDay -> {
            form.setDateOfBirthQuestion(new DateOfBirthQuestion(birthDay));
            form.getDateOfBirthQuestion().setDateofBirthConditionsEnum(getDateOfBirthCondition(claimDB, birthDay));
        });
    }

    private DateofBirthConditionsEnum getDateOfBirthCondition(final ClaimDB claimDB, final LocalDate birthDay) {
        LocalDate dateOfClaim = claimDB.getCircumstances().getDateOfClaim();
        LocalDate localDateBefore16Years = dateOfClaim.minusYears(SIXTEEN);
        LocalDate localDateBefore18Years = dateOfClaim.minusYears(EIGHTEEN).plusDays(ONE);

        if (birthDay.isAfter(localDateBefore16Years)) {
            return DateofBirthConditionsEnum.LESS_THAN_16;
        } else if (birthDay.isBefore(localDateBefore18Years)) {
            return DateofBirthConditionsEnum.GREATER_THAN_18;
        } else {
            return DateofBirthConditionsEnum.BETWEEN_16_17;
        }
    }

    @Override
    public DateOfBirthForm getForm() {
        return new DateOfBirthForm(new DateOfBirthQuestion());
    }

    @Override
    public DateOfBirthForm getTypedForm() {
        return getForm();
    }
}
