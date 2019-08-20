package com.elitecore.aaa.ws.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import com.elitecore.aaa.ws.authentication.AAARestSecurityConfig;
import com.elitecore.aaa.ws.controller.AAARestController;
import com.elitecore.aaa.ws.exceptionmapper.ClientErrorExceptionMapper;
import com.elitecore.aaa.ws.exceptionmapper.JaxbExceptionMapper;
import com.elitecore.aaa.ws.exceptionmapper.URLNotFoundExceptionMapper;

/**
 * <p> This is responsible for creating JAXRSServerFactoryBean including services controller,
 * Exception mappers, providers(like XML, JSON parser, JAXB bean validator) configurations. </p>
 * 
 * @author chirag i prajapati
 */

@Configuration
@Import(AAARestSecurityConfig.class)
public class AAARestConfig {

	@Bean(destroyMethod = "shutdown")
	public SpringBus cxf() {
		return new SpringBus();
	}

	@Bean
	@DependsOn("cxf")
	public Server jaxRsServer() {

		JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint(getAAARestApplication(),
				JAXRSServerFactoryBean.class);

		factory.setServiceBeans(Arrays.<Object>asList(getRootController()));

		factory.setAddress(factory.getAddress());

		/* providers (like exception mapper, jsonprovider etc.) */
		factory.setProviders(getProviders());

		/* extension mappings */
		Map<Object, Object> extensions = new HashMap<>();
		extensions.put("json", MediaType.APPLICATION_JSON);
		extensions.put("xml", MediaType.APPLICATION_XML);
		factory.setExtensionMappings(extensions);

		/*
		 * default interceptor by cxf for showing details incoming request in log
		 */
		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());

		return factory.create();
	}

	@Bean
	public AAARestApplication getAAARestApplication() {
		return new AAARestApplication();
	}

	@Bean
	public AAARestController getRootController() {
		return new AAARestController();
	}

	private List<Object> getProviders() {
		List<Object> providers = new ArrayList<>();
		providers.add(new JacksonJaxbJsonProvider());
		providers.add(new JAXBElementProvider<>());

		providers.add(new ClientErrorExceptionMapper());
		providers.add(new URLNotFoundExceptionMapper());
		providers.add(new JaxbExceptionMapper());
		return providers;
	}

}

@ApplicationPath("/rest")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
class AAARestApplication extends Application {
	
}