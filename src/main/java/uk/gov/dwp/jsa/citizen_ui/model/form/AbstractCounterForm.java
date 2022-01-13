package uk.gov.dwp.jsa.citizen_ui.model.form;

/**
 * This class has a counter used to track
 * previous employment loop.
 */
public abstract class AbstractCounterForm<T extends Question> extends AbstractForm<T> {

    private Integer count;
    private int maxCount = 1;

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(final int maxCount) {
        this.maxCount = maxCount;
    }
}
