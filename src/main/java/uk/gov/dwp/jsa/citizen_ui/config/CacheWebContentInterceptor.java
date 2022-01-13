package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.http.CacheControl;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

public class CacheWebContentInterceptor extends WebContentInterceptor {

    public CacheWebContentInterceptor() {
        super();
        addCacheMapping(CacheControl.noCache(), "/**");

    }
}
