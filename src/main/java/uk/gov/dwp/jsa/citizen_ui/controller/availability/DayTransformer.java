package uk.gov.dwp.jsa.citizen_ui.controller.availability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.form.availability.Day;

@Component
public class DayTransformer {
    private ReasonTransformer reasonTransformer;

    @Autowired
    public DayTransformer(final ReasonTransformer reasonTransformer) {
        this.reasonTransformer = reasonTransformer;
    }

    public Day transform(final uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Day day) {
        final Day citizenDay = new Day();
        citizenDay.setDate(day.getDate());
        citizenDay.setMorning(reasonTransformer.transform(day.getMorning()));
        citizenDay.setAfternoon(reasonTransformer.transform(day.getAfternoon()));
        return citizenDay;
    }
}

