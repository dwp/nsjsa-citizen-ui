package uk.gov.dwp.jsa.citizen_ui.controller.claimstart;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClaimWarningDateControllerTest {

    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;

    @Mock
    private HttpServletResponse response;

    private ClaimWarningDateController sut;

    @Before
    public void setUp() {
        sut = new ClaimWarningDateController(mockClaimRepository, mockRoutingService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void claimResetReturnsExceptionWhenClaimIdIsEmpty() {
        sut.claimReset("", response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void claimResetReturnsExceptionWhenClaimIdIsNull() {
        sut.claimReset(null, response);
    }

    @Test
    public void claimResetReturnsClaimStartPage() {
        when(mockClaimRepository.save(any())).thenReturn(new Claim());
        String nextPage = sut.claimReset("id", response);
        assertEquals("redirect:/" + ClaimStartDateController.IDENTIFIER, nextPage);
    }

    @Test
    public void claimResetCreatesNewClaim() {
        when(mockClaimRepository.save(any())).thenReturn(new Claim());
        String nextPage = sut.claimReset("id", response);

        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(captor.capture());

        assertThat(captor.getValue().getName(), is("claim_id"));
    }
}
