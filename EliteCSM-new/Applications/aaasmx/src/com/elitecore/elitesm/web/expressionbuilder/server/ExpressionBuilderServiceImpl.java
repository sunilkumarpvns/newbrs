package com.elitecore.elitesm.web.expressionbuilder.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.elitesm.blmanager.diameter.dictionary.DiameterDictionaryBLManager;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.web.expressionbuilder.client.ExpressionBuilderService;
import com.elitecore.elitesm.web.expressionbuilder.shared.AttributeData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ExpressionBuilderServiceImpl extends RemoteServiceServlet implements ExpressionBuilderService {

	private final static String MODULE="EXPRESSION BUILDER IMPL"; 
	private final String ACTION_ALIAS="EXPRESSION BUILDER";
	
	
	
	public List<AttributeData> getAttributeList(String dictionaryType)throws IllegalArgumentException {
		
		List<AttributeData> attributeList=new ArrayList<AttributeData>();
		
		/*
		 *   Dictionary Type is Radius 
		 */
		
		try{
		   if("Radius".equalsIgnoreCase(dictionaryType)){
			   

			    List<DictionaryParameterDetailData> paramList=null;
				RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
				
					paramList=dictionaryBLManager.getDictionaryParameterDetailListById(0L);
					if(paramList != null){
						for(int i = 0 ; i < paramList.size() ; i ++){
							DictionaryParameterDetailData dictionaryParameterDetailData = (DictionaryParameterDetailData) paramList.get(i);
							AttributeData clientData=convertToClientData(dictionaryParameterDetailData);
							attributeList.add(clientData);							
						}
					}
		   }
		   
		   /*
			* Dictionary Type is Diameter 
		   */   
		   
		   else if("Diameter".equalsIgnoreCase(dictionaryType)){
			   
			   
			   
			   List<DiameterdicParamDetailData> paramList=null;
			   DiameterDictionaryBLManager diameterDictionaryBLManager = new DiameterDictionaryBLManager();
			   String actionAlias=ACTION_ALIAS;
			   paramList=diameterDictionaryBLManager.getDictionaryParameterDetailListById("0");
			   for (Iterator iterator = paramList.iterator(); iterator.hasNext();) {
				
				   DiameterdicParamDetailData diameterdicParamDetailData = (DiameterdicParamDetailData) iterator.next();
				   AttributeData clientData=convertToClientData(diameterdicParamDetailData);
				   attributeList.add(clientData);
				
			   }
			   
		   }
		
		}catch(DataManagerException e){
			e.printStackTrace();
		}
		
		
		return attributeList;

		
		
	}

	private AttributeData convertToClientData(DiameterdicParamDetailData serverData) {
		
		//Logger.logDebug(MODULE,"convertToClientData() call start");
		AttributeData attributeData = new AttributeData();
		try{
			attributeData.setDataTypeId(serverData.getDataTypeId());
			attributeData.setAttributeId(serverData.getAttributeId());
			attributeData.setName(serverData.getName());
			attributeData.setPredefinedValues(serverData.getPredefinedValues());
		}catch(Exception e){
			e.printStackTrace();
		}
		return attributeData;
		
	}

	private AttributeData convertToClientData(DictionaryParameterDetailData serverData) {
         
		//Logger.logDebug(MODULE,"convertToClientData() call start");
		AttributeData attributeData = new AttributeData();
		try{
			attributeData.setDataTypeId(serverData.getDataTypeId());
			attributeData.setAttributeId(serverData.getAttributeId());
			attributeData.setName(serverData.getName());
			attributeData.setPredefinedValues(serverData.getPredefinedValues());
		}catch(Exception e){
			e.printStackTrace();
		}
		return attributeData;

	}
	
	
	
     /*private final IStaffData getStaffObject(SystemLoginForm systemLoginForm) throws DataManagerException{
		
		StaffBLManager staffBLManager = new StaffBLManager();
		String userId = systemLoginForm.getUserId();
		
		IStaffData staffData = new StaffData();
		staffData = staffBLManager.getStaffData(userId);
		return staffData; 
		
	}*/	
	
	
	
	
}
