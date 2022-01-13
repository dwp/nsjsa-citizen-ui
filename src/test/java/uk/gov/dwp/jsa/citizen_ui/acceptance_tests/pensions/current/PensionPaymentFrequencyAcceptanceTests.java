package uk.gov.dwp.jsa.citizen_ui.acceptance_tests.pensions.current;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.acceptance_tests.TrustSessionConfig;
import uk.gov.dwp.jsa.citizen_ui.model.form.currentwork.PaymentFrequency;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {App.class, TrustSessionConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local_test", "mockredis"})
public class PensionPaymentFrequencyAcceptanceTests {
    @Autowired
    private MockMvc mockMvc;
    private static int count = 1;

    @Test
    public void GetPaymentFrequency_ReturnsPaymentFrequencyForm() throws Exception {
        mockMvc.perform(get("/form/pensions/current/details/1/payment-frequency")
                .with(csrf())).andExpect(status().isOk())
                .andExpect(content().string(containsString("paymentFrequencyQuestion.paymentFrequency")));
    }

    @Test
    public void SubmitPaymentFrequencyForm_NetSelectedWithTwoDecimalPlaces_ReturnsNextScreen() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/payment-frequency", count))
                .param("count", "1")
                .param("paymentFrequencyQuestion.weeklyPaymentAmounts.net", "11.11")
                .param("paymentFrequencyQuestion.paymentFrequency", PaymentFrequency.WEEKLY.name())
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/pensions/current/details/1/pension-increase")));
    }

    @Test
    public void SubmitPaymentFrequencyForm_NetSelectedWithOneDecimalPlace_ReturnsNextScreen() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/payment-frequency", count))
                .param("count", "1")
                .param("paymentFrequencyQuestion.weeklyPaymentAmounts.net", "11.1")
                .param("paymentFrequencyQuestion.paymentFrequency", PaymentFrequency.WEEKLY.name())
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/pensions/current/details/1/pension-increase")));
    }

    @Test
    public void SubmitPaymentFrequencyForm_NetSelectedWithZeroDecimalPlaces_ReturnsNextScreen() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/payment-frequency", 1))
                .param("count", "1")
                .param("paymentFrequencyQuestion.weeklyPaymentAmounts.net", "11")
                .param("paymentFrequencyQuestion.paymentFrequency", PaymentFrequency.WEEKLY.name())
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", is("/form/pensions/current/details/1/pension-increase")));
    }

    @Test
    public void SubmitPaymentFrequencyForm_WithNoNetSelected_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/payment-frequency", 1))
                .param("count", "1")
                .param("paymentFrequencyQuestion.weeklyPaymentAmounts.net", "")
                .param("paymentFrequencyQuestion.paymentFrequency", PaymentFrequency.WEEKLY.name())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enter your gross pension")));
    }

    @Test
    public void SubmitPaymentFrequencyForm_WithNegativeAmount_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/payment-frequency", count))
                .param("count", "1")
                .param("paymentFrequencyQuestion.weeklyPaymentAmounts.net", "-20")
                .param("paymentFrequencyQuestion.paymentFrequency", PaymentFrequency.WEEKLY.name())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enter your gross pension")));
    }

    @Test
    public void SubmitPaymentFrequencyForm_WithNoPaymentReferenceSelected_ReturnsErrors() throws Exception {
        mockMvc.perform(post(String.format("/form/pensions/current/details/%s/payment-frequency", count))
                .param("count", "1")
                .param("paymentFrequencyQuestion.weeklyPaymentAmounts.net", "11.11")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Select how often your pension or annuity is paid")));
    }


}
