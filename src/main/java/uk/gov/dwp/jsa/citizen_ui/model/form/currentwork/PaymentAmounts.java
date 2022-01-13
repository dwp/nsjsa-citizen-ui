package uk.gov.dwp.jsa.citizen_ui.model.form.currentwork;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentAmounts {
    private static final int MAX_NUMBER_OF_DECIMAL_POINTS = 2;
    private BigDecimal net;

    public PaymentAmounts() {

    }
    public PaymentAmounts(final BigDecimal net) {
        this.net = convertToPoundsAndPenceFormat(net);
    }
    public BigDecimal getNet() {
        return net;
    }
    public void setNet(final BigDecimal net) {
        this.net = convertToPoundsAndPenceFormat(net);
    }

    private BigDecimal convertToPoundsAndPenceFormat(final BigDecimal amount) {
        if (amount == null) {
            return null;
        } else if (amount.scale() < MAX_NUMBER_OF_DECIMAL_POINTS) {
            return amount.setScale(MAX_NUMBER_OF_DECIMAL_POINTS, RoundingMode.HALF_UP);
        } else {
            return amount;
        }
    }

}
