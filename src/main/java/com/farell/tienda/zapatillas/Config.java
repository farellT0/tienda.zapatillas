package com.farell.tienda.zapatillas;

import com.farell.tienda.zapatillas.interceptores.InterceptorAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class Config implements WebMvcConfigurer {

    private final InterceptorAdmin interceptorAdmin;

    public Config(InterceptorAdmin interceptorAdmin) {
        this.interceptorAdmin = interceptorAdmin;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorAdmin);
        registry.addInterceptor(localeChangeInterceptor());
    }

    // el idioma elegido se mantiene en la sesion del usuario
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.forLanguageTag("es"));
        return localeResolver;
    }

    // cambia el idioma cuando la url lleva ?idioma=es, ?idioma=en o ?idioma=fr
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setIgnoreInvalidLocale(true);
        localeChangeInterceptor.setParamName("idioma");
        return localeChangeInterceptor;
    }
}
