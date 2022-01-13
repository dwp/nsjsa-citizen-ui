package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PreviousEmploymentTest {

    private static final String TEST_CONSULTANCY_SERVICES = "Test Consultancy Services";
    private PreviousEmployment sut = new PreviousEmployment();

    @Before
    public void setUp() {
        sut = new PreviousEmployment();
    }

    @Test
    public void getEmployerDetailsReturnsNewObject() {
        EmployersDetails employerDetails = sut.getEmployerDetails(1);
        assertThat(employerDetails, is(new EmployersDetails()));
    }

    @Test
    public void getEmployerDetailsReturnsExistingObject() {
        sut.setEmployerDetailsList(createEmployerDetailsList());
        EmployersDetails employerDetailsReturned = sut.getEmployerDetails(1);
        assertThat(employerDetailsReturned.getEmployersNameQuestion().getValue(), is(TEST_CONSULTANCY_SERVICES));
    }

    @Test
    public void updateEmployerDetailsAddsNewObject() {
        sut.updateEmployerDetails(3, getEmployersDetailsWithName("Test Name"));

        assertThat(sut.getEmployerDetailsList().indexOf(getEmployersDetailsWithName("Test Name")),
                is(0));
    }

    @Test
    public void updateEmployerDetailsUpdatesExistingObject() {
        sut.setEmployerDetailsList(createEmployerDetailsList());

        sut.updateEmployerDetails(2, getEmployersDetailsWithName("Test Name"));

        assertThat(sut.getEmployerDetailsList().indexOf(getEmployersDetailsWithName("Test Name")),
                is(1));
    }

    private List<EmployersDetails> createEmployerDetailsList() {
        EmployersDetails employerDetail1 = getEmployersDetailsWithName(TEST_CONSULTANCY_SERVICES);
        EmployersDetails employerDetail2 = getEmployersDetailsWithName(TEST_CONSULTANCY_SERVICES + "2");
        return asList(employerDetail1, employerDetail2);
    }

    private EmployersDetails getEmployersDetailsWithName(final String employerName) {
        StringQuestion employerNameQuestion = new StringQuestion();
        employerNameQuestion.setValue(employerName);

        EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setEmployersNameQuestion(employerNameQuestion);
        return employersDetails;
    }
}
