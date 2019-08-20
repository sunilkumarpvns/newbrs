package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DBSubscriberProfileParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDBSubscriberProfileParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.ISQLParamPoolValueData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.SQLParamPoolValueData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateValuePoolForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

public class UpdateValuePoolAction extends BaseDictionaryAction{
	
	private static final String VIEW_FORWARD = "updateFieldValuePool";
	  private static final String FAILURE_FORWARD = "failure";
	  private static final String SUCCESS_FORWARD = "popupsuccess";
	  public static final String ACTION_ALIAS = ConfigConstant.UPDATE_VALUE_POOL;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		if(checkAccess(request, ACTION_ALIAS)) {
			Logger.logTrace(MODULE, "Enter the execute method of :" + getClass().getName());
			try {
				UpdateValuePoolForm updateValuePoolForm = (UpdateValuePoolForm)form;
				DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
				IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
		
				String checkRadio = updateValuePoolForm.getStatus();
            	updateValuePoolForm.setStatus(checkRadio);
            	
            	String strFieldId = request.getParameter("fieldId");
            	String fieldId=null;
                if (strFieldId == null) {
                	fieldId = updateValuePoolForm.getParamId();
                }else{
                	fieldId = strFieldId;
                }
                
        		String valuePoolAction = updateValuePoolForm.getValuePoolAction();
				if(valuePoolAction == null){
					updateValuePoolForm.setValuePoolAction("");
				}
				
				updateValuePoolForm.setValuePoolAction(valuePoolAction);
			
				datasourceSchemaData.setFieldId(fieldId);
	            datasourceSchemaData = blManager.getDatasourceSchema(datasourceSchemaData);
	            request.setAttribute("datasourceSchemaData", datasourceSchemaData);
	            request.setAttribute("updateValuePoolForm", updateValuePoolForm);
            	
            	
	            
	            if(checkRadio!=null && checkRadio.equalsIgnoreCase("valuePool")){
	            	if (valuePoolAction == null || valuePoolAction.equalsIgnoreCase("")) {
	            		updateValuePoolForm.getDbdsDatasourceParamPoolList().clear();
	            		
	            		if (Strings.isNullOrEmpty(fieldId) == false) {
	            			updateValuePoolForm.setParamId(fieldId);
	            			datasourceSchemaData.setFieldId(fieldId);
                    	
	            			List paramValuePoolList = blManager.getParamValuePoolList(datasourceSchemaData);
                    	
	            			for(Iterator iter = paramValuePoolList.iterator(); iter.hasNext();){
                    			DatasourceSchemaData data1 = (DatasourceSchemaData) iter.next();
	            			}
                    	
	            			if (paramValuePoolList.size() > 0) {
	            				datasourceSchemaData = (IDatasourceSchemaData) paramValuePoolList.get(0);
	            				Set setDBDSDatabaseParamValuePool = datasourceSchemaData.getDbdsParamPoolValueSet();
                            
	            				Iterator itSetDBDSDatabaseParamValuePool = setDBDSDatabaseParamValuePool.iterator();
	            				List lstDBDSDatabaseParamValuePool = new ArrayList();
                            
	            				while (itSetDBDSDatabaseParamValuePool.hasNext()) {
	            					lstDBDSDatabaseParamValuePool.add(itSetDBDSDatabaseParamValuePool.next());
	            				}
                            
	            				for(Iterator iter = lstDBDSDatabaseParamValuePool.iterator(); iter.hasNext();){
	            					DBSubscriberProfileParamPoolValueData data1 = (DBSubscriberProfileParamPoolValueData) iter.next();
	            					System.out.println("NAME PRINTING : "+data1.getName());
	            					System.out.println("VALUE PRINTING : "+data1.getValue());
	            				}
	            				int size = lstDBDSDatabaseParamValuePool.size();
                           
	            				for(Iterator iter = lstDBDSDatabaseParamValuePool.iterator(); iter.hasNext();){
	            					IDBSubscriberProfileParamPoolValueData prin = (DBSubscriberProfileParamPoolValueData)iter.next();
	            				}
                            
	            				updateValuePoolForm.getDbdsDatasourceParamPoolList().addAll(lstDBDSDatabaseParamValuePool);
                            
	            				//updateValuePoolForm.getDbdsDatasourceParamPoolList().add(new DBDSParamPoolValueData());
                       
                        
	            			}
                        	updateValuePoolForm.setNavigationCode("1");
                        	return mapping.findForward(VIEW_FORWARD);
                        
	            			} else {
	            				return mapping.findForward(FAILURE_FORWARD);
	            			}
    				
    				
	            	} else if (valuePoolAction.equalsIgnoreCase("add")) {
	            		updateValuePoolForm.getDbdsDatasourceParamPoolList().add(new DBSubscriberProfileParamPoolValueData());
	            		updateValuePoolForm.setNavigationCode("1");
	            		return mapping.findForward(VIEW_FORWARD);
	            	} else if (valuePoolAction.equalsIgnoreCase("Remove")) {
	            		updateValuePoolForm.getDbdsDatasourceParamPoolList().remove(updateValuePoolForm.getItemIndex());
	            		updateValuePoolForm.setNavigationCode("1");
	            		return mapping.findForward(VIEW_FORWARD);
	            	} else if (valuePoolAction != null && valuePoolAction.equalsIgnoreCase("update")) {
	            		System.out.println("here in update button  class update");
	            		List lstDBDSDatasourceParamPoolDetail = (List) updateValuePoolForm.getDbdsDatasourceParamPoolList();
	            		Iterator itLstDBDSDatasourceParamPoolDeail = lstDBDSDatasourceParamPoolDetail.iterator();
	            		
	            		while (itLstDBDSDatasourceParamPoolDeail.hasNext()) {
	            			IDBSubscriberProfileParamPoolValueData dbSubscriberProfileParamPoolValueData = (IDBSubscriberProfileParamPoolValueData)itLstDBDSDatasourceParamPoolDeail.next();
	            			if (dbSubscriberProfileParamPoolValueData.getName() == null || dbSubscriberProfileParamPoolValueData.getValue() == null 
	            					|| dbSubscriberProfileParamPoolValueData.getName().equalsIgnoreCase("") || dbSubscriberProfileParamPoolValueData.getValue().equalsIgnoreCase(""))
	            				itLstDBDSDatasourceParamPoolDeail.remove();
	            		}
	            		int j = 1;
	            		for ( int i = 0; i < lstDBDSDatasourceParamPoolDetail.size(); i++ ) {
	            			IDBSubscriberProfileParamPoolValueData dbSubscriberProfileParamPoolValueData = (IDBSubscriberProfileParamPoolValueData)lstDBDSDatasourceParamPoolDetail.get(i);
	            			System.out.println("Names from the update list : "+dbSubscriberProfileParamPoolValueData.getName());
	            			dbSubscriberProfileParamPoolValueData.setAppOrder(j);
	            			j++;   
	            		}
	            		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	    				
	            		Set tempSet = new HashSet();
                    	tempSet.clear();
                    	tempSet.addAll(lstDBDSDatasourceParamPoolDetail);
                    	datasourceSchemaData.setDbdsParamPoolValueSet(tempSet);
                   
                    	blManager.updateValuePool(datasourceSchemaData,fieldId);
                    	updateValuePoolForm.setNavigationCode("1");
                    
                    	/*request.setAttribute("responseUrl", "/updateValuePool");
                    
                    	ActionMessage message = new ActionMessage("database.datasource.parampoolvalue.update.success");
                    	ActionMessages messages = new ActionMessages();
                    	messages.add("information", message);
                    	saveMessages(request, messages);*/
                    	doAuditing(staffData, ACTION_ALIAS);
                    	ActionMessage message = new ActionMessage("servermgr.datasource.updatevaluepool.success");
                    	ActionMessages messages = new ActionMessages();
                    	messages.add("information", message);
                    	saveMessages(request, messages);
                    	Logger.logInfo(MODULE, "Forwarding to :" + mapping.findForward(SUCCESS_FORWARD).getPath());
                    
                    	return mapping.findForward(SUCCESS_FORWARD);
	            	}
            	} else if(checkRadio!=null && checkRadio.equalsIgnoreCase("query")){
            	
            		if (valuePoolAction == null || valuePoolAction.equalsIgnoreCase("")) {
            		
            			if ( Strings.isNullOrBlank(fieldId) == false) {
            				updateValuePoolForm.setParamId(fieldId);
            			}
            			datasourceSchemaData.setFieldId(fieldId);
                	
            			List schemaList = blManager.getParamValuePoolList(datasourceSchemaData);
            			if (schemaList.size() > 0) {
            				datasourceSchemaData = (IDatasourceSchemaData) schemaList.get(0);
            			}
                	
            			if(datasourceSchemaData.getSqlData() != null){
            				String sqlId = datasourceSchemaData.getSqlData().getSqlId();
                    		String queryString = datasourceSchemaData.getSqlData().getQuery();
                    		
                    		if(queryString != null){
                    			updateValuePoolForm.setQueryString(queryString);
                    		}
                    	
            			}
                	
            			updateValuePoolForm.setNavigationCode("2");
            			return mapping.findForward(VIEW_FORWARD);
            		}else{
            			if (Strings.isNullOrEmpty(fieldId) == false) {
            				updateValuePoolForm.setParamId(fieldId);
            			}
            			datasourceSchemaData.setFieldId(fieldId);
                	
            			List schemaList = blManager.getParamValuePoolList(datasourceSchemaData);
            			if (schemaList.size() > 0) {
            				datasourceSchemaData = (IDatasourceSchemaData) schemaList.get(0);
            			}
                	
            			ISQLParamPoolValueData sqlData = new SQLParamPoolValueData();
            			if(datasourceSchemaData.getSqlData() !=null){
            				String sqlId = datasourceSchemaData.getSqlData().getSqlId();
                    		sqlData = datasourceSchemaData.getSqlData();
                    		sqlData.setQuery(updateValuePoolForm.getQueryString());
                    		blManager.updateQueryPool(sqlData);
            			} else {
            				String queryString = updateValuePoolForm.getQueryString();
            				sqlData.setQuery(queryString);
            				blManager.updateQueryPool(sqlData, fieldId);
            			}
            			ActionMessage message = new ActionMessage("servermgr.datasource.updatequery.success");
            			ActionMessages messages = new ActionMessages();
            			messages.add("information", message);
            			saveMessages(request, messages);
            			Logger.logInfo(MODULE, "Forwarding to :" + mapping.findForward(SUCCESS_FORWARD).getPath());	
            			return mapping.findForward(SUCCESS_FORWARD);
            		}
            	} else {
            		updateValuePoolForm.setParamId(fieldId);
            	
            		if(datasourceSchemaData.getSqlData()!= null){
            			String sqlId = datasourceSchemaData.getSqlData().getSqlId();
                		String queryString = datasourceSchemaData.getSqlData().getQuery();
                		if(queryString != null){
                			updateValuePoolForm.setQueryString(queryString);
                		}
                		updateValuePoolForm.setStatus("query");
            		} else {
            			updateValuePoolForm.setStatus("valuePool");
            			Set setDBDSDatabaseParamValuePool = datasourceSchemaData.getDbdsParamPoolValueSet();
            			Iterator itSetDBDSDatabaseParamValuePool = setDBDSDatabaseParamValuePool.iterator();
            			List lstDBDSDatabaseParamValuePool = new ArrayList();
                	
            			while (itSetDBDSDatabaseParamValuePool.hasNext()) {
                    	lstDBDSDatabaseParamValuePool.add(itSetDBDSDatabaseParamValuePool.next());
            			}
            			updateValuePoolForm.setDbdsDatasourceParamPoolList(lstDBDSDatabaseParamValuePool);
            		}
            		updateValuePoolForm.setNavigationCode("0");
            		return mapping.findForward(VIEW_FORWARD);
            	}
			} catch (Exception managerExp) {
				managerExp.printStackTrace();
				Logger.logError(MODULE, "Error during data Manager operation,reason :" + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			  }
			ActionMessage message = new ActionMessage("database.datasource.view.fieldfailure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

}
