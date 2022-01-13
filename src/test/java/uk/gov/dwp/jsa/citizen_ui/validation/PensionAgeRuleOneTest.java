package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class PensionAgeRuleOneTest {

    private LocalDate dateOfBirth;
    private LocalDate dateOfClaim;

    @Test
    @Parameters({
            "6, 12, 1953, 5, 3, 2019",
            "5, 1, 1954, 5, 3, 2019",
            "6, 1, 1954, 6, 3, 2019",
            "5, 2, 1954, 5, 5, 2019",
            "6, 2, 1954, 6, 6, 2019",
            "5, 3, 1954, 5, 7, 2019",
            "6, 3, 1954, 6, 8, 2019",
            "5, 4, 1954, 5, 9, 2019",
            "6, 5, 1954, 6, 10, 2019",
            "5, 5, 1954, 5, 11, 2019",
            "6, 6, 1954, 6, 2, 2020",
            "5, 7, 1954, 5, 3, 2020",
            "6, 7, 1954, 6, 4, 2020",
            "5, 8, 1954, 5, 5, 2020",
            "6, 8, 1954, 6, 6, 2020",
            "5, 9, 1954, 5, 7, 2020",
    })
    public void isUnderPensionAgeReturnsTrue(int dobDay, int dobMonth, int dobYear,
                                             int claimDtDay, int claimDtMonth, int claimDtYear) {
        givenDatesOfBirthAndClaimAre(dobDay, dobMonth, dobYear, claimDtDay, claimDtMonth, claimDtYear);

        boolean isUnderPensionAge = PensionAgeRuleOne.isUnderPensionAge(dateOfBirth, dateOfClaim);

        assertThat(isUnderPensionAge, is(true));
    }

    @Test
    @Parameters({
            "5, 12, 1953, 5, 12, 2018",
            "17, 8, 1953, 18, 8, 2018",
            "6, 12, 1953, 6, 3, 2019",
            "5, 1, 1954, 7, 4, 2019",
            "6, 1, 1954, 7, 5, 2019",
            "5, 2, 1954, 9, 9, 2019",
            "6, 2, 1954, 7, 7, 2019",
            "5, 3, 1954, 9, 8, 2019",
            "6, 3, 1954, 7, 9, 2019",
            "5, 4, 1954, 9, 10, 2019",
            "6, 4, 1954, 7, 11, 2019",
            "5, 5, 1954, 9, 12, 2019",
            "6, 5, 1954, 7, 1, 2020",
            "5, 6, 1954, 9, 2, 2020",
            "6, 7, 1954, 7, 5, 2020",
            "5, 8, 1954, 5, 6, 2020",
            "6, 8, 1954, 7, 7, 2020",
            "5, 9, 1954, 9, 8, 2020",
            "6, 9, 1954, 7, 9, 2020",
            "5, 10, 1954, 9, 10, 2020",
    })
    public void isUnderPensionAgeReturnsFalse(int dobDay, int dobMonth, int dobYear,
                                             int claimDtDay, int claimDtMonth, int claimDtYear) {
        givenDatesOfBirthAndClaimAre(dobDay, dobMonth, dobYear, claimDtDay, claimDtMonth, claimDtYear);

        boolean isUnderPensionAge = PensionAgeRuleOne.isUnderPensionAge(dateOfBirth, dateOfClaim);

        assertThat(isUnderPensionAge, is(false));
    }

    private void givenDatesOfBirthAndClaimAre(final int dobDay, final int dobMonth, final int dobYear,
                                              final int claimDtDay, final int claimDtMonth, final int claimDtYear) {
        dateOfBirth = LocalDate.of(dobYear, dobMonth, dobDay);
        dateOfClaim = LocalDate.of(claimDtYear, claimDtMonth, claimDtDay);
    }

}
