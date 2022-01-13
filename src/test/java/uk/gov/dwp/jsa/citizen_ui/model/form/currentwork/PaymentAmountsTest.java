package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;

public class PaymentAmountsTest {

    private static final int NET_AMOUNT = 12;

    private static final BigDecimal EXPECTED_BIG_DECIMAL =
            new BigDecimal(NET_AMOUNT).setScale(2, RoundingMode.HALF_UP);

    private PaymentAmounts sut;

    @Before
    public void setUp() {
        sut = new PaymentAmounts();
    }

    @Test
    public void setNet_inputWithNoDecimalPlaces_setsValueWithTwoDecimalPlaces() throws Exception {
        BigDecimal bigDecimalArgument = new BigDecimal(NET_AMOUNT).setScale(0, RoundingMode.HALF_UP);
        sut.setNet(bigDecimalArgument);
        assertEquals(EXPECTED_BIG_DECIMAL, getFieldValueUsingReflection(sut));
    }

    @Test
    public void setNet_inputWithOneDecimalPlace_setsValueWithTwoDecimalPlaces() throws Exception{
        BigDecimal bigDecimalArgument = new BigDecimal(NET_AMOUNT).setScale(1, RoundingMode.HALF_UP);
        sut.setNet(bigDecimalArgument);
        assertEquals(EXPECTED_BIG_DECIMAL, getFieldValueUsingReflection(sut));
    }

    @Test
    public void setNet_inputWithTwoDecimalPlace_setsValueWithTwoDecimalPlaces() throws Exception{
        BigDecimal bigDecimalArgument = new BigDecimal(NET_AMOUNT).setScale(2, RoundingMode.HALF_UP);
        sut.setNet(bigDecimalArgument);
        assertEquals(EXPECTED_BIG_DECIMAL, getFieldValueUsingReflection(sut));
    }

    @Test
    public void paymentAmountsConstructor_inputWithNoDecimalPlaces_setsValueWithTwoDecimalPlaces() throws Exception {
        BigDecimal bigDecimalArgument = new BigDecimal(NET_AMOUNT).setScale(0, RoundingMode.HALF_UP);
        PaymentAmounts paymentAmounts = new PaymentAmounts(bigDecimalArgument);
        assertEquals(EXPECTED_BIG_DECIMAL, getFieldValueUsingReflection(paymentAmounts));
    }

    @Test
    public void paymentAmountsConstructor_inputWithOneDecimalPlace_setsValueWithTwoDecimalPlaces() throws Exception{
        BigDecimal bigDecimalArgument = new BigDecimal(NET_AMOUNT).setScale(1, RoundingMode.HALF_UP);
        PaymentAmounts paymentAmounts = new PaymentAmounts(bigDecimalArgument);
        assertEquals(EXPECTED_BIG_DECIMAL, getFieldValueUsingReflection(paymentAmounts));
    }

    @Test
    public void paymentAmountsConstructor_inputWithTwoDecimalPlace_setsValueWithTwoDecimalPlaces() throws Exception{
        BigDecimal bigDecimalArgument = new BigDecimal(NET_AMOUNT).setScale(2, RoundingMode.HALF_UP);
        PaymentAmounts paymentAmounts = new PaymentAmounts(bigDecimalArgument);
        assertEquals(EXPECTED_BIG_DECIMAL, getFieldValueUsingReflection(paymentAmounts));
    }

    private BigDecimal getFieldValueUsingReflection(PaymentAmounts paymentAmounts) throws Exception {
        final Field field = paymentAmounts.getClass().getDeclaredField("net");
        field.setAccessible(true);
        return (BigDecimal) field.get(paymentAmounts);
    }
}
