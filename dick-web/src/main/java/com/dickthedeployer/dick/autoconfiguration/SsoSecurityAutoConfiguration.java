/*
 * Copyright dick the deployer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dickthedeployer.dick.autoconfiguration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ClientConfiguration;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
@AutoConfigureBefore({OAuth2AutoConfiguration.class})
@ConditionalOnProperty(prefix = "security", name = "schema", havingValue = "oauth2")
public class SsoSecurityAutoConfiguration {


    @Configuration
    @EnableConfigurationProperties(OAuth2SsoProperties.class)
    @Import({OAuth2ClientConfiguration.class, ResourceServerTokenServicesConfiguration.class})
    public static class SsoWebSecurityAdapter extends WebSecurityConfigurerAdapter implements BeanFactoryAware {

        private BeanFactory beanFactory;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().anyRequest().and()
                    .authorizeRequests().anyRequest()
                    .authenticated().and().csrf()
                    .csrfTokenRepository(csrfTokenRepository()).and()
                    .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);

            OAuth2SsoProperties sso = this.beanFactory.getBean(OAuth2SsoProperties.class);
            // Delay the processing of the filter until we know the
            // SessionAuthenticationStrategy is available:
            http.apply(
                    (SecurityConfigurerAdapter) new OAuth2ClientAuthenticationConfigurer(oauth2SsoFilter(sso))
            );
            addAuthenticationEntryPoint(http, sso);
        }

        private OAuth2ClientAuthenticationProcessingFilter oauth2SsoFilter(
                OAuth2SsoProperties sso) {
            OAuth2RestOperations restTemplate = this.beanFactory
                    .getBean(OAuth2RestOperations.class);
            ResourceServerTokenServices tokenServices = this.beanFactory
                    .getBean(ResourceServerTokenServices.class);
            OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
                    sso.getLoginPath());
            filter.setRestTemplate(restTemplate);
            filter.setTokenServices(tokenServices);
            return filter;
        }

        private void addAuthenticationEntryPoint(HttpSecurity http, OAuth2SsoProperties sso)
                throws Exception {
            ExceptionHandlingConfigurer<HttpSecurity> exceptions = http.exceptionHandling();
            ContentNegotiationStrategy contentNegotiationStrategy = http
                    .getSharedObject(ContentNegotiationStrategy.class);
            if (contentNegotiationStrategy == null) {
                contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
            }
            MediaTypeRequestMatcher preferredMatcher = new MediaTypeRequestMatcher(
                    contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
                    new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
            preferredMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
            exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint(sso.getLoginPath()),
                    preferredMatcher);
            // When multiple entry points are provided the default is the first one
            exceptions.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        }

        private static class OAuth2ClientAuthenticationConfigurer
                extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

            private OAuth2ClientAuthenticationProcessingFilter filter;

            OAuth2ClientAuthenticationConfigurer(
                    OAuth2ClientAuthenticationProcessingFilter filter) {
                this.filter = filter;
            }

            @Override
            public void configure(HttpSecurity builder) throws Exception {
                OAuth2ClientAuthenticationProcessingFilter ssoFilter = this.filter;
                ssoFilter.setSessionAuthenticationStrategy(
                        builder.getSharedObject(SessionAuthenticationStrategy.class));
                builder.addFilterAfter(ssoFilter,
                        AbstractPreAuthenticatedProcessingFilter.class);
            }

        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        private Filter csrfHeaderFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    CsrfToken csrf = (CsrfToken) request
                            .getAttribute(CsrfToken.class.getName());
                    if (csrf != null) {
                        Cookie cookie = new Cookie("XSRF-TOKEN",
                                csrf.getToken());
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                    filterChain.doFilter(request, response);
                }
            };
        }

        private CsrfTokenRepository csrfTokenRepository() {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            repository.setHeaderName("X-XSRF-TOKEN");
            return repository;
        }
    }
}
