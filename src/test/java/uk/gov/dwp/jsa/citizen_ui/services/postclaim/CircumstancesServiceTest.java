package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.DeclarationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.CurrentWorkAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.HoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.PaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.SelfEmployedConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.WorkPaidOrVoluntaryController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployerWhyJobEndController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.EmployersDatesController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.ExpectPaymentController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.TypeOfWorkQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.EmploymentDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.resolvers.employment.CurrentWorkResolver;
import uk.gov.dwp.jsa.citizen_ui.resolvers.employment.PreviousWorkResolver;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDate.of;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.services.postclaim.CircumstancesTest.VERSION;

@RunWith(MockitoJUnitRunner.class)
public class CircumstancesServiceTest {

    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private Claim mockClaim;
    private final int counter = 1;
    private static final UUID COOKIE = UUID.randomUUID();
    private CircumstancesService circumstancesService;

    @Before
    public void setUp() {
        when(mockClaimRepository.findById(COOKIE.toString())).thenReturn(Optional.of(mockClaim));
        circumstancesService = new CircumstancesService(mockClaimRepository, null, asList(new PreviousWorkResolver(),
                new CurrentWorkResolver()),
                VERSION, false);
        DeclarationQuestion declarationQuestion = new DeclarationQuestion(true);
        declarationQuestion.setLocale(Locale.ENGLISH.getLanguage());
        when(mockClaim.get(DeclarationController.IDENTIFIER)).thenReturn(Optional.of(declarationQuestion));
        when(mockClaim.getClaimantId()).thenReturn(CLAIMANT_ID.toString());
    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptCurrentWorkDataSuccessfully() {

        givenCurrentWorkIsSet();

        Optional<Circumstances> dataFromClaimOptional = circumstancesService.getDataFromClaim(COOKIE);

        Circumstances circumstances = dataFromClaimOptional.get();

        thenCurrentWorkIsFetchedAsExpected(circumstances);
    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptDeclarationConfirmationSuccessfully() {
        Optional<Circumstances> dataFromClaimOptional = circumstancesService.getDataFromClaim(COOKIE);

        assertThat(dataFromClaimOptional.get().getClaimantId(), is(CLAIMANT_ID));
        assertThat(dataFromClaimOptional.get().isDeclarationAgreed(), is(true));
        assertThat(dataFromClaimOptional.get().getLocale(), is(Locale.ENGLISH.getLanguage()));
    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptPreviousWorkDataSuccessfully() {
        givenPreviousWorkIsSet();

        Optional<Circumstances> dataFromClaimOptional = circumstancesService.getDataFromClaim(COOKIE);

        Circumstances circumstances = dataFromClaimOptional.get();

        thenPreviousWorkIsFetchedAsExpected(circumstances);
    }

    private void thenCurrentWorkIsFetchedAsExpected(final Circumstances circumstances) {
        assertThat(circumstances.getCurrentWork().get(0).isPaid(), is(true));
        assertThat(circumstances.getCurrentWork().get(0).isVoluntary(), is(false));
        assertThat(circumstances.getCurrentWork().get(0).getHoursPerWeek(), is(23));
        assertThat(circumstances.getCurrentWork().get(0).getEmployerName(), is("Valtech"));
        assertThat(circumstances.getCurrentWork().get(0).getNetPay(), is(new BigDecimal(123.45)));
        assertThat(circumstances.getCurrentWork().get(0).getPaymentFrequency(), is("MONTHLY"));
        assertThat(circumstances.getCurrentWork().get(0).isSelfEmployedOrDirector(), is(true));
    }

    private void thenPreviousWorkIsFetchedAsExpected(final Circumstances circumstances) {
        assertThat(circumstances.getPreviousWork().get(0).getStartDate(), is(of(2018, 7, 1)));
        assertThat(circumstances.getPreviousWork().get(0).getEndDate(), is(of(2018, 9, 3)));
        assertThat(circumstances.getPreviousWork().get(0).getReasonEnded(), is(WhyJobEndedReason.OTHER.name()));
        assertThat(circumstances.getPreviousWork().get(0).getOtherReasonDetails(), is("Other reasons"));
        assertThat(circumstances.getPreviousWork().get(0).getEmployerName(), is("Valtech"));
        assertThat(circumstances.getPreviousWork().get(0).isPaymentExpected(), is(true));
        assertThat(circumstances.getPreviousWork().get(0).isSelfEmployedOrDirector(), is(true));
    }

    private void givenCurrentWorkIsSet() {
        TypeOfWorkQuestion typeOfWorkQuestion = new TypeOfWorkQuestion();
        typeOfWorkQuestion.setUserSelectionValue(TypeOfWork.PAID);
        givenQuestionIsSetForIdentifier(typeOfWorkQuestion, WorkPaidOrVoluntaryController.IDENTIFIER, counter);

        PaymentFrequencyQuestion paymentFrequencyQuestion = new PaymentFrequencyQuestion();
        paymentFrequencyQuestion.setPaymentFrequency(PaymentFrequency.MONTHLY);
        paymentFrequencyQuestion.setMonthlyPaymentAmounts(new PaymentAmounts(new BigDecimal(123.45)));
        givenQuestionIsSetForIdentifier(paymentFrequencyQuestion, PaymentFrequencyController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(new StringQuestion("Valtech"), EmployersNameController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(getEmployersAddressQuestion(), CurrentWorkAddressController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(new HoursQuestion(23), HoursController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(new BooleanQuestion(true), SelfEmployedConfirmationController.IDENTIFIER, counter);
    }

    private void givenPreviousWorkIsSet() {

        EmploymentDurationQuestion employmentDatesQuestion = new EmploymentDurationQuestion();
        DateQuestion startDateQuestion = new DateQuestion(1, 7, 2018);
        DateQuestion endDateQuestion = new DateQuestion(3, 9, 2018);
        employmentDatesQuestion.setStartDate(startDateQuestion);
        employmentDatesQuestion.setEndDate(endDateQuestion);
        givenQuestionIsSetForIdentifier(employmentDatesQuestion, EmployersDatesController.IDENTIFIER, counter);

        WhyJobEndQuestion whyJobEndQuestion = new WhyJobEndQuestion();
        whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.OTHER);
        whyJobEndQuestion.setDetailedReason("Other reasons");
        givenQuestionIsSetForIdentifier(whyJobEndQuestion, EmployerWhyJobEndController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(new StringQuestion("Valtech"), uk.gov.dwp.jsa.citizen_ui.controller.
                previousemployment.employerdetails.EmployersNameController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(getEmployersAddressQuestion(), EmployersAddressController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(new BooleanQuestion(true), ExpectPaymentController.IDENTIFIER, counter);

        givenQuestionIsSetForIdentifier(new BooleanQuestion(true), uk.gov.dwp.jsa.citizen_ui.controller.
                previousemployment.employerdetails.EmploymentStatusController.IDENTIFIER, counter);
    }

    protected EmployersAddressQuestion getEmployersAddressQuestion() {
        return new EmployersAddressQuestion("32 Basil Chambers",
                "Arndale",
                "Manchester",
                "M28 3UB"
        );
    }

    private void givenQuestionIsSetForIdentifier(final Question question, final String identifier, final int counter) {
        when(mockClaim.get(identifier, counter)).thenReturn(Optional.of(question));
    }

    private void givenQuestionIsSetForIdentifier(final Question question, final String identifier) {
        when(mockClaim.get(identifier)).thenReturn(Optional.of(question));
    }
}
