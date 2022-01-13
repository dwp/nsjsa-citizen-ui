package uk.gov.dwp.jsa.citizen_ui.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class SortCode {

    /**
     * Sort code format of either 112233 or 11-22-33.
     */
    @NotEmpty
    @Size(min = 6, max = 8)
    @Pattern(regexp = BankAccountForm.SORT_CODE_REGEX)
    private String code;

    public SortCode() {
    }

    public SortCode(final String code) {
        Assert.hasLength(code, "Sort code must have format of 112233 or 11-22-33");
        this.code = code;
    }

    /**
     * Returns sort code as entered, could be either 112233 or 11-22-33. Use formatter methods to ensure format.
     *
     * @return sort code as provided - either 11-22-33 or 112233
     */
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * Whether the code is empty or not.
     *
     * @return true if the code is empty (null, zero length string, whitespace only) false otherwise
     */
    public boolean isEmpty() {
        return StringUtils.isBlank(code);
    }

    /**
     * Strips out dashes from the sort code to leave the sort code as numerics only.
     *
     * @return sort code with dashes removed or {@code null} if code empty
     */
    public String getSanitisedCode() {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return code.replace("-", "");
    }

    /**
     * Returns a formatted version of the sort code where each part is separated with hyphens e.g. 11-22-33.
     *
     * @return formatted sort code with hyphens e.g. 11-22-33 or {@code null} if code empty
     */
    public String getFormattedCode() {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        /*
         * The sort code validator and Spring validators should have ensured the pattern at this point so just check
         * for a single hyphen to determine if code is already formatted
         */
        if (code.contains("-")) {
            return code;
        }
        return code.substring(0, 2)
                + "-"
                + code.substring(2, 4)
                + "-"
                + code.substring(4, 6);
    }
}
