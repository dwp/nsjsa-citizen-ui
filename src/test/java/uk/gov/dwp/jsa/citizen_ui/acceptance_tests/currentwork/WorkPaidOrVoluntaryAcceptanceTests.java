package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.currentwork;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class WorkPaidOrVoluntaryAcceptanceTests {

    private static final String FORM_URL = "/form/current-work/details/1/is-work-paid";
    private static final String NEXT_URL = "/form/current-work/details/1/how-often-paid";
    private static final String ALT_NEXT_URL = "/form/current-work/details/1/choose-payment";
    private static final String PARAM_NAME = "multipleOptionsQuestion.userSelectionValue";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    public void get_ReturnsForm() throws Exception {
        mockMvc.perform(get("/form/current-work/details/1/is-work-paid")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("multipleOptionsQuestion.option")))
                .andExpect(content().string(containsString("govuk-hint")));
    }

    @Test
    public void getFormForFourthJob_ReturnsFormWithoutHint() throws Exception {
        mockMvc.perform(get("/form/current-work/details/4/is-work-paid")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("multipleOptionsQuestion.option")))
                .andExpect(content().string(not(containsString("govuk-hint"))));
    }


    @Test
    public void post_WithPaidSelected_ReturnsHowOftenPaidForm() throws Exception {
        mockMvc.perform(post(FORM_URL)
                .param("multipleOptionsQuestion.userSelectionValue", "PAID")
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(NEXT_URL)));
    }

    @Test
    public void post_WithVoluntarySelected_ReturnsIsItPaid() throws Exception {
        Claim claim = new Claim();
        claimRepository.save(claim);

        mockMvc.perform(post(FORM_URL)
                .param("multipleOptionsQuestion.userSelectionValue", "VOLUNTARY")
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is(ALT_NEXT_URL)));
    }

    @Test
    public void post_WithEmptySelection_ReturnsErrors() throws Exception {
        mockMvc.perform(post(FORM_URL)
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Select if you’re in paid or voluntary work")));
    }

    @Test
    public void post_WithIllegalArgument_ReturnsErrors() throws Exception {
        mockMvc.perform(post(FORM_URL)
                .param(PARAM_NAME, "invalid")
                .param("count", "1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

}
