package uk.gov.dwp.jsa.citizen_ui.services.postclaim;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.jsa.citizen_ui.services.postclaim.CircumstancesTest.VERSION;

@RunWith(MockitoJUnitRunner.class)
public class BankDetailsServiceTest {
    private static final UUID CLAIMANT_ID = UUID.randomUUID();

    private static final String ACCOUNT_HOLDER = "Test Bank Account";
    private static final String ACCOUNT_NUMBER = "1234567";
    private static final String TEST_REFERENCE = "test reference";
    private static final String FORMATTED_SORT_CODE = "11-22-33";
    private static final String SANITISED_SORT_CODE = FORMATTED_SORT_CODE.replace("-", "");
    @Mock
    private ClaimRepository mockClaimRepository;

    @Mock
    private Claim mockClaim;

    private static final UUID CLAIM_ID = UUID.randomUUID();

    private BankDetailsService bankDetailsService;

    @Before
    public void setUp() {
        when(mockClaimRepository.findById(CLAIM_ID.toString())).thenReturn(of(mockClaim));
        bankDetailsService = new BankDetailsService(mockClaimRepository, VERSION);
        when(mockClaim.getClaimantId()).thenReturn(CLAIMANT_ID.toString());
    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptDataSuccessfully() {
        givenBankAccountQuestionIsSetWithSortCode(SANITISED_SORT_CODE);

        Optional<BankDetails> dataFromClaimOptional = bankDetailsService.getDataFromClaim(CLAIM_ID);

        thenAllBankDetailFieldsAreRetrievedSuccessfully(dataFromClaimOptional.get(), SANITISED_SORT_CODE);
    }

    @Test
    public void getDataFromClaimRetrievesAndAdaptDataSuccessfullySendingFormattedSortCode() {
        givenBankAccountQuestionIsSetWithSortCode(FORMATTED_SORT_CODE);

        Optional<BankDetails> dataFromClaimOptional = bankDetailsService.getDataFromClaim(CLAIM_ID);

        thenAllBankDetailFieldsAreRetrievedSuccessfully(dataFromClaimOptional.get(), SANITISED_SORT_CODE);
    }

    @Test
    public void getDataFromClaimReturnsEmptyIfClaimNotFound() {
        when(mockClaimRepository.findById(CLAIM_ID.toString())).thenReturn(empty());

        Optional<BankDetails> dataFromClaimOptional = bankDetailsService.getDataFromClaim(CLAIM_ID);

        Assert.assertThat(dataFromClaimOptional.isPresent(), is(false));
    }

    private void thenAllBankDetailFieldsAreRetrievedSuccessfully(final BankDetails dataFromClaim,
                                                                 final String expectedSortCode) {
        assertThat(dataFromClaim.getClaimantId(), is(CLAIMANT_ID));
        assertThat(dataFromClaim.getAccountNumber(), is(ACCOUNT_NUMBER));
        assertThat(dataFromClaim.getAccountHolder(), is(ACCOUNT_HOLDER));
        assertThat(dataFromClaim.getSortCode(), is(expectedSortCode));
        assertThat(dataFromClaim.getReference(), is(TEST_REFERENCE));
    }

    private void givenBankAccountQuestionIsSetWithSortCode(final String sortCode) {
        BankAccountQuestion bankAccountQuestion = new BankAccountQuestion();
        bankAccountQuestion.setAccountHolder(ACCOUNT_HOLDER);
        bankAccountQuestion.setAccountNumber(ACCOUNT_NUMBER);
        bankAccountQuestion.setSortCode(new SortCode(sortCode));
        bankAccountQuestion.setReferenceNumber(TEST_REFERENCE);
        when(mockClaim.get(BankAccountFormController.IDENTIFIER)).thenReturn(of(bankAccountQuestion));
    }
}
