package com.elitecore.netvertexsm.hibernate.core.base;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.hibernate.core.system.util.HibernateDataSession;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseData;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;

public class HBaseDataManager implements DataManager {
	
	private static String MODULE = HBaseDataManager.class.getSimpleName();
    
    private IDataManagerSession dataManagerSession;
    
    public final void setDataManagerSession(IDataManagerSession session) {
        this.dataManagerSession = session;
    }

    protected final IDataManagerSession getDataManagerSession(){
        return dataManagerSession;
    }
    
    protected final HibernateDataSession getHibernateDataSession(){        
        if (dataManagerSession != null && dataManagerSession instanceof HibernateDataSession) {
            return (HibernateDataSession) dataManagerSession;
        }        
        return null;
    }
    
    protected final Session getSession(){
        HibernateDataSession dataSession = getHibernateDataSession();
        if (dataSession != null){
            return dataSession.getSession();
        }
        return null;
    }
    protected Timestamp getCurrentTimeStamp(Connection connection) throws Exception {
    	String dbProductName;
    	Timestamp timestamp = null;

    	Statement stmt = connection.createStatement(); 
    	dbProductName = connection.getMetaData().getDatabaseProductName();
    	String queryStringDt = "SELECT SYSDATE FROM DUAL";	

    	if(dbProductName.equalsIgnoreCase("postgresql")){
    		queryStringDt = "SELECT CURRENT_TIMESTAMP";
    	}

    	ResultSet rs1 = stmt.executeQuery(queryStringDt);
    	if(rs1.next()){
    		timestamp = rs1.getTimestamp(1);
    	}
    	rs1.close();
    	return timestamp;

    }
    protected Object getValueObject(String javaClassName,String fieldValue) throws SQLException {
    	Object object = new Object();
    	Logger.logDebug(MODULE, "Method called: getValueObject(String javaClassName,String fieldValue) , javaclass is: "+javaClassName+ "fieldValue is: "+fieldValue);
    	
    	try{
	    	if(javaClassName.equalsIgnoreCase("java.lang.String")){
	    		object = new String(fieldValue);
			} else if(javaClassName.equalsIgnoreCase("java.lang.Integer")) {
	    		object = new Integer(fieldValue);
	    	} else if(javaClassName.equalsIgnoreCase("java.lang.Long")) {
	    		object = new Long(fieldValue);
	    	} else if(javaClassName.toUpperCase().contains("TIMESTAMP")) {
	    		try {
	    			SimpleDateFormat sp = new SimpleDateFormat(ConfigManager.get("SHORT_DATE_FORMAT"));
	    			sp.setLenient(false);
	    			Date date = sp.parse(fieldValue);
	    			object = new java.sql.Timestamp(date.getTime());
				} catch (Exception e) {
					 throw new SQLException(e);
				}
	    	} else if(javaClassName.equalsIgnoreCase("java.math.BigDecimal")) {
	    	   	object = new BigDecimal(fieldValue);
	    	}
	    	return object;
    	} catch(Exception e) {
    		throw new SQLException(e);
    	}
    }
	protected void deleteObjectList(List<?> list,Session session){
		if(list!=null && !list.isEmpty()){
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if(obj != null){
					session.delete(obj);
					session.flush();
				}	
			}
		}
	}
	protected void deleteObjectSet(Set<?> set,Session session){
		if(set!=null && !set.isEmpty()){
			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if(obj != null){
					session.delete(obj);
					session.flush();
				}	
			}
		}
	}
	
	public void setUpdateAuditDetail(Object object) {
		if(object != null && object instanceof BaseData){		
			BaseData baseData = (BaseData)object;
			baseData.setClientIp(EliteUtility.getClientIP());
			baseData.setModifiedDate(EliteUtility.getCurrentTimeStamp());
			baseData.setModifiedByStaffId(EliteUtility.getCurrentUserId());
		}
	}
}