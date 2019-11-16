package com.myblog.config.security;

import com.myblog.config.security.filter.LoginTimeLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 *
 *  .formLogin().loginPage("/toLogin").loginProcessingUrl("/login").successForwardUrl("/index")
 *  请求方式是post
 *
 *  hasRole默认前缀为:ROLE_,所以省略前缀即可
 *
 *  EnableGlobalMethodSecurity注解等价于
        <security:global-method-security pre-post-annotations="enabled" proxy-target-class="true" />
 *
 *
 * ==========================================================

 错误：Refused to display  in a frame because it set 'X-Frame-Options' to 'deny'.
 同源下可以加载iframe
     DENY：浏览器拒绝当前页面加载任何Frame页面  （默认）
     SAMEORIGIN：frame页面的地址只能为同源域名下的页面
     ALLOW-FROM：origin为允许frame加载的页面地址。
    http.headers().frameOptions().sameOrigin();
 *
 */




@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)     //开启注解权限拦截方法
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("myUserDetailService")
    private MyUserdetailService userDetail;

    @Autowired
    @Qualifier("loginFailureHandler")
    private Failurelhandler failurelhandler;

    @Autowired
    private DataSource dataSource;


    @Resource(name = "LoginTimeLimitFilter")
    private LoginTimeLimitFilter loginTimeLimitFilter;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/toToLogin").fullyAuthenticated()
                //.antMatchers("/**").permitAll()
                //.antMatchers("/public").hasRole("BLOGGER")
                .and()
                .formLogin()
                .loginPage("/toLogin").loginProcessingUrl("/login").successForwardUrl("/")
                        .failureHandler(failurelhandler)
                        .and()
                        .logout().logoutUrl("/logout").logoutSuccessUrl("/index")
                         .and()
                .headers().frameOptions().sameOrigin().and()
                .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(3600)
                    .userDetailsService(userDetail)
                .and()
                .csrf().disable();

        //自定义限制登陆过滤器位置
        http.addFilterBefore(loginTimeLimitFilter, UsernamePasswordAuthenticationFilter.class);




    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(userDetail).passwordEncoder(getPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }





    //========================== Bean  =============================================
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl persistentTokenRepository = new JdbcTokenRepositoryImpl();
        // 将 DataSource 设置到 PersistentTokenRepository
        persistentTokenRepository.setDataSource(dataSource);
        // 第一次启动的时候自动建表（可以不用这句话，自己手动建表，源码中有语句的）
         //persistentTokenRepository.setCreateTableOnStartup(true);
        return persistentTokenRepository;
    }



}
