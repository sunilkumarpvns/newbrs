package com.app;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AppConfig
{
    @Bean
	public DriverManagerDataSource dsObj()
	{
		DriverManagerDataSource d=new DriverManagerDataSource();
		d.setDriverClassName("com.mysql.jdbc.Driver");
		d.setUrl("jdbc:mysql://localhost:3306/test");
		d.setUsername("root");
	    d.setPassword("root");
	    return d;
	}
    @Bean
    public JdbcTemplate jtObj()
    {
    	JdbcTemplate jt=new JdbcTemplate();
    	jt.setDataSource(dsObj());
    	return jt;
    }
}
