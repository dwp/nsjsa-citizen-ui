package uk.gov.dwp.jsa.citizen_ui.validation;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.LocalDate.of;

public enum PensionAgeRuleOne {
    RANGE_1(of(1953, 12, 6), of(1954, 1, 5), of(2019, 3, 6)),
    RANGE_2(of(1954, 1, 6), of(1954, 2, 5), of(2019, 5, 6)),
    RANGE_3(of(1954, 2, 6), of(1954, 3, 5), of(2019, 7, 6)),
    RANGE_4(of(1954, 3, 6), of(1954, 4, 5), of(2019, 9, 6)),
    RANGE_5(of(1954, 4, 6), of(1954, 5, 5), of(2019, 11, 6)),
    RANGE_6(of(1954, 5, 6), of(1954, 6, 5), of(2020, 1, 6)),
    RANGE_7(of(1954, 6, 6), of(1954, 7, 5), of(2020, 3, 6)),
    RANGE_8(of(1954, 7, 6), of(1954, 8, 5), of(2020, 5, 6)),
    RANGE_9(of(1954, 8, 6), of(1954, 9, 5), of(2020, 7, 6));

    private LocalDate fromDateOfBirth;
    private LocalDate toDateOfBirth;
    private LocalDate pensionAge;

    PensionAgeRuleOne(final LocalDate fromDateOfBirth, final LocalDate toDateOfBirth, final LocalDate pensionAge) {
        this.fromDateOfBirth = fromDateOfBirth;
        this.toDateOfBirth = toDateOfBirth;
        this.pensionAge = pensionAge;
    }

    public static boolean isUnderPensionAge(final LocalDate dateOfBirth, final LocalDate dateOfClaim) {
        return Stream.of(PensionAgeRuleOne.values())
                .anyMatch(value -> isUnderPensionAge(value, dateOfBirth, dateOfClaim));
    }

    private static boolean isUnderPensionAge(final PensionAgeRuleOne pensionAgeRuleOne, final LocalDate dateOfBirth,
                                             final LocalDate dateOfClaim) {
        return dateOfBirthIsWithinRange(pensionAgeRuleOne, dateOfBirth)
                && dateOfClaim.isBefore(pensionAgeRuleOne.pensionAge);
    }

    private static boolean dateOfBirthIsWithinRange(final PensionAgeRuleOne pensionAgeRuleOne,
                                                    final LocalDate dateOfBirth) {
        return dateOfBirth.isEqual(pensionAgeRuleOne.fromDateOfBirth)
                || dateOfBirth.isEqual(pensionAgeRuleOne.toDateOfBirth)
                || (dateOfBirth.isAfter(pensionAgeRuleOne.fromDateOfBirth)
                && dateOfBirth.isBefore(pensionAgeRuleOne.toDateOfBirth));
    }

}
