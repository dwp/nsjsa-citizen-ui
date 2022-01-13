package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.backdating;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class WereYouAvailableForWorkAcceptanceTests {
    private static final String WERE_YOU_AVAILABLE_FOR_WORK_URL = "/form/backdating/were-you-available-for-work";
    private static final String NEXT_PAGE = "/form/backdating/were-you-searching-for-work";
    private static final String FORMATTED_PATTERN    = "d MMMM yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATTED_PATTERN, Locale.UK);

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;

    @Autowired
    private ClaimRepository claimRepository;

    LocalDate claimStartDate = LocalDate.now();

    private String format(final LocalDate date) {
        String formattedDateEnglish = FORMATTER.format(date);
        return formattedDateEnglish;
    }

    String formattedDate = format(claimStartDate);

    private final String PAGE_TITLE = "Were you available for work from " + formattedDate + "?";

    @Before
    public void setUp() {
        claimRepository.deleteAll();
        assertThat(claimRepository.count(), is(0L));
    }

    @After
    public void tearDown() {
        claimRepository.deleteAll();
    }

    @Test
    public void getWereYouAvailableForWork_ReturnsForm() throws Exception {
        mockMvc.perform(get(WERE_YOU_AVAILABLE_FOR_WORK_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(PAGE_TITLE)));
    }

    @Test
    public void postWereYouAvailableForWork_WithYesSelected_redirectsToNextPage() throws Exception {
        mockMvc.perform(post(WERE_YOU_AVAILABLE_FOR_WORK_URL)
                .with(csrf())
                .param("question.choice", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", NEXT_PAGE));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void postWereYouAvailableForWork_WithNoSelected_redirectsToNextPage() throws Exception {
        mockMvc.perform(post(WERE_YOU_AVAILABLE_FOR_WORK_URL)
                .with(csrf())
                .param("question.choice", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", NEXT_PAGE));
        assertThat(claimRepository.count(), is(1L));
    }

    @Test
    public void postWereYouAvailableForWork_WithEmptySelection_staysOnPageWithCorrectErrors() throws Exception {
        mockMvc.perform(post(WERE_YOU_AVAILABLE_FOR_WORK_URL)
                .param("question.choice", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("govuk-form-group--error")));
    }

}
