package uk.gov.dwp.jsa.citizen_ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.gov.dwp.jsa.adaptors.ServicesProperties;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingService;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.services.PensionsService;
import uk.gov.dwp.jsa.citizen_ui.services.RequiredDataService;
import uk.gov.dwp.jsa.citizen_ui.services.SummaryMappingService;
import uk.gov.dwp.jsa.citizen_ui.services.UpdateService;
import uk.gov.dwp.jsa.citizen_ui.util.date.DateFormatterUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SummaryControllerTest {

    @Mock
    private Model mockModel;

    @Mock private ClaimRepository mockClaimRepository;

    @Mock
    private RoutingService mockRoutingService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private SummaryMappingService mockSummaryMappingService;
    @Mock
    private CookieLocaleResolver mockCookieLocaleResolver;
    @Mock
    private PensionsService mockPensionsService;
    @Mock
    private Step step;
    @Mock
    private UpdateService updateService;

    @Mock
    private ServicesProperties servicesProperties;

    @Mock
    private DateFormatterUtils mockDateFormatterUtils;

    @Mock
    private RequiredDataService mockRequiredDataService;

    private String claimId = "123e4567-e89b-12d3-a456-426655440000";

    private SummaryController sut;

    @Before
    public void setUp() {
        sut = new SummaryController(mockClaimRepository, mockRoutingService, mockSummaryMappingService,
                mockCookieLocaleResolver, mockPensionsService,
                updateService,servicesProperties,mockRequiredDataService);
        ReflectionTestUtils.setField(sut, "dateFormatterUtils", mockDateFormatterUtils);

        when(mockRequest.getParameter("edit")).thenReturn(EditMode.NONE.toString());
        when(mockRoutingService.getStep("form/summary")).thenReturn(Optional.of(step));
        when(mockDateFormatterUtils.getTodayDate()).thenReturn(LocalDate.now());
    }

    @Test
    public void getClearSummaryModeStepHistory() {
        final boolean agentMode = true;
        sut.getView(mockModel, agentMode, claimId, mockRequest);

        verify(mockRoutingService).clearSummaryHistory(claimId);
        verify(mockModel).addAttribute("agentMode", agentMode);
    }

}
