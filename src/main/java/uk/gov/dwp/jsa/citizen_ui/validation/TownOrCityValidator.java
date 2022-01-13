package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.TownOrCityConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX;
import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.TOWN_OR_CITY_LINE_REGEX;

public class TownOrCityValidator implements ConstraintValidator<TownOrCityConstraint, String>, Validator {

    @Override
    public boolean isValid(final String townOrCity, final ConstraintValidatorContext context) {
        if (townOrCity == null || townOrCity.isEmpty()) {
            return addInvalidMessage(context, "about.address.town.blank");
        } else if (!townOrCity.matches(TOWN_OR_CITY_LINE_REGEX)) {
            return addInvalidMessage(context, "about.address.town.error");
        } else if (!townOrCity.matches(NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX)) {
            return addInvalidMessage(context, "about.address.town.error");
        }
        return true;
    }
}
