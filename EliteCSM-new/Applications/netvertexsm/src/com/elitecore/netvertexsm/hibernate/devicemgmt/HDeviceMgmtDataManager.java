package com.elitecore.netvertexsm.hibernate.devicemgmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.exception.ConstraintViolationException;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.util.csv.CSVData;
import com.elitecore.core.util.csv.CSVRecordData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.MessageData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.devicemgmt.DeviceMgmtDataManager;
import com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.logger.Logger;

public class HDeviceMgmtDataManager extends HBaseDataManager implements DeviceMgmtDataManager{
	
	private static final String MODULE=HDeviceMgmtDataManager.class.getSimpleName();
	

	@Override
	public void create(TACDetailData tacDetailData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(tacDetailData);
			session.flush();
		}catch(ConstraintViolationException hExp){
	        throw hExp;
	    }catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public void update(TACDetailData tacDetailData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(TACDetailData.class);
			criteria.add(Restrictions.eq("tacDetailId", tacDetailData.getTacDetailId()));
			
			TACDetailData data = (TACDetailData)criteria.uniqueResult();
			data.setBrand(tacDetailData.getBrand());
			data.setHardwareType(tacDetailData.getHardwareType());
			data.setOperatingSystem(tacDetailData.getOperatingSystem());
			data.setModel(tacDetailData.getModel());
			data.setTac(tacDetailData.getTac());
			data.setYear(tacDetailData.getYear());
			data.setAdditionalInfo(tacDetailData.getAdditionalInfo());
			
			setUpdateAuditDetail(data);
			
			session.update(data);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	@Override
	public void updateByTAC(TACDetailData tacDetailData) throws DataManagerException {
		try{
			
			Session session = getSession();
			Criteria criteria = session.createCriteria(TACDetailData.class);
			criteria.add(Restrictions.eq("tac", tacDetailData.getTac()));
			
			TACDetailData data = (TACDetailData)criteria.uniqueResult();
			data.setBrand(tacDetailData.getBrand());
			data.setHardwareType(tacDetailData.getHardwareType());
			data.setOperatingSystem(tacDetailData.getOperatingSystem());
			data.setModel(tacDetailData.getModel());
			data.setYear(tacDetailData.getYear());
			data.setAdditionalInfo(tacDetailData.getAdditionalInfo());
			
			setUpdateAuditDetail(data);
			
			session.update(data);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	@Override
	public void delete(Long[] tacDetailIds) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(TACDetailData.class).add(Restrictions.in("tacDetailId", tacDetailIds)).list();
			deleteObjectList(list,session);
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}
	
	@Override
	public List<TACDetailData> getTACDetails() throws DataManagerException {
		try{
			List<TACDetailData> tacDetailDatas=null;
			Session session = getSession();			
			tacDetailDatas = session.createCriteria(TACDetailData.class).list();
			return tacDetailDatas;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	
		
	}

	@Override
	public TACDetailData getTACDetailData(Long tacDetailId) throws DataManagerException {
		TACDetailData tacDetailData=null;
		try{
			Session session = getSession();			
			List list = session.createCriteria(TACDetailData.class).add(Restrictions.eq("tacDetailId", tacDetailId)).list();
			if(list!=null && !list.isEmpty()){
				tacDetailData = (TACDetailData) list.get(0);
			}
			return tacDetailData;
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	    
		
	}

	@Override
	public PageList search(TACDetailData tacDetailData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			//Example example = Example.create(tacDetailData).enableLike(MatchMode.ANYWHERE).ignoreCase();
			
			//Criteria criteria = session.createCriteria(TACDetailData.class).add(example).addOrder(Order.asc("brand"));			
			
			Criteria criteria = session.createCriteria(TACDetailData.class).addOrder(Order.asc("tac"));
			if(tacDetailData!=null){
				if( tacDetailData.getTac()!=null && tacDetailData.getTac().longValue()>0){
					criteria.add(Restrictions.like("tac", tacDetailData.getTac()));
				}
				if( tacDetailData.getBrand()!=null && tacDetailData.getBrand().trim().length()>0){
					criteria.add(Restrictions.ilike("brand", tacDetailData.getBrand(),MatchMode.ANYWHERE));
				}
				if(tacDetailData.getModel()!=null && tacDetailData.getModel().trim().length()>0){
					criteria.add(Restrictions.ilike("model", tacDetailData.getModel(),MatchMode.ANYWHERE));
				}
				if(tacDetailData.getHardwareType()!=null && tacDetailData.getHardwareType().trim().length()>0){
					criteria.add(Restrictions.ilike("hardwareType", tacDetailData.getHardwareType(),MatchMode.ANYWHERE));
				}
				if(tacDetailData.getOperatingSystem()!=null && tacDetailData.getOperatingSystem().trim().length()>0){
					criteria.add(Restrictions.ilike("operatingSystem", tacDetailData.getOperatingSystem(),MatchMode.ANYWHERE));
				}
				if(Strings.isNullOrBlank(tacDetailData.getAdditionalInfo()) == false){
					criteria.add(Restrictions.ilike("additionalInfo", tacDetailData.getAdditionalInfo(),MatchMode.ANYWHERE));
				}
			}
			
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List bodList = criteria.list();
	        long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0)
				totalPages-=1;
	        
	        pageList = new PageList(bodList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}
	
	@Override
	public List<TACDetailData> getTacDetails(Long[] tacDetailIds)
			throws DataManagerException {
		try{
			Session session = getSession();			
			List tacDetails = session.createCriteria(TACDetailData.class).add(Restrictions.in("tacDetailId", tacDetailIds)).list();
			return tacDetails;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public List<MessageData> uploadCSV(CSVData csvData) throws DataManagerException, SQLException {
		List<MessageData> messages = new ArrayList<MessageData>();
		Session session = getSession();
		
		//SessionFactoryImplementor impl = (SessionFactoryImplementor)session.getSessionFactory();
		//ConnectionProvider cp = impl.getConnectionProvider();
		SessionImplementor sim = (SessionImplementor) session;
		
		
		Connection conn = sim.connection();
		//DBConnectionManager.getInstance().getSMDatabaseConection(); // cp.getConnection();
		PreparedStatement insertStatement = null;
		PreparedStatement updateStatement = null;
        try{
        	
        	if(csvData!=null){
        		String[] fieldNames = csvData.getFieldNames();

        		String strInsertQuery  = getInsertQuery(fieldNames);
        		String strUpdateQuery  = getUpdateQuery(fieldNames);
        		
        		if(strInsertQuery!=null && strUpdateQuery!=null){
        			
        			Logger.logDebug(MODULE, "Insert Query for Tac Detail :"+strInsertQuery);
        			Logger.logDebug(MODULE, "Update Query for Tac Detail :"+strUpdateQuery);
        			
        			insertStatement = conn.prepareStatement(strInsertQuery);
        			updateStatement = conn.prepareStatement(strUpdateQuery);
        			
        			conn.setAutoCommit(false);
        			List<CSVRecordData> csvRecordList = csvData.getRecords();
        			if(csvRecordList!=null && !csvRecordList.isEmpty()){
        				for(CSVRecordData csvRecordData:csvRecordList){
        					
        					String fieldValues[] = csvRecordData.getRecord();
        					int insertColumnIndex = 1;
        					for (int i = 0; i < fieldNames.length; i++) {
        						if(fieldNames[i]!=null && fieldNames[i].length()>0){
        							if(i<=fieldValues.length-1){
        								insertStatement.setString(insertColumnIndex, fieldValues[i]);
        							}else{
        								insertStatement.setString(insertColumnIndex, null);
        							}
        							insertColumnIndex++;
        						}
							}
        					
        					String tac = getTAC(fieldNames,fieldValues);
        					try{
        						int result = insertStatement.executeUpdate();
        						if(result>0){
        							conn.commit();
        							messages.add(getMessage("insert.success",csvRecordData.getLineNumber(), tac , null));
        						}
        					}catch(SQLException  e){
        						e.printStackTrace();
        						if(e.getMessage().contains("ORA-00001")){
        							try{
        								int updateColumnIndex = 1;
        	        					for (int i = 0; i < fieldNames.length; i++) {
        	        						if(fieldNames[i]!=null && fieldNames[i].length()>0){
        	        							if(i<=fieldValues.length-1){
        	        								updateStatement.setString(updateColumnIndex, fieldValues[i]);
        	        							}else{
        	        								updateStatement.setString(updateColumnIndex, null);
        	        							}
        	        							updateColumnIndex++;
        	        						}
        								}
        								updateStatement.setString(updateColumnIndex,tac);
        								int result = updateStatement.executeUpdate();
        								if(result>0){
        									conn.commit();
        									messages.add(getMessage("update.success",csvRecordData.getLineNumber(), tac,null));
        								}else{
        									Logger.logError(MODULE, "Error while updating TAC("+tac+"),Reason : Record Not Found ");
        									messages.add(getMessage("update.fail",csvRecordData.getLineNumber(), tac,"Record-Not-Found"));
        								}

        							}catch(Exception ex){
        								ex.printStackTrace();
        								Logger.logError(MODULE, "Error while updating TAC("+tac+"), Reason:"+ex.toString());
        								messages.add(getMessage("update.fail",csvRecordData.getLineNumber(), tac,ex.toString()));
        							}
        						}else{
        							Logger.logError(MODULE, "Error while inserting TAC("+tac+"), Reason:"+e.toString());
            						messages.add(getMessage("insert.fail",csvRecordData.getLineNumber(), tac,e.toString()));	
        						}
        					}catch(Exception e){
        						e.printStackTrace();
        						Logger.logError(MODULE, "Error while inserting TAC("+tac+"), Reason:"+e.toString());
        						messages.add(getMessage("insert.fail",csvRecordData.getLineNumber(), tac,e.toString()));	
        					}
        				}
        			}
        		}
        	}
        	return messages;
        }finally{
        	DBUtility.closeQuietly(insertStatement);
        	DBUtility.closeQuietly(updateStatement);
        	DBUtility.closeQuietly(conn);
        	session.close();
        }
      
	}
	private String getTAC(String[] fieldNames, String[] fieldValues) {
		if(fieldNames!=null && fieldValues!=null){
			for (int i = 0; i < fieldNames.length; i++) {
				if(fieldNames[i]!=null && fieldNames[i].equalsIgnoreCase("tac")){
					return fieldValues[i];
				}

			}
		}
		return null;
	}

	private String getUpdateQuery(String[] fieldNames) {
		if(fieldNames!=null && fieldNames.length>0){
			String updateQuery = "UPDATE TBLMTACDETAIL SET ";
			for (int i = 0; i < fieldNames.length-1; i++) {
				if(fieldNames[i]!=null && fieldNames[i].length()>0){
					updateQuery = updateQuery + " "+fieldNames[i]+"=?, ";
				}
			}
			if(fieldNames[fieldNames.length-1]!=null && fieldNames[fieldNames.length-1].length()>0){
				updateQuery = updateQuery + " "+fieldNames[fieldNames.length-1]+"=? ";
			}
			updateQuery += "WHERE TAC=?";
			return updateQuery;
		}
		return null;
	}

	private String getInsertQuery(String[] fieldNames) {
		if(fieldNames!=null && fieldNames.length>0){
			String insertQuery = "INSERT INTO TBLMTACDETAIL (TACDETAILID,";
			String values=" VALUES (SEQ_MTACDETAIL.nextval, ";
			for (int i = 0; i < fieldNames.length-1; i++) {
				if(fieldNames[i]!=null && fieldNames[i].length()>0){
					insertQuery +=  " "+fieldNames[i]+", ";
					values += "?, ";
				}
			}
			if(fieldNames[fieldNames.length-1]!=null && fieldNames[fieldNames.length-1].length()>0){
				insertQuery +=  " "+fieldNames[fieldNames.length-1]+" )";
				values += "? )";
			}
			insertQuery = insertQuery+ values;
			return insertQuery;
		}
		return null;
	}

	private MessageData getMessage(String messageType, int line, String tac, String failureReason){
  		MessageData message = null; 
  		if("insert.success".equals(messageType)){
  			message=new MessageData(MessageData.INFO, "  Line:"+line+" TAC("+tac+") Successfully Inserted");
  		}else if("insert.fail".equals(messageType)){
  			message=new MessageData(MessageData.ERROR, "  Line:"+line+" TAC("+tac+") Failed to Insert. Reason-  "+failureReason);
  		}else if("update.success".equals(messageType)){
  			message=new MessageData(MessageData.WARN, "  Line:"+line+" TAC("+tac+") Successfully Updated");
  		}else if("update.fail".equals(messageType)){
  			message=new MessageData(MessageData.ERROR, "  Line:"+line+" TAC("+tac+") Failed to Update. Reason-  "+failureReason);
  		}
  		return message;
  	}
}
