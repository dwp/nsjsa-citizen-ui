package uk.gov.dwp.jsa.citizen_ui.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentAmounts;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PaymentFrequencyQuestion;

import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmploymentPaymentFrequencyValidatorTest {

    private static final BigDecimal TOO_MUCH_MONEY = new BigDecimal("999999.99");
    private static final BigDecimal TOO_LITTLE_MONEY = new BigDecimal("-1");
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WIYH_TWO_DECIMAL_PLACES =
            Optional.of(new PaymentAmountsBuilder().withNet(new BigDecimal("11.11")).build());
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WITH_EMPTY_NET =
            Optional.of(new PaymentAmountsBuilder().withNet(null).build());
    private static final String INVALID_CHARACTER = "dd";
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WITH_TOO_MANY_DECIMAL_PLACES =
            Optional.of(new PaymentAmountsBuilder().withNet(new BigDecimal("111.111")).build());
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WITH_ONE_DECIMAL_PLACE =
            Optional.of(new PaymentAmountsBuilder().withNet(new BigDecimal("111.1")).build());
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WITH_NO_DECIMAL_PLACES =
            Optional.of(new PaymentAmountsBuilder().withNet(new BigDecimal("111")).build());
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WITH_NET_TOO_HIGH =
            Optional.of(new PaymentAmountsBuilder().withNet(TOO_MUCH_MONEY).build());
    private static final Optional<PaymentAmounts> PAYEMENT_AMOUNTS_WITH_NET_TOO_LOW =
            Optional.of(new PaymentAmountsBuilder().withNet(TOO_LITTLE_MONEY).build());
    private static final PaymentFrequencyQuestion QUESTION_WITH_NET_WITH_TWO_DECIMAL_PLACES =
            new PaymentFrequencyQuestionBuilder().withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WIYH_TWO_DECIMAL_PLACES.get()).build();
    private static final PaymentFrequencyQuestion QUESTION_WITH_EMPTY_NET =
            new PaymentFrequencyQuestionBuilder()
                    .withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WITH_EMPTY_NET.get()).build();
    private static final PaymentFrequencyQuestion QUESTION_WITH_NET_WITH_TOO_MANY_DECIMAL_PLACES =
            new PaymentFrequencyQuestionBuilder()
                    .withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WITH_TOO_MANY_DECIMAL_PLACES.get()).build();
    private static final PaymentFrequencyQuestion QUESTION_WITH_NET_WITH_ONE_DECIMAL_PLACE =
            new PaymentFrequencyQuestionBuilder()
                    .withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WITH_ONE_DECIMAL_PLACE.get()).build();
    private static final PaymentFrequencyQuestion QUESTION_WITH_NET_WITH_NO_DECIMAL_PLACES =
            new PaymentFrequencyQuestionBuilder()
                    .withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WITH_NO_DECIMAL_PLACES.get()).build();
    private static final PaymentFrequencyQuestion QUESTION_WITH_NET_TOO_HIGH =
            new PaymentFrequencyQuestionBuilder()
                    .withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WITH_NET_TOO_HIGH.get()).build();
    private static final PaymentFrequencyQuestion QUESTION_WITH_NET_TOO_LOW =
            new PaymentFrequencyQuestionBuilder()
                    .withMonthlyPaymentAmounts(PAYEMENT_AMOUNTS_WITH_NET_TOO_LOW.get()).build();

    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
            nodeBuilderCustomizableContext;

    private EmploymentPaymentFrequencyValidator validator;
    private boolean isValid;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void questionWithNetWithTwoDecimalPlacesIsValid() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_WITH_TWO_DECIMAL_PLACES);
        thenTheQuestionIsValid();
    }

    @Test
    public void questionWithNoDecimalPlacesIsValid() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_WITH_NO_DECIMAL_PLACES);
        thenTheQuestionIsValid();
    }

    @Test
    public void questionWithOneDecimalPlaceIsValid() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_WITH_ONE_DECIMAL_PLACE);
        thenTheQuestionIsValid();
    }

    @Test
    public void emptyNetRaisesError() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_EMPTY_NET);
        thenTheQuestionIsInvalid();
    }

    @Test
    public void invalidNetRaisesError() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_WITH_TOO_MANY_DECIMAL_PLACES);
        thenTheQuestionIsInvalid();
    }

    @Test
    public void tooMuchNetRaisesError() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_TOO_HIGH);
        thenTheQuestionIsInvalid();
    }

    @Test
    public void tooManyDecimalPlacesRaisesError() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_WITH_TOO_MANY_DECIMAL_PLACES);
        thenTheQuestionIsInvalid();
    }

    @Test
    public void tooLittleNetRaisesError() {
        givenAValidator();
        whenICallIsValid(QUESTION_WITH_NET_TOO_LOW);
        thenTheQuestionIsInvalid();
    }

    private void givenAValidator() {
        validator = new EmploymentPaymentFrequencyValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
    }

    private void whenICallIsValid(final PaymentFrequencyQuestion question) {
        isValid = validator.isValid(question, context);
    }

    private void thenTheQuestionIsInvalid() {
        assertThat(isValid, is(false));
    }

    private void thenTheQuestionIsValid() {
        assertThat(isValid, is(true));
    }


    public static class PaymentAmountsBuilder {
        public static final BigDecimal NET = ONE;

        private BigDecimal net = NET;

        public PaymentAmountsBuilder withNet(final BigDecimal net) {
            this.net = net;
            return this;
        }

        public PaymentAmounts build() {
            PaymentAmounts paymentAmounts = new PaymentAmounts();
            paymentAmounts.setNet(net);
            return paymentAmounts;
        }
    }

    public static class PaymentFrequencyQuestionBuilder {

        public PaymentFrequency PAYMENT_FREQUENCY = PaymentFrequency.MONTHLY;

        private PaymentFrequency paymentFrequency = PAYMENT_FREQUENCY;
        private PaymentAmounts monthlyPaymentAmounts;

        public PaymentFrequencyQuestionBuilder withMonthlyPaymentAmounts(PaymentAmounts monthlyPaymentAmounts) {
            this.monthlyPaymentAmounts = monthlyPaymentAmounts;
            return this;
        }

        public PaymentFrequencyQuestion build() {
            PaymentFrequencyQuestion paymentFrequencyQuestion = new PaymentFrequencyQuestion();
            paymentFrequencyQuestion.setPaymentFrequency(paymentFrequency);
            paymentFrequencyQuestion.setMonthlyPaymentAmounts(monthlyPaymentAmounts);
            return paymentFrequencyQuestion;
        }
    }
}
