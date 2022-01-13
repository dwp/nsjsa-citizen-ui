package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EmploymentPaymentFrequencyConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class EmploymentPaymentFrequencyValidator
        implements ConstraintValidator<EmploymentPaymentFrequencyConstraint, PaymentFrequencyQuestion>, Validator {


    private static final String MUST_SELECT_FREQUENCY_ERROR_MESSAGE =
            "common.how.often.paid.frequency.missing.error";
    private static final String MUST_SELECT_GROSS_AMOUNT_ERROR_MESSAGE =
            "current.work.paymentFrequency.empty.error";
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999.99");

    @Override
    public boolean isValid(
            final PaymentFrequencyQuestion question,
            final ConstraintValidatorContext context) {

        Optional<PaymentAmounts> paymentAmountsOptional = question.getSelectedPaymentAmounts();
        if (!paymentAmountsOptional.isPresent()) {
            return addInvalidMessage(context, MUST_SELECT_FREQUENCY_ERROR_MESSAGE, "paymentFrequency");

        }
        PaymentAmounts paymentAmounts = paymentAmountsOptional.get();
        BigDecimal net = paymentAmounts.getNet();
        if (Objects.isNull(net)
                || net.compareTo(MAX_AMOUNT) > 0
                || net.compareTo(BigDecimal.ZERO) < 0
                || !hasValidAmountOfDecimalPlaces(net)) {
            return addInvalidMessage(context, MUST_SELECT_GROSS_AMOUNT_ERROR_MESSAGE,
                    question.getPaymentFrequency().name().toLowerCase(Locale.ENGLISH) + "PaymentAmounts.net");
        }

        return true;
    }

    private boolean hasValidAmountOfDecimalPlaces(final BigDecimal net) {
        return net.scale() == 0 || net.scale() == 2;
    }
}
