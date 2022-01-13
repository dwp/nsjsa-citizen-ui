package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.ViewQuestion;

import java.util.ArrayList;
import java.util.List;

@Service
public class SummaryMappingService {

    private final List<MappingService> mappingServices;

    public SummaryMappingService(
            final List<MappingService> mappingServices) {
        this.mappingServices = mappingServices;
    }

    public List<ViewQuestion> map(final Claim claim) {

        List<ViewQuestion> questions = new ArrayList<>();
        mappingServices.forEach(m -> m.map(claim, questions));
        return questions;
    }
}
