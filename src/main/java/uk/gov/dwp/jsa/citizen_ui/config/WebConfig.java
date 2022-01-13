package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

import static uk.gov.dwp.jsa.citizen_ui.Constants.LANG_COOKIE_EXPIRY_SECONDS;
import static uk.gov.dwp.jsa.citizen_ui.Constants.LANG_COOKIE_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.LANG_PARAM_NAME;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CookieLocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setCookieName(LANG_COOKIE_ID);
        localeResolver.setCookieMaxAge(LANG_COOKIE_EXPIRY_SECONDS);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(LANG_PARAM_NAME);
        return interceptor;
    }

    @Bean
    public BackLinkInterceptor backLinkInterceptor() {
        return new BackLinkInterceptor();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(localeInterceptor());
        registry.addInterceptor(cacheWebContentInterceptor());
        registry.addInterceptor(backLinkInterceptor());
    }

    @Bean
    public CacheWebContentInterceptor cacheWebContentInterceptor() {
        return new CacheWebContentInterceptor();
    }

}
