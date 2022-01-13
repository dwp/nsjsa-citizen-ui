package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Test;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import uk.gov.dwp.jsa.citizen_ui.security.JWTGeneratorFilter;
import uk.gov.dwp.jsa.security.JWTFilter;

import javax.servlet.Filter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class SecurityConfigCitizenModeTest extends SecurityConfigTestBase {

    @Test
    public void test_JWTGeneratorFilterMustBeBforeJWTTokenWhenAgentModeDisabled() {
        FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
        List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
        List<Class<? extends Filter>> filters = list.stream()
                .flatMap(chain -> chain.getFilters().stream())
                .map(Filter::getClass)
                .collect(Collectors.toList());
        assertTrue(filters.indexOf(JWTGeneratorFilter.class) != -1);
        assertTrue(filters.indexOf(JWTGeneratorFilter.class) < filters.indexOf(JWTFilter.class));
    }
}
