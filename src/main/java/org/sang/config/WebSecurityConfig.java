package org.sang.config;

import org.sang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Autowired
    CustomizeAuthenticationEntryPoint customizeAuthenticationEntryPoint;

    @Autowired
    CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;

    @Autowired
    CustomizeAuthenticationFailureHandler customizeAuthenticationFailureHandler;

    @Autowired
    CustomizeLogoutSuccessHandler customizeLogoutSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/knowledge/mobile").permitAll() // 不通过验证即可登录
//                .antMatchers("/admin/category/all").authenticated()
                .antMatchers("/admin/**","/reg").hasRole("超级管理员")///admin/**的URL都需要有超级管理员角色，如果使用.hasAuthority()方法来配置，需要在参数中加上ROLE_,如下.hasAuthority("ROLE_超级管理员")
                .anyRequest().authenticated()
                .and().formLogin()
                .successHandler(customizeAuthenticationSuccessHandler) // 登录成功
                .failureHandler(customizeAuthenticationFailureHandler) // 登录失败
//                .loginProcessingUrl("/login")
//                .usernameParameter("username").passwordParameter("password").permitAll()
                //异常处理(权限拒绝、登录失效等)
                .and().exceptionHandling()
                .authenticationEntryPoint(customizeAuthenticationEntryPoint)  //匿名用户访问无权限资源时的异常处理
                .and().logout().permitAll().logoutSuccessHandler(customizeLogoutSuccessHandler).deleteCookies("JSESSIONID")
                .and().csrf().disable().exceptionHandling().accessDeniedHandler(getAccessDeniedHandler());
//        http.formLogin().and().cors();
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/blogimg/**","/index.html","/static/**");
    }

    @Bean
    AccessDeniedHandler getAccessDeniedHandler() {
        return new AuthenticationAccessDeniedHandler();
    }
}