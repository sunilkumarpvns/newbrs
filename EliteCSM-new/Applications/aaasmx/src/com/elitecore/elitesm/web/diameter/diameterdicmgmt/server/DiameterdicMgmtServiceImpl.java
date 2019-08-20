package com.elitecore.elitesm.web.diameter.diameterdicmgmt.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;
import com.elitecore.elitesm.blmanager.diameter.dictionary.DiameterDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.client.DiameterdicMgmtService;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AVPRuleData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AttributeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DataTypeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DiameterdicMgmtServiceImpl extends RemoteServiceServlet implements DiameterdicMgmtService {

	private static final String MODULE = "Diameter DictionaryMgmt ServiceImpl";

    private DiameterdicData dictionaryData=null;

	public List<AttributeData> getAttributeList(String strDictionaryId)	throws IllegalArgumentException {

		List<AttributeData> attributeList=new ArrayList<AttributeData>();
		List<DiameterdicParamDetailData> paramList=null;
		DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
		try{
			paramList=dictionaryBLManager.getDictionaryParameterDetailListById(strDictionaryId);
			for (Iterator iterator = paramList.iterator(); iterator.hasNext();) {

				DiameterdicParamDetailData dictionaryParameterDetailData = (DiameterdicParamDetailData) iterator.next();
				AttributeData clientData=convertToClientAttributeData(dictionaryParameterDetailData);
				attributeList.add(clientData);

			}

		}catch(DataManagerException e){
			e.printStackTrace();
		}

		return attributeList;
	}



	private AttributeData convertToClientAttributeData(DiameterdicParamDetailData serverData) {
		
		AttributeData clientAttributeData=new AttributeData();
		try{
			clientAttributeData.setAlias(serverData.getAlias());
			clientAttributeData.setDataTypeId(serverData.getDataTypeId());
			clientAttributeData.setDescription(serverData.getDescription());
			clientAttributeData.setDiameterdciParamDetailId(serverData.getDiameterdciParamDetailId());
			clientAttributeData.setDictionaryId(serverData.getDictionaryId());
			clientAttributeData.setDictionaryNumber(serverData.getDictionaryNumber());
			clientAttributeData.setEncryption(serverData.getEncryption());
            clientAttributeData.setMandatory(serverData.getMandatory());
            clientAttributeData.setName(serverData.getName());
            clientAttributeData.setNetworkFilterSupport(serverData.getNetworkFilterSupport());
            clientAttributeData.setPredefinedValues(serverData.getPredefinedValues());
            clientAttributeData.setStrProtected(serverData.getStrProtected());
            clientAttributeData.setVendorId(serverData.getVendorId());
            clientAttributeData.setVendorParameterId(serverData.getVendorParameterId());
            clientAttributeData.setVendorParameterOveridden(serverData.getVendorParameterOveridden());
            
            com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData typeData =(com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData) serverData.getDataType();
            clientAttributeData.setDataTypeData(convertToClientData(typeData));
            
            Map<String, AvpRule> fixedGroupedAttribute=serverData.getFixedGroupedAttribute();
           
            if(fixedGroupedAttribute != null){
            	List<AVPRuleData> fixedGroupedAttributeList=new ArrayList<AVPRuleData>();
            	Iterator<String> itr=fixedGroupedAttribute.keySet().iterator();
			     
			      while(itr.hasNext()){
			    	   
			           AvpRule serverAVPData=fixedGroupedAttribute.get(itr.next());
			           AVPRuleData clientAvpData=converToClientAvpData(serverAVPData);
			           fixedGroupedAttributeList.add(clientAvpData);
			           
			      }
			     
			      clientAttributeData.setFixedGroupedAttribute(fixedGroupedAttributeList);
            }
            
            Map<String, AvpRule> optionalGroupedAttribute=serverData.getOptionalGroupedAttribute();
            
            if(optionalGroupedAttribute != null){
            	List<AVPRuleData> optionalGroupedAttributeList=new ArrayList<AVPRuleData>();
            	Iterator<String> itr=optionalGroupedAttribute.keySet().iterator();
			     
			      while(itr.hasNext()){
			    	   
			           AvpRule serverAVPData=optionalGroupedAttribute.get(itr.next());
			           AVPRuleData clientAvpData=converToClientAvpData(serverAVPData);
			           optionalGroupedAttributeList.add(clientAvpData);
			           
			      }
			     
			      clientAttributeData.setOptionalGroupedAttribute(optionalGroupedAttributeList);
            }
            
            Map<String, AvpRule> requiredGroupedAttribute=serverData.getRequiredGroupedAttribute();
            
            if(requiredGroupedAttribute != null){
            	List<AVPRuleData> requiredGroupedAttributeList=new ArrayList<AVPRuleData>();
            	Iterator<String> itr=requiredGroupedAttribute.keySet().iterator();
			     
			      while(itr.hasNext()){
			    	   
			           AvpRule serverAVPData=requiredGroupedAttribute.get(itr.next());
			           AVPRuleData clientAvpData=converToClientAvpData(serverAVPData);
			           requiredGroupedAttributeList.add(clientAvpData);
			           
			      }
			     
			      clientAttributeData.setRequiredGroupedAttribute(requiredGroupedAttributeList);
            	
            }
            
            
            
            
		}catch(Exception exp){
			exp.printStackTrace();
		}
		
		return clientAttributeData;
	}



	private AVPRuleData converToClientAvpData(AvpRule serverAVPData) {
		AVPRuleData clientAvpData = new AVPRuleData();
		
		clientAvpData.setName(serverAVPData.getName());
		clientAvpData.setMaximum(serverAVPData.getMaximum());
		clientAvpData.setMinimum(serverAVPData.getMaximum());
		clientAvpData.setAttrId(serverAVPData.getAttrId());
		clientAvpData.setVendorId(serverAVPData.getVendorId());
		return clientAvpData;
		
	}





	private DataTypeData convertToClientData(com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData typeData) {
		DataTypeData dataTypeData = null;
		if(typeData!=null){
			dataTypeData = new DataTypeData();
			dataTypeData.setAlias(typeData.getAlias());
			dataTypeData.setDataTypeId(typeData.getDataTypeId());
			dataTypeData.setDescription(typeData.getDescription());
			dataTypeData.setErrorMessage(typeData.getErrorMessage());
			dataTypeData.setJavaRegex(typeData.getJavaRegex());
			dataTypeData.setJavaScriptRegex(typeData.getJavaScriptRegex());
			dataTypeData.setName(typeData.getName());
			dataTypeData.setSystemGenerated(typeData.getSystemGenerated());
		}
		return dataTypeData;
	}

	public DictionaryData getDictionaryData(String strDictionaryId)
	throws IllegalArgumentException {

		DictionaryData  clientData = null;	

		DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
		DiameterdicData serverData=new DiameterdicData(); 
		serverData.setDictionaryId(strDictionaryId);
		try {
			
			serverData=dictionaryBLManager.getDictionaryById(serverData);
			dictionaryData=serverData;

			clientData=convertToClientDictionaryData(serverData);
		} catch (DataManagerException e) {
			e.printStackTrace();
		}


		return clientData;
	}



	private DictionaryData convertToClientDictionaryData(DiameterdicData serverData) {
		
		DictionaryData clientData=new DictionaryData();
		clientData.setApplicationId(serverData.getApplicationId());
		clientData.setApplicationName(serverData.getApplicationName());
        clientData.setCommonStatusId(serverData.getCommonStatusId());
        clientData.setCreateDate(serverData.getCreateDate());
        clientData.setCreatedByStaffId(serverData.getCreatedByStaffId());
        clientData.setDescription(serverData.getDescription());
        clientData.setDictionaryId(serverData.getDictionaryId());
        clientData.setDictionaryNumber(serverData.getDictionaryNumber());
        clientData.setEditable(serverData.getEditable());
        clientData.setLastModifiedByStaffId(serverData.getLastModifiedByStaffId());
        clientData.setLastModifiedDate(serverData.getLastModifiedDate());
        clientData.setModalNumber(serverData.getModalNumber());
        clientData.setStatusChangedDate(serverData.getStatusChangedDate());
        clientData.setSystemGenerated(serverData.getSystemGenerated());
        clientData.setVendorId(serverData.getVendorId());
        clientData.setVendorName(serverData.getVendorName());
        
		
		return clientData;
	}



	public ArrayList<String> getStaticStringTree() {

		return null;
	}



	

	/*public DictionaryData saveDictionary(String xmlString,String strDictionaryId) {
		DictionaryServiceManager dictionaryServiceManager = new  DictionaryServiceManager();
		DictionaryData  clientDictionaryData = null;
		List<AttributeData> attributeList = new ArrayList<AttributeData>();
		try{

			SystemLoginForm systemLoginForm =(SystemLoginForm)this.getThreadLocalRequest().getSession().getAttribute("radiusLoginForm");

			dictionaryServiceManager.saveDictionary(xmlString,strDictionaryId,systemLoginForm.getUserId());
			clientDictionaryData = getDictionaryData(strDictionaryId);    
			attributeList=getAttributeList(strDictionaryId); 
			clientDictionaryData.setAttributeList(attributeList);



		}catch(Exception exception){
			exception.printStackTrace();
		}
		return clientDictionaryData;		

	}*/

	public List<DataTypeData> getDataTypeList(){

		Hashtable htab_strName_strDataTypeId = null;
		List<DataTypeData> datatypeList = null;
		DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
		try {

			htab_strName_strDataTypeId = dictionaryBLManager.getDatatype();
			if(htab_strName_strDataTypeId != null && !htab_strName_strDataTypeId.isEmpty()){
				datatypeList = convertToClientData(htab_strName_strDataTypeId);
			}

		}catch (DataManagerException e) {
			e.printStackTrace();
		}


		return datatypeList;

	}



	private List<DataTypeData> convertToClientData(Hashtable htab_strName_strDataTypeId) {

		Iterator<String> iterator = htab_strName_strDataTypeId.keySet().iterator();

		List<DataTypeData> dataTypeList = new ArrayList<DataTypeData>();
		while(iterator.hasNext()){

			DataTypeData dataTypeData = new DataTypeData();
			String dataTypeName = (String)iterator.next();
			String id = (String)htab_strName_strDataTypeId.get(dataTypeName);
			dataTypeData.setName(dataTypeName);
			dataTypeData.setDataTypeId(id);
			dataTypeList.add(dataTypeData);

		}

		return dataTypeList;

	}



	public DictionaryData updateDictionary(DictionaryData dictionaryData) {

		DiameterdicData serverData=null; 
		
		serverData = convertToDictionaryServerData(dictionaryData);
		DiameterDictionaryBLManager dictionaryBLManager = new  DiameterDictionaryBLManager();
		DictionaryData  clientDictionaryData = null;
		List<AttributeData> attributeList = new ArrayList<AttributeData>();
		String strDictionaryId = String.valueOf(dictionaryData.getDictionaryId());
		try{
			dictionaryBLManager.updateDictionary(serverData);
			clientDictionaryData = getDictionaryData(strDictionaryId);    
			attributeList=getAttributeList(strDictionaryId); 
			clientDictionaryData.setAttributeList(attributeList);


		}catch(DataManagerException exp){
			exp.printStackTrace();
		}

		return clientDictionaryData;
	}



	private DiameterdicData convertToDictionaryServerData(DictionaryData data) {

		DiameterdicData serverData=new DiameterdicData();
		Set<DiameterdicParamDetailData> lstDictionaryDetails = new LinkedHashSet();
		
		serverData.setApplicationId(data.getApplicationId());
		serverData.setApplicationName(data.getApplicationName());
		serverData.setCommonStatusId(data.getCommonStatusId());
		serverData.setCreateDate(data.getCreateDate());
        serverData.setCreatedByStaffId(data.getCreatedByStaffId());
        serverData.setDescription(data.getDescription());
        serverData.setDictionaryId(data.getDictionaryId());
        serverData.setDictionaryNumber(data.getDictionaryNumber());
        serverData.setEditable(data.getEditable());
        serverData.setLastModifiedByStaffId(data.getLastModifiedByStaffId());
        serverData.setLastModifiedDate(data.getLastModifiedDate());
        serverData.setModalNumber(data.getModalNumber());
        serverData.setStatusChangedDate(data.getStatusChangedDate());
        serverData.setSystemGenerated(data.getSystemGenerated());
        serverData.setVendorId(data.getVendorId());
        serverData.setVendorName(data.getVendorName());
        
        
        /*
         * set diameterdic parameter data..
         * 
         */
        List<AttributeData> attributeList=data.getAttributeList();
        
        for (Iterator iterator = attributeList.iterator(); iterator.hasNext();) {
        	
        	
			AttributeData attributeData = (AttributeData) iterator.next();
			DiameterdicParamDetailData serverAttribute=converToAttributeServerData(attributeData);
			lstDictionaryDetails.add(serverAttribute);
			
		}
		serverData.setDiameterdicParamDetailSet(lstDictionaryDetails);
		return serverData;
	}



	private DiameterdicParamDetailData converToAttributeServerData(AttributeData attributeData) {
		
		DiameterdicParamDetailData serverData=new DiameterdicParamDetailData();
		try{
			serverData.setAlias(attributeData.getAlias());
			serverData.setDataTypeId(attributeData.getDataTypeId());
			serverData.setDescription(attributeData.getDescription());
			serverData.setDiameterdciParamDetailId(attributeData.getDiameterdciParamDetailId());
			serverData.setDictionaryId(attributeData.getDictionaryId());
			serverData.setDictionaryNumber(attributeData.getDictionaryNumber());
			serverData.setEncryption(attributeData.getEncryption());
            serverData.setMandatory(attributeData.getMandatory());
            serverData.setName(attributeData.getName());
            serverData.setNetworkFilterSupport(attributeData.getNetworkFilterSupport());
            serverData.setStrProtected(attributeData.getStrProtected());
            serverData.setVendorId(attributeData.getVendorId());
            serverData.setVendorParameterId(attributeData.getVendorParameterId());
            serverData.setVendorParameterOveridden(attributeData.getVendorParameterOveridden());
            if("DTT16".equalsIgnoreCase(attributeData.getDataTypeId())){
            	serverData.setPredefinedValues(attributeData.getPredefinedValues());
            }
       
            if("DTT12".equalsIgnoreCase(attributeData.getDataTypeId())){
            	 Map<String,AvpRule> fixedGroupedAttribute= new HashMap<String, AvpRule>();
            	 Map<String,AvpRule> requiredGroupedAttribute= new HashMap<String, AvpRule>();
            	 Map<String,AvpRule> optionalGroupedAttribute= new HashMap<String, AvpRule>();
            	 
            	 Collection<AVPRuleData> fixedAttributeList=attributeData.getFixedGroupedAttribute();
            	 Collection<AVPRuleData> requiredAttributeList=attributeData.getRequiredGroupedAttribute();
            	 Collection<AVPRuleData> optionalAttributeList=attributeData.getOptionalGroupedAttribute();
            	 
            	 if(fixedAttributeList != null){
            		 
            		 for(Iterator iterator = fixedAttributeList.iterator(); iterator.hasNext();){
				  		 
            			 AVPRuleData ruleData = (AVPRuleData) iterator.next();
				  		 AvpRule data=convertToServerAVPRule(ruleData);
				  		 fixedGroupedAttribute.put(data.getName(),data);
				  		 
						
					}
            		serverData.setFixedGroupedAttribute(fixedGroupedAttribute); 
            	 }
            	 
            	 if(requiredAttributeList != null){
            		 
                    for(Iterator iterator = requiredAttributeList.iterator(); iterator.hasNext();){
				  		 
            			 AVPRuleData ruleData = (AVPRuleData) iterator.next();
				  		 AvpRule data=convertToServerAVPRule(ruleData);
				  		 requiredGroupedAttribute.put(data.getName(),data);
						
					}
            		serverData.setRequiredGroupedAttribute(requiredGroupedAttribute);
            		 
            	 }
            	 
            	 if(optionalAttributeList != null){
                              
                    for(Iterator iterator = optionalAttributeList.iterator(); iterator.hasNext();){
				  		 
            			 AVPRuleData ruleData = (AVPRuleData) iterator.next();
				  		 AvpRule data=convertToServerAVPRule(ruleData);
				  		 optionalGroupedAttribute.put(data.getName(),data);
						
					}
            		serverData.setOptionalGroupedAttribute(optionalGroupedAttribute);
            		 
            	 }
            	 
            	 
            }
            
            
            
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return serverData;
	}



	private AvpRule convertToServerAVPRule(AVPRuleData ruleData) {
		
		AvpRule data = new AvpRule();
		data.setName(ruleData.getName());
		data.setMaximum(ruleData.getMaximum());
		data.setMinimum(ruleData.getMinimum());
		data.setAttrId(ruleData.getAttrId());
		data.setVendorId(ruleData.getVendorId());
		return data;
	}



	


	



	private com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData convertToServerData(DataTypeData clientDataTypeData) {

		com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData dataTypeData = null;
		if(clientDataTypeData!=null){
			dataTypeData = new com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData();
			dataTypeData.setAlias(clientDataTypeData.getAlias());
			dataTypeData.setDataTypeId(clientDataTypeData.getDataTypeId());
			dataTypeData.setDescription(clientDataTypeData.getDescription());
			dataTypeData.setErrorMessage(clientDataTypeData.getErrorMessage());
			dataTypeData.setJavaRegex(clientDataTypeData.getJavaRegex());
			dataTypeData.setJavaScriptRegex(clientDataTypeData.getJavaScriptRegex());
			dataTypeData.setName(clientDataTypeData.getName());
			dataTypeData.setSystemGenerated(clientDataTypeData.getSystemGenerated());
		}
		return dataTypeData;

	}
	
	
	

	public String getDictionaryAsXML(String strdictionaryId){
		
		 /* get dictionary as xml format*/
		 
		String xmlString=new String();
		try{
			DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
			//com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData dictionaryData= dictionaryBLManager.getDictionaryById(dictionaryId);
			DiameterdicData diameterdicData = new DiameterdicData();
			diameterdicData.setDictionaryId(strdictionaryId);
			diameterdicData=dictionaryBLManager.getDictionaryById(diameterdicData);
			
			xmlString=dictionaryBLManager.convertAsXml(diameterdicData);

		}catch (Exception e) {

		}
		return xmlString;
	}
	
	
	public List<DictionaryData> getDictionaryDataList(){

		List<DictionaryData> dictionaryDataList = new ArrayList<DictionaryData>();

		try{
			DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
			List<DiameterdicData> dictionaryList= dictionaryBLManager.getAllDictionaryList();
			dictionaryDataList=convertToClientDictionaryDataList(dictionaryList,dictionaryBLManager);

		}catch(Exception e){
                e.printStackTrace();
		}

		return dictionaryDataList;


	}



private List<DictionaryData> convertToClientDictionaryDataList(List<DiameterdicData> dictionaryList, DiameterDictionaryBLManager dictionaryBLManager) {
	
	List<DictionaryData> dictionaryDataList = new ArrayList<DictionaryData>();
	
	for(int i=0;i<dictionaryList.size();i++){
		DiameterdicData serverData=dictionaryList.get(i);
		try {
			
			List<DiameterdicParamDetailData> serverAttrList=dictionaryBLManager.getDictionaryParameterDetailListById(serverData.getDictionaryId());
			List<AttributeData> attributeList = new ArrayList<AttributeData>();
			for(int j=0;j<serverAttrList.size();j++){
				DiameterdicParamDetailData serverAttrData=serverAttrList.get(j);
				//AttributeData clientData=convertToClientAttributeData(serverAttrData);
				AttributeData clientData= new AttributeData();
				
				clientData.setName(serverAttrData.getName());
				clientData.setVendorId(serverAttrData.getVendorId());
				clientData.setVendorParameterId(serverAttrData.getVendorParameterId());
				
				attributeList.add(clientData);
			}
			
			DictionaryData clientServerData=convertToClientDictionaryData(serverData);
			clientServerData.setAttributeList(attributeList);
			dictionaryDataList.add(clientServerData);
			
		} catch (DataManagerException e) {
			e.printStackTrace();
		}
		
	}
	
	return dictionaryDataList;
}





}

