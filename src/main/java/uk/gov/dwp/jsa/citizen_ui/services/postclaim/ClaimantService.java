package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.dto.claim.Address;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.adaptors.dto.claim.ContactDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.LanguagePreference;
import uk.gov.dwp.jsa.adaptors.dto.claim.Name;
import uk.gov.dwp.jsa.adaptors.enums.ClaimType;
import uk.gov.dwp.jsa.citizen_ui.Constants;
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
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.LanguagePreferenceQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.PersonalDetailsQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimDBRepository;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static uk.gov.dwp.jsa.citizen_ui.Constants.DATA_VERSION_KEY;

@Service
public class ClaimantService {

    private final String appVersion;
    private ClaimRepository claimRepository;
    private final boolean agentMode;
    private final ClaimDBRepository claimDBRepository;

    private QuestionValueExtractor questionValueExtractor = new QuestionValueExtractor();

    public ClaimantService(final ClaimRepository claimRepository,
                           @Value("${" + DATA_VERSION_KEY + "}") final String appVersion,
                           @Value("${" + Constants.AGENT_MODE + "}") final boolean agentMode,
                           final ClaimDBRepository claimDBRepository) {
        this.claimRepository = claimRepository;
        this.appVersion = appVersion;
        this.agentMode = agentMode;
        this.claimDBRepository = claimDBRepository;
    }

    public Optional<Claimant> getDataFromClaim(final UUID claimId) {
        Claim claim = claimRepository.findById(claimId.toString()).orElse(null);
        if (claim != null) {
            Claimant claimantData = new Claimant();
            final String claimantId = claim.getClaimantId();
            if (null != claimantId) {
                claimantData.setClaimantId(UUID.fromString(claimantId));
            }
            claimantData.setServiceVersion(appVersion);

            claimantData.setDateOfClaim(getDateOfClaim(claim.getClaimantId(), claim.getClaimType()));

            String ninoValue = questionValueExtractor.getStringQuestionValueWithIdentifier(claim,
                    NinoController.IDENTIFIER, 0);
            claimantData.setNino(ninoValue);

            setPersonalDetailsQuestion(claim, claimantData);

            claimantData.setDateOfBirth(getDateOfBirthQuestionValueWithIdentifier(claim));

            claimantData.setAddress(getAddressQuestionValueWithIdentifier(claim,
                    AboutAddressController.IDENTIFIER));

            setLanguagePreference(claim, claimantData);

            boolean hasPostalAddress = questionValueExtractor.getBooleanValueWithIdentifier(
                    claim, AboutPostalController.IDENTIFIER, 0
            );
            if (hasPostalAddress) {
                claimantData.setPostalAddress(getAddressQuestionValueWithIdentifier(claim,
                        AboutPostalAddressController.IDENTIFIER));
            }

            getContactDetails(claim, claimantData);

            return Optional.of(claimantData);
        }
        return empty();
    }

    private LocalDate getDateOfClaim(final String claimantId, final ClaimType claimType) {
        if (agentMode && ClaimType.EDIT_CLAIM.equals(claimType)) {
            return claimDBRepository.findById(claimantId)
                    .map(c -> c.getClaimant().getDateOfClaim()).orElse(null);
        } else {
            return LocalDate.now();
        }
    }

    private void getContactDetails(final Claim claim, final Claimant claimantData) {
        ContactDetails contactDetails = new ContactDetails();
        Optional<Question> phoneQuestionOptional = claim.get(ClaimantPhoneController.IDENTIFIER);
        if (phoneQuestionOptional.isPresent()) {
            PhoneQuestion phoneQuestion =
                    (PhoneQuestion) phoneQuestionOptional.get();
            contactDetails.setNumberProvided(phoneQuestion.getHasProvidedPhoneNumber());
            contactDetails.setNumber(phoneQuestion.getPhoneNumber());
        }
        Optional<Question> emailQuestionOptional = claim.get(EmailController.IDENTIFIER);
        if (emailQuestionOptional.isPresent()) {
            EmailStringQuestion emailStringQuestion =
                    (EmailStringQuestion) emailQuestionOptional.get();
            contactDetails.setEmailProvided(emailStringQuestion.getHasProvidedEmail());
            contactDetails.setEmail(emailStringQuestion.getEmail());
        }
        claimantData.setContactDetails(contactDetails);
    }

    private LocalDate getDateOfBirthQuestionValueWithIdentifier(final Claim claim) {
        Optional<Question> question = claim.get(DateOfBirthFormController.IDENTIFIER);
        if (question.isPresent() && question.get() instanceof DateOfBirthQuestion) {
            DateOfBirthQuestion dobQuestion = (DateOfBirthQuestion) question.get();
            return LocalDate.of(dobQuestion.getYear(), dobQuestion.getMonth(), dobQuestion.getDay());
        } else {
            return null;
        }
    }

    private Address getAddressQuestionValueWithIdentifier(final Claim claim, final String identifier) {
        Address address = new Address();
        Optional<Question> question = claim.get(identifier);
        if (question.isPresent() && question.get() instanceof AddressQuestion) {
            AddressQuestion addressQuestion = (AddressQuestion) question.get();
            address.setFirstLine(addressQuestion.getAddressLine1());
            address.setSecondLine(addressQuestion.getAddressLine2());
            address.setTown(addressQuestion.getTownOrCity());
            address.setPostCode(addressQuestion.getPostCode());
        }
        return address;
    }

    private void setPersonalDetailsQuestion(final Claim claim, final Claimant claimantData) {
        Optional<Question> question = claim.get(PersonalDetailsFormController.IDENTIFIER);
        if (question.isPresent() && question.get() instanceof PersonalDetailsQuestion) {
            PersonalDetailsQuestion personalDetails = (PersonalDetailsQuestion) question.get();
            Name name = new Name(personalDetails.getTitleQuestion().getUserSelectionValue().name(),
                    personalDetails.getFirstNameQuestion().getValue(),
                    personalDetails.getLastNameQuestion().getValue());
            claimantData.setName(name);
        }
    }

    private void setLanguagePreference(final Claim claim, final Claimant claimantData) {
    Optional<Question> languagePreferenceQuestionOptional = claim.get(LanguagePreferenceController.IDENTIFIER);
    if (languagePreferenceQuestionOptional.isPresent()) {
        LanguagePreferenceQuestion languagePreferenceQuestion =
                (LanguagePreferenceQuestion) languagePreferenceQuestionOptional.get();

        LanguagePreference languagePreference = new LanguagePreference();
        languagePreference.setWelshContact(languagePreferenceQuestion.getWelshContact());
        languagePreference.setWelshSpeech(languagePreferenceQuestion.getWelshSpeech());
        claimantData.setLanguagePreference(languagePreference);
    }
}

}
