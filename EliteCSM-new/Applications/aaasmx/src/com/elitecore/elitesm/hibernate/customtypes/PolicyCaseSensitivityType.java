package com.elitecore.elitesm.hibernate.customtypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

public class PolicyCaseSensitivityType implements UserType{

	@Override
	public Class<String> returnedClass() {
		return String.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}
	
	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names,
			SessionImplementor session, Object arg3)
			throws HibernateException, SQLException {
		String value = (String)StandardBasicTypes.STRING.nullSafeGet(resultSet, names[0], session);
		
		String policyCaseSensitivity = ConfigManager.policyCaseSensitivity;
		
		if(Strings.isNullOrEmpty(policyCaseSensitivity) == false){
			if(ConfigManager.LOWER_CASE.equalsIgnoreCase(policyCaseSensitivity)){
				value = value.toLowerCase();
			}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(policyCaseSensitivity)){
				value = value.toUpperCase();
			}
		}
	     return ((value != null) ? new String(value) : null); 
	}

	@Override
	public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index,
			SessionImplementor session)
			throws HibernateException, SQLException {

		String policyCaseSensitivity = ConfigManager.policyCaseSensitivity;
		
		if(Strings.isNullOrEmpty(policyCaseSensitivity) == false){
			if(ConfigManager.LOWER_CASE.equalsIgnoreCase(policyCaseSensitivity)){
				value = value.toString().toLowerCase();
			}else if(ConfigManager.UPPER_CASE.equalsIgnoreCase(policyCaseSensitivity)){
				value = value.toString().toUpperCase();
			}
		}
		StandardBasicTypes.STRING.nullSafeSet(preparedStatement, (value != null && value.toString().trim().length() > 0) ? value.toString() : null, index, session);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		 return original;
	}

	/* "default" implementations */
	
	@Override
	public Object assemble(Serializable cached, Object arg1) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return ObjectUtils.equals(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		assert (x != null);
	     return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}	
}