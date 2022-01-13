package uk.gov.dwp.jsa.citizen_ui.routing;

import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;

public class StepBuilder {
    public static final String NEXT_STEP = "NEXT_STEP";
    public static final String ALT_NEXT = "ALT_NEXT";

    private String id;
    private Form form;
    private Section section;
    private String nextStep = NEXT_STEP;

    public StepBuilder withId(final String id) {
        this.id = id;
        return this;
    }

    public StepBuilder withForm(final Form form) {
        this.form = form;
        return this;
    }

    public StepBuilder withSection(final Section section) {
        this.section = section;
        return this;
    }

    public StepBuilder withNextStep(final String nextStep) {
        this.nextStep = nextStep;
        return this;
    }

    public Step build() {
        return new Step(id, nextStep, ALT_NEXT, section);
    }
}
