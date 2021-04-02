package com.okta.spring.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/resources/**", "/forgot-password", "/verify", "/change-password").permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/custom-login")
                .loginProcessingUrl("/")
                .permitAll()
                .and()

                .logout()
                .logoutUrl("/")
                .permitAll();
    }
    // @formatter:on

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}