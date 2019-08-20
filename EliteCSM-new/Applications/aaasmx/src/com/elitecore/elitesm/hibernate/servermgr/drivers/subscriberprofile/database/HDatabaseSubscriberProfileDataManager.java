package com.elitecore.elitesm.hibernate.servermgr.drivers.subscriberprofile.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.InvalidSQLStatementException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.data.SubscriberProfileData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DBSubscriberProfileParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatabaseSubscriberProfileDataBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDBSubscriberProfileParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileDataBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.ISQLParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.SQLParamPoolValueData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData;


public class HDatabaseSubscriberProfileDataManager extends HBaseDataManager implements DatabaseSubscriberProfileDataManager {
	private static String MODULE = "HDatabaseSubscriberProfileDataManager";

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


	public List<DatasourceSchemaData> getDatasourceSchemaList() throws DataManagerException {

		List<DatasourceSchemaData> datasourceSchemaList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatasourceSchemaData.class);
			datasourceSchemaList = criteria.list();

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive list of Datasource Schema, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive list of Datasource Schema, Reason: " +  e.getMessage(),e);
		}
		return datasourceSchemaList;

	}
	
	public void updateDatabaseSubscribeProfileSchema(String dbAuthId, Set<IDatasourceSchemaData> schemaDetailSet) throws DataManagerException {
		Session session = getSession();
		try {
			Criteria criteria = session.createCriteria(DatasourceSchemaData.class).add(Restrictions.eq("dbAuthId", dbAuthId));
			List<IDatasourceSchemaData> list = criteria.list();
			if(list!=null && !list.isEmpty()){
				if(Collectionz.isNullOrEmpty(list) == false){
					for (Iterator<IDatasourceSchemaData> iterator = list.iterator(); iterator.hasNext();) {
						DatasourceSchemaData object = (DatasourceSchemaData) iterator.next();
						session.delete(object);
					}	
				}
			}
			session.flush();
			if(Collectionz.isNullOrEmpty(schemaDetailSet) == false){
				for (Iterator<IDatasourceSchemaData> iterator = schemaDetailSet.iterator(); iterator.hasNext();) {
					DatasourceSchemaData object = (DatasourceSchemaData) iterator.next();
					session.save(object);
					session.flush();
				}
			}
			session.flush();
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Database Subcriber Profile Schema, Reason: " +hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException("Failed to update Database Subcriber Profile Schema, Reason: " +exp.getMessage(),exp);
		}
	}

	public List<IDatasourceSchemaData> getFieldNames(DBAuthDriverData dbAuthDriverData,Connection con) throws DataManagerException {
		List<IDatasourceSchemaData> datasourceList = new ArrayList<IDatasourceSchemaData>();
		Statement stmt;
		try {
			if (con != null) {
				ResultSet rs;
				ResultSetMetaData rsmtadta;
				int colCount;
				int i;
				stmt = con.createStatement();
				stmt.setMaxRows(100);
				
				String query = "SELECT * FROM " + dbAuthDriverData.getTableName() + " where 1=2";
				Logger.logDebug(MODULE, "Fetch All the Records Query  :"+ query);
				
				rs = stmt.executeQuery(query);
				rsmtadta = rs.getMetaData();
				colCount = rsmtadta.getColumnCount();
				
				for (i = 1; i <= colCount; i++) {
					IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
					datasourceSchemaData.setFieldName(rsmtadta.getColumnName(i));
					datasourceSchemaData.setDataType(rsmtadta.getColumnTypeName(i));
					datasourceSchemaData.setLength(rsmtadta.getColumnDisplaySize(i));
					datasourceList.add(datasourceSchemaData);
				}
				stmt.close();
			} else {
				Logger.logTrace(MODULE, "Connection is Null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive field name of Datasource Schema, Reason: "+e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive field name of Datasource Schema, Reason: "+e.getMessage(),e);
		}

		return datasourceList;
	}

	public List<IDatasourceSchemaData> getColumnNames(DBAuthDriverData dbAuthDriverData)throws DataManagerException {
		List<IDatasourceSchemaData> datasourceSchemaList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatasourceSchemaData.class);

			if (dbAuthDriverData.getDbAuthId() != null)
				criteria.add(Restrictions.eq("dbAuthId",dbAuthDriverData.getDbAuthId()));

			datasourceSchemaList = criteria.list();

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive column name(s), Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive column name(s), Reason: "+exp.getMessage(), exp);
		}
		return datasourceSchemaList;
	}

	public List<List<IDatabaseSubscriberProfileDataBean>> getFieldData(DBAuthDriverData dbAuthDriverData,Connection con)
	throws DataManagerException {

		List<List<IDatabaseSubscriberProfileDataBean>> totalRowList = new ArrayList<List<IDatabaseSubscriberProfileDataBean>>();
		Statement stmt;
		try {
			
			if (con != null) {

				ResultSet rs;
				stmt = con.createStatement();
				String query = "SELECT * FROM " + dbAuthDriverData.getTableName();
				Logger.logDebug(MODULE, "Fetch All the Records Query  :"
						+ query);
				stmt.setMaxRows(100);
				rs = stmt.executeQuery(query);
				int colCount = 0;
				ResultSetMetaData rsmtadta;
				rsmtadta = rs.getMetaData();
				colCount = rsmtadta.getColumnCount();
				int i;
				
				while (rs.next()) {
					List<IDatabaseSubscriberProfileDataBean> colList = new ArrayList<IDatabaseSubscriberProfileDataBean>();
					for (i = 1; i <= colCount; i++) {

						IDatabaseSubscriberProfileDataBean databaseSubscriberProfileDataBean = new DatabaseSubscriberProfileDataBean();
						databaseSubscriberProfileDataBean.setFieldId(rs.getString(1));
						databaseSubscriberProfileDataBean.setFieldName(rsmtadta.getColumnName(1));
						databaseSubscriberProfileDataBean.setFieldValue(rs.getString(i));
						colList.add(databaseSubscriberProfileDataBean);
					}
					totalRowList.add(colList);
				}
				stmt.close();
			} else {
				Logger.logDebug(MODULE,"Connection is Null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive field data of Subcriber Profile Data, Reason:"+e.getMessage(),e);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive field data of Subcriber Profile Data, Reason: "+exp.getMessage(), exp);
		}
		return totalRowList;
	}

	public PageList getSearchFieldData(DBAuthDriverData dbAuthDriverData,Connection con,String searchData, int pageNo, int pageSize, Map filterData)
	throws DataManagerException {

		List<List<IDatabaseSubscriberProfileDataBean>> totalRowList = new ArrayList<List<IDatabaseSubscriberProfileDataBean>>();
		PageList pageList = null;
		Statement stmt = null;
		ResultSet rsTotalRecs = null;

		try {
			String whereClause = " where 1 = 1 ";
			String condition = "";
			String totalCondition = ""; 

			if(searchData == null){
				searchData="";
			}
			
			String firstFieldName = filterData.get("firstFieldName").toString();
			if(firstFieldName.trim().equalsIgnoreCase("USERNAME")){
				condition = condition + " AND USERNAME LIKE '%" + searchData.trim() + "%' OR USER_IDENTITY LIKE '%" + searchData.trim() + "%'";
				totalCondition = totalCondition + " AND USERNAME LIKE '%" + searchData.trim() + "%' OR USER_IDENTITY LIKE '%" + searchData.trim() + "%'";
			}else{
				if(firstFieldName != null && !firstFieldName.trim().equalsIgnoreCase("")){
					condition = condition + " AND "+ firstFieldName +" LIKE '%" + searchData.trim() + "%'";
					totalCondition = totalCondition + " AND "+ firstFieldName +" LIKE '%" + searchData.trim() + "%'";
				}
			}

			condition += ") b";  

			if(pageNo == 0){
				pageNo = 1;
			}
			condition = condition + "  where rownumber between " + ((pageNo*pageSize)-(pageSize)+1) + " and " + (pageNo*pageSize) ;

			String query = "SELECT b.* FROM (select a.*,ROW_NUMBER() OVER(ORDER BY " + filterData.get("idFieldName") + " ) rownumber from " + dbAuthDriverData.getTableName() + " a " + whereClause + condition  ;
			String totalQuery = "SELECT count(1) as numRows FROM " + dbAuthDriverData.getTableName() + whereClause + totalCondition;
			Logger.logInfo(MODULE, " Search Query : " + query);
			Logger.logDebug(MODULE," TOTAL QUERY : " +totalQuery);


			if (con != null) {

				stmt = con.createStatement();
				rsTotalRecs = stmt.executeQuery(totalQuery);
				int totalItems=0;
				while(rsTotalRecs.next()){
					totalItems=rsTotalRecs.getInt("NUMROWS");
				}


				ResultSet rs;
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
				int colCount = 0;
				ResultSetMetaData rsmtadta;
				rsmtadta = rs.getMetaData();
				colCount = rsmtadta.getColumnCount();

				int i;
				while (rs.next()) {
					List<IDatabaseSubscriberProfileDataBean> colList = new ArrayList<IDatabaseSubscriberProfileDataBean>();
					for (i = 1; i <= colCount; i++) {
						IDatabaseSubscriberProfileDataBean datasourceDataBean = new DatabaseSubscriberProfileDataBean();
						datasourceDataBean.setFieldId(rs.getString(1));
						datasourceDataBean.setFieldName(rsmtadta
								.getColumnName(i));
						if (rsmtadta.getColumnType(i) == Types.TIMESTAMP) {
							datasourceDataBean
							.setFieldValue(rs.getTimestamp(i));
						} else {
							datasourceDataBean.setFieldValue(rs.getString(i));
						}

						colList.add(datasourceDataBean);
					}
					totalRowList.add(colList);
				}
				stmt.close();

				long totalPages = (long) Math.ceil(totalItems / pageSize);
				if(totalItems%pageSize == 0)
					totalPages-=1;

				pageList = new PageList(totalRowList, pageNo, totalPages,totalItems);



			} else {
				Logger.logTrace(MODULE, "Connection is Null");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive field data, Reason:"+e.getMessage(),e);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive field data, Reason: "+exp.getMessage(), exp);
		}
		return pageList;
	}

	public List<IDatabaseSubscriberProfileRecordBean> getDatabaseSubscriberProfileRecord(DBAuthDriverData dbAuthDriverData,Connection con,String fieldName, String fieldId) throws DataManagerException {

		List<IDatabaseSubscriberProfileRecordBean> listDatabaseDatasourceRecord = new ArrayList<IDatabaseSubscriberProfileRecordBean>();

		Statement stmt;
		Statement StmtForuniqueKey; 
		try {
			
			if (con != null) {

				ResultSet rs;
				ResultSet rsForUniqueKey;
				ResultSetMetaData rsmtadta;

				stmt = con.createStatement();
				String query = "SELECT * FROM " + dbAuthDriverData.getTableName()
				+ " where " + fieldName + " = '" + fieldId+"'";
				Logger.logDebug(MODULE, "Select Query  :" + query);
				rs = stmt.executeQuery(query);
				
				StmtForuniqueKey = con.createStatement();
				
				String queryForUniqueKey = null;
				if (con.getMetaData().getURL().contains(ConfigConstant.ORACLE)) {
					queryForUniqueKey =  "select b.column_name from user_constraints a,user_cons_columns b " + 
							"where a.owner" + "="  + "b.owner and " + 
							"a.constraint_name" + "=" + "b.constraint_name " +
							"and   a.constraint_type" + "= '" + "U'" +
							" and   b.table_name" + "=" + "'" + dbAuthDriverData.getTableName() + "'";
				}else if(con.getMetaData().getURL().contains(ConfigConstant.POSTGRESQL)){
					queryForUniqueKey = "select b.column_name from information_schema.table_constraints a , information_schema.constraint_column_usage b " +
							"where a.constraint_schema = b.constraint_schema "+
							"and a.constraint_name = b.constraint_name " +
							"and a.constraint_type = 'UNIQUE' "+
							"and b.table_name = 'Table_name' "+
							"and a.constraint_schema = '"+con.getMetaData().getUserName() + "'";
				}
				
				rsForUniqueKey = StmtForuniqueKey.executeQuery(queryForUniqueKey);
				List<String> uniqueKeyList = new ArrayList<String>();
				
				while(rsForUniqueKey.next()) {
					uniqueKeyList.add(rsForUniqueKey.getString("COLUMN_NAME"));
				}
				
				int colCount = 0;
				rsmtadta = rs.getMetaData();
				colCount = rsmtadta.getColumnCount();
				
				while (rs.next()) {

					for (int i = 1; i <= colCount; i++) {
						IDatabaseSubscriberProfileRecordBean databaseDatasourceRecordBean = new DatabaseSubscriberProfileRecordBean();

						IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
						datasourceSchemaData.setFieldName(rsmtadta.getColumnName(i));

						List<DatasourceSchemaData> lstDataSourceDataFieldName = getDatasourceSchemaFieldNameList(datasourceSchemaData);

						if (lstDataSourceDataFieldName.size() > 0) {
							String str = null;
							String columnName =rsmtadta.getColumnName(i);
							int columnIndex = rs.findColumn(columnName);
							boolean isUniqueKey  = uniqueKeyList.contains(columnName);
							databaseDatasourceRecordBean.setUniqueKeyConstraint(isUniqueKey);
							String javaClassName = rsmtadta.getColumnClassName(columnIndex);
							if (javaClassName.contains("TIMESTAMP")) {

								Timestamp sqlTimeStamp = rs.getTimestamp(i);

								if (sqlTimeStamp != null && !sqlTimeStamp.equals("")) {
									SimpleDateFormat sp = new SimpleDateFormat(ConfigManager.get("DATE_FORMAT"));
									str = sp.format(sqlTimeStamp);
								} else
									str = null;

							}else if (javaClassName.equalsIgnoreCase("java.sql.Date")) {

								java.sql.Date sqlDate = rs.getDate(i);

								if (sqlDate != null && !sqlDate.equals("")) {
									SimpleDateFormat sp = new SimpleDateFormat(ConfigManager.get("DATE_FORMAT"));
									str = sp.format(sqlDate);
								} else
									str = null;

							}else if (javaClassName.equalsIgnoreCase("java.sql.Time")) {

								java.sql.Time sqlTime = rs.getTime(i);

								if (sqlTime != null && !sqlTime.equals("")) {
									SimpleDateFormat sp = new SimpleDateFormat(ConfigManager.get("DATE_FORMAT"));
									str = sp.format(sqlTime);
								} else
									str = null;

							}else {
								str = rs.getString(i);
							}

							databaseDatasourceRecordBean.setFieldValue(str);
							databaseDatasourceRecordBean.setFieldName(rsmtadta.getColumnName(i));
							databaseDatasourceRecordBean.setNullableValue(rsmtadta.isNullable(i));
							databaseDatasourceRecordBean.setColumnTypeName(rsmtadta.getColumnTypeName(i));
							listDatabaseDatasourceRecord.add(databaseDatasourceRecordBean);
						}
					}
				}
				stmt.close();
			} else {
				Logger.logTrace(MODULE, "Connection is Null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to get Database Subscriber Profile, Reason:"+e.getMessage(),e);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Database Subscriber Profile, Reason: "+exp.getMessage(), exp);
		}
		return listDatabaseDatasourceRecord;
	}
	
	@Override
	public void updateDatabaseSubscriberProfileRecord(Connection con,List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField,String tableName, String identityFieldName, String identityFieldValue) 
			throws DataManagerException, SQLException {
		try {
			if (con != null && lstDataRecordField!=null && !lstDataRecordField.isEmpty()) {
				con.setAutoCommit(false);
				PreparedStatement preparedStatement;
				ResultSet rs = con.createStatement().executeQuery("select * from "+tableName+" where 1=2");
				ResultSetMetaData rsMetaData = rs.getMetaData();
				Map<String, String> columnDetailMap = new HashMap<String, String>(rsMetaData.getColumnCount()); 
				for(int index = 1 ;index <= rsMetaData.getColumnCount(); index++){
					columnDetailMap.put(rsMetaData.getColumnName(index).toUpperCase(), rsMetaData.getColumnClassName(index));
				}
				
				String updateQry = getUpdateQuery(lstDataRecordField, tableName, identityFieldName);
				Logger.logDebug(MODULE, "Update Query:"+updateQry);
				
				int parameterIndex = 1;
				preparedStatement = con.prepareStatement(updateQry);
				for(IDatabaseSubscriberProfileRecordBean subscriberProfileRecordBean : lstDataRecordField){
					Object obj = getValueObject(columnDetailMap.get(subscriberProfileRecordBean.getFieldName().toUpperCase()),subscriberProfileRecordBean.getFieldValue());
					preparedStatement.setObject(parameterIndex++, obj);
				}
				/*Set where condition parameter*/
				Object obj = getValueObject(columnDetailMap.get(identityFieldName.toUpperCase()),identityFieldValue);
				preparedStatement.setObject(parameterIndex++, obj);
				
				int noOfUpdate = preparedStatement.executeUpdate();
				Logger.logDebug(MODULE, noOfUpdate+" records updated successfully");
				con.commit();

			} else {
				Logger.logTrace(MODULE, "Connection is Null");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update Database Subscriber Profile, Reason:"+e.getMessage(),e);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Database Subscriber Profile, Reason: "+exp.getMessage(), exp);
		}
	}

	private String getUpdateQuery(List<IDatabaseSubscriberProfileRecordBean> lstDataRecordField, String tableName, String identityField){
		StringBuilder updateQueryBuilder = new StringBuilder((lstDataRecordField.size()*15)+tableName.length()+16);
		updateQueryBuilder.append("UPDATE ")
		  .append(tableName)
		  .append(" SET ");
		
		Iterator<IDatabaseSubscriberProfileRecordBean> fieldIterator = lstDataRecordField.iterator();
		while(fieldIterator.hasNext()){
			IDatabaseSubscriberProfileRecordBean subscriberProfileDataBean = (IDatabaseSubscriberProfileRecordBean) fieldIterator.next();
			updateQueryBuilder.append(subscriberProfileDataBean.getFieldName()).append("=?");
			if(fieldIterator.hasNext()){
				updateQueryBuilder.append(",");
			}
		}
		updateQueryBuilder.append(" where ").append(identityField).append("=?");
		return updateQueryBuilder.toString();
	}
	
	private List<DatasourceSchemaData> getDatasourceSchemaFieldNameList(IDatasourceSchemaData datasourceSchemaData) throws DataManagerException {

		List<DatasourceSchemaData> datasourceSchemaList = null;
		try {
			Session session = getSession();
			Criteria criteria = session
			.createCriteria(DatasourceSchemaData.class);
			if (datasourceSchemaData.getFieldName() != null)
				criteria.add(Restrictions.eq("fieldName", datasourceSchemaData.getFieldName()));

			datasourceSchemaList = criteria.list();

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get DataSource Schema field named List, Reason:"+hExp.getMessage(), hExp);
		}catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get DataSource Schema field named List, Reason: "+exp.getMessage(), exp);
		}
		return datasourceSchemaList;
	}

	public List<IDatabaseSubscriberProfileRecordBean> getDatabaseDataSchema(DBAuthDriverData dbAuthDriverData, Connection con) throws DataManagerException {

		List<IDatabaseSubscriberProfileRecordBean> listDataSchema = new ArrayList<IDatabaseSubscriberProfileRecordBean>();

		Statement stmt;
		try {
			
			if (con != null) {
				ResultSet rs;
				ResultSetMetaData rsmtadta;
				stmt = con.createStatement();
				String query = "SELECT * FROM " + dbAuthDriverData.getTableName() + " where 1=2";
				Logger.logDebug(MODULE, "Schema Query :" + query);
				rs = stmt.executeQuery(query);

				int colCount = 0;
				rsmtadta = rs.getMetaData();

				colCount = rsmtadta.getColumnCount();
				for (int i = 1; i <= colCount; i++) {
					IDatabaseSubscriberProfileRecordBean databaseDatasourceRecordBean = new DatabaseSubscriberProfileRecordBean();
					databaseDatasourceRecordBean.setNullableValue(rsmtadta.isNullable(i));
					databaseDatasourceRecordBean.setFieldName(rsmtadta.getColumnName(i));
					databaseDatasourceRecordBean.setColumnTypeName(rsmtadta.getColumnTypeName(i));
					listDataSchema.add(databaseDatasourceRecordBean);
				}
				stmt.close();
			} else {
				Logger.logDebug(MODULE,"Connection is Null");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to get DataBase  Schema List, Reason: "+e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to get DataBase Schema List, Reason: "+e.getMessage(), e);
		}
		return listDataSchema;
	}

	@Override
	public void addDatabaseSusbscriberProfileRecord(DBAuthDriverData dbAuthDriverData, Connection con,List<IDatabaseSubscriberProfileRecordBean> lstSubscriberProfileRecordBean) throws DataManagerException, SQLException {
		try {
			if (con != null && lstSubscriberProfileRecordBean != null && !lstSubscriberProfileRecordBean.isEmpty()) {

				PreparedStatement pstmt;

				String query = "INSERT INTO " + dbAuthDriverData.getTableName();
				String fieldName = "( ";
				String value = " VALUES( ";

				for (int i = 0; i < lstSubscriberProfileRecordBean.size(); i++) {
					IDatabaseSubscriberProfileRecordBean datasourceDataBean = ((IDatabaseSubscriberProfileRecordBean) lstSubscriberProfileRecordBean.get(i));
					if (datasourceDataBean!=null) {
						if (i == (lstSubscriberProfileRecordBean.size() - 1)) {
							fieldName = fieldName + datasourceDataBean.getFieldName();
							value = value + "?";
						} else {
							fieldName = fieldName + datasourceDataBean.getFieldName()
							+ ",";
							value = value + "?,";
						}
					}
				}

				query = query + fieldName + " )" + " " + value + " )";

				pstmt = con.prepareStatement(query);
				Logger.logDebug(MODULE, "Insert Query :" + query);
				ResultSet rs = con.createStatement().executeQuery("select * from "+dbAuthDriverData.getTableName()+" where 1=2");
				ResultSetMetaData rsMetaData = rs.getMetaData();
				if (lstSubscriberProfileRecordBean.size() > 0) {
					for (int k = 0; k < lstSubscriberProfileRecordBean.size(); k++) {
						IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
						IDatabaseSubscriberProfileRecordBean datasourceDataBean = ((IDatabaseSubscriberProfileRecordBean) lstSubscriberProfileRecordBean.get(k));
						datasourceSchemaData.setFieldName(datasourceDataBean.getFieldName());

						List<DatasourceSchemaData> lstDatasourceDataFieldId = getDatasourceSchemaFieldNameList(datasourceSchemaData);

						if (lstDatasourceDataFieldId.size() > 0 && lstDatasourceDataFieldId.size() > 0) {

							DatasourceSchemaData tempDataName = (DatasourceSchemaData) lstDatasourceDataFieldId.get(0);
							Logger.logDebug(MODULE, "DATA TYPE IS:"+tempDataName.getDataType());

							if (lstDatasourceDataFieldId.size() > 0) {
								int  columnIndex = rs.findColumn((((IDatabaseSubscriberProfileRecordBean) lstSubscriberProfileRecordBean.get(k)).getFieldName()).toString());    
								String javaClassName = rsMetaData.getColumnClassName(columnIndex);
								String fieldValue = ((IDatabaseSubscriberProfileRecordBean) lstSubscriberProfileRecordBean.get(k)).getFieldValue();
									Object obj = getValueObject(javaClassName,fieldValue);
									pstmt.setObject((k + 1), obj);
							}
						}
					}
				}
				pstmt.executeUpdate();
				Logger.logDebug(MODULE,"Commit called");
				con.commit();

			} else {
				if(con == null) {
					Logger.logDebug(MODULE,"Connection is Null");
				} else {
					throw new DataValidationException("No Field provide for insert record");
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to add Database Subcriber Profile Record, Reason: "+e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to add Database Subcriber Profile Record, Reason: "+e.getMessage(), e);
		} finally{
			con.close();
		}
	}

	public void deleteSubscriberProfileRecord(DBAuthDriverData dbAuthDriverData, Connection con,IDatasourceSchemaData datasourceSchemaData, String subscriberProfileId)
	throws DataManagerException {

		if (datasourceSchemaData != null
				&& datasourceSchemaData.getFieldName() != null
				&& subscriberProfileId != null
				&& !subscriberProfileId.equalsIgnoreCase(null)) {

			Statement stmt = null;
			PreparedStatement preparedStatementForCommit = null;
			String query = "DELETE FROM " + dbAuthDriverData.getTableName()
			+ " WHERE " + datasourceSchemaData.getFieldName() + " = "
			+ "'" + subscriberProfileId + "'";
			try {
				
				if (con != null) {
					
					stmt = con.createStatement();
					Logger.logDebug(MODULE, "query :" + query);
					
					stmt.executeUpdate(query);
					
					String connectionUrl = con.getMetaData().getURL();
					
					if(connectionUrl.contains(ConfigConstant.ORACLE)){
						preparedStatementForCommit = con.prepareStatement("COMMIT WORK WRITE NOWAIT ");
					}else if(connectionUrl.contains(ConfigConstant.POSTGRESQL)){
						preparedStatementForCommit = con.prepareStatement("COMMIT");
					}
					
					preparedStatementForCommit.executeUpdate();
				}

			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException("Failed to delete Database Subcriber Profile Record, Reason: "+hExp.getMessage(), hExp);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DataManagerException("Failed to delete Database Subcriber Profile Record, Reason: "+e.getMessage(), e);
			} finally {
				DBUtility.closeQuietly(stmt);
				DBUtility.closeQuietly(preparedStatementForCommit);
			}
		}
	}

	public void updateValuePool(IDBSubscriberProfileParamPoolValueData dbdsParamPoolValueData)throws DataManagerException {

		try {
			Session session = getSession();
			session.save(dbdsParamPoolValueData);

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Value Pool, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Value Pool, Reason: "+exp.getMessage(), exp);
		}
	}

	public void updateValuePool(IDatasourceSchemaData idatasourceSchemaData,String fieldId) throws DataManagerException {

		Session session = getSession();
		try {
			if (idatasourceSchemaData != null) {
				Criteria criteria = session
				.createCriteria(DBSubscriberProfileParamPoolValueData.class);
				criteria.add(Restrictions.eq("fieldId", fieldId));
				List<DBSubscriberProfileParamPoolValueData> lstDBDSParamPoolValue = criteria.list();

				if (lstDBDSParamPoolValue.size() > 0) {
					for (int i = 0; i < lstDBDSParamPoolValue.size(); i++) {
						IDBSubscriberProfileParamPoolValueData dbdsParamPoolValueData = (IDBSubscriberProfileParamPoolValueData) lstDBDSParamPoolValue
						.get(i);

						session.delete(dbdsParamPoolValueData);
					}
				}
				session.flush();
				Set stDbdsParamPoolValue = idatasourceSchemaData.getDbdsParamPoolValueSet();
				Iterator itrDatasourceSchemaDetail = stDbdsParamPoolValue.iterator();
				while (itrDatasourceSchemaDetail.hasNext()) {

					IDBSubscriberProfileParamPoolValueData dbdsParamPoolValue = (IDBSubscriberProfileParamPoolValueData) itrDatasourceSchemaDetail
					.next();

					dbdsParamPoolValue.setFieldId(fieldId);
					session.save(dbdsParamPoolValue);
				}
			}

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Value Pool, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Value Pool, Reason: "+exp.getMessage(), exp);
		}
	}

	public List<IDatasourceSchemaData> getParamValuePoolList(IDatasourceSchemaData datasourceSchemaData)throws DataManagerException {

		List<IDatasourceSchemaData> paramValuePoolList = null;

		try {
			Session session = getSession();
			Criteria criteria = session
			.createCriteria(DatasourceSchemaData.class);

			if (datasourceSchemaData.getFieldId() != null)
				criteria.add(Restrictions.eq("fieldId", datasourceSchemaData
						.getFieldId()));

			paramValuePoolList = criteria.list();

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Value Pool List, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Value Pool List, Reason: "+exp.getMessage(), exp);
		}

		return paramValuePoolList;
	}

	public IDatasourceSchemaData getDatasourceSchema(IDatasourceSchemaData datasourceSchemaData) throws DataManagerException {

		List<DatasourceSchemaData> datasourceSchemaList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatasourceSchemaData.class);

			if (datasourceSchemaData.getFieldId() != null){
				criteria.add(Restrictions.eq("fieldId", datasourceSchemaData.getFieldId()));
			}

			datasourceSchemaList = criteria.list();
			
			if(datasourceSchemaList!=null && !datasourceSchemaList.isEmpty()){
				datasourceSchemaData =(IDatasourceSchemaData)datasourceSchemaList.get(0);
			}else{
				datasourceSchemaData=null;
			}

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Datasource schema, Reason:"+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Datasource schema, Reason:"+exp.getMessage(), exp);
		}

		return datasourceSchemaData;
	}

	public void updateQueryPool(ISQLParamPoolValueData sqlData, String fieldId)
	throws DataManagerException {
		Session session = getSession();
		IDatasourceSchemaData idatasourceSchemaData = null;
		try {
			session.save(sqlData);

			Criteria criteria1 = session
			.createCriteria(DatasourceSchemaData.class);
			idatasourceSchemaData = (IDatasourceSchemaData) criteria1.add(Restrictions.eq("fieldId", fieldId)).uniqueResult();

			idatasourceSchemaData.setSqlData((SQLParamPoolValueData) sqlData);
			session.update(idatasourceSchemaData);

			session.flush();

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Query Pool, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Query Pool, Reason: "+exp.getMessage(), exp);
		}

	}

	public void updateQueryPool(ISQLParamPoolValueData sqlData)
	throws DataManagerException {
		Session session = getSession();
		ISQLParamPoolValueData isqlParamPoolValueData = null;
		try {

			Criteria criteria = session
			.createCriteria(SQLParamPoolValueData.class);
			isqlParamPoolValueData = (ISQLParamPoolValueData) criteria.add(Restrictions.eq("sqlId", sqlData.getSqlId()))
					.uniqueResult();

			isqlParamPoolValueData.setQuery(sqlData.getQuery());
			session.update(isqlParamPoolValueData);

			session.flush();

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Query Pool, Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Query Pool, Reason: "+exp.getMessage(), exp);
		}

	}

	public List<SQLPoolValueData> getPoolValueFromQuery(Connection con,String queryString) throws DataManagerException,InvalidSQLStatementException {

		List<SQLPoolValueData> lstPoolFromQuery = new ArrayList<SQLPoolValueData>();
		Statement stmt;

		try {

			Logger.logInfo(MODULE, "pool value query: " + queryString);

			if(queryString != null
					&& queryString.trim().length() > 0){
				if((queryString.contains(";"))){
					String[]resultQuery = queryString.split(";");
					if(resultQuery.length == 1){
						queryString=resultQuery[0];
					}
				}
			}

			if (con != null && queryString != null
					&& queryString.trim().length() > 0) {
				ResultSet rs = null;
				stmt = con.createStatement();

				try {
					rs = stmt.executeQuery(queryString);

				} catch (SQLException sq) {

					Logger.logError(MODULE,"Invalid SQL Statement : " + queryString);
					throw new InvalidSQLStatementException("Not able to execute ["+queryString+"] :"+sq.getMessage(), sq);
				}

				while (rs.next()) {

					SQLPoolValueData sqlpoolValueData = new SQLPoolValueData();

					sqlpoolValueData.setName(rs.getString(1));
					sqlpoolValueData.setPoolValue(rs.getString(2));

					if (sqlpoolValueData.getName() != null && sqlpoolValueData.getPoolValue() != null) {
						lstPoolFromQuery.add(sqlpoolValueData);
					}

				}

			}

		}catch (SQLException sqle) {
			Logger.logError(MODULE,"SQL Operation Exception : " + sqle.getMessage());
			sqle.printStackTrace();
			throw new DataManagerException("Failed to retrive Query Pool, Reason: "+sqle.getMessage(), sqle);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Query Pool, Reason: "+e.getMessage(), e);
		}
		return lstPoolFromQuery;
	}

	public void createSchema(List<IDatasourceSchemaData> datasourceSchemaList)throws DataManagerException {
		try {
			Logger.logDebug(MODULE," datasourceSchemaList : " + datasourceSchemaList);
			Logger.logDebug(MODULE," datasourceSchemaList size : "+ datasourceSchemaList.size());
			Session session = getSession();
			if (datasourceSchemaList != null && datasourceSchemaList.size() > 0) {
				for (Iterator<IDatasourceSchemaData> ite = datasourceSchemaList.iterator(); ite.hasNext();) {
					IDatasourceSchemaData datasourceSchema = (IDatasourceSchemaData) ite.next();
					Logger.logDebug(MODULE," datasourceSchema : " + datasourceSchema);
					session.save(datasourceSchema);
				}
				session.flush();
			}
		} catch (HibernateException hExp) {
			Logger.logError(MODULE,"Datasource Schema creation failed : " + hExp.getMessage());
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create Datasource Schema, Reason: " + hExp.getMessage(),hExp);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to create Datasource Schema, Reason: " + e.getMessage(),e);
		}
	}


	public int getNextId(DBAuthDriverData dbAuthDriverData,Connection con, String primaryKeyColumn) throws DataManagerException {
		
		int nextId = 0;
		try {
			if (con != null) {
				Statement st = con.createStatement();
				String query = "SELECT max(" + primaryKeyColumn + ") FROM "+ dbAuthDriverData.getTableName();
				Logger.logDebug(MODULE," Find next id query :" + query);
				ResultSet rs = st.executeQuery(query);
				if (rs.next()) {
					nextId = rs.getInt(1);
				}
				rs.close();
				st.close();
			}
		} catch (SQLException sqle) {
			Logger.logError(MODULE,"Find next id Exception : " + sqle.getMessage());
			sqle.printStackTrace();
			throw new DatabaseConnectionException("Database Connection failed ." + sqle.getMessage(), sqle);
		} catch (Exception e) {
			Logger.logError(MODULE,"Find next id Exception : " + e.getMessage());
			e.printStackTrace();
			throw new DatabaseConnectionException("Database Connection failed ." + e.getMessage(), e);
		}
		nextId = nextId + 1;
		return nextId;
	}


	public List<IDatasourceSchemaData> getDatabaseSchemaList(DBAuthDriverData dbAuthDriverData,Connection con) throws DataManagerException ,TableDoesNotExistException{
		try {

			List<IDatasourceSchemaData> datasourceSchemaList = new ArrayList<IDatasourceSchemaData>();
			Statement stmt;
			if (con != null) {
				ResultSet rs;
				stmt = con.createStatement();
				try {
					rs = stmt.executeQuery("select * from "+ dbAuthDriverData.getTableName() + " where 1=2");
				}catch (SQLException se) {
					Logger.logError(MODULE,"SQL exception, Table Dosent exist : "+ se.getMessage());
					Logger.logDebug(MODULE, "Error code : "+se.getErrorCode());
					if(se.getErrorCode()==942){
						throw new TableDoesNotExistException("Table or View Does Not exist :" + se.getMessage(),se);
					}else{
						throw new DataManagerException(se.getMessage(), se);
					}
				}
				int colCount;
				int i;
				ResultSetMetaData rsmtadta;
				rsmtadta = rs.getMetaData();
				colCount = rsmtadta.getColumnCount();
				for (i = 1; i <= colCount; i++) {

					IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
					datasourceSchemaData.setFieldName(rsmtadta.getColumnName(i));
					datasourceSchemaData.setDataType(rsmtadta.getColumnTypeName(i).toUpperCase());
					datasourceSchemaData.setLength(rsmtadta.getColumnDisplaySize(i));
					datasourceSchemaData.setDbAuthId(dbAuthDriverData.getDbAuthId());
					datasourceSchemaData.setDisplayName(rsmtadta.getColumnName(i));
					datasourceSchemaData.setSqlData(null);
					datasourceSchemaData.setAppOrder(i);
					datasourceSchemaList.add(datasourceSchemaData);
				}
			}
			return datasourceSchemaList;
		} catch (TableDoesNotExistException tne) {
			tne.printStackTrace();
			throw new TableDoesNotExistException("Failed to get Schema List : " + tne.getMessage(), tne);
		} catch (SQLException se) {
			se.printStackTrace();
			Logger.logError(MODULE, "SQL exception : "+ se.getMessage());
			throw new DataManagerException("Failed to get Schema List : " + se.getMessage(), se);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Schema List" + hExp.getMessage(), hExp);
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to get Schema List" + e.getMessage(), e);
		}
	}
	
	public List<String> getInputFieldsForDuplicateRecord(Connection connection,String tableName) throws DataManagerException {
		List<String> uniqueKeyList = null;
		try{
			if(connection != null) {
				ResultSet rsForUniqueKey;
				Statement StmtForuniqueKey; 
				StmtForuniqueKey = connection.createStatement();
				
				String queryForUniqueKey = null;
				if (connection.getMetaData().getURL().contains(ConfigConstant.ORACLE)) {
					queryForUniqueKey =  "select b.column_name from user_constraints a,user_cons_columns b " + 
							"where a.owner" + "="  + "b.owner and " + 
							"a.constraint_name" + "=" + "b.constraint_name " +
							"and   a.constraint_type" + "= '" + "U'" +
							" and   b.table_name" + "=" + "'" + tableName + "'";
				}else if(connection.getMetaData().getURL().contains(ConfigConstant.POSTGRESQL)){
					queryForUniqueKey = "select b.column_name from information_schema.table_constraints a , information_schema.constraint_column_usage b " +
							"where a.constraint_schema = b.constraint_schema "+
							"and a.constraint_name = b.constraint_name " +
							"and a.constraint_type = 'UNIQUE' "+
							"and b.table_name = 'Table_name' "+
							"and a.constraint_schema = '"+connection.getMetaData().getUserName() + "'";
				}
				
				rsForUniqueKey = StmtForuniqueKey.executeQuery(queryForUniqueKey);
				uniqueKeyList = new ArrayList<String>();

				while(rsForUniqueKey.next()) {
					uniqueKeyList.add(rsForUniqueKey.getString("COLUMN_NAME"));
				}
				
			}else{
				Logger.logTrace(MODULE, "Connection is Null");
			}
		}catch(SQLException se){
			Logger.logError(MODULE, "SQL exception : "+ se.getMessage());
			Logger.logTrace(MODULE, se);
			se.printStackTrace();
			throw new DataManagerException("SQL exception : " + se.getMessage(), se);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}
		return uniqueKeyList;
	}

	@Override
	public SubscriberProfileData getSubscriberProfileData(String name) throws DataManagerException {
		
		SubscriberProfileData subscriberProfileData = null;
		try{
			Session session = getSession();						
			Criteria criteria = session.createCriteria(SubscriberProfileData.class).add(Restrictions.eq("userIdentity", name));
			subscriberProfileData =  (SubscriberProfileData) criteria.uniqueResult();
			
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Subscriber Profile, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Subscriber Profile, Reason: "+ exp.getMessage(), exp);
		}
		return subscriberProfileData;
	}

	@Override
	public void addSubscriberProfileData(SubscriberProfileData subscriberProfileData) throws DataManagerException {
		
		try{
			Session session=getSession();
			session.save(subscriberProfileData);
			session.flush();
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create Subscriber Profile, Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create Subscriber Profile, Reason: "+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to create Subscriber Profile, Reason: "+ exp.getMessage(),exp);
		}
	}

	@Override
	public SubscriberProfileData deleteSubscriberProfileData(String subscriberUserIdentity) throws DataManagerException {
		SubscriberProfileData subscriberProfileData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubscriberProfileData.class).add(Restrictions.eq("userIdentity",subscriberUserIdentity));
			subscriberProfileData = (SubscriberProfileData) criteria.uniqueResult();

			if(subscriberProfileData == null){
				throw new DataManagerException("Subscriber Profile not found");
			}
			session.delete(subscriberProfileData);
			session.flush();
		} catch (ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete Subscriber Profile, Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete Subscriber Profile, Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete Subscriber Profile, Reason: " + exp.getMessage(), exp);
		}
		return subscriberProfileData;
	}
}