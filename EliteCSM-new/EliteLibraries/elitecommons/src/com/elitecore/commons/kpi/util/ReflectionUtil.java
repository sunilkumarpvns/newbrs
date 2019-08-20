package com.elitecore.commons.kpi.util;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

	public static <T> T createInstance(Class<T> clazz) throws Exception{
		try{
			return (T) clazz.newInstance();
		}catch (Throwable e) {
			String reasonForInstanceCreationFailure;
			
			if(e instanceof IllegalAccessException){
				reasonForInstanceCreationFailure = "Constructor for class: " + clazz.getSimpleName()
													+ " is not accessible. Default Constructor is not public.";
			}else if(e instanceof InstantiationException){ 
				if(clazz.isInterface()){
					reasonForInstanceCreationFailure = "Cannot create an instance of interface.";
				}else{
					reasonForInstanceCreationFailure = "Constructor with no-args not found or class is abstract";
				}
			}else{
				reasonForInstanceCreationFailure = "Cannot instantiate class: " + clazz.getSimpleName() + " due to reason: "
													+ e.getMessage();
			}
			throw new Exception(reasonForInstanceCreationFailure);
		}
	}

	public static <T> T createInstance(Class<T> clazz, Class<?>[] argTypes, Object...args){
		try{
			Constructor<T> constuctor = clazz.getConstructor(argTypes);
			return (T) constuctor.newInstance(args);
		}catch (Throwable e) {
			return null;
		}
	}
	
	public static List<Method> getAllMethodsAnnotatedWith(Class<?> classToQuery, Class<? extends Annotation> annotationClass){
		List<Method> list = new ArrayList<Method>();
		for(Method method : classToQuery.getMethods()){
			if(method.isAnnotationPresent(annotationClass)){
				list.add(method);
			}
		}
		return list;
	}
	
	public static List<Method> getDeclaredMethodsAnnotatedWith(Class<?> classToQuery, Class<? extends Annotation> annotationClass){
		List<Method> list = new ArrayList<Method>();
		for(Method method : classToQuery.getDeclaredMethods()){
			method.setAccessible(true);
			if(method.isAnnotationPresent(annotationClass)){
				list.add(method);
			}
		}
		return list;
	}
	
	public static List<Field> getAllFieldsAnnotatedWith(Class<?> classToQuery, Class<? extends Annotation> annotationClass){
		List<Field> list = new ArrayList<Field>();
		for(Field field : classToQuery.getFields()){
			if(field.isAnnotationPresent(annotationClass)){
				list.add(field);
			}
		}
		return list;
	}
	
	public static Method getMethodAnnotatedWith(Class<?> classToQuery, Class<? extends Annotation> annotationClass) throws Exception{
		Method methodContainingAnnotation = null;
		for(Method method : classToQuery.getMethods()){
			if(method.isAnnotationPresent(annotationClass)){
				methodContainingAnnotation = method;
				break;
			}
		}
		if(methodContainingAnnotation == null)
			throw new Exception("No method of class: " + classToQuery.getSimpleName()
								+ " is annotated with " + annotationClass.getSimpleName() 
								+ " or the method is not public.");
		
		return methodContainingAnnotation;
	}
	
	public static Object invokeMethodWithNoArgs(Object obj, String methodName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = obj.getClass().getDeclaredMethod(methodName, (Class<?>)null);
		return method.invoke(obj, (Object)null);
	}
	
	public static void injectFieldAnnotatedWith(Object obj, Object objToInsert, Class<? extends Annotation> annotation) throws IllegalArgumentException, IllegalAccessException{
		for(Field field : obj.getClass().getDeclaredFields()){
			if(field.isAnnotationPresent(annotation)){
				field.setAccessible(true);
				field.set(obj, objToInsert);
			}
		}
	}
	
	public static Field getFieldWithName(String fieldName, Class<?> classToQuery) throws SecurityException, NoSuchFieldException{
		Class<?> searchType = classToQuery;
		
		while(!Object.class.equals(searchType) && searchType != null){
			Field[] allFields = searchType.getDeclaredFields();
			for(Field field : allFields){
				if(field != null && field.getName().equals(fieldName)){
					field.setAccessible(true);
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		
		//Loop ends means the field is not found in whole inheritance hierarchy
		throw new NoSuchFieldException("Field with name: " + fieldName + " not found in class: " + classToQuery);
	}
}
