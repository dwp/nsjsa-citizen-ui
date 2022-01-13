package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.RestoreClaim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimDBRepository;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.services.RestoreClaimService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.adaptors.enums.ClaimType.EDIT_CLAIM;
import static uk.gov.dwp.jsa.adaptors.enums.ClaimType.NEW_CLAIM;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

    @Mock
    private HttpServletResponse response;
    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private ClaimDBRepository claimDBRepository;
    @Mock
    private RestoreClaimService restoreClaimService;
    private final UUID claimantId = UUID.randomUUID();

    private HomeController sut;

    @Before
    public void setUp() {
        sut = new HomeController(claimRepository, claimDBRepository, restoreClaimService,10);
    }

    @Test
    public void testInitClaimShouldReturnClaimant() {

        String result = sut.initClaim(NEW_CLAIM, claimantId, response);

        assertEquals("Should match", "redirect:/", result);
        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository).save(captor.capture());
        assertEquals("Claim type should be equal", NEW_CLAIM, captor.getValue().getClaimType());

        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    public void testInitClaimShouldReturnSummary() {
        when(claimDBRepository.findById(anyString())).thenReturn(Optional.of(new ClaimDB()));

        String result = sut.initClaim(EDIT_CLAIM, claimantId, response);

        assertEquals("Should match", "redirect:/form/summary", result);
        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository, times(2)).save(captor.capture());

        verify(response).addCookie(any(Cookie.class));
        verify(restoreClaimService).restore(any(RestoreClaim.class));
    }

    @Test
    public void testAccessibilityStatement() {
        Model model = Mockito.mock(Model.class);
        assertEquals("accessibility", sut.accessibility(model));
    }

    @Test
    public void testEligibleUrl() {
        Model model = Mockito.mock(Model.class);
        sut.eligible(claimantId.toString(), model, response);
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(captor.capture());

        List<Cookie> cookies = captor.getAllValues();
        assertEquals(cookies.get(0).getName(), "eligibility-done");
    }
}
