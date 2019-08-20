package com.elitecore.corenetvertex.pkg;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import javax.persistence.Id;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.util.ReflectionUtil;

/**
 * Unique Id Generator for whole nvsmx
 * 
 * @author aditya.shrivastava
 *
 */
public class EliteSequenceGenerator implements  IdentifierGenerator {

 private static final String MODULE="ELITE-SEQ-GEN";
  public EliteSequenceGenerator(){
    	
    }
	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		LogManager.getLogger().debug(MODULE,"Method called generate");
		List<Method> methods = ReflectionUtil.getAllMethodsAnnotatedWith(object.getClass(), Id.class);

		if(methods.size() != 1) {
			return UUID.randomUUID().toString();
		}

		try {

			Object id = methods.get(0).invoke(object, new Object[]{});
			if (id == null) {
				return UUID.randomUUID().toString();
			}

			if (id instanceof String) {
				if (Strings.isNullOrBlank((String) id)) {
					return UUID.randomUUID().toString();
				} else {
					return (String) id;
				}
			} else if(id instanceof  Serializable) {
				return (Serializable)id;
			} else {
				return UUID.randomUUID().toString();
			}


		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to generate Id. Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}

		return UUID.randomUUID().toString();
	}
    
    
}
