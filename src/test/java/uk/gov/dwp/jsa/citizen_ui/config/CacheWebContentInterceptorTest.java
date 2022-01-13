package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Test;
import org.springframework.http.CacheControl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CacheWebContentInterceptorTest {

    private CacheWebContentInterceptor interceptor;

    @Test
    public void configuresNoCacheHeader() {
        givenAnInterceptor();
        thenNoCacheIsConfigured();
    }

    private void givenAnInterceptor() {
        interceptor = new CacheWebContentInterceptor();
    }

    private void thenNoCacheIsConfigured() {
        final Map<String, CacheControl> cacheControlMappings = (Map<String, CacheControl>) ReflectionTestUtils.getField(interceptor, "cacheControlMappings");
        assertThat(cacheControlMappings.get("/**").getHeaderValue(), is(CacheControl.noCache().getHeaderValue()));
    }

}
