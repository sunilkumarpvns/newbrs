package com.elitecore.config.core;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.elitecore.config.core.annotations.Reloadable;
import com.elitecore.config.core.readerimpl.XMLReader;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.config.util.ReflectionUtil;

/**
 * 
 * This class is used to provide reload functionality to object or property having "@Reloadable" annotation
 * 
 * @author Jay Trivedi
 *
 */

public class Reloader {

	public static void reload(ConfigurationContext configurationContext, Configurable configurable, Reader reader) throws LoadConfigurationException {
		try{
			Configurable newConfigurable = reader.read(configurationContext, configurable.getClass());
			if(isTotallyReloadable(configurable)){
				BeanUtils.copyProperties(configurable, newConfigurable);
			}else{
				doRecursiveProcessing(configurable.getClass(), configurable, newConfigurable);
			}
		}catch(Exception e){
			throw new LoadConfigurationException("Error in reloading configuration for class: "	+ configurable.getClass().getSimpleName() + 
												". Reason: "+ e.getMessage(), e);
		}
	}
	
	private static boolean isTotallyReloadable(Configurable configurable){
		return configurable.getClass().isAnnotationPresent(Reloadable.class);
	}
	
	private static void doRecursiveProcessing(Class<?> clazz, Object obj,Object newObj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if(obj == null && newObj == null)
			return;
		
		List<Method> methods = ReflectionUtil.getAllMethodsAnnotatedWith(clazz, Reloadable.class);
		for(Method method : methods){

			boolean isCollection = isReturnTypeCollection(method);
			Reloadable reloadable = method.getAnnotation(Reloadable.class);
			Class<?> type = reloadable.type();
			
			if(isCollection){
				boolean isUserDefinedCollection = isUserDefinedType(type);

				if(isUserDefinedCollection){
					
					if(type.getAnnotation(Reloadable.class) != null){
						String setMethodName = removeAndPutSet(method.getName());
						Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
						setMethod.setAccessible(true);
						setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
					}else{
						compareCollectionAndReplace(type, method, obj, newObj);
					}
				}else{
					//The generic type in List is JAVA defined. so reload it completely
					String setMethodName = removeAndPutSet(method.getName());
					Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
					setMethod.setAccessible(true);
					setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
				}
			}else{
				//The return type is not collection but can be user defined type
				boolean isUserDefinedType = isUserDefinedType(type);

				if(isUserDefinedType){
					Object Obj1 = method.invoke(obj, new Object[]{});
					Object Obj2 = method.invoke(newObj, new Object[]{});
					if(type.getAnnotation(Reloadable.class) != null){
						String setMethodName = removeAndPutSet(method.getName());
						Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
						setMethod.setAccessible(true);
						setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
					}else{
						doRecursiveProcessing(type, Obj1, Obj2);
					}
				}else{
					String setMethodName = removeAndPutSet(method.getName());
					Method setMethod = clazz.getMethod(setMethodName, new Class<?>[]{method.getReturnType()});
					setMethod.setAccessible(true);
					setMethod.invoke(obj, method.invoke(newObj, new Object[]{}));
				}
			}
		}
	}
	
	/*
	 *Compare Collection Objects which are obtained using reflection.
	 *if both objects are equals then return true and do recursive processing.
	 */
	@SuppressWarnings("unchecked")
	private static void compareCollectionAndReplace(Class<?> type, Method method, Object configurableInstance, Object newInstance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{

		Collection<Object> cltnObj1 = (Collection<Object>) method.invoke(configurableInstance, new Object[]{});
		Collection<Object> cltnObj2 = (Collection<Object>) method.invoke(newInstance, new Object[]{});

		for(Object obj : cltnObj1){
			for(Object innerObj : cltnObj2){
				if(compare(obj, innerObj)){
					doRecursiveProcessing(type, obj, innerObj);
				}
			}
		}
	}

	/*
	 * Compare two Collection type of Objects 
	 * 
	 */
	private static boolean compare( Object obj1, Object obj2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		if(obj1 == null || obj2 == null)
			return false;

		try {
			Method equals = obj1.getClass().getMethod("equals", new Class<?>[]{Object.class});
			if(!isUserDefinedType(equals.getDeclaringClass())){
				return false;
			}
			return obj1.equals(obj2);
		} catch (NoSuchMethodException e) { 
			ignoreTrace(e);
		} catch (SecurityException e) {
			ignoreTrace(e);
		} 

		return false;
	}
	
	private static boolean isUserDefinedType(Class<?> type) {
		//this logic is dependent on explicit interface or annotation
		//return UserDefined.class.isAssignableFrom(type);
		
		//this logic is dependent on whether the same class loader loaded the class as this class
		//If yes then the same application class loader is used which means that this class
		//belongs to our application
		return XMLReader.class.getClassLoader().equals(type.getClassLoader());
	}

	private static boolean isReturnTypeCollection(Method method) {
		return Collection.class.isAssignableFrom(method.getReturnType());
	}
	
	private static String removeAndPutSet(String name){
		return name.replaceFirst("g", "s");
	}
}
