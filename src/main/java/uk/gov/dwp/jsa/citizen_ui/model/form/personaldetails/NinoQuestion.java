package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.NinoConstraint;

import javax.validation.constraints.NotBlank;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class NinoQuestion extends StringQuestion {

    public NinoQuestion() {
    }

    public NinoQuestion(@NotBlank final String nino) {
        super(nino);
    }

    @Override
    @NinoConstraint
    public String getValue() {
        return super.getValue();
    }

    /**
     * Formats the given nino in the format 'AA NN NN NN A'.
     * <p>
     * Note that the argument nino is sanitised before being
     *
     * @return formatted nino
     */
    public final String getFormattedNino() {
        if (this.getValue() == null) {
            return null;
        }
        final String nino = this.getValue().replaceAll("\\s", "");

        return IntStream.range(0, nino.length())
                .mapToObj((int i) -> {
                    if ((i % 2) != 0) {
                        return String.format("%c ", nino.charAt(i));
                    } else {
                        return String.format("%c", nino.charAt(i));
                    }
                }).collect(Collectors.joining(""));
    }
}
