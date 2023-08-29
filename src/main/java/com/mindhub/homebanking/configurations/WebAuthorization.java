package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
class WebAuthorization {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/index.html", "/web/js/index.js", "/web/css/**", "/web/img/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients").permitAll()
                .antMatchers("/api/login", "api/logout").permitAll()
                .antMatchers("/api/clients/{id}","/api/accounts","/api/accounts/{id}").hasAuthority("CLIENT")
                .antMatchers("/api/clients/current").hasAuthority("CLIENT")
                .antMatchers("/rest/**", "/api/**").hasAuthority("ADMIN")
                .antMatchers("/manager.html", "/manager.js").hasAuthority("ADMIN")
                .antMatchers("/h2-console").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/clients").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/accounts").hasAuthority("ADMIN")
                .antMatchers("/web/**").hasAnyAuthority("CLIENT","ADMIN")
                .anyRequest().denyAll();


        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        //disabling frameOptions so h2-console can be accessed
        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        return http.build();

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }

    }

}
