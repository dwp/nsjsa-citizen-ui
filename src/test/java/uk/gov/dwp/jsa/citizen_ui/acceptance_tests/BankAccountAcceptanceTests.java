package uk.gov.dwp.jsa.citizen_ui.acceptance_tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.controller.BankAccountFormController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.SortCode;
import uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountQuestion;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class })
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class BankAccountAcceptanceTests {
    public static final String FORM_BANK_ACCOUNT = "/form/bank-account";
    @Autowired private MockMvc mockMvc;
    @Autowired private ClaimRepository claimRepository;

    @Before
    public void setUp() {
        claimRepository.deleteAll();
    }

    @After
    public void tearDown() {
        claimRepository.deleteAll();
    }

    @Test
    public void GetBankAccount_ReturnsBankAccountForm() throws Exception {
        mockMvc.perform(get(FORM_BANK_ACCOUNT).with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"bank-account\"")))
                .andExpect(content().string(containsString("id=\"account-holder\"")))
                .andExpect(content().string(containsString("id=\"account-number\"")))
                .andExpect(content().string(containsString("id=\"reference-number\"")))
                .andExpect(content().string(containsString("id=\"sortCode\"")));
    }


    @Test
    public void GivenValidBankAccount_PostForm_ReturnsSuccess() throws Exception {
        String holder = "John Doe";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("11-22-33");

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode()))
                .andExpect(status().isFound());

        Claim claim = claimRepository.findAll().iterator().next();
        Optional<BankAccountQuestion> bankAccountQuestion =
                claim.get(BankAccountFormController.IDENTIFIER, BankAccountQuestion.class);
        assertThat(bankAccountQuestion.get().getAccountHolder(), is(holder));
        assertThat(bankAccountQuestion.get().getAccountNumber(), is(accountNumber));
        assertThat(bankAccountQuestion.get().getSortCode().getCode(), is(sortCode.getCode()));
    }

    @Test
    public void GivenValidBankAccountWithReference_PostForm_ReturnsSuccess() throws Exception {
        String holder = "John Doe";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("112233");
        String reference = "Ref123";

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode())
                        .param("question.referenceNumber", reference))
                .andExpect(status().isFound());

        Claim claim = claimRepository.findAll().iterator().next();
        Optional<BankAccountQuestion> bankAccountQuestion =
                claim.get(BankAccountFormController.IDENTIFIER, BankAccountQuestion.class);
        assertThat(bankAccountQuestion.get().getAccountHolder(), is(holder));
        assertThat(bankAccountQuestion.get().getAccountNumber(), is(accountNumber));
        assertThat(bankAccountQuestion.get().getSortCode().getCode(), is(sortCode.getCode()));
    }

    @Test
    public void GivenInvalidFields_PostForm_ShowsErrorMessages() throws Exception {
        String holder = "John~~Doe";
        String accountNumber = "ab123456";
        SortCode sortCode = new SortCode("2233");
        String reference = "Ref~~123";

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode())
                        .param("question.referenceNumber", reference))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("bankaccount.holder.pattern.error")))
                .andExpect(content().string(containsString("bankaccount.accountnumber.pattern.error")))
                .andExpect(content().string(containsString("bankaccount.sortcode.invalid")))
                .andExpect(content().string(containsString("bankaccount.reference.pattern.invalid")));
    }

    @Test
    public void GivenLongAccountHolder_PostForm_ShowsErrorMessages() throws Exception {
        String holder = "this string holder is longer than eighty one characters so it should throw an error";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("112233");

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("bankaccount.holder.length.error")));
    }

    /**
     * These tests checks that the name allows these characters [alpha, full-stop, apostrophe, dash]
     * anywhere including the end
     */
    @Test
    public void GivenDashAnywhereInBankHoldersName_PostForm_Accepts() throws Exception {

        String holder = "Mrs Jane-Smith-";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("112233");
        String reference = "Ref123";

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode())
                        .param("question.referenceNumber", reference))
                .andExpect(status().isFound());
    }

    @Test
    public void GivenFullStopAnywhereInBankHoldersName_PostForm_Accepts() throws Exception {

        String holder = "Mrs. Jane-Smith's.";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("112233");
        String reference = "Ref123";

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode())
                        .param("question.referenceNumber", reference))
                .andExpect(status().isFound());
    }

    @Test
    public void GivenApostropheAnywhereInBankHoldersName_PostForm_Accepts() throws Exception {

        String holder = "Mrs. Jane-Smith's'";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("112233");
        String reference = "Ref123";

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode())
                        .param("question.referenceNumber", reference))
                .andExpect(status().isFound());
    }

    @Test
    public void GivenAlphaAnywhereInBankHoldersName_PostForm_Accepts() throws Exception {

        String holder = "Mrs Jane Smith";
        String accountNumber = "12345678";
        SortCode sortCode = new SortCode("112233");
        String reference = "Ref123";

        mockMvc.perform(
                post(FORM_BANK_ACCOUNT).with(csrf()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("question.accountHolder", holder)
                        .param("question.accountNumber", accountNumber)
                        .param("question.sortCode.code", sortCode.getCode())
                        .param("question.referenceNumber", reference))
                .andExpect(status().isFound());
    }
}
