package uk.gov.dwp.jsa.citizen_ui.resolvers.pensions;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasAnotherCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.resolvers.Resolver;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.util.Arrays;
import java.util.List;

@Component
public class HasExtraPensionResolver implements Resolver {
    private final QuestionValueExtractor extractor = new QuestionValueExtractor();

    private static final List<String> HAS_PENSIONS_IDENTIFIERS = Arrays.asList(
            HasAnotherCurrentPensionController.IDENTIFIER
    );

    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {
        circumstances.getPensions().setHasExtraPensions(HAS_PENSIONS_IDENTIFIERS.stream()
                .filter(id -> extractor.getLoopEndQuestionValue(claim, id))
                .count() > 0);
    }
}
