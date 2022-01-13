package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.security.JWTGeneratorFilter;
import uk.gov.dwp.jsa.citizen_ui.security.PrivateKeyProvider;
import uk.gov.dwp.jsa.security.JWTConfigurer;
import uk.gov.dwp.jsa.security.TokenProvider;

import static uk.gov.dwp.jsa.citizen_ui.Constants.SESSION_TIMEOUT;

/**
 * Spring Boot Security Configuration.
 * {@inheritDoc}
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClaimRepository claimRepository;
    private final int sessionTimeout;
    private final TokenProvider tokenProvider;
    private final Environment environment;
    private final boolean agentMode;
    private PrivateKeyProvider privateKeyProvider;
    private final boolean isSecure;

    @Autowired
    public SecurityConfig(
            final ClaimRepository claimRepository,
            final TokenProvider tokenProvider,
            @Value("${" + SESSION_TIMEOUT + "}") final int sessionTimeout,
            final Environment environment,
            @Value("${" + Constants.AGENT_MODE + "}") final boolean pAgentMode,
            final PrivateKeyProvider pPrivateKeyProvider) {
        this.claimRepository = claimRepository;
        this.sessionTimeout = sessionTimeout;
        this.tokenProvider = tokenProvider;
        this.environment = environment;
        this.agentMode = pAgentMode;
        this.privateKeyProvider = pPrivateKeyProvider;
        this.isSecure = !this.environment.acceptsProfiles(Profiles.of("nosecure", "local_test"));
    }

    @Override
    public final void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/assets/**", "**/favicon.ico");
    }

    @Override
    protected final void configure(final HttpSecurity http)
            throws Exception {

        http
                .apply(new JWTConfigurer(tokenProvider, environment))
                .and()
                .headers()
                    .xssProtection().disable()
                    .httpStrictTransportSecurity().disable()
                .and()
                .addFilterBefore(
                        new SessionValidationFilter(claimRepository, sessionTimeout),
                        BasicAuthenticationFilter.class);

        if (!this.agentMode) {
            http.addFilterBefore(new JWTGeneratorFilter(this.privateKeyProvider, this.isSecure),
                    UsernamePasswordAuthenticationFilter.class);
        }
    }
}
