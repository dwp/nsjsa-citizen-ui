package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.availability;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class AttendInterviewAcceptanceTests {
    public static final String AVAILABILITY_ADDRESS = "/form/availability/availability";
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void GivenWeDontSelectAnyAvailableSlots_PostForm_ShowsErrorMessage() throws Exception {
        mockMvc.perform(
                post(AVAILABILITY_ADDRESS).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("attendInterviewQuestion.daysNotToAttend[0].date", "9/12/18")
                        .param("attendInterviewQuestion.daysNotToAttend[0].morning.selected", "false")
                        .param("attendInterviewQuestion.daysNotToAttend[0].afternoon.selected", "false"))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("availability.days.error")));
    }

    @Test
    public void GivenWeSelectAnyAvailableSlot_PostForm_ContinuesToSummary() throws Exception {
        mockMvc.perform(
                post(AVAILABILITY_ADDRESS).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("attendInterviewQuestion.daysNotToAttend[0].date", "9/12/18")
                        .param("attendInterviewQuestion.daysNotToAttend[0].morning.selected", "true")
                        .param("attendInterviewQuestion.daysNotToAttend[0].afternoon.selected", "false"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/form/summary"));
    }
}
