package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;

import java.util.Optional;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;


public class Step {

    private final String identifier;
    private final String nextStepIdentifier;
    private final String alternateNextStepIdentifier;
    private final Section section;
    private boolean sectionTerminator;

    public Step(
            final String identifier,
            final String nextStepIdentifier,
            final String alternateNextStepIdentifier,
            final Section section) {

        Assert.notNull(identifier, "identifier");
        Assert.hasLength(identifier, "identifier");

        this.identifier = identifier;
        this.nextStepIdentifier = nextStepIdentifier;
        this.alternateNextStepIdentifier = alternateNextStepIdentifier;
        this.section = section;
        this.sectionTerminator = false;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getNextStepIdentifier() {
        return nextStepIdentifier;
    }

    public Optional<String> getNextAlternateStepIdentifier() {
        return Optional.ofNullable(alternateNextStepIdentifier);
    }

    public Section getSection() {
        return section;
    }

    public boolean isSectionTerminator() {
        return sectionTerminator;
    }

    public void setSectionTerminator(final boolean sectionTerminator) {
        this.sectionTerminator = sectionTerminator;
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }
}
