package uk.gov.dwp.jsa.citizen_ui.validation;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.LocalDate.of;
import static java.time.temporal.ChronoUnit.MONTHS;

public enum PensionAgeRuleTwo {
    RANGE_1(LocalDate.MIN, of(1953, 12, 5), -12),
    RANGE_2(of(1954, 9, 6), of(1960, 4, 5), 0),
    RANGE_3(of(1960, 4, 6), of(1960, 5, 5), 1),
    RANGE_4(of(1960, 5, 6), of(1960, 6, 5), 2),
    RANGE_5(of(1960, 6, 6), of(1960, 7, 5), 3),
    RANGE_6(of(1960, 7, 6), of(1960, 8, 5), 4),
    RANGE_7(of(1960, 8, 6), of(1960, 9, 5), 5),
    RANGE_8(of(1960, 9, 6), of(1960, 10, 5), 6),
    RANGE_9(of(1960, 10, 6), of(1960, 11, 5), 7),
    RANGE_10(of(1960, 11, 6), of(1960, 12, 5), 8),
    RANGE_11(of(1960, 12, 6), of(1961, 1, 5), 9),
    RANGE_12(of(1961, 1, 6), of(1961, 2, 5), 10),
    RANGE_13(of(1961, 2, 6), of(1961, 3, 5), 11),
    RANGE_14(of(1961, 3, 6), LocalDate.MAX, 12);

    private static final Integer BASE_PENSION_AGE_MONTHS = 792;
    private LocalDate fromDateOfBirth;
    private LocalDate toDateOfBirth;
    private Integer pensionAgeOffset;

    PensionAgeRuleTwo(final LocalDate fromDateOfBirth, final LocalDate toDateOfBirth, final Integer pensionAgeOffset) {
        this.fromDateOfBirth = fromDateOfBirth;
        this.toDateOfBirth = toDateOfBirth;
        this.pensionAgeOffset = pensionAgeOffset;
    }

    public static boolean isUnderPensionAge(final LocalDate dateOfBirth, final LocalDate dateOfClaim) {
        return Stream.of(PensionAgeRuleTwo.values())
                .anyMatch(value -> isUnderPensionAge(value, dateOfBirth, dateOfClaim));
    }

    private static boolean isUnderPensionAge(final PensionAgeRuleTwo pensionAgeRuleTwo, final LocalDate dateOfBirth,
                                             final LocalDate dateOfClaim) {
        int ageDiffInMonths = BASE_PENSION_AGE_MONTHS + pensionAgeRuleTwo.pensionAgeOffset;
        return dateOfBirthIsWithinRange(pensionAgeRuleTwo, dateOfBirth)
                && MONTHS.between(dateOfBirth, dateOfClaim) < ageDiffInMonths;
    }

    private static boolean dateOfBirthIsWithinRange(final PensionAgeRuleTwo pensionAgeRuleTwo,
                                                    final LocalDate dateOfBirth) {
        return dateOfBirth.isEqual(pensionAgeRuleTwo.fromDateOfBirth)
                || dateOfBirth.isEqual(pensionAgeRuleTwo.toDateOfBirth)
                || (dateOfBirth.isAfter(pensionAgeRuleTwo.fromDateOfBirth)
                && dateOfBirth.isBefore(pensionAgeRuleTwo.toDateOfBirth));
    }

}
