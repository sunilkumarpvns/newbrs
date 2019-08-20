package com.elitecore.netvertexsm.hibernate.datasource.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetConfigurationValuesData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;

public class HDatabaseDSDataManager extends HBaseDataManager implements DatabaseDSDataManager{
	private static final String MODULE = "HDatabaseDSDataManager";
	
	
	private static final ArrayList<Integer> dataTypes ;
	private static final ArrayList<String> parameterIds;

	static{
		dataTypes = new ArrayList<Integer>();
		dataTypes.add(java.sql.Types.BIT);
		dataTypes.add(java.sql.Types.TINYINT);
		dataTypes.add(java.sql.Types.SMALLINT);
		dataTypes.add(java.sql.Types.INTEGER);
		dataTypes.add(java.sql.Types.BIGINT);
		dataTypes.add(java.sql.Types.FLOAT);
		dataTypes.add(java.sql.Types.REAL);
		dataTypes.add(java.sql.Types.DOUBLE);
		dataTypes.add(java.sql.Types.NUMERIC);
		dataTypes.add(java.sql.Types.DECIMAL);
		dataTypes.add(java.sql.Types.CHAR);
		dataTypes.add(java.sql.Types.VARCHAR);
		dataTypes.add(java.sql.Types.LONGVARCHAR);
		dataTypes.add(java.sql.Types.DATE);
		dataTypes.add(java.sql.Types.TIME);
		dataTypes.add(java.sql.Types.TIMESTAMP);
		dataTypes.add(java.sql.Types.BINARY);
		dataTypes.add(java.sql.Types.VARBINARY);
		dataTypes.add(java.sql.Types.LONGVARBINARY);
		dataTypes.add(java.sql.Types.BLOB);
		dataTypes.add(java.sql.Types.CLOB);
		
		parameterIds=new ArrayList<String>(3);
		parameterIds.add("CPM000415");//for primary datasource in usage metering
		parameterIds.add("CPM000416");//for secondary datasource in usage metering
		parameterIds.add("CPM000263");// for data soure in netvertex server
	}
	
	public void create(IDatabaseDSData databaseDSData) throws DataManagerException
	{
		EliteAssert.notNull(databaseDSData,"Database Datasource Data must not be null.");
		try{
			verifyDatabaseDSName(databaseDSData);
			Session session = getSession();
			session.save(databaseDSData);
		} catch (DuplicateInstanceNameFoundException dExp) {
			throw new DuplicateInstanceNameFoundException(dExp.getMessage(), dExp);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		
		
	}
	
	public void verifyDatabaseDSName(IDatabaseDSData databaseDSData) throws DataManagerException,DuplicateInstanceNameFoundException
	{
		
        List databaseDSList = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(DatabaseDSData.class);
            criteria.add(Restrictions.eq("name", databaseDSData.getName()));
            databaseDSList = criteria.list();
            Logger.logDebug(MODULE,"LIST SIZE IS:"+databaseDSList.size());
            if (databaseDSList != null && databaseDSList.size() > 0) {
            	throw new DuplicateInstanceNameFoundException("Duplicate DatabaseDSName.");
            }
            
        }catch(DuplicateInstanceNameFoundException dpfExp){
			throw new DuplicateInstanceNameFoundException("Duplicate DatabaseDSName.",dpfExp);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}	
	}
	
	public List getAllList() throws DataManagerException
	{
		List databasedsList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class);
			databasedsList = criteria.list();

		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return databasedsList;
	}
	
	public void updateStatus(long databaseDSId, String commonStatus, Timestamp statusChangeDate) throws DataManagerException{
//		System.out.println("*********** ACCESSING THE UPDATESTATUS METHOD ********************");
		Session session = getSession();			
		DatabaseDSData databaseDSData = null;			
		try{
			Criteria criteria = session.createCriteria(DatabaseDSData.class);
			databaseDSData = (DatabaseDSData)criteria.add(Restrictions.eq("databaseId",databaseDSId)).uniqueResult();
	
			setUpdateAuditDetail(databaseDSData);
			session.update(databaseDSData);
			
			databaseDSData = null;
			criteria = null;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public void delete(long databaseDSId) throws DataManagerException{
//    	System.out.println("**************** ACCESSING THE DELETE METHOD *****************************");
        List databaseDSList = null;
        
        try{
            Session session = getSession();
             if(isDataSourceConfigured(session,databaseDSId)==true){
            	 throw new ConstraintViolationException("data source is already in use");
             }
            Criteria criteria = session.createCriteria(DatabaseDSData.class);
        	criteria.add(Restrictions.eq("databaseId",databaseDSId));
            databaseDSList = criteria.list();
            if(databaseDSList != null && databaseDSList.size() > 0){
            	DatabaseDSData databaseDSData = (DatabaseDSData)databaseDSList.get(0);
            	session.delete(databaseDSData);
            }
        }catch(ConstraintViolationException exp){
        	throw exp;
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	throw new DataManagerException(exp.getMessage(), exp);
        }

    }

	private boolean isDataSourceConfigured(Session session,long databaseDSId) {
		Criteria netConfigParamValuePoolCriteria=session.createCriteria(NetConfigurationValuesData.class)
				.setProjection(Projections.projectionList().add(Projections.property("value")))
				.add(Restrictions.in("parameterId", parameterIds));
		
		List configuredValueList = netConfigParamValuePoolCriteria.list();
		if(configuredValueList != null && 
				 configuredValueList.isEmpty() == false){
			if(configuredValueList.contains(""+databaseDSId)){
				Logger.logDebug(MODULE, "Database data source with id : " +databaseDSId+ " is configured in Netvertex serevr / Netvertex usage Metering ");
				return true;
			}
		}
		return false;
	}
	
	public PageList search(IDatabaseDSData databaseDSData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		
			try{
					Session session = getSession();
					Criteria criteria = session.createCriteria(DatabaseDSData.class);
											  
		            
		            if((databaseDSData.getName() != null && databaseDSData.getName().length()>0 )){
		            	criteria.add(Restrictions.ilike("name",databaseDSData.getName()));
		            }

		            
		            int totalItems = criteria.list().size();
	            	criteria.setFirstResult(((pageNo-1) * pageSize));
	            	
	            	if (pageSize > 0 ){
	            		criteria.setMaxResults(pageSize);
	            	}
	            	
	            	List databaseDSList = criteria.list();
		           
		              
		            long totalPages = (long)Math.ceil(totalItems/pageSize);
		            if(totalItems%pageSize == 0)
		           	totalPages-=1;
		            
		            pageList = new PageList(databaseDSList, pageNo, totalPages ,totalItems);
		            Logger.logDebug(MODULE,"LIST SIZE IS:"+databaseDSList.size());
		            
           }catch(HibernateException hExp){
        	   throw new DataManagerException(hExp.getMessage(), hExp);
           }catch(Exception exp){
        	   throw new DataManagerException(exp.getMessage(), exp);
           }
        return pageList;
	}
	
	public List getList(IDatabaseDSData databaseDSData) throws DataManagerException
	{
		
		List databaseDSList = null;

      try{
          Session session = getSession();
          Criteria criteria = session.createCriteria(DatabaseDSData.class);

          if(databaseDSData.getDatabaseId()!= 0)
          	criteria.add(Restrictions.eq("databaseId",databaseDSData.getDatabaseId()));
       
          databaseDSList = criteria.list();
          
         

      }catch(HibernateException hExp){
          throw new DataManagerException(hExp.getMessage(), hExp);
      }catch(Exception exp){
      	throw new DataManagerException(exp.getMessage(), exp);
      }
      return databaseDSList;	
	}
	
	public DatabaseDSData getDatabaseDS(Long databaseDSID) throws DataManagerException{
		DatabaseDSData databaseDSData = null;
      try{
          Session session = getSession();
          Criteria criteria = session.createCriteria(DatabaseDSData.class);
          if(databaseDSID != 0)
          	criteria.add(Restrictions.eq("databaseId",databaseDSID));       
          databaseDSData = (DatabaseDSData)criteria.uniqueResult();         
      }catch(HibernateException hExp){
          throw new DataManagerException(hExp.getMessage(), hExp);
      }catch(Exception exp){
      	throw new DataManagerException(exp.getMessage(), exp);
      }
      return databaseDSData;		
	}
	
	public void updateDatabaseDSDetail(IDatabaseDSData idatabaseDSData) throws DataManagerException
	{
		
		Session session = getSession();
		DatabaseDSData databaseDSData = null;

		if(idatabaseDSData != null){
			EliteAssert.notNull(idatabaseDSData,"Database Datasource Data must not be null.");
			try{
				verifyDatabaseDSNameForUpdate(idatabaseDSData);
				Criteria criteria = session.createCriteria(DatabaseDSData.class);
				databaseDSData = (DatabaseDSData)criteria.add(Restrictions.eq("databaseId",idatabaseDSData.getDatabaseId()))
												  .uniqueResult();
				
				databaseDSData.setDatabaseId(idatabaseDSData.getDatabaseId());
				databaseDSData.setConnectionUrl(idatabaseDSData.getConnectionUrl());
				databaseDSData.setMaximumPool(idatabaseDSData.getMaximumPool());
				databaseDSData.setMinimumPool(idatabaseDSData.getMinimumPool());
				databaseDSData.setName(idatabaseDSData.getName());
				databaseDSData.setUserName(idatabaseDSData.getUserName());
				databaseDSData.setPassword(idatabaseDSData.getPassword());
                databaseDSData.setTimeout(idatabaseDSData.getTimeout());
                databaseDSData.setStatusCheckDuration(idatabaseDSData.getStatusCheckDuration());
                
                setUpdateAuditDetail(databaseDSData);
				session.update(databaseDSData);
				session.flush();
			} catch (DuplicateInstanceNameFoundException dExp) {
				throw new DuplicateInstanceNameFoundException(dExp.getMessage(), dExp);
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException("UpdateDatabaseDatasource Failed");
		}
	}

	private void verifyDatabaseDSNameForUpdate(IDatabaseDSData idatabaseDSData) throws DuplicateInstanceNameFoundException {
		
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(DatabaseDSData.class);
		List list = criteria.add(Restrictions.eq("name",idatabaseDSData.getName())).add(Restrictions.ne("databaseId",idatabaseDSData.getDatabaseId())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateInstanceNameFoundException("Database Datasource Name Is Duplicated.");
		}
		
	}

	public IDatabaseDSData getDatabaseDSData(Long databaseId) throws DataManagerException {

		IDatabaseDSData databaseDSData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class).add(Restrictions.eq("databaseId",databaseId));

			List databaseDSList =  criteria.list();

			if(databaseDSList != null && databaseDSList.size() > 0){
				databaseDSData = (IDatabaseDSData)databaseDSList.get(0);

			}

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return databaseDSData;     
	}

	public Connection getConnection(IDatabaseDSData databaseDSData) throws DatabaseConnectionException {
		
		DBConnectionManager dbConnManager = DBConnectionManager.getInstance();
		return dbConnManager.getSessionDBConnection(databaseDSData.getName());
		 
	}
	@Override
	public Connection getDBConnection(Long databaseDSId) throws DatabaseConnectionException{
		
		try {

			IDatabaseDSData databaseDSData = getDatabaseDSData(databaseDSId);
			if(databaseDSData == null){
				throw new DatabaseConnectionException("Configured DataSource: "+ databaseDSId +" not found");
			}
			
			String dbUrl = databaseDSData.getConnectionUrl();
			String dbUser = databaseDSData.getUserName();
			String dbPassword = databaseDSData.getPassword();
			String driverClass = getDriverClass(dbUrl);
			
			Logger.logDebug(MODULE, "Database Url :" + dbUrl);
			Logger.logDebug(MODULE,"Database Class :" + driverClass);
			Logger.logDebug(MODULE,"Database Username :" + dbUser);
			
			Class.forName(driverClass);
			DriverManager.setLoginTimeout(3); //in seconds
			Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			
			if (con == null){
				throw new DatabaseConnectionException("Connection pool is full");
			}
			Logger.logDebug(MODULE,"connection successfull");
			return con;
			
		 
		} catch (SQLException sqle) {
			throw new DatabaseConnectionException("Database exception [Error Code : " + sqle.getErrorCode() + ", SQL state : " + sqle.getSQLState() + "] Caused by:" + sqle.getMessage(), sqle);
		} catch (Exception exp) {
			throw new DatabaseConnectionException(exp.getMessage(), exp);
		}

		
	}

	private String getDriverClass(String dbUrl) {
		String driverClass = "oracle.jdbc.driver.OracleDriver";
		if(dbUrl!=null && dbUrl.toLowerCase().contains("postgres")){
			driverClass = "org.postgresql.Driver";
		}else if(dbUrl!=null && dbUrl.toLowerCase().contains("timesten")){
			driverClass="com.timesten.jdbc.TimesTenDriver";
		}else if(dbUrl!=null && dbUrl.toLowerCase().contains("mysql")){
			driverClass="com.mysql.jdbc.Driver";
		}
		
		return driverClass;
	}
	
	
	
	public Set<String> getDataTypeList(Long databaseId) throws DataManagerException 
	{ 

		Connection con = getDBConnection(databaseId);
		Set<String> dataTypeList = null;
		try{
			if(con != null){
				dataTypeList = getDataTypes(con);

			}
		}catch(Exception e){
			e.printStackTrace();

		}finally{
			DBUtility.closeQuietly(con);
		}
		return dataTypeList;

	}



	private static Set<String> getDataTypes(Connection con) throws DataManagerException  {

		Set<String> set = new TreeSet<String>();

		DatabaseMetaData dbmd;
		try {
			dbmd = con.getMetaData();
			ResultSet rs = dbmd.getTypeInfo();

			while (rs.next()) {

				int codeNumber = rs.getInt("DATA_TYPE");
				String dbmsName = rs.getString("TYPE_NAME");

				for (int i = 0; i < dataTypes.size(); i++) {

					if (Integer.parseInt(dataTypes.get(i).toString()) == codeNumber) {
						String result = dbmsName.toUpperCase();
						set.add(result);

					}

				}

			}

			set.add("varchar");


		} catch (SQLException sqle) {
			throw new DatabaseConnectionException("Database Connection Exception ." + sqle.getMessage(), sqle);
		} catch (Exception exp) {
			throw new DatabaseConnectionException(exp.getMessage(), exp);
		}

		return set;

	}

	
	
	
	
	
	
}