package com.elitecore.elitesm.hibernate.datasource.database;

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
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.PasswordEncryption;

import net.sf.json.JSONArray;

public class HDatabaseDSDataManager extends HBaseDataManager implements DatabaseDSDataManager{
	private static final String REASON = ", Reason: ";
	private static final String FAILED_TO_CREATE = "Failed to create ";
	private static final String NAME = "name";
	private static final String DATABASE_ID = "databaseId";
	private static final String MODULE = "HDatabaseDSDataManager";
	
	
	private static final ArrayList<Integer> dataTypes ;

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
	}
	
	@Override
	public String create(Object object) throws DataManagerException {
		DatabaseDSData databaseDSData = (DatabaseDSData) object;
		try{
			Session session = getSession();
			session.clear();
			
			//fetch next audit id
			String auditId= UUIDGenerator.generate();
			
			databaseDSData.setAuditUId(auditId);
			session.save(databaseDSData);
			
			session.flush();
			session.clear();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + databaseDSData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+databaseDSData.getName()+REASON+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+databaseDSData.getName()+REASON+exp.getMessage(), exp);
		}
		return databaseDSData.getName();
	}
	
	public void verifyDatabaseDSName(IDatabaseDSData databaseDSData) throws DataManagerException,DuplicateInstanceNameFoundException
	{
        List<?> databaseDSList = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(DatabaseDSData.class);
            criteria.add(Restrictions.eq(NAME, databaseDSData.getName()));
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
	public List<?> getAllList() throws DataManagerException
	{
		List<?> databasedsList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class);
			databasedsList = criteria.list();

		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return databasedsList;
	}
	
	public void updateStatus(String databaseDSId, String commonStatus, Timestamp statusChangeDate) throws DataManagerException{
//		System.out.println("*********** ACCESSING THE UPDATESTATUS METHOD ********************");
		Session session = getSession();			

		DatabaseDSData databaseDSData = null;
			
		try{
				Criteria criteria = session.createCriteria(DatabaseDSData.class);
				databaseDSData = (DatabaseDSData)criteria.add(Restrictions.eq(DATABASE_ID,databaseDSId))
														.uniqueResult();

		
				databaseDSData.setLastmodifiedDate(statusChangeDate);
				session.update(databaseDSData);

				databaseDSData = null;
				criteria = null;
				

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	@Override
	public String deleteById(String databaseDSId) throws DataManagerException{
		return delete(DATABASE_ID,databaseDSId);
	}
	
	@Override
	public String deleteByName(String databaseDSName) throws DataManagerException{
		return delete(NAME,databaseDSName);
	}
	
	private String delete(String propertyName, Object propertyValue) throws DataManagerException{
		String datasourceName = (NAME.equals(propertyName)) ? (String)propertyValue : "Database Datasource";
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(DatabaseDSData.class);
        	criteria.add(Restrictions.eq(propertyName,propertyValue));
        	
        	DatabaseDSData databaseDSData = (DatabaseDSData) criteria.uniqueResult();
            if(databaseDSData == null){
            	throw new InvalidValueException("Database DataSource not found");
            }
            
            session.delete(databaseDSData);
            return databaseDSData.getName();
    	} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + datasourceName + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
            throw new DataManagerException("Failed to delete "+datasourceName+REASON+hExp.getMessage(), hExp);
        } catch(Exception exp) {
        	exp.printStackTrace();
        	throw new DataManagerException("Failed to delete "+datasourceName+REASON+exp.getMessage(), exp);
        }
    }
	
	public PageList search(IDatabaseDSData databaseDSData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class);


			if((databaseDSData.getName() != null && databaseDSData.getName().length()>0 )){
				criteria.add(Restrictions.ilike(NAME,databaseDSData.getName()));
			}


			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List<?> databaseDSList = criteria.list();

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
	
	public List<DatabaseDSData> getList(IDatabaseDSData databaseDSData) throws DataManagerException
	{
		List<DatabaseDSData> databaseDSList = null;
		//      System.out.println("***************** ACCESSING THE GETLIST WITH PARAMETER IN HIBERNATE CLASS :**************");
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class);

			if(Strings.isNullOrBlank(databaseDSData.getDatabaseId()) == false)
				criteria.add(Restrictions.eq(DATABASE_ID,databaseDSData.getDatabaseId()));

			databaseDSList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return databaseDSList;
	}
	
	@Override
	public void updateDatabaseDSDetailById(IDatabaseDSData idatabaseDSData,IStaffData staffData,String actionAlias) 
			throws DataManagerException
	{
		updateDatabaseDSDetail(idatabaseDSData,staffData,actionAlias,DATABASE_ID,idatabaseDSData.getDatabaseId());
	}
	
	@Override
	public void updateDatabaseDSDetailByName(IDatabaseDSData idatabaseDSData,
			IStaffData staffData, String actionAlias, String name) throws DataManagerException
	{
		updateDatabaseDSDetail(idatabaseDSData,staffData,actionAlias,NAME,name);
	}
	
	private void updateDatabaseDSDetail(IDatabaseDSData idatabaseDSData,
			IStaffData staffData, String actionAlias, String propertyName, Object propertyValue) throws DataManagerException
	{
		Session session = getSession();
		DatabaseDSData databaseDSData = null;

		try{
			Criteria criteria = session.createCriteria(DatabaseDSData.class);
			databaseDSData = (DatabaseDSData)criteria.add(Restrictions.eq(propertyName,propertyValue)).uniqueResult();

			if(databaseDSData == null){
				throw new InvalidValueException("Database Datasource not found");
			}

			JSONArray jsonArray=ObjectDiffer.diff(databaseDSData,(DatabaseDSData)idatabaseDSData);   

			databaseDSData.setConnectionUrl(idatabaseDSData.getConnectionUrl());
			databaseDSData.setLastmodifiedByStaffId(idatabaseDSData.getLastmodifiedByStaffId());
			databaseDSData.setLastmodifiedDate(idatabaseDSData.getLastmodifiedDate());
			databaseDSData.setMaximumPool(idatabaseDSData.getMaximumPool());
			databaseDSData.setMinimumPool(idatabaseDSData.getMinimumPool());
			databaseDSData.setName(idatabaseDSData.getName());
			databaseDSData.setUserName(idatabaseDSData.getUserName());
			databaseDSData.setPassword(idatabaseDSData.getPassword());
			databaseDSData.setTimeout(idatabaseDSData.getTimeout());
			databaseDSData.setStatusCheckDuration(idatabaseDSData.getStatusCheckDuration());

			staffData.setAuditId(databaseDSData.getAuditUId());
			staffData.setAuditName(idatabaseDSData.getName());

			session.update(databaseDSData);
			session.flush();

			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + databaseDSData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update "+idatabaseDSData.getName()+REASON+hExp.getMessage(),hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update "+idatabaseDSData.getName()+REASON+exp.getMessage(),exp);
		}
	}

	@Override
	public Connection getConnection(IDatabaseDSData databaseDSData, boolean isDescrept) throws DatabaseConnectionException {
		Connection con = null;
		try {

			String driverClass = "";
			String dbUrl = "";
			String dbUser = "";
			String dbPassword = "";
			
			dbUrl = databaseDSData.getConnectionUrl();
			dbUser = databaseDSData.getUserName();
			driverClass = getDriverClass(dbUrl);
				
			if(isDescrept){
				dbPassword = PasswordEncryption.getInstance().decrypt(databaseDSData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			}
			Logger.logDebug(MODULE, "Database Url :" + dbUrl);
			Logger.logDebug(MODULE,"Database Class :" + driverClass);
			Logger.logDebug(MODULE,"Database Username :" + dbUser);
			
			Class.forName(driverClass);
			DriverManager.setLoginTimeout(3); //in seconds
			con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			Logger.logDebug(MODULE,"connection successfull");
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			throw new DatabaseConnectionException("Failed to get Connection " + sqle.getMessage(), sqle);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DatabaseConnectionException("Failed to get Connection "+exp.getMessage(), exp);
		}

		if (con == null)
			throw new DatabaseConnectionException("Database Connection not found");
		return con;
	}
	
	private String getDriverClass(String dbUrl) {
		String driverClass = "oracle.jdbc.driver.OracleDriver";
		if(dbUrl!=null && dbUrl.toLowerCase().contains("postgres")){
			driverClass = "org.postgresql.Driver";
		}
		return driverClass;
	}
	
	
	
	public Set<String> getDataTypeList(IDatabaseDSData databaseDSData) throws DataManagerException 
	{ 
		Connection con = getConnection(databaseDSData, true);
		Set<String> dataTypeList = null;
		try{
			if(con != null){
				dataTypeList = getDataTypes(con);

			}
		}catch(Exception e){
			e.printStackTrace();

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
	
	
	@Override
	public IDatabaseDSData getDatabaseDSDataById(String databaseId) throws DataManagerException {
		return getDatabaseDSData(DATABASE_ID, databaseId);
	}

	@Override
	public IDatabaseDSData getDatabaseDSDataByName(String databaseName) throws DataManagerException {
		return getDatabaseDSData(NAME, databaseName);
	}
	
	private IDatabaseDSData getDatabaseDSData(String propertyName, Object value) throws DataManagerException {
		IDatabaseDSData databaseDSData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class)
					.add(Restrictions.eq(propertyName,value));
			
			databaseDSData =  (IDatabaseDSData) criteria.uniqueResult();
			
			if(databaseDSData == null){
				throw new InvalidValueException("Database Datasource not found");
			}
		}catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Database Datasource, Reason: "+exp.getMessage(), exp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Database Datasource, Reason: "+exp.getMessage(), exp);
		}
		return databaseDSData;     
	}
	
	@Override
	public String getDatabaseIdFromName(String databaseName) throws DataManagerException {
		String datasourceId = "";
		try{
			if( databaseName != null ){
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				IDatabaseDSData databaseDsdata = databaseDSBLManager.getDatabaseDSDataByName(databaseName);
				if(databaseDsdata == null){
					throw new InvalidValueException("Database Datasource not found");
				}
				datasourceId = databaseDsdata.getDatabaseId();
			}
		}catch( Exception e ){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}
		return datasourceId;
	}
}