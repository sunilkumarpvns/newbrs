package com.app.config;

import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.app.model.Employee;

//ENABLE MVC,HTM
//COMPONENT SCAN
//PropertySource

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
@ComponentScan(basePackages= {"com.app"})
public class AppConfig {

	//ENV
	@Autowired
	private Environment env;
	
	//1. BasicDataSource
	@Bean
	public BasicDataSource ds() {
		BasicDataSource ds=new BasicDataSource();
		ds.setDriverClassName(env.getProperty("driver"));
		ds.setUrl(env.getProperty("url"));
		ds.setUsername(env.getProperty("user"));
		ds.setPassword(env.getProperty("pwd"));
		ds.setInitialSize(10);
		ds.setMinIdle(10);
		ds.setMaxIdle(20);
		ds.setMaxTotal(50);
		return ds;
	}
	
	//2. LSFB
	@Bean
	public LocalSessionFactoryBean sf() {
		LocalSessionFactoryBean sf=new LocalSessionFactoryBean();
		sf.setDataSource(ds());
		sf.setAnnotatedClasses(Employee.class);
		sf.setHibernateProperties(props());
		return sf;
	}
	
	private Properties props() {
		Properties p=new Properties();
		p.setProperty("hibernate.dialect", env.getProperty("dialect"));
		p.setProperty("hibernate.show_sql", env.getProperty("show-sql"));
		p.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hbm2ddl"));
		return p;
	}
	
	//3. HTM
	@Bean
	public HibernateTransactionManager htm() {
		return new HibernateTransactionManager(sf().getObject());
	}
	//4. IRVR
	@Bean
	public InternalResourceViewResolver viewResolver() {
		return new InternalResourceViewResolver("/WEB-INF/views/",".jsp");
	}
	
}
