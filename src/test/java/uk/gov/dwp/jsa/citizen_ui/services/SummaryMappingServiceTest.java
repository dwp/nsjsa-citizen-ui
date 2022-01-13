package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SummaryMappingServiceTest {

    @Mock
    private MappingService mockService;

    @Mock
    private Claim mockClaim;

    private SummaryMappingService sut;

    @Test
    public void map() {
        sut = new SummaryMappingService(Arrays.asList(mockService));

        sut.map(mockClaim);

        verify(mockService).map(eq(mockClaim), anyList());
    }
}
