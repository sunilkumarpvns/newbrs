package com.elitecore.elitesm.blmanager.radius.bwlist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.bwlist.BWListDataManager;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.ErrorBean;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BWListConstant;
import com.elitecore.elitesm.util.logger.Logger;

public class BWListBLManager extends BaseBLManager{
	private static final String MODULE="BWLIST BL MANAGER";
	public BWListData create(BWListData bwlistData) throws DataManagerException {
		EliteAssert.notNull(bwlistData,"bwlistData Must Specified");
		IDataManagerSession session = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			BWListDataManager bwlistDataManager = getBWListDataManager(session);
			EliteAssert.notNull(bwlistDataManager, "Data Manager Implementation not found for " + BWListDataManager.class.getSimpleName());
			session.beginTransaction();

			BWListData createdBwlistData = bwlistDataManager.create(bwlistData);

			commit(session);
			return createdBwlistData;
		}
		catch (DataManagerException hExp) {
			rollbackSession(session);
			Logger.logDebug(MODULE, "--END-- Crate BWList Instance Result:- Faild. Reason :-" + hExp.getMessage());
			throw new DataManagerException("Create Action failed : " + hExp.getMessage(), hExp);
		}catch (Exception e) {
			rollbackSession(session);
			Logger.logDebug(MODULE, "--END-- Crate BWList Instance Result:- Faild. Reason :-" + e.getMessage());
			throw new DataManagerException("Create Action failed : " + e.getMessage(), e);
		}
		finally {
			closeSession(session);
		}
	}
	public BWListDataManager getBWListDataManager(IDataManagerSession session) {
		return   (BWListDataManager) DataManagerFactory.getInstance().getDataManager(BWListDataManager.class, session);
	}
	public PageList search(BWListData bwListData, int requiredPageNo,Integer pageSize) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PageList bwlist;

		BWListDataManager bwlistDataManager = getBWListDataManager(session);
		EliteAssert.notNull(bwlistDataManager, "Data Manager Implementation not found for " + BWListDataManager.class.getSimpleName());

		try{
			session.beginTransaction();	

			bwlist = bwlistDataManager.search(bwListData, requiredPageNo, pageSize); 

			session.commit();
			session.close();
		}
		catch(Exception e){
			Logger.logTrace(MODULE, e);
			session.rollback();
			session.close();

			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}
		return bwlist;
	}
	public void delete(List<String> asList)  throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		BWListDataManager bwlistDataManager = getBWListDataManager(session);
		EliteAssert.notNull(bwlistDataManager, "Data Manager Implementation not found for " + BWListDataManager.class.getSimpleName());

		try{
			session.beginTransaction();
			if(asList != null){
				for(int i=0;i<asList.size();i++){
					if(asList.get(i) != null){
						String bwId = asList.get(i);
						bwlistDataManager.delete(bwId);
					}
				}
				session.commit();
				session.close();
			}else{
				session.rollback();
				session.close();
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			}
			
		}catch(DataManagerException exp){
			Logger.logTrace(MODULE, exp);
			session.rollback();
			session.close();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp.getCause());
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			session.rollback();
			session.close();
			throw new DataManagerException("Action failed : "+e.getMessage());
			
		}finally{
			closeSession(session);
		}
		
	}

	public List<ErrorBean> addCSVDataToDatabase(FormFile fileUpload) throws DataManagerException {
		List<ErrorBean> errorBeanList = new ArrayList<ErrorBean>();
		IDataManagerSession session=null;
		
		try{

			FormFile file = fileUpload;
			Logger.logDebug(MODULE, "file url is:"+file.getFileName());
			String record = null;
			int recCount = 0;
             
			if(file!=null){
				String fileName = file.getFileName();
				if (fileName != null && !fileName.equalsIgnoreCase("")) {
					BufferedReader breader = new BufferedReader(new InputStreamReader(file.getInputStream()));
					int attributeColIndex = 0;
					int attributeValueColIndex=1;
					
					record = "";
					
					int lineIndex=0;
					
				    while((record = breader.readLine())!=null){
				    	String attribute=null;
						String attributeValue = null;
						lineIndex++;
						Logger.logDebug(MODULE, "Record :"+record);
						
						if(record.trim() != null && record.trim().length() > 0){
							StringTokenizer st = new StringTokenizer(record,",");
							
							if(recCount == 0){
								recCount++;
								//finding header in CSV file.
								int count = 0;

								while(st.hasMoreTokens()){
									String token = st.nextToken();

									if(token.equalsIgnoreCase(BWListConstant.ATTRIBUTE)){
										attributeColIndex = count;
										count++;
									}else if(token.equalsIgnoreCase(BWListConstant.ATTRIBUTE_VALUE)){
										attributeValueColIndex=count;
										count++;
									}

								}
								if(count!=0){
									Logger.logDebug(MODULE, "Header :"+record);
									continue;
								}
							}


							st = new StringTokenizer(record,",");
							int col=0;
							while(st.hasMoreTokens()){
								String token = st.nextToken();

								if(col==attributeColIndex){
									attribute=token;

								}else if(col==attributeValueColIndex){
									attributeValue = token;
								}
								col++;

							}

							BWListData bwData = new BWListData();
							bwData.setCommonStatusId("CST01");
							bwData.setTypeId("LST00");
							bwData.setAttributeId(attribute);
							bwData.setAttributeValue(attributeValue);
							
							Logger.logDebug(MODULE, lineIndex+". Attribute :"+attribute +",AttributeValue: "+attributeValue);
							
							try{
								session = DataManagerSessionFactory.getInstance().getDataManagerSession();
								BWListDataManager bwlistDataManager = getBWListDataManager(session);
								EliteAssert.notNull(bwlistDataManager, "Data Manager Implementation not found for " + BWListDataManager.class.getSimpleName());
								session.beginTransaction();
								bwlistDataManager.create(bwData);
								commit(session);
								
								
							}catch(DuplicateEntityFoundException cexp){
								Logger.logError(MODULE,"Duplicate..."+cexp.getMessage());
								ErrorBean errorBean =new ErrorBean();
								errorBean.setAttribute(bwData.getAttributeId());
								errorBean.setAttributeValue(bwData.getAttributeValue());
								errorBean.setLineNo(lineIndex);
								errorBean.setMessage("Duplicate Values,");
								errorBeanList.add(errorBean);
								session.close();

							}catch(DataManagerException exp){
								Logger.logError(MODULE,"Error while bulk insert by file..."+exp.getMessage());   

								ErrorBean errorBean =new ErrorBean();
								errorBean.setAttribute(bwData.getAttributeId());
								errorBean.setAttributeValue(bwData.getAttributeValue());
								errorBean.setLineNo(lineIndex);
								errorBean.setMessage("Error while inserting, ");
								errorBeanList.add(errorBean);
								session.close();
								
							}

						}

					}
				}

			}
		}catch(Exception e){
			if(session != null){
			session.rollback();
			session.close();
			}
			throw new DataManagerException("Action failed : "+e.getMessage());
		}finally{
			if(session != null)
				session.close();
				
		}

		return errorBeanList;


}
	
	public BWListData getBWListData(String bwId)  throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BWListDataManager bwlistDataManager = getBWListDataManager(session);
		
		try {
			session.beginTransaction();
			return bwlistDataManager.getBWListData(bwId);
		} catch(DataManagerException exp){
			throw new DataManagerException("Action Failed : "+exp.getMessage());
		}finally{
			closeSession(session);
		}
	}
	
	
	public void updateStatus(List<String> lstbwid, String commonStatusId) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BWListDataManager bwlistDataManager = getBWListDataManager(session);
    	
    	EliteAssert.notNull(bwlistDataManager, "Data Manager Implementation not found for " + BWListDataManager.class.getSimpleName());
    	
    	try{
    		updateStatusValidate(lstbwid,commonStatusId);
	    	session.beginTransaction();
    		if(lstbwid != null){
				for(int i=0;i<lstbwid.size();i++){
					if(lstbwid.get(i) != null){
						
						String transactionId = lstbwid.get(i).toString();
						bwlistDataManager.updateStatus(transactionId,commonStatusId);
					}
				}
			session.commit();
			session.close();
	    	}else{
	    		throw new DataManagerException("Data Manager implementation not found for ");    		
	    	}
    	}catch(DataManagerException exp){
    		session.rollback();
    		session.close();
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	}finally{
			closeSession(session);
		}
		
	}
	
	 public void updateStatusValidate(List lstbwid,String commonStatusId)throws DataManagerException{
	        // commonStatusId
	    	if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
	    		throw (new DataValidationException("Invalid BlackList Candidate commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
	    	}

	    }
	 
	public void update(BWListData bwlistOldData, BWListData bwlistData, IStaffData staffData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BWListDataManager bwlistDataManager = getBWListDataManager(session);
    	
    	try{
	    	session.beginTransaction();
			bwlistDataManager.update(bwlistOldData, bwlistData, staffData);
			commit(session);
	     }catch(DataManagerException exp){
    		rollbackSession(session);
    		throw new DataManagerException("Action Failed : "+exp.getMessage());
    	}finally{
			closeSession(session);
		}
	}	
	
}	
	
