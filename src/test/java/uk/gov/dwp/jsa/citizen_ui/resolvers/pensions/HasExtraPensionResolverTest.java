package uk.gov.dwp.jsa.citizen_ui.resolvers.pensions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Pensions;
import uk.gov.dwp.jsa.citizen_ui.controller.pensions.current.HasAnotherCurrentPensionController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.LoopEndBooleanQuestion;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HasExtraPensionResolverTest {

    private HasExtraPensionResolver hasExtraPensionResolver = new HasExtraPensionResolver();

    @Mock
    private Claim mockClaim;

    @Test
    public void resolveSetHasExtrapensionsToTrue() {
        when(mockClaim.get(HasAnotherCurrentPensionController.IDENTIFIER))
                .thenReturn(Optional.of(new LoopEndBooleanQuestion(false, false)));
        when(mockClaim.get(HasAnotherCurrentPensionController.IDENTIFIER))
                .thenReturn(Optional.of(new LoopEndBooleanQuestion(true, true)));

        Circumstances circumstances = new Circumstances();
        circumstances.setPensions(new Pensions());
        hasExtraPensionResolver.resolve(mockClaim, circumstances);

        assertThat(circumstances.getPensions().isHasExtraPensions(), is(true));
    }

    @Test
    public void resolveSetHasExtrapensionsToFalse() {
        when(mockClaim.get(HasAnotherCurrentPensionController.IDENTIFIER))
                .thenReturn(Optional.of(new LoopEndBooleanQuestion(false, false)));

        Circumstances circumstances = new Circumstances();
        circumstances.setPensions(new Pensions());
        hasExtraPensionResolver.resolve(mockClaim, circumstances);

        assertThat(circumstances.getPensions().isHasExtraPensions(), is(false));
    }

}
