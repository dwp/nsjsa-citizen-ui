package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.security.PrivateKeyProvider;
import uk.gov.dwp.jsa.security.TokenProvider;

import javax.servlet.Filter;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SecurityConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
public abstract class SecurityConfigTestBase {

    @MockBean
    private ClaimRepository claimRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private Environment environment;

    @MockBean
    private PrivateKeyProvider privateKeyProvider;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    Filter springSecurityFilterChain;

}
