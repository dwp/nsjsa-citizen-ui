package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RequiredDataServiceTest {
    private RequiredDataService sut;

    @Mock
    private Claim mockClaim;

    private Optional<Question> question = Optional.of(new GuardQuestion());

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new RequiredDataService();
    }

    @Test
    public void testGetQuestionUrl_WhenQuestionsSet() {
        when(mockClaim.get(anyString())).thenReturn(question);

        String result = sut.getQuestionUrl(mockClaim);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetQuestionUrl_WhenQuestionsNotSet() {
        String result = sut.getQuestionUrl(mockClaim);
        assertThat(result, is("error/500"));
    }
}
