package com.elitecore.elitesm.hibernate.core.system.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Id;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;

/**
 * Unique Id Generator for aaasmx
 * 
 * @author nayana.rathod
 *
 */
public class UUIDGenerator implements IdentifierGenerator{

	private static final String MODULE="ELITE-UUID-GEN";
	
	public UUIDGenerator(){}
	  
	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		
		List<Method> methods = getAllMethodsAnnotatedWith(object.getClass(), Id.class);

		if(methods.size() != 1) {
			return removeHyphenFromUUID(UUID.randomUUID().toString());
		}

		try {

			Object id = methods.get(0).invoke(object, new Object[]{});
			if (id == null) {
				return removeHyphenFromUUID(UUID.randomUUID().toString());
			}

			if (id instanceof String) {
				if (Strings.isNullOrBlank((String) id)) {
					return removeHyphenFromUUID(UUID.randomUUID().toString());
				} else {
					return (String) id;
				}
			} else if(id instanceof  Serializable) {
				return (Serializable)id;
			} else {
				return removeHyphenFromUUID(UUID.randomUUID().toString());
			}


		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to generate Id. Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}

		return removeHyphenFromUUID(UUID.randomUUID().toString());
	}
	
	private List<Method> getAllMethodsAnnotatedWith(Class<?> classToQuery, Class<? extends Annotation> annotationClass){
		List<Method> list = new ArrayList<Method>();
		for(Method method : classToQuery.getMethods()){
			if(method.isAnnotationPresent(annotationClass)){
				list.add(method);
			}
		}
		return list;
	}
	
	public static String generate() {
		return removeHyphenFromUUID(UUID.randomUUID().toString());
	}
	
	private static String removeHyphenFromUUID(String uuid){
		return uuid.replaceAll("[\\s\\-()]", "");
	}
}
