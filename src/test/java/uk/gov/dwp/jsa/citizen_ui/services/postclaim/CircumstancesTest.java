package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.DeclarationController;
import uk.gov.dwp.jsa.citizen_ui.controller.availability.AttendInterviewController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.ClaimStartDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.claimstart.juryservice.JuryServiceDatesController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.HasAnotherCurrentJobController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.EmployersNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.currentwork.details.*;
import uk.gov.dwp.jsa.citizen_ui.controller.education.*;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitDetailsController;
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.*;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.AddWorkController;
import uk.gov.dwp.jsa.citizen_ui.controller.previousemployment.employerdetails.*;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.AttendInterviewQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Reason;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.*;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.HoursQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.TypeOfWork;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.Months;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMonthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.EmploymentDurationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.resolvers.*;
import uk.gov.dwp.jsa.citizen_ui.resolvers.employment.CurrentWorkResolver;
import uk.gov.dwp.jsa.citizen_ui.resolvers.employment.PreviousWorkResolver;
import uk.gov.dwp.jsa.citizen_ui.resolvers.pensions.CurrentPensionsResolver;
import uk.gov.dwp.jsa.citizen_ui.resolvers.pensions.HasExtraPensionResolver;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.resolvers.CurrentPensionsResolverTest.NET_PAY;
import static uk.gov.dwp.jsa.citizen_ui.resolvers.CurrentPensionsResolverTest.PROVIDERS_ADDRESS_QUESTION;
import static uk.gov.dwp.jsa.citizen_ui.resolvers.EducationResolverTest.*;

@RunWith(MockitoJUnitRunner.class)
public class CircumstancesTest {

    public static final String OTHER_BENEFITS = "Other benefits";
    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private Claim mockClaim;
    private static final UUID COOKIE = UUID.randomUUID();
    private CircumstancesService circumstancesService;
    private CircumstancesServiceTest helperTest = new CircumstancesServiceTest();

    public static final String VERSION = "V1";

    @Before
    public void setUp() {
        when(mockClaimRepository.findById(COOKIE.toString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.getClaimantId()).thenReturn(UUID.randomUUID().toString());
        circumstancesService = new CircumstancesService(mockClaimRepository, null, asList(new PreviousWorkResolver(),
                new CurrentWorkResolver(), new AvailabilityResolver(), new ClaimStartDateResolver(),
                new CurrentPensionsResolver(), new EducationResolver(),
                new HasExtraPensionResolver(), new JuryDatesResolver()),
                VERSION, false);
    }

    @Test
    public void circumstancesModelsIsAdaptedSuccessfullyFromClaimObj() throws Exception {
        givenClaimObjectIsSet();

        Optional<Circumstances> dataFromClaimOptional = circumstancesService.getDataFromClaim(COOKIE);

        String requestJson = whenDataIsAdapted(dataFromClaimOptional);
        JSONObject jsonObjectActual = new JSONObject(requestJson);

        thenJsonHasAllNecessarySections(jsonObjectActual);
    }

    private void thenJsonHasAllNecessarySections(final JSONObject jsonObjectActual) throws JSONException {
        assertThat(jsonObjectActual.get("pensions"), notNullValue());
        assertThat(jsonObjectActual.get("previousWork"), notNullValue());
        assertThat(jsonObjectActual.get("currentWork"), notNullValue());
        assertThat(jsonObjectActual.get("hasExtraCurrentWork"), notNullValue());
        assertThat(jsonObjectActual.get("hasExtraPreviousWork"), notNullValue());
        assertThat(jsonObjectActual.get("education"), notNullValue());
        assertThat(jsonObjectActual.get("juryService"), notNullValue());
        assertThat(jsonObjectActual.get("hasNonUKWorkBenefit"), notNullValue());
        assertThat(jsonObjectActual.get("availableForInterview"), notNullValue());
    }

    private JSONObject getExpectedJsonObject() throws IOException, JSONException {
        File file = new File(ClassLoader.getSystemResource("json/circumstances.json").getFile());
        String content = new String(Files.readAllBytes(file.toPath()));
        return new JSONObject(content);
    }

    private String whenDataIsAdapted(Optional<Circumstances> dataFromClaimOptional) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.setDateFormat(new StdDateFormat());
        return objectMapper.writeValueAsString(dataFromClaimOptional.get());
    }

    private void givenClaimObjectIsSet() {
        DeclarationQuestion declarationQuestion = new DeclarationQuestion(true);
        declarationQuestion.setLocale(Locale.ENGLISH.getLanguage());
        when(mockClaim.get(DeclarationController.IDENTIFIER)).thenReturn(Optional.of(declarationQuestion));
        givenClaimStartDateIsSet();

        when(mockClaim.get(HasOutsideWorkController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(true)));

        when(mockClaim.get(OtherBenefitDetailsController.IDENTIFIER)).thenReturn(Optional.of(new StringQuestion(OTHER_BENEFITS)));

        givenCurrentWorkIsSet();

        givenPreviousWorkIsSet();

        givenAvailabilityIsSet();

        givenCurrentPensionsIsSetCorrectly();

        givenEducationIsSetCorrectly();

        when(mockClaim.get(JuryServiceConfirmationController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(true)));
        when(mockClaim.get(JuryServiceDatesController.IDENTIFIER)).thenReturn(Optional.of(JuryDatesResolverTest.JURY_DATE_QUESTION));
    }

