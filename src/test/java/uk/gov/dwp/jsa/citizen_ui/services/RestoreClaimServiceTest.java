package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController;
import uk.gov.dwp.jsa.citizen_ui.controller.CounterFormController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.RestoreClaim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RestoreClaimServiceTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Model model;
    @Mock
    private CounterFormController mockController1;
    @Mock
    private BaseFormController mockController2;
    @Mock
    private ClaimDB claimDB;
    @Mock
    private Claim claim;
    @Mock
    private RoutingService routingService;
    private RestoreClaimService sut;
    @Mock
    private Step step;

    @Before
    public void setUp() {
        sut = new RestoreClaimService(Arrays.asList(mockController1, mockController2), routingService);
    }

    @Test
    public void testGetIdentifierReplaceRedirectAndLoopCount() {
        String nextIdentifier = "redirect:/form/do-something/3/name";

        String actual = sut.getIdentifier(nextIdentifier);

        assertEquals("Should match", "form/do-something/name", actual);
    }

    @Test
    public void testGetIdentifierReplaceRedirect() {
        String nextIdentifier = "redirect:/form/do-something/name";

        String actual = sut.getIdentifier(nextIdentifier);

        assertEquals("Should match", "form/do-something/name", actual);
    }

    @Test
    public void testGetCountShouldReturnCount() {
        String nextIdentifier = "redirect:/form/do-something/3/name";

        String actual = sut.getCount(nextIdentifier);

        assertEquals("Should match", "3", actual);
    }

    @Test
    public void testGetCountWhenNoCountShouldReturnEmptyCount() {
        String nextIdentifier = "redirect:/form/do-something/name";

        String actual = sut.getCount(nextIdentifier);

        assertEquals("Should match", "", actual);
    }

    @Test
    public void testLoadDataOnBaseFormControllerShouldCallController() {

        final ClaimDB claimDB = new ClaimDB();
        when(routingService.getStep(any())).thenReturn(Optional.of(step));
        when(mockController1.createNewForm(any())).thenReturn(new StringForm());
        sut.loadData(new RestoreClaim(claimDB, claim), mockController1, "");

        verify(mockController1).loadForm(eq(claimDB), any());
        verify(routingService).arrivedOnPage(any(), any());
        verify(routingService).leavePage(any(), any());
        verify(routingService).getLastGuard(any(), any());
    }

    @Test
    public void testLoadDataOnCountFormControllerShouldCallController() {

        final ClaimDB claimDB = new ClaimDB();
        final String finalCount = "1";
        when(routingService.getStep(any())).thenReturn(Optional.of(step));
        when(mockController1.createNewForm(any(), anyInt())).thenReturn(new StringForm());
        sut.loadData(new RestoreClaim(claimDB, claim), mockController1, finalCount);

        verify(mockController1).loadForm(eq(claimDB), any());
        verify(routingService).arrivedOnPage(any(), any());
        verify(routingService).leavePage(any(), any());
        verify(routingService).getLastGuard(any(), any());
    }
}
