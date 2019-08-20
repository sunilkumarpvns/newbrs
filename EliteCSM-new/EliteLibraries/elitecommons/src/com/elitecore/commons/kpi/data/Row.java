package com.elitecore.commons.kpi.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.util.ReflectionUtil;

public class Row {
	
	private List<ColumnData> columns;
	private Object annotedClass;
	
	public Row(Object annotedClass) {
		this.columns = new ArrayList<ColumnData>();
		this.annotedClass = annotedClass;
	}
	
	public void generateColumns() {
		ColumnData column = null;
		List<Method> columnList = ReflectionUtil.getAllMethodsAnnotatedWith(annotedClass.getClass(), Column.class);
		for (Method col : columnList) {
			Column columnNameAnnotation = col.getAnnotation(Column.class);
			column = new ColumnData(columnNameAnnotation.name(), annotedClass, col, columnNameAnnotation.type());
			columns.add(column);
		}
	}

	public List<ColumnData> getColumns() {
		return columns;
	}
	
	public Object getAnnotedClass() {
		return annotedClass;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof Row)) {
			return false;
		}
		
		return this.annotedClass == ((Row)obj).getAnnotedClass();
	}
	
	@Override
	public int hashCode() {
		return this.columns.hashCode();
	}
}
