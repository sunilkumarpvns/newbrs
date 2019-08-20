package com.elitecore.elitesm.ws.rest.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

import com.elitecore.elitesm.ws.rest.util.ListWrapper;

/**
 * CXF default JAXBProvider was not able to properly initialize JAXB context for ListWrapper.class, 
 * which is a generic class. 
 * In such a scenario one has to manually initialize the context by extending ContextResolver.
 * <pre>
 * for example:
 *  <b>com.elitecore.elitesm.ws.rest.util.ListWrapper</b>
 * </pre>
 * @author animesh christie
 */

public class JAXBResolver implements ContextResolver<JAXBContext>{
	private JAXBContext getJAXBResolver(Message message){
		JAXBContext jc = null;

		OperationResourceInfo ori = message.getExchange().get(OperationResourceInfo.class);
		
		Method annotatedMethod = ori.getAnnotatedMethod();
		Type[] genericParameterTypes = annotatedMethod.getGenericParameterTypes();
		
		int size = 0;
		
		try {
			for(Type type : genericParameterTypes){
				if(type instanceof ParameterizedType){
					size++;
				}
			}
			Class<?>[] cls = new Class[size+1];
			cls[0] = ListWrapper.class;
			
			int i =1;
			for(Type type : genericParameterTypes){
				if(type instanceof ParameterizedType){
					cls[i] = Class.forName(((ParameterizedType)type).getActualTypeArguments()[0].toString().replace("class ", ""));
					i++;
				}
			}
			jc = JAXBContext.newInstance(cls);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return jc;
	}
	
	@Override
	public JAXBContext getContext(Class<?> clazz) {
		if(ListWrapper.class == clazz) {
            return getJAXBResolver(PhaseInterceptorChain.getCurrentMessage());
        }
		return null;
	}
}
