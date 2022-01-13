package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.OtherBenefit;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Pensions;
import uk.gov.dwp.jsa.adaptors.enums.ClaimType;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.DeclarationController;
import uk.gov.dwp.jsa.citizen_ui.controller.otherbenefits.OtherBenefitDetailsController;
import uk.gov.dwp.jsa.citizen_ui.controller.outsidework.HasOutsideWorkController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.DeclarationQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimDBRepository;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.resolvers.Resolver;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thymeleaf.util.StringUtils.isEmpty;
import static uk.gov.dwp.jsa.citizen_ui.Constants.DATA_VERSION_KEY;

@Service
public class CircumstancesService {

    private final ClaimRepository claimRepository;
    private final ClaimDBRepository claimDBRepository;
    private final List<Resolver> resolversList;
    private final String appVersion;
    private final boolean agentMode;

    private QuestionValueExtractor extractor = new QuestionValueExtractor();

    @Autowired
    public CircumstancesService(final ClaimRepository claimRepo,
                                final ClaimDBRepository claimDBRepository,
                                final List<Resolver> resolvers,
                                @Value("${" + DATA_VERSION_KEY + "}") final String appVersion,
                                @Value("${" + Constants.AGENT_MODE + "}") final boolean agentMode) {
        this.claimDBRepository = claimDBRepository;
        this.resolversList = resolvers;
        this.claimRepository = claimRepo;
        this.appVersion = appVersion;
        this.agentMode = agentMode;
    }

    public Optional<Circumstances> getDataFromClaim(final UUID claimId) {
        Claim claim = claimRepository.findById(claimId.toString()).orElse(null);
        if (claim != null) {
            Circumstances circumstances = new Circumstances();
            final String claimantId = claim.getClaimantId();
            if (null != claimantId) {
                circumstances.setClaimantId(UUID.fromString(claimantId));
            }

            circumstances.setServiceVersion(appVersion);

            circumstances.setDateOfClaim(getDateOfClaim(UUID.fromString(claim.getClaimantId()), claim.getClaimType()));

            Optional<Question> declarationQuestionOptional = claim.get(DeclarationController.IDENTIFIER);
            if (declarationQuestionOptional.isPresent()) {
            DeclarationQuestion declarationQuestion = (DeclarationQuestion) declarationQuestionOptional.get();
                circumstances.setDeclarationAgreed(
                        declarationQuestion.isAgreed()
                );
                circumstances.setLocale(declarationQuestion.getLocale());
            }
            circumstances.setHasNonUKWorkBenefit(
                    extractor.getBooleanValueWithIdentifier(claim,
                            HasOutsideWorkController.IDENTIFIER, 0)
            );

            setOtherBenefits(claim, circumstances);

            circumstances.setPensions(new Pensions());

            resolversList.forEach(resolver -> {
                resolver.resolve(claim, circumstances);
            });

            return Optional.of(circumstances);

        }
        return Optional.empty();
    }

    private LocalDate getDateOfClaim(final UUID claimantId, final ClaimType claimType) {
        if (agentMode && ClaimType.EDIT_CLAIM.equals(claimType)) {
            return claimDBRepository.findById(claimantId.toString())
                    .map(c -> c.getClaimant().getDateOfClaim()).orElse(null);
        } else {
            return LocalDate.now();
        }
    }

    private void setOtherBenefits(final Claim claim, final Circumstances circumstances) {
        String otherBenefits = extractor.getStringQuestionValueWithIdentifier(claim,
                OtherBenefitDetailsController.IDENTIFIER, 0);
        if (!isEmpty(otherBenefits)) {
            circumstances.setOtherBenefit(new OtherBenefit(otherBenefits));
        }
    }

}
