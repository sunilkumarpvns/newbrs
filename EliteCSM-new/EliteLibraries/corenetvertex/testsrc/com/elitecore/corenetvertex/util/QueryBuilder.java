package com.elitecore.corenetvertex.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.util.ReflectionUtil;
import com.elitecore.corenetvertex.spr.Table;

public class QueryBuilder {

    
    /**
     * Syntax:
     * 
     * CREATE TABLE table_name
     * (
     * column_name1 data_type(size),
     * column_name2 data_type(size),
     * column_name3 data_type(size),
     * ....
     * );
     * 
     */
    public static String buildCreateQuery(Class<?> clazz) throws IllegalStateException, NullPointerException {

	StringBuilder builder = new StringBuilder("CREATE TABLE ");

	builder.append(extractTableName(clazz)).append("(");

	appendColums(builder, clazz);

	builder.append(")");
	return builder.toString();
    }

    /**
     * Syntax:
     * 
     * INSERT INTO table_name (column1,column2,column3,...)
     * VALUES (value1,value2,value3,...);
     * 
     * NOTE: if value is null, it will be skipped in query
     */
    public static String buildInsertQuery(Object object) throws IllegalArgumentException, IllegalAccessException,
	    InvocationTargetException, NullPointerException {

	Map<Column, String> columnValue = generateKeyValue(object);

	StringBuilder builder = new StringBuilder("INSERT INTO ");

	builder.append(extractTableName(object.getClass())).append("(");

	StringBuilder columnBuilder = new StringBuilder();
	StringBuilder valueBuilder = new StringBuilder();

	for (Entry<Column, String> entry : columnValue.entrySet()) {
		columnBuilder.append(entry.getKey().name()).append(", ");
		DBTypes.fromId(entry.getKey().type()).setVal(valueBuilder, entry.getValue());
		valueBuilder.append(", ");

	}

	columnBuilder.delete(columnBuilder.length() - 2, columnBuilder.length());
	valueBuilder.delete(valueBuilder.length() - 2, valueBuilder.length());

	builder.append(columnBuilder.toString()).append(") values (").append(valueBuilder.toString()).append(")");

	return builder.toString();
    }

    private static Map<Column, String> generateKeyValue(Object object) throws IllegalArgumentException, IllegalAccessException,
	    InvocationTargetException {

	Map<Column, String> columnValues = new HashMap<Column, String>();

	List<Method> methods = getMethods(object.getClass());

	for (Method method : methods) {
	    Column column = method.getAnnotation(Column.class);
	    Object value = method.invoke(object);
	    if(value != null) {
		columnValues.put(column, value.toString());
	    }
	}

	return columnValues;
    }

    public static List<Method> getMethods(Class<?> clazz) {
	List<Method> methods = ReflectionUtil.getAllMethodsAnnotatedWith(clazz, Column.class);
	return methods;
    }

    private static void appendColums(StringBuilder builder, Class<?> clazz) {

	List<Method> methods = getMethods(clazz);

	for (Method method : methods) {
	    Column column = method.getAnnotation(Column.class);
	    DBTypes type = DBTypes.fromId(column.type());
	    builder.append(column.name()).append(" ").append(type.name);

	    if (type.size != 0) {
		builder.append("(").append(type.size).append(")");
	    }
	    builder.append(", ");
	}

	builder.delete(builder.length() - 2, builder.length());
    }

    private static String extractTableName(Class<?> clazz) {
    Table table = clazz.getAnnotation(Table.class);
	return table.name();
    }

    /**
     * Syntax:
     * DROP TABLE table_name
     */
    public static String buildDropQuery(Class<?> clazz) {
	return new StringBuilder("DROP TABLE ").append(extractTableName(clazz)).toString();
    }
    
    /**
     * Syntax:
     * 
     * UPDATE table_name
     * SET column1=value1, column2=value2,...
     * WHERE some_column=some_value;
     * 
     */
    //FIXME need to think about where clause params
    public static String buildUpdateQuery(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	StringBuilder builder = new StringBuilder("UPDATE ");
	builder.append(extractTableName(object.getClass())).append(" SET (");
	
	//TODO 
	
	return builder.toString();
    }
    
    public static String buildDeleteQuery(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	StringBuilder builder = new StringBuilder("DELETE FROM ");
    	builder.append(extractTableName(object.getClass())).append(" WHERE ");
    	
    	//TODO 
    	
    	return builder.toString();
        }
}
