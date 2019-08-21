package com.app.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.app.entity.Employee;

@Configuration
public class MyConfig
{
	@Bean
    public DriverManagerDataSource dsObj()
    {
	DriverManagerDataSource ds=new DriverManagerDataSource();
	ds.setDriverClassName("com.mysql.jdbc.Driver");
	ds.setUrl("jdbc:mysql://localhost:3306/test");
	ds.setUsername("root");
	ds.setPassword("root");
    return ds;
    }   
	@Bean
	public Properties propsObj()
	{
		Properties ps=new Properties();
		ps.put("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
		ps.put("hibernate.show_sql","true");
		ps.put("hibernate.hbm2ddl.auto","update");
		return ps;
	}
	@Bean
	public LocalSessionFactoryBean lsObj()
	{
		LocalSessionFactoryBean ls=new LocalSessionFactoryBean();
		ls.setDataSource(dsObj());
		ls.setHibernateProperties(propsObj());
		ls.setAnnotatedClasses(Employee.class);
		return ls;
		
	}
	@Bean
	public HibernateTemplate htObj()
	{
		HibernateTemplate ht=new HibernateTemplate();
		ht.setSessionFactory(lsObj().getObject());
		return ht;
	}
	
}
