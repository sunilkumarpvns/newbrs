package com.elitecore.nvsmx.ws.util;


import com.elitecore.commons.logging.LogManager;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * CXF default JAXBProvider was not able to properly initialize JAXB context for ListWrapper.class, 
 * which is a generic class. 
 * In such a scenario one has to manually initialize the context by extending ContextResolver.
 * <pre>
 * for example:
 *  <b>com.elitecore.nvsmx.ws.util.ListWrapper</b>
 * </pre>
 * @author aditya shrivastava
 */

public class JAXBResolver implements ContextResolver<JAXBContext>{
	private static final String MODULE = "JAXB-RESOLVER";

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
			
		} catch (ClassNotFoundException | JAXBException e) {
			getLogger().error(MODULE,e.getMessage());
			LogManager.ignoreTrace(e);
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
