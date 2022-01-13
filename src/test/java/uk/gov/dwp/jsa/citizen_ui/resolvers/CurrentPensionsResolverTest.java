package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PensionDetail;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Pensions;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.*;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMonthQuestion;
import uk.gov.dwp.jsa.citizen_ui.resolvers.pensions.CurrentPensionsResolver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrentPensionsResolverTest {

    public static final BigDecimal NET_PAY = new BigDecimal(123).setScale(2, RoundingMode.HALF_UP);
    @Mock
    private Claim mockClaim;

    private CurrentPensionsResolver testSubject;

    public static final ProvidersAddressQuestion PROVIDERS_ADDRESS_QUESTION = new ProvidersAddressQuestion("32", "Basil Chambers", "Manchester", "M28 3UB");
    public static final ProvidersAddressQuestion PROVIDERS_ADDRESS_QUESTION_WITH_SPACES = new ProvidersAddressQuestion("32", "Basil Chambers", "Manchester", " M28 3UB ");
    public static final ProvidersAddressQuestion PROVIDERS_ADDRESS_QUESTION_NO_POSTCODE = new ProvidersAddressQuestion("32", "Basil Chambers", "Manchester", "");

    @Before
    public void setUp() {
        testSubject = new CurrentPensionsResolver();

    }

    @Test
    public void currentPensionDetailsAreResolvedFromClaimSuccessfully() {
        givenCurrentPensionsIsSetCorrectly();

        Circumstances circumstances = new Circumstances();
        circumstances.setPensions(new Pensions());
        testSubject.resolve(mockClaim, circumstances);

        thenPensionDetailsisSetAsExpected(circumstances.getPensions().getCurrent().get(0));
        thenPensionDetailsisSetAsExpected(circumstances.getPensions().getCurrent().get(1));
    }

    @Test
    public void GivenPostCodeHasSpaces_ThenPensionDetailsAreResolvedCorrectly() {
        givenPensionDetailsHasSpacesInPostcode();

        Circumstances circumstances = new Circumstances();
        circumstances.setPensions(new Pensions());
        testSubject.resolve(mockClaim, circumstances);

        thenPensionDetailsisSetAsExpected(circumstances.getPensions().getCurrent().get(0));
        thenPensionDetailsisSetAsExpected(circumstances.getPensions().getCurrent().get(1));
    }

    @Test
    public void GivenNoPostCode_ThenPensionDetailsAreResolvedCorrectly() {
        givenPensionDetailsHasSpacesNoPostCode();

        Circumstances circumstances = new Circumstances();
        circumstances.setPensions(new Pensions());
        testSubject.resolve(mockClaim, circumstances);

        thenPensionDetailsisSetWithoutPostcode(circumstances.getPensions().getCurrent().get(0));
        thenPensionDetailsisSetWithoutPostcode(circumstances.getPensions().getCurrent().get(1));
    }


    private void givenCurrentPensionsIsSetCorrectly() {
        for (int count = 1; count < 3; count++) {
            when(mockClaim.get(PensionIncreaseController.IDENTIFIER, count)).thenReturn(Optional.of(new BooleanQuestion(true)));

            PensionIncreaseMonthQuestion pensionIncreaseMonthQuestion = new PensionIncreaseMonthQuestion();
            pensionIncreaseMonthQuestion.setUserSelectionValue(Months.DECEMBER);
            when(mockClaim.get(PensionIncreaseDateController.IDENTIFIER, count)).thenReturn(Optional.of(pensionIncreaseMonthQuestion));

            PaymentFrequencyQuestion paymentFrequencyQuestion = new PaymentFrequencyQuestion();
            paymentFrequencyQuestion.setPaymentFrequency(PaymentFrequency.MONTHLY);
            paymentFrequencyQuestion.setMonthlyPaymentAmounts(new PaymentAmounts(NET_PAY));
            when(mockClaim.get(PensionPaymentFrequencyController.IDENTIFIER, count)).thenReturn(Optional.of(paymentFrequencyQuestion));

            when(mockClaim.get(ProviderAddressController.IDENTIFIER, count)).thenReturn(Optional.of(PROVIDERS_ADDRESS_QUESTION));

            when(mockClaim.get(ProviderNameController.IDENTIFIER, count)).thenReturn(Optional.of(new StringQuestion("Aviva")));
        }
    }

    private void givenPensionDetailsHasSpacesInPostcode() {
        for (int count = 1; count < 3; count++) {
            when(mockClaim.get(PensionIncreaseController.IDENTIFIER, count)).thenReturn(Optional.of(new BooleanQuestion(true)));

            PensionIncreaseMonthQuestion pensionIncreaseMonthQuestion = new PensionIncreaseMonthQuestion();
            pensionIncreaseMonthQuestion.setUserSelectionValue(Months.DECEMBER);
            when(mockClaim.get(PensionIncreaseDateController.IDENTIFIER, count)).thenReturn(Optional.of(pensionIncreaseMonthQuestion));

            PaymentFrequencyQuestion paymentFrequencyQuestion = new PaymentFrequencyQuestion();
            paymentFrequencyQuestion.setPaymentFrequency(PaymentFrequency.MONTHLY);
            paymentFrequencyQuestion.setMonthlyPaymentAmounts(new PaymentAmounts(NET_PAY));
            when(mockClaim.get(PensionPaymentFrequencyController.IDENTIFIER, count)).thenReturn(Optional.of(paymentFrequencyQuestion));

            when(mockClaim.get(ProviderAddressController.IDENTIFIER, count)).thenReturn(Optional.of(PROVIDERS_ADDRESS_QUESTION_WITH_SPACES));

            when(mockClaim.get(ProviderNameController.IDENTIFIER, count)).thenReturn(Optional.of(new StringQuestion("Aviva")));
        }
    }

    private void givenPensionDetailsHasSpacesNoPostCode() {
        for (int count = 1; count < 3; count++) {
            when(mockClaim.get(PensionIncreaseController.IDENTIFIER, count)).thenReturn(Optional.of(new BooleanQuestion(true)));

            PensionIncreaseMonthQuestion pensionIncreaseMonthQuestion = new PensionIncreaseMonthQuestion();
            pensionIncreaseMonthQuestion.setUserSelectionValue(Months.DECEMBER);
            when(mockClaim.get(PensionIncreaseDateController.IDENTIFIER, count)).thenReturn(Optional.of(pensionIncreaseMonthQuestion));

            PaymentFrequencyQuestion paymentFrequencyQuestion = new PaymentFrequencyQuestion();
            paymentFrequencyQuestion.setPaymentFrequency(PaymentFrequency.MONTHLY);
            paymentFrequencyQuestion.setMonthlyPaymentAmounts(new PaymentAmounts(NET_PAY));
            when(mockClaim.get(PensionPaymentFrequencyController.IDENTIFIER, count)).thenReturn(Optional.of(paymentFrequencyQuestion));

            when(mockClaim.get(ProviderAddressController.IDENTIFIER, count)).thenReturn(Optional.of(PROVIDERS_ADDRESS_QUESTION_NO_POSTCODE));

            when(mockClaim.get(ProviderNameController.IDENTIFIER, count)).thenReturn(Optional.of(new StringQuestion("Aviva")));
        }
    }

    private void thenPensionDetailsisSetAsExpected(final PensionDetail pensionDetail) {
        assertThat(pensionDetail.getProviderName(), is("Aviva"));
        assertThat(pensionDetail.getPaymentFrequency(), is(PaymentFrequency.MONTHLY.name()));
        assertThat(pensionDetail.getGrossPay(), is(NET_PAY));
        assertThat(pensionDetail.getPensionIncreaseMonth(), is(Months.DECEMBER.name()));
        assertThat(pensionDetail.getProviderAddress().getFirstLine(), is(PROVIDERS_ADDRESS_QUESTION.getAddressLine1()));
        assertThat(pensionDetail.getProviderAddress().getSecondLine(), is(PROVIDERS_ADDRESS_QUESTION.getAddressLine2()));
        assertThat(pensionDetail.getProviderAddress().getTown(), is(PROVIDERS_ADDRESS_QUESTION.getTownOrCity()));
        assertThat(pensionDetail.getProviderAddress().getPostCode(), is(PROVIDERS_ADDRESS_QUESTION.getPostCode()));
    }

    private void thenPensionDetailsisSetWithoutPostcode(final PensionDetail pensionDetail) {
        assertThat(pensionDetail.getProviderName(), is("Aviva"));
        assertThat(pensionDetail.getPaymentFrequency(), is(PaymentFrequency.MONTHLY.name()));
        assertThat(pensionDetail.getGrossPay(), is(NET_PAY));
        assertThat(pensionDetail.getPensionIncreaseMonth(), is(Months.DECEMBER.name()));
        assertThat(pensionDetail.getProviderAddress().getFirstLine(), is(PROVIDERS_ADDRESS_QUESTION_NO_POSTCODE.getAddressLine1()));
        assertThat(pensionDetail.getProviderAddress().getSecondLine(), is(PROVIDERS_ADDRESS_QUESTION_NO_POSTCODE.getAddressLine2()));
        assertThat(pensionDetail.getProviderAddress().getTown(), is(PROVIDERS_ADDRESS_QUESTION_NO_POSTCODE.getTownOrCity()));
        assertThat(pensionDetail.getProviderAddress().getPostCode(), is(PROVIDERS_ADDRESS_QUESTION_NO_POSTCODE.getPostCode()));
    }


}
