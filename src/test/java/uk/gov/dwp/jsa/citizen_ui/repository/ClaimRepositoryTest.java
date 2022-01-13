package uk.gov.dwp.jsa.citizen_ui.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.PersonalDetails;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.DateOfBirthQuestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class ClaimRepositoryTest {

    @Autowired
    private ClaimRepository unit;

    @Before
    public void setUp() {
        unit.deleteAll();
    }

    @After
    public void tearDown() {
        unit.deleteAll();
    }

    @Test
    public void GivenBirthDate_saveAndRead() throws Exception {
        Integer day = 21;
        Integer month = 7;
        Integer year = 1980;
        DateOfBirthQuestion dateOfBirthQuestionResponse = new
                DateOfBirthQuestion();
        dateOfBirthQuestionResponse.setDay(day);
        dateOfBirthQuestionResponse.setMonth(month);
        dateOfBirthQuestionResponse.setYear(year);
        Claim claim = new Claim();
        claim.getPersonalDetails().setDateOfBirthQuestion(dateOfBirthQuestionResponse);

        unit.save(claim);

        claim = unit.findById(claim.getId()).orElseThrow(() -> new Exception
                ("n"));

        PersonalDetails personalDetails = claim.getPersonalDetails();
        assertNotNull(personalDetails.getDateOfBirthQuestion());
        assertEquals(day, personalDetails.getDateOfBirthQuestion().getDay());
        assertEquals(month, personalDetails.getDateOfBirthQuestion().getMonth());
        assertEquals(year, personalDetails.getDateOfBirthQuestion().getYear());
    }
}
