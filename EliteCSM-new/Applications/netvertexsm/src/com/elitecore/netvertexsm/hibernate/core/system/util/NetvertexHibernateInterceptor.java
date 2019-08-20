package com.elitecore.netvertexsm.hibernate.core.system.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class NetvertexHibernateInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,String[] propertyNames, Type[] types) {
		if(entity != null && entity instanceof BaseData){
			setValue(state, propertyNames, "createdDate", getCurrentTimeStamp());
			setValue(state, propertyNames, "createdByStaffId",EliteUtility.getCurrentUserId());
			setValue(state, propertyNames, "clientIp",EliteUtility.getClientIP());
			return true;
		}
		return false;
	}
	
	private void setValue(Object[] currentState, String[] propertyNames,String propertyToSet, Object value) {
		if(propertyNames != null && propertyNames.length > 0){
			for (int i = 0; i < propertyNames.length; i++) {
				if(propertyNames[i].equalsIgnoreCase(propertyToSet)){
					currentState[i] = value;
				}
			}
		}
	}
	
	private Timestamp getCurrentTimeStamp(){
		return new Timestamp(new Date().getTime());
	}
}
