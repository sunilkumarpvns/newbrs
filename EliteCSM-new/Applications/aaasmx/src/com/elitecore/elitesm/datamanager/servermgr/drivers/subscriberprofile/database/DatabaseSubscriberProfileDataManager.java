package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.InvalidSQLStatementException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.data.SubscriberProfileData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileDataBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.ISQLParamPoolValueData;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData;

public interface DatabaseSubscriberProfileDataManager extends DataManager{
	
	public List<IDatasourceSchemaData> getFieldNames(DBAuthDriverData dbAuthDriverData,Connection con) throws DataManagerException;

	public List<List<IDatabaseSubscriberProfileDataBean>> getFieldData(DBAuthDriverData dbAuthDriverData,Connection con) throws DataManagerException;

	public List<IDatasourceSchemaData> getColumnNames(DBAuthDriverData dbAuthDriverData) throws DataManagerException;

	public List<IDatabaseSubscriberProfileRecordBean> getDatabaseSubscriberProfileRecord(DBAuthDriverData dbAuthDriverData,Connection con,String fieldName ,String fieldId) throws DataManagerException;

	public void updateDatabaseSubscriberProfileRecord(Connection con,List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField,String tableName,String  identityFieldName ,String identityFieldValue) throws DataManagerException, SQLException;

	public PageList getSearchFieldData(DBAuthDriverData dbAuthDriverData,Connection con,String searchFieldData,int pageNo,int pageSize,Map filterData) throws DataManagerException;

	public List<IDatabaseSubscriberProfileRecordBean> getDatabaseDataSchema(DBAuthDriverData dbAuthDriverData,Connection con) throws DataManagerException;

	public void addDatabaseSusbscriberProfileRecord(DBAuthDriverData dbAuthDriverData, Connection con,List<IDatabaseSubscriberProfileRecordBean> lstSubscriberProfileRecordBean) throws DataManagerException,SQLException;

	public void deleteSubscriberProfileRecord(DBAuthDriverData dbAuthDriverData, Connection con, IDatasourceSchemaData datasourceSchemaData,String subscriberProfileId)  throws DataManagerException;

	public List<IDatasourceSchemaData> getParamValuePoolList(IDatasourceSchemaData datasourceSchemaData) throws DataManagerException;

	public void updateValuePool(IDatasourceSchemaData datasourceSchemaData,String fieldId) throws DataManagerException;

	public void updateQueryPool(ISQLParamPoolValueData sqlData,String fieldId) throws DataManagerException;

	public void updateQueryPool(ISQLParamPoolValueData sqlData) throws DataManagerException;

	public List<SQLPoolValueData> getPoolValueFromQuery(Connection con,String queryString) throws DataManagerException,InvalidSQLStatementException; 

	public void createSchema(List<IDatasourceSchemaData> datasourceSchemaList) throws DataManagerException;

	public List<IDatasourceSchemaData> getDatabaseSchemaList(DBAuthDriverData dbAuthDriverData,Connection con) throws DataManagerException,TableDoesNotExistException;

	public void updateDatabaseSubscribeProfileSchema(String dbAuthId, Set<IDatasourceSchemaData> schemaDetailSet) throws DataManagerException;

	public IDatasourceSchemaData getDatasourceSchema(IDatasourceSchemaData datasourceSchemaData) throws DataManagerException;
	
	public List<String> getInputFieldsForDuplicateRecord(Connection connection,String tableName) throws DataManagerException;

	public SubscriberProfileData getSubscriberProfileData(String name) throws DataManagerException;
	
	public void addSubscriberProfileData(SubscriberProfileData subscriberProfileData) throws DataManagerException;

	public SubscriberProfileData deleteSubscriberProfileData(String userIdentity) throws DataManagerException;

}
