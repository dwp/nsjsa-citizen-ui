package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class PensionAgeRuleTwoTest {

    private LocalDate dateOfBirth;
    private LocalDate dateOfClaim;

    @Test
    @Parameters({
            "6, 9, 1854, 5, 9, 1919",
            "6, 9, 1954, 6, 8, 2020",
            "4, 4, 1960, 3, 4, 2026",
            "6, 4, 1960, 5, 5, 2026",
            "4, 5, 1960, 3, 6, 2026",
            "6, 5, 1960, 5, 7, 2026",
            "4, 6, 1960, 3, 8, 2026",
            "6, 6, 1960, 5, 9, 2026",
            "4, 7, 1960, 3, 10, 2026",
            "6, 7, 1960, 5, 11, 2026",
            "4, 8, 1960, 3, 12, 2026",
            "6, 8, 1960, 5, 1, 2027",
            "4, 9, 1960, 3, 2, 2027",
            "6, 9, 1960, 5, 3, 2027",
            "4, 10, 1960, 3, 4, 2027",
            "6, 10, 1960, 5, 5, 2027",
            "4, 11, 1960, 3, 6, 2027",
            "6, 10, 1960, 5, 5, 2027",
            "6, 11, 1960, 5, 7, 2027",
            "4, 12, 1960, 3, 8, 2027",
            "6, 12, 1960, 5, 9, 2027",
            "4, 1, 1961, 3, 10, 2027",
            "6, 1, 1961, 5, 11, 2027",
            "4, 2, 1961, 3, 12, 2027",
            "6, 2, 1961, 5, 1, 2028",
            "6, 3, 1961, 5, 3, 2028",
            "11, 11, 1980, 8, 8, 2018"
    })
    public void isUnderPensionAgeReturnsTrue(int dobDay, int dobMonth, int dobYear,
                                             int claimDtDay, int claimDtMonth, int claimDtYear) {
        givenDatesOfBirthAndClaimAre(dobDay, dobMonth, dobYear, claimDtDay, claimDtMonth, claimDtYear);

        boolean isUnderPensionAge = PensionAgeRuleTwo.isUnderPensionAge(dateOfBirth, dateOfClaim);

        assertThat(isUnderPensionAge, is(true));
    }

    @Test
    @Parameters({
            "6, 9, 1854, 6, 9, 1919",
            "6, 9, 1954, 6, 9, 2020",
            "4, 4, 1960, 5, 4, 2026",
            "6, 4, 1960, 6, 5, 2026",
            "4, 5, 1960, 5, 6, 2026",
            "6, 5, 1960, 6, 7, 2026",
            "4, 6, 1960, 5, 8, 2026",
            "6, 6, 1960, 6, 9, 2026",
            "4, 7, 1960, 5, 10, 2026",
            "6, 7, 1960, 6, 11, 2026",
            "4, 8, 1960, 5, 12, 2026",
            "6, 8, 1960, 6, 1, 2027",
            "4, 9, 1960, 5, 2, 2027",
            "6, 9, 1960, 6, 3, 2027",
            "4, 10, 1960, 5, 4, 2027",
            "6, 10, 1960, 6, 5, 2027",
            "4, 11, 1960, 5, 6, 2027",
            "6, 10, 1960, 6, 5, 2027",
            "6, 11, 1960, 6, 7, 2027",
            "4, 12, 1960, 6, 8, 2027",
            "6, 12, 1960, 6, 9, 2027",
            "4, 1, 1961, 5, 10, 2027",
            "6, 1, 1961, 6, 11, 2027",
            "4, 2, 1961, 5, 12, 2027",
            "6, 2, 1961, 6, 1, 2028",
            "6, 3, 1961, 7, 3, 2028",
            "11, 11, 1980, 11, 11, 2047"
    })
    public void isUnderPensionAgeReturnsFalse(int dobDay, int dobMonth, int dobYear,
                                             int claimDtDay, int claimDtMonth, int claimDtYear) {
        givenDatesOfBirthAndClaimAre(dobDay, dobMonth, dobYear, claimDtDay, claimDtMonth, claimDtYear);

        boolean isUnderPensionAge = PensionAgeRuleTwo.isUnderPensionAge(dateOfBirth, dateOfClaim);

        assertThat(isUnderPensionAge, is(false));
    }

    private void givenDatesOfBirthAndClaimAre(final int dobDay, final int dobMonth, final int dobYear,
                                              final int claimDtDay, final int claimDtMonth, final int claimDtYear) {
        dateOfBirth = LocalDate.of(dobYear, dobMonth, dobDay);
        dateOfClaim = LocalDate.of(claimDtYear, claimDtMonth, claimDtDay);
    }

}
