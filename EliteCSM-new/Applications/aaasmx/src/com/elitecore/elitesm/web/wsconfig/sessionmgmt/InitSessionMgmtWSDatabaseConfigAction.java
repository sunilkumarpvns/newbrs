package com.elitecore.elitesm.web.wsconfig.sessionmgmt;

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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSAttrFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSDBFieldMapData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.wsconfig.sessionmgmt.forms.SessionMgmtWSDatabaseConfigForm;
import com.elitecore.elitesm.ws.sessionmgmt.SessionMgmtWebServiceBLManager;


public class InitSessionMgmtWSDatabaseConfigAction extends BaseWebAction{
	private static final String SESSION_MGMT_MODULE = "InitSessionMgmtWSDatabaseConfigAction";
	private static final String UPDATE_FORWARD = "updateSessionMgmtWSDatabaseConfig";
	private static final String FAILURE_FORWARD = "failure";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
		Logger.logInfo(SESSION_MGMT_MODULE, "Entered execute method of " + getClass().getName());
		SessionMgmtWSDatabaseConfigForm sessionMgmtWSDatabaseConfigForm = (SessionMgmtWSDatabaseConfigForm)form;
		ActionMessages messages = new ActionMessages();

		try {

			if(sessionMgmtWSDatabaseConfigForm.getCheckAction().equalsIgnoreCase("refresh")){
				SessionMgmtWebServiceBLManager.setConfiguration();
			 }
			
			WebServiceConfigBLManager configBLManager= new WebServiceConfigBLManager();

			DatabaseDSBLManager databaseDSBLManager= new DatabaseDSBLManager();
			List<IDatabaseDSData> lstDatasource=databaseDSBLManager.getDatabaseDSList();
			sessionMgmtWSDatabaseConfigForm.setLstDatasource(lstDatasource);

			IWSConfigData sessionMgmtConfigData = configBLManager.getSessionMgmtConfiguration();

			convertBeanToForm(sessionMgmtWSDatabaseConfigForm,sessionMgmtConfigData);

			/*
			 * set Field map to request attr
			 */

			 Set<IWSDBFieldMapData>  wsDBFieldMapSet=sessionMgmtConfigData.getWsDBFieldMapSet();

			 String[] fields=null;
			 String[] keys=null;

			 if(Collectionz.isNullOrEmpty(wsDBFieldMapSet) ==  false){

				 int len=wsDBFieldMapSet.size();
				 fields= new String[len];
				 keys= new String[len];
				 int index=0;
				 for (Iterator iterator = wsDBFieldMapSet.iterator(); iterator.hasNext();) {

					 IWSDBFieldMapData fieldMapData = (IWSDBFieldMapData) iterator.next();
					 fields[index]=fieldMapData.getFieldName();
					 keys[index]=fieldMapData.getKey();
					 index++;
				 }
			 }

			 /*
			  * set Attribute Field map to request attribute
			  * 
			  */

			  Set<IWSAttrFieldMapData>  wsAttrFieldMapSet=sessionMgmtConfigData.getWsAttrFieldMapSet();

			  String[] attrFields=null;
			  String[] attrKeys=null;

			  if(Collectionz.isNullOrEmpty(wsAttrFieldMapSet) == false){

				  int len=wsAttrFieldMapSet.size();
				  attrFields= new String[len];
				  attrKeys= new String[len];
				  int index=0;
				  for (Iterator iterator = wsAttrFieldMapSet.iterator(); iterator.hasNext();) {

					  IWSAttrFieldMapData fieldMapData = (IWSAttrFieldMapData) iterator.next();
					  attrFields[index]=fieldMapData.getFieldName();
					  attrKeys[index]=fieldMapData.getAttribute();
					  index++;
				  }
			  }

			  request.setAttribute("attrFields",attrFields);
			  request.setAttribute("attrKeys",attrKeys);
			  request.setAttribute("fields",fields);
			  request.setAttribute("keys",keys);
			  request.setAttribute("subscriberWSDatabaseConfigForm", sessionMgmtWSDatabaseConfigForm);

			  /*request.getSession().removeAttribute("dbFieldMapList");
        	if(subscriberDBConfigData.getDbFieldMapList()!=null){
        		request.getSession().setAttribute("dbFieldMapList", subscriberDBConfigData.getDbFieldMapList());
        	}else{
        		request.getSession().setAttribute("dbFieldMapList", new ArrayList());
        	}*/            	
		

			return mapping.findForward(UPDATE_FORWARD); 	

		}catch (DataManagerException e){
			Logger.logError(SESSION_MGMT_MODULE, "Error during Data Manager operation , Reason : " + e.getMessage());
			Logger.logTrace(SESSION_MGMT_MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch (Exception e) {
			Logger.logError(SESSION_MGMT_MODULE, "Error during Data Manager operation , Reason : " + e.getMessage());
			Logger.logTrace(SESSION_MGMT_MODULE, e);
			ActionMessage message = new ActionMessage("general.error");
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}

	}
	private void convertBeanToForm(SessionMgmtWSDatabaseConfigForm form, IWSConfigData data){
		if(data != null){
			form.setDatabaseId(data.getDatabasedsId());	
			form.setTableName(data.getTableName());
			//form.setUserIdentityFieldName(data.getUserIdentityFieldName());
			form.setRecordFetchLimit(data.getRecordFetchLimit());
			form.setWsConfigId(data.getWsconfigId());
		}

	}
	

}
