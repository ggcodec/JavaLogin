package com.haotchen.server.config.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haotchen.server.mapper.AdminMapper;
import com.haotchen.server.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Configuration
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AdminMapper adminMapper;
    @Autowired
    RestAuthorizationEntryPoint restAuthorizationEntryPoint;
    @Autowired
    RestfulAccessDeniedHandler restfulAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/login",
                "/logout",
                "/doc.html",
                "/css/**",
                "/html/**",
                "/index.html",
                "/favicon.ico",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs/**"

        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // ???????????????????????????
        http.authorizeRequests()
                .antMatchers("/vc").permitAll()
                .anyRequest().authenticated()
                .and()
                .headers()
                .cacheControl();

        // ??????jwt?????????????????????
        http.addFilterBefore(jwtAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class);

        // ????????????????????????????????????????????????
        http.exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

                    }
                })
                // ?????????????????????????????????
                .accessDeniedHandler(restfulAccessDeniedHandler)
                // ????????????????????????????????????
                .authenticationEntryPoint(restAuthorizationEntryPoint);


        // ??????jwt,?????????csrf
        http.csrf().disable();

        // ??????token,??????session??????
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationConfigurer<AuthenticationManagerBuilder,
                UserDetailsService> authenticationConfigurer =
                auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsService(){
            @Override
            public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
                Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, s)
                        .eq(Admin::isEnabled, true));

                if (Objects.isNull(admin)) {
                    throw new UsernameNotFoundException("??????????????????! ");
                }
                return admin;
            }
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

}
