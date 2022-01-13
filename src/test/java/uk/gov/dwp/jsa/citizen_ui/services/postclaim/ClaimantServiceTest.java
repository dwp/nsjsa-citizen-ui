package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutPostalAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.AboutPostalController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.DateOfBirthFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.LanguagePreferenceController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.NinoController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.PersonalDetailsFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.ClaimantPhoneController;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.contactpreferences.EmailController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.*;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.TitleEnum.MR;
import static uk.gov.dwp.jsa.citizen_ui.services.postclaim.CircumstancesTest.VERSION;

@RunWith(MockitoJUnitRunner.class)
public class ClaimantServiceTest {

    private static final String NINO = "SL709883V";
    private static final String FIRST_LINE = "32";
    private static final String SECOND_LINE = "Grange Avenue";
    private static final String TOWN = "Manchester";
    private static final String POSTCODE = "M29 3FY";
    private static final String FORENAME = "Anthony";
    private static final String SURNAME = "Biganza";
    private static final String EMAIL_ADDRESS = "a@a.com";
    private static final String CONTACT_NUMBER = "07580043131";
    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private Claim mockClaim;

    private ClaimantService claimantService;

    private static final UUID CLAIM_ID = UUID.randomUUID();

    @Before
    public void setUp() {
        when(mockClaimRepository.findById(CLAIM_ID.toString())).thenReturn(of(mockClaim));
        when(mockClaim.getClaimantId()).thenReturn(CLAIMANT_ID.toString());
        claimantService = new ClaimantService(mockClaimRepository, VERSION, false, null);
    }

    @Test
    public void getDataFromClaimReturnsEmptyIfNotPresent() {
        when(mockClaimRepository.findById(CLAIM_ID.toString())).thenReturn(Optional.empty());

        Optional<Claimant> dataFromClaimOptional = claimantService.getDataFromClaim(CLAIM_ID);

        assertThat(dataFromClaimOptional.isPresent(), is(false));

    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptsDataSuccessfully() {
        givenDataForClaimIsSetup();

        Optional<Claimant> dataFromClaimOptional = claimantService.getDataFromClaim(CLAIM_ID);
        Claimant dataFromClaim = dataFromClaimOptional.get();

        assertThat(dataFromClaim.getClaimantId(), is(CLAIMANT_ID));
        assertThat(dataFromClaim.getNino(), is(NINO));
        assertThat(dataFromClaim.getName().getTitle(), is(MR.name()));
        assertThat(dataFromClaim.getName().getFirstName(), is(FORENAME));
        assertThat(dataFromClaim.getName().getLastName(), is(SURNAME));
        assertThat(dataFromClaim.getDateOfBirth(), is(LocalDate.of(2018, 1, 1)));
        assertThat(dataFromClaim.getContactDetails().getEmail(), is(EMAIL_ADDRESS));
        assertThat(dataFromClaim.getContactDetails().getNumber(), is(CONTACT_NUMBER));

        thenAddressIsAsExpected(dataFromClaim);

        thenPostalAddressIsAsExpected(dataFromClaim);
    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptsNullEmailAsExpected() {

        Optional<Claimant> dataFromClaimOptional = claimantService.getDataFromClaim(CLAIM_ID);
        Claimant dataFromClaim = dataFromClaimOptional.get();


        assertThat(dataFromClaim.getContactDetails().getEmail(), CoreMatchers.nullValue());
    }

    @Test
    public void getDataFromClaimRetrievesLanguagePreferencesDataAsExpected() {
        LanguagePreferenceQuestion languagePreferenceQuestion = new LanguagePreferenceQuestion();
        languagePreferenceQuestion.setWelshContact(true);
        languagePreferenceQuestion.setWelshSpeech(true);

        when(mockClaim.get(LanguagePreferenceController.IDENTIFIER)).thenReturn(of(languagePreferenceQuestion));

        Optional<Claimant> dataFromClaimOptional = claimantService.getDataFromClaim(CLAIM_ID);
        Claimant dataFromClaim = dataFromClaimOptional.get();

        assertThat(dataFromClaim.getLanguagePreference().getWelshSpeech(), is(true));
        assertThat(dataFromClaim.getLanguagePreference().getWelshContact(), is(true));
    }

    private void thenPostalAddressIsAsExpected(final Claimant dataFromClaim) {
        assertThat(dataFromClaim.getPostalAddress().getFirstLine(), is(FIRST_LINE));
        assertThat(dataFromClaim.getPostalAddress().getSecondLine(), is(SECOND_LINE));
        assertThat(dataFromClaim.getPostalAddress().getTown(), is(TOWN));
        assertThat(dataFromClaim.getPostalAddress().getPostCode(), is(POSTCODE));
    }

    private void thenAddressIsAsExpected(final Claimant dataFromClaim) {
        assertThat(dataFromClaim.getAddress().getFirstLine(), is(FIRST_LINE));
        assertThat(dataFromClaim.getAddress().getSecondLine(), is(SECOND_LINE));
        assertThat(dataFromClaim.getAddress().getTown(), is(TOWN));
        assertThat(dataFromClaim.getAddress().getPostCode(), is(POSTCODE));
    }


    private void givenDataForClaimIsSetup() {
        when(mockClaim.get(NinoController.IDENTIFIER)).thenReturn(of(new NinoQuestion(NINO)));

        setUpAddress();

        setUpPersonalDetails();

        setUpDateOfBirth();

        setUpEmail();

        when(mockClaim.get(ClaimantPhoneController.IDENTIFIER)).thenReturn(of(new PhoneQuestion(CONTACT_NUMBER, true)));
    }

    private void setUpEmail() {
        when(mockClaim.get(EmailController.IDENTIFIER)).thenReturn(of(new EmailStringQuestion(EMAIL_ADDRESS, true)));
    }

    private void setUpDateOfBirth() {
        DateOfBirthQuestion dateOfBirthQuestion = new DateOfBirthQuestion();
        dateOfBirthQuestion.setDay(1);
        dateOfBirthQuestion.setMonth(1);
        dateOfBirthQuestion.setYear(2018);
        when(mockClaim.get(DateOfBirthFormController.IDENTIFIER)).thenReturn(of(dateOfBirthQuestion));
    }

    private void setUpPersonalDetails() {
        PersonalDetailsQuestion personalDetailQuestion = new PersonalDetailsQuestion();
        NameStringTruncatedQuestion foreName = new NameStringTruncatedQuestion();
        foreName.setValue(FORENAME);
        NameStringTruncatedQuestion surName = new NameStringTruncatedQuestion();
        surName.setValue(SURNAME);
        personalDetailQuestion.setFirstNameQuestion(foreName);
        personalDetailQuestion.setLastNameQuestion(surName);
        TitleQuestion titleQuestion = new TitleQuestion();
        titleQuestion.setUserSelectionValue(MR);
        personalDetailQuestion.setTitleQuestion(titleQuestion);
        when(mockClaim.get(PersonalDetailsFormController.IDENTIFIER)).thenReturn(of(personalDetailQuestion));
    }

    private void setUpAddress() {
        AddressQuestion addressQuestion = new AddressQuestion();
        addressQuestion.setAddressLine1(FIRST_LINE);
        addressQuestion.setAddressLine2(SECOND_LINE);
        addressQuestion.setTownOrCity(TOWN);
        addressQuestion.setPostCode(POSTCODE);
        when(mockClaim.get(AboutAddressController.IDENTIFIER)).thenReturn(of(addressQuestion));
        when(mockClaim.get(AboutPostalController.IDENTIFIER)).thenReturn(of(new BooleanQuestion(true)));
        when(mockClaim.get(AboutPostalAddressController.IDENTIFIER)).thenReturn(of(addressQuestion));
    }
}
