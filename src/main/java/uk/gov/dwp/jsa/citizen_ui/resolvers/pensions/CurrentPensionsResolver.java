package uk.gov.dwp.jsa.citizen_ui.resolvers.pensions;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PensionDetail;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.PensionIncreaseController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.PensionIncreaseDateController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.PensionPaymentFrequencyController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderAddressController;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderNameController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.ProvidersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.pensions.PensionIncreaseMonthQuestion;
import uk.gov.dwp.jsa.citizen_ui.resolvers.Resolver;
import uk.gov.dwp.jsa.citizen_ui.util.QuestionValueExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
public class CurrentPensionsResolver implements Resolver {
    private final QuestionValueExtractor extractor = new QuestionValueExtractor();

    private static final List<String> CURRENT_PENSIONS_IDENTIFIERS = Arrays.asList(
            PensionIncreaseController.IDENTIFIER,
            PensionIncreaseDateController.IDENTIFIER,
            PensionPaymentFrequencyController.IDENTIFIER,
            ProviderAddressController.IDENTIFIER,
            ProviderNameController.IDENTIFIER
    );

    @Override
    public void resolve(final Claim claim, final Circumstances circumstances) {
        final List<PensionDetail> currentPensions = new ArrayList<>();

        IntStream.rangeClosed(1, Constants.MAX_PENSIONS_ALLOWED).forEach(counter -> {
            final PensionDetail pensionDetail = new PensionDetail();
            final boolean hasFilledAPensionDetail = fillAllCurrentPensionQuestions(claim, counter, pensionDetail);
            if (hasFilledAPensionDetail) {
                currentPensions.add(pensionDetail);
            }
        });

        circumstances.getPensions().setCurrent(currentPensions);
    }

    private boolean fillAllCurrentPensionQuestions(
            final Claim claim,
            final int counter,
            final PensionDetail pensionDetail
    ) {
        boolean hasFilledAPensionDetail = false;
        for (String identifier : CURRENT_PENSIONS_IDENTIFIERS) {
            Optional<Question> question = claim.get(identifier, counter);
            if (question.isPresent()) {
                hasFilledAPensionDetail = true;
                fill(claim, identifier, question.get(), pensionDetail, counter);
            }
        }
        return hasFilledAPensionDetail;
    }

    private void fill(
            final Claim claim,
            final String id,
            final Question question,
            final PensionDetail pensionDetail,
            final int counter
    ) {
        switch (id) {
            case PensionIncreaseController.IDENTIFIER:
                if (question instanceof BooleanQuestion) {
                    pensionDetail.setHasPeriodicIncrease(((BooleanQuestion) question).getChoice());
                }
                break;
            case PensionIncreaseDateController.IDENTIFIER:
                if (question instanceof PensionIncreaseMonthQuestion) {
                    pensionDetail.setPensionIncreaseMonth(
                            ((PensionIncreaseMonthQuestion) question).getUserSelectionValue().toString());
                }
                break;
            case PensionPaymentFrequencyController.IDENTIFIER:
                if (question instanceof PaymentFrequencyQuestion) {
                    pensionDetail.setPaymentFrequency(
                            ((PaymentFrequencyQuestion) question).getPaymentFrequency().toString()
                    );
                    ((PaymentFrequencyQuestion) question).getSelectedPaymentAmounts()
                            .ifPresent(payment -> pensionDetail.setGrossPay(payment.getNet()));
                }
                break;
            case ProviderAddressController.IDENTIFIER:
                if (question instanceof ProvidersAddressQuestion) {
                    pensionDetail.setProviderAddress(extractor.extractProviderAddress(claim, id, counter));
                }
                break;
            case ProviderNameController.IDENTIFIER:
                if (question instanceof StringQuestion) {
                    pensionDetail.setProviderName(((StringQuestion) question).getValue());
                }
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unsupported controller with identifier %s", id));
        }
    }
}