    private void givenEducationIsSetCorrectly() {
        when(mockClaim.get(EducationConfirmationController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(true)));
        when(mockClaim.get(EducationPlaceController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_PLACE));
        when(mockClaim.get(EducationCourseDurationController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_DATERANGE));
        when(mockClaim.get(EducationCourseNameController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_COURSE_NAME));
        when(mockClaim.get(EducationCourseHoursController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_HOURS));
    }

    private void givenClaimStartDateIsSet() {
        ClaimStartDateQuestion startDateQuestion = new ClaimStartDateQuestion();
        startDateQuestion.setDay(22);
        startDateQuestion.setMonth(10);
        startDateQuestion.setYear(2018);
        when(mockClaim.get(ClaimStartDateController.IDENTIFIER)).thenReturn(Optional.of(startDateQuestion));
    }

    private void givenCurrentWorkIsSet() {
        for (int count = 1; count <= Constants.MAX_JOBS_ALLOWED; count++) {
            TypeOfWorkQuestion typeOfWorkQuestion = new TypeOfWorkQuestion();
            typeOfWorkQuestion.setUserSelectionValue(TypeOfWork.PAID);
            givenQuestionIsSetForIdentifier(typeOfWorkQuestion, WorkPaidOrVoluntaryController.IDENTIFIER, count);

            PaymentFrequencyQuestion paymentFrequencyQuestion = new PaymentFrequencyQuestion();
            paymentFrequencyQuestion.setPaymentFrequency(PaymentFrequency.MONTHLY);
            paymentFrequencyQuestion.setMonthlyPaymentAmounts(new PaymentAmounts(new BigDecimal(123.45)));
            givenQuestionIsSetForIdentifier(paymentFrequencyQuestion, PaymentFrequencyController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(new StringQuestion("Valtech"), EmployersNameController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(helperTest.getEmployersAddressQuestion(), CurrentWorkAddressController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(new HoursQuestion(23), HoursController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(new BooleanQuestion(true), EmploymentStatusController.IDENTIFIER, count);
        }
        givenQuestionIsSetForIdentifier(new LoopEndBooleanQuestion(true, true), HasAnotherCurrentJobController.IDENTIFIER, 0);
    }

    private void givenPreviousWorkIsSet() {

        for (int count = 1; count <= Constants.MAX_JOBS_ALLOWED; count++) {

            EmploymentDurationQuestion employmentDatesQuestion = new EmploymentDurationQuestion();
            DateQuestion startDateQuestion = new DateQuestion(1, 7, 2018);
            DateQuestion endDateQuestion = new DateQuestion(3, 9, 2018);
            employmentDatesQuestion.setStartDate(startDateQuestion);
            employmentDatesQuestion.setEndDate(endDateQuestion);
            givenQuestionIsSetForIdentifier(employmentDatesQuestion, EmployersDatesController.IDENTIFIER, count);

            WhyJobEndQuestion whyJobEndQuestion = new WhyJobEndQuestion();
            whyJobEndQuestion.setWhyJobEndedReason(WhyJobEndedReason.OTHER);
            whyJobEndQuestion.setDetailedReason("Other reasons");
            givenQuestionIsSetForIdentifier(whyJobEndQuestion, EmployerWhyJobEndController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(new StringQuestion("Valtech"), uk.gov.dwp.jsa.citizen_ui.controller.
                    previousemployment.employerdetails.EmployersNameController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(helperTest.getEmployersAddressQuestion(), EmployersAddressController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(new BooleanQuestion(true), ExpectPaymentController.IDENTIFIER, count);

            givenQuestionIsSetForIdentifier(new BooleanQuestion(true), uk.gov.dwp.jsa.citizen_ui.controller.
                    previousemployment.employerdetails.EmploymentStatusController.IDENTIFIER, count);
        }
        givenQuestionIsSetForIdentifier(new LoopEndBooleanQuestion(true, true), AddWorkController.IDENTIFIER, 0);
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


    private void givenAvailabilityIsSet() {
        AttendInterviewQuestion attendInterviewQuestion = new AttendInterviewQuestion();
        List<Day> daysNotToAttend = new ArrayList<>();
        Reason morningReason = new Reason(true);
        Reason afterNoonReason = new Reason(true);
        daysNotToAttend.add(new Day(now(), morningReason, afterNoonReason));
        attendInterviewQuestion.setDaysNotToAttend(daysNotToAttend);
        when(mockClaim.get(AttendInterviewController.IDENTIFIER)).thenReturn(Optional.of(attendInterviewQuestion));
    }

    private void givenQuestionIsSetForIdentifier(final Question question, final String identifier, final int counter) {
        if (counter <= 0) {
            when(mockClaim.get(identifier)).thenReturn(Optional.of(question));
        } else {
            when(mockClaim.get(identifier, counter)).thenReturn(Optional.of(question));
        }
    }
}
