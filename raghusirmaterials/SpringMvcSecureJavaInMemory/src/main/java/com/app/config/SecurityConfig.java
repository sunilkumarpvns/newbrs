package com.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	//1.un,pwd,role(authority)
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("{noop}").authorities("ADMIN");
		auth.inMemoryAuthentication().withUser("sam").password("{noop}sam").authorities("EMP");
		auth.inMemoryAuthentication().withUser("ram").password("{noop}ram").authorities("STD");
	}
	
	//2.url -levels
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/all").permitAll()
		.antMatchers("/admin").hasAuthority("ADMIN")
		.antMatchers("/emp").hasAuthority("EMP")
		.anyRequest().authenticated()
		
		
		.and()
		.formLogin()
		.defaultSuccessUrl("/view")
		
		.and()
		.logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		
		.and()
		.exceptionHandling()
		.accessDeniedPage("/denied")
		
		;
		
		
		
	}
	
	
}
