package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasAnotherCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.GuardQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;

@RunWith(MockitoJUnitRunner.class)
public class PensionsServiceTest {

    private final static String CURRENT_PENSION_IDENTIFIER =
            uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.details.ProviderNameController.IDENTIFIER;

    private static final int FIRST_PENSION = 1;

    private PensionsService sut;

    @Mock
    private Claim mockClaim;

    @Mock
    private Question mockQuestion;

    @Mock
    private GuardQuestion mockGuardQuestion;

    @Mock
    private ClaimRepository mockClaimRepository;


    @Before
    public void setUp() {
        sut = new PensionsService(mockClaimRepository);
    }

    @Test
    public void givenNoPensionQuestionWereAnswered_canAddPension_shouldReturnTrue() {
        boolean actual = sut.canAddPension(mockClaim);
        assertTrue(actual);
    }

    @Test
    public void givenMaxPensionQuestionsWereAchieved_canAddPension_shouldReturnFalse() {
        when(mockClaim.count(anyString(), anyInt())).thenReturn(Constants.MAX_PENSIONS_ALLOWED);

        boolean actual = sut.canAddPension(mockClaim);

        assertFalse(actual);
    }

    @Test
    public void givenUserInOnPenultimatePension_isPenultimatePension_shouldReturnTrue() {
        boolean actual = sut.isPenultimatePension(MAX_PENSIONS_ALLOWED-1);
        assertTrue(actual);
    }

    @Test
    public void givenUserIsNotOnPenultimatePension_isPenultimatePension_shouldReturnFalse() {
        boolean actual = sut.isPenultimatePension(MAX_PENSIONS_ALLOWED);
        assertFalse(actual);
    }

    @Test
    public void whenCanAddPensionIsCalled_shouldCallClaimCountForAllIdentifiers() {
        boolean actual = sut.canAddPension(mockClaim);

        assertTrue(actual);

        verify(mockClaim, times(1)).count(CURRENT_PENSION_IDENTIFIER, Constants.MAX_PENSIONS_ALLOWED);
    }

    @Test
    public void whenNineCurrentPensionsWereAdded_canAddPension_shouldReturnFalse() {
        Claim claim = new Claim();
        IntStream.rangeClosed(FIRST_PENSION, Constants.MAX_PENSIONS_ALLOWED)
                .forEach(counter -> claim.save(createStepInstance(CURRENT_PENSION_IDENTIFIER, counter, Section.CURRENT_PENSIONS), mockQuestion, Optional.empty()));

        boolean actual = sut.canAddPension(claim);

        assertFalse(actual);
    }

    @Test
    public void GivenPensionExists_HasXPension_ReturnsTrue() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.get(anyString(), anyInt(), any(GuardQuestion.class.getClass()))).thenReturn(Optional.of(mockGuardQuestion));
        when(mockGuardQuestion.getChoice()).thenReturn(Boolean.TRUE);

        final String claimId = "123e4567-e89b-12d3-a456-426655440000";
        boolean actualCurrent = sut.hasCurrentPension(claimId);

        assertTrue(actualCurrent);
    }

    @Test
    public void GivenPensionDoesNotExist_HasXPension_ReturnsFalse() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.get(anyString(), anyInt(), any(GuardQuestion.class.getClass()))).thenReturn(Optional.empty());

        final String claimId = "123e4567-e89b-12d3-a456-426655440000";
        boolean actualCurrent = sut.hasCurrentPension(claimId);

        assertFalse(actualCurrent);
    }

    @Test
    public void GivenPensionExistsButAnswerIsNo_HasXPension_ReturnsFalse() {
        when(mockClaimRepository.findById(anyString())).thenReturn(Optional.of(mockClaim));
        when(mockClaim.get(anyString(), anyInt(), any(GuardQuestion.class.getClass()))).thenReturn(Optional.of(mockGuardQuestion));
        when(mockGuardQuestion.getChoice()).thenReturn(Boolean.FALSE);

        final String claimId = "123e4567-e89b-12d3-a456-426655440000";
        boolean actualCurrent = sut.hasCurrentPension(claimId);

        assertFalse(actualCurrent);
    }

    @Test
    public void whenLessThanMaxAllowed_thenReturnFalse() {
        when(mockClaim.count(anyString(), eq(Constants.MAX_PENSIONS_ALLOWED))).thenReturn(0);

        boolean result = sut.hasMoreThanMaxAllowed(mockClaim);

        assertFalse(result);
    }

    @Test
    public void whenMoreThanMaxAllowed_thenReturnTrue() {
        when(mockClaim.count(anyString(), eq(Constants.MAX_PENSIONS_ALLOWED))).thenReturn(MAX_PENSIONS_ALLOWED);
        Optional<Question> loopEndQuestionOptional = Optional.of(new LoopEndBooleanQuestion(true, true));
        when(mockClaim.get(HasAnotherCurrentPensionController.IDENTIFIER)).thenReturn(loopEndQuestionOptional);

        boolean result = sut.hasMoreThanMaxAllowed(mockClaim);

        assertTrue(result);
    }

    @Test
    public void whenMaxAllowedButNoMore_thenReturnFalse() {
        when(mockClaim.count(anyString(), eq(Constants.MAX_PENSIONS_ALLOWED))).thenReturn(MAX_PENSIONS_ALLOWED);
        Optional<Question> loopEndQuestionOptional = Optional.of(new LoopEndBooleanQuestion(true, false));

        boolean result = sut.hasMoreThanMaxAllowed(mockClaim);

        assertFalse(result);
    }

    private StepInstance createStepInstance(final String identifier, final int counter, final Section section) {
        return new StepInstance(
                new Step(identifier, null, null, section),
                counter,
                false,
                false,
                false
        );
    }
}
