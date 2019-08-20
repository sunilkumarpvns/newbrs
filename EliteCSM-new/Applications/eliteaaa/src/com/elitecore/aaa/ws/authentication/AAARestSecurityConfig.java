package com.elitecore.aaa.ws.authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * <p> This is responsible for authentication of REST API. It uses SM admin user's credentials using which you have created server instance .</p>
 * 
 * <p> If credentials are valid than only resource will be accessible otherwise end user get response with proper message and <b>Result code-403 Forbidden.</b><p>
 * 
 * <p> <b> Note: </b> Currently on every operation this authentication applied in future we will skip checking on GET type of HTTP method.</p>
 * 
 *  <pre> REST request flow with authentication
 *  	+-------------------------------------------------------------------------------------------+
 *      |                  +-----------------<-----------------------<--------------+               |
 *      |                  |           (response with user requested resource)      |(SUCESS)       |
 *      |                  |                                                        |               |
 *      |        +---------+------+           +---------------+             +-------+------+        |
 *      |  REQ   |                |           |               |  if valid   |              |        |
 *      --------->    REST server |  eligible |   Security    | credentials |    Resource  |        | 
 *      |  RES   |      (Jetty)   ------------>    Handler    -------------->              |        | 
 *      <---------                |           |               |             |              |        |
 *      |        +--------+-------+           +-------+-------+             +--------------+        | 
 *      |                 |                           |                                             |
 *      |                 |                           |invalid credential                           |
 *      |                 +-----<------------<--------+                                             |
 *      |                 (response with proper message)                                            |
 *      +-------------------------------------------------------------------------------------------+
 *      
 *  </pre>
 * @author chirag i. prajapati
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class AAARestSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new RESTAuthenticationHandler());
    }

    @Override
	protected void configure(HttpSecurity http) throws Exception {
    	
    	http.csrf().disable();
    	
    	http.httpBasic().authenticationEntryPoint(new RESTBasicAuthenticationEntryPoint());
    	
        http
           .httpBasic().and()
           .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
           .authorizeRequests().antMatchers("/eliteaaa/**").hasRole( "USER" );
    }
}
