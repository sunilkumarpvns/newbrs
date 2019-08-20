package com.elitecore.corenetvertex.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.util.ReflectionUtil;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class DBHelper {

	
	public static <T> List<T> create(ResultSet resultSet, Class<T> clas) throws Exception {
	
		List<Method> methods = getMethods(clas);
		List<T> listToReturn = null;
		while (resultSet.next()) {
			
			if (listToReturn  == null) {
				listToReturn = new ArrayList<T>();
			}
			
			T instance = clas.newInstance();
			
			for (Method method : methods) {
				
					String setter;
					Method setterMethod;
					Column column = method.getAnnotation(Column.class);
					switch (column.type()) {
					case Types.VARCHAR:
						
						setter = createSetter(method.getName());
						setterMethod = clas.getMethod(setter, String.class);
						if (setterMethod == null) {
							throw new NoSuchMethodException(setter + " method not found"); 
						}
						setterMethod.invoke(instance, resultSet.getString(column.name()));
						
						break;
					case Types.NUMERIC:
						
						setter = createSetter(method.getName());
						setterMethod = clas.getMethod(setter, long.class);
						if (setterMethod == null) {
							throw new NoSuchMethodException(setter + " method not found"); 
						}
						setterMethod.invoke(instance, resultSet.getLong(column.name()));
						
						break;
					case Types.TIMESTAMP:
						
						setter = createSetter(method.getName());
						setterMethod = clas.getMethod(setter, long.class);
						if (setterMethod == null) {
							throw new NoSuchMethodException(setter + " method not found"); 
						}
						Timestamp timestamp = resultSet.getTimestamp(column.name());
						setterMethod.invoke(instance, timestamp.getTime());
						break;
					default:
						break;
					}
				}
			
			listToReturn.add(instance);
		}
		
		return listToReturn;
		
	}
	
	private static String createSetter(String getterMethodName) {

			return "s" + getterMethodName.substring(1);
	}

	public static List<Method> getMethods(Class<?> clazz) {
		List<Method> methods = ReflectionUtil.getAllMethodsAnnotatedWith(clazz, Column.class);
		return methods;
	}
}
