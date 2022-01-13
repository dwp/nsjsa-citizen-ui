package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Test;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import uk.gov.dwp.jsa.citizen_ui.security.JWTGeneratorFilter;

import java.util.List;

import static org.junit.Assert.assertTrue;

@TestPropertySource(properties = "agent.mode=true")
public class SecurityConfigAgentModeTest extends SecurityConfigTestBase {

    @Test
    public void test_JWTGeneratorFilterMustNotExistWhenAgentModeEnabled() {

        FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
        List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
        assertTrue(list.stream()
                .flatMap(chain -> chain.getFilters().stream())
                .noneMatch(filter -> filter.getClass().isAssignableFrom(JWTGeneratorFilter.class)));
    }
}
