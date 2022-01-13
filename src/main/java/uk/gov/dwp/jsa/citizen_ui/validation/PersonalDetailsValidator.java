package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.PersonalDetailsConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.common.NameStringTruncatedQuestion.NAME_VALIDATION_REGEX;

public class PersonalDetailsValidator implements ConstraintValidator<PersonalDetailsConstraint,
        PersonalDetailsQuestion>, Validator {

    @Override
    public boolean isValid(final PersonalDetailsQuestion question, final ConstraintValidatorContext context) {
        boolean isValid = true;
        if (question.getTitleQuestion().getUserSelectionValue() == null) {
            question.getTitleQuestion().setValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.title.error",
                    "titleQuestion");
        }

        String firstName = question.getFirstNameQuestion().getValue();
        if (firstName == null
                || firstName.isEmpty()) {
            question.getFirstNameQuestion().setIsValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.firstname.blank",
                    "firstNameQuestion");
        } else if (!firstName.substring(firstName.length() - 1).matches("^[A-Za-z]")) {
            question.getFirstNameQuestion().setIsValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.firstname.error.end",
                    "firstNameQuestion");
        } else if (!firstName.matches(NAME_VALIDATION_REGEX)) {
            question.getFirstNameQuestion().setIsValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.firstname.error",
                    "firstNameQuestion");
        }
        String lastName = question.getLastNameQuestion().getValue();

        if (lastName == null
                || lastName.isEmpty()) {
            question.getLastNameQuestion().setIsValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.lastname.blank",
                    "lastNameQuestion");
        } else if (!lastName.substring(lastName.length() - 1).matches("^[A-Za-z]")) {
            question.getLastNameQuestion().setIsValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.lastname.error.end",
                    "lastNameQuestion");
        } else if (!lastName.matches(NAME_VALIDATION_REGEX)) {
            question.getLastNameQuestion().setIsValid(true);
            isValid = addInvalidMessage(context, "personaldetails.field.lastname.error",
                    "lastNameQuestion");
        }
        return isValid;
    }
}
