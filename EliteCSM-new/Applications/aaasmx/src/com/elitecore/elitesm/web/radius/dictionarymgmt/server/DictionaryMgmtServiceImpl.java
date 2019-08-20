package com.elitecore.elitesm.web.radius.dictionarymgmt.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.dictionarymgmt.client.DictionaryMgmtService;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.AttributeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DataTypeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DictionaryData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DictionaryMgmtServiceImpl extends RemoteServiceServlet implements DictionaryMgmtService {

	public List<AttributeData> getAttributeList(String strDictionaryId)	throws IllegalArgumentException {
		List<AttributeData> attributeList=new ArrayList<AttributeData>();
		List<DictionaryParameterDetailData> paramList=null;
		RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
		try{
			Long dictionaryId=Long.parseLong(strDictionaryId);
			paramList=dictionaryBLManager.getDictionaryParameterDetailListById(dictionaryId);
			for (Iterator<DictionaryParameterDetailData> iterator = paramList.iterator(); iterator.hasNext();) {
				DictionaryParameterDetailData dictionaryParameterDetailData = (DictionaryParameterDetailData) iterator.next();
				AttributeData clientData=convertToClientData(dictionaryParameterDetailData);
				attributeList.add(clientData);
			}

		}catch(DataManagerException e){
			e.printStackTrace();
		}

		return attributeList;
	}


	private AttributeData convertToClientData(DictionaryParameterDetailData serverData) {
		AttributeData attributeData = new AttributeData();
		try{
			attributeData.setAlias(serverData.getAlias());
			attributeData.setAvPair(serverData.getAvPair());
			attributeData.setDataTypeId(serverData.getDataTypeId());
			attributeData.setDictionaryId(serverData.getDictionaryId());
			attributeData.setDictionaryNumber(serverData.getDictionaryNumber());
			attributeData.setDictionaryParameterDetailId(serverData.getDictionaryParameterDetailId());
			attributeData.setHasTag(serverData.getHasTag());
			attributeData.setIgnoreCase(serverData.getIgnoreCase());
			attributeData.setLengthFormat(serverData.getLengthFormat()!=null ? serverData.getLengthFormat() : LengthFormat.TLV.getName());
			attributeData.setPaddingType(serverData.getPaddingType()!=null ? serverData.getPaddingType() : PaddingType.NONE.getType());
			
			attributeData.setEncryptStandard(serverData.getEncryptStandard());
			
			attributeData.setName(serverData.getName());
			attributeData.setNetworkFilterSupport(serverData.getNetworkFilterSupport());
			attributeData.setOperatorId(serverData.getOperatorId());
			attributeData.setParentDetailId(serverData.getParentDetailId());
			attributeData.setPredefinedValues(serverData.getPredefinedValues());
			attributeData.setRadiusRFCDictionaryParameterId(serverData.getRadiusRFCDictionaryParameterId());
			attributeData.setUsageType(serverData.getUsageType());
			attributeData.setVendorId(serverData.getVendorId());
			attributeData.setVendorParameterId(serverData.getVendorParameterId());
			attributeData.setVendorParameterOveridden(serverData.getVendorParameterOveridden());

			com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData typeData =(com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData) serverData.getDataType();
			attributeData.setDataType(convertToClientData(typeData));

			Set<DictionaryParameterDetailData> childSet = serverData.getNestedParameterDetailSet();
			if(childSet != null && !childSet.isEmpty()){
				Iterator<DictionaryParameterDetailData> iterator = childSet.iterator();
				List<AttributeData> childAttributeList = new ArrayList<AttributeData>();
				while (iterator.hasNext()) {

					DictionaryParameterDetailData childParameterDetailData = (DictionaryParameterDetailData) iterator.next();
					AttributeData childAttribute=convertToClientData(childParameterDetailData);
					childAttributeList.add(childAttribute);
				}
				attributeData.setChildAttributeList(childAttributeList);	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return attributeData;

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

		RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
		com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData serverData= new com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData(); 
		try {

			serverData=dictionaryBLManager.getDictionaryById(strDictionaryId);
			clientData=convertToClientData(serverData);
		} catch (DataManagerException e) {
			e.printStackTrace();
		}


		return clientData;
	}



	public ArrayList<String> getStaticStringTree() {

		return null;
	}



	private DictionaryData convertToClientData(com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData serverData) {

		DictionaryData clientData = new  DictionaryData(); 

		clientData.setCommonStatusId(serverData.getCommonStatusId());
		clientData.setCreateDate(serverData.getCreateDate());

		clientData.setDescription(serverData.getDescription());
		clientData.setDictionaryId(serverData.getDictionaryId());
		clientData.setDictionaryNumber(serverData.getDictionaryNumber());
		clientData.setEditable(serverData.getEditable());
		clientData.setLastModifiedByStaffId(Long.parseLong(serverData.getLastModifiedByStaffId()));
		clientData.setLastModifiedDate(serverData.getLastModifiedDate());
		clientData.setModalNumber(serverData.getModalNumber());
		clientData.setName(serverData.getName());
		/*clientData.setStatusChangedDate(serverData.getStatusChangedDate());
		   clientData.setSystemGenerated(serverData.getSystemGenerated());
		 */clientData.setVendorId(serverData.getVendorId());
		 //SystemLoginForm form = (SystemLoginForm) this.getThreadLocalRequest().getSession().getAttribute("radiusLoginForm");

		 return clientData;
	}

	public DictionaryData saveDictionary(String xmlString,String strDictionaryId) {
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

	}

	public List<DataTypeData> getDataTypeList(){

		Hashtable htab_strName_strDataTypeId = null;
		List<DataTypeData> datatypeList = null;
		RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
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

		com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData serverData = null;
		serverData = convertToServerData(dictionaryData);
		RadiusDictionaryBLManager dictionaryBLManager = new  RadiusDictionaryBLManager();
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



	private com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData convertToServerData(DictionaryData clientData) {


		/*
		 * convert client dictionary data to server dictionary data
		 */
		com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData serverData = new com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData();
		serverData.setCommonStatusId(clientData.getCommonStatusId());

		serverData.setDescription(clientData.getDescription());
		serverData.setDictionaryId(clientData.getDictionaryId());
		serverData.setDictionaryNumber(clientData.getDictionaryNumber());
		serverData.setEditable(clientData.getEditable());
		serverData.setName(clientData.getName());
		serverData.setVendorId(clientData.getVendorId());
		serverData.setModalNumber(clientData.getModalNumber()); 

		SystemLoginForm staffform = (SystemLoginForm) this.getThreadLocalRequest().getSession().getAttribute("radiusLoginForm");
		String strStaffId = staffform.getUserId();
		serverData.setLastModifiedByStaffId(strStaffId);
		Date currentDate = new Date();
		serverData.setLastModifiedDate(new Timestamp(currentDate.getTime()));




		/*
		 * convert client attribute data to server attribute data 
		 */

		List<AttributeData> attributeList = clientData.getAttributeList();
		Set lstDictionaryDetails = new LinkedHashSet();
		for(int i=0;i<attributeList.size();i++){

			AttributeData attributeData = attributeList.get(i);
			DictionaryParameterDetailData dictionaryParameterDetailData=null;
			dictionaryParameterDetailData = convertToServerData(attributeData);
			lstDictionaryDetails.add(dictionaryParameterDetailData);

		}
		if(lstDictionaryDetails != null && !lstDictionaryDetails.isEmpty()){
			serverData.setDictionaryParameterDetail(lstDictionaryDetails);   
		}

		return serverData;
	}



	private DictionaryParameterDetailData convertToServerData(AttributeData attributeData) {

		DictionaryParameterDetailData dictionaryParameterDetailData = new DictionaryParameterDetailData();

		try{
			dictionaryParameterDetailData.setAlias(attributeData.getAlias());
			dictionaryParameterDetailData.setAvPair(attributeData.getAvPair());
			dictionaryParameterDetailData.setDataTypeId(attributeData.getDataTypeId());
			dictionaryParameterDetailData.setDictionaryId(attributeData.getDictionaryId());
			dictionaryParameterDetailData.setDictionaryNumber(attributeData.getDictionaryNumber());
			dictionaryParameterDetailData.setDictionaryParameterDetailId(attributeData.getDictionaryParameterDetailId());
			dictionaryParameterDetailData.setHasTag(attributeData.getHasTag());
			dictionaryParameterDetailData.setIgnoreCase(attributeData.getIgnoreCase());
			dictionaryParameterDetailData.setEncryptStandard(attributeData.getEncryptStandard());
			dictionaryParameterDetailData.setLengthFormat(attributeData.getLengthFormat());
			dictionaryParameterDetailData.setPaddingType(attributeData.getPaddingType());
			dictionaryParameterDetailData.setName(attributeData.getName());
			dictionaryParameterDetailData.setNetworkFilterSupport(attributeData.getNetworkFilterSupport());
			dictionaryParameterDetailData.setOperatorId(attributeData.getOperatorId());
			dictionaryParameterDetailData.setParentDetailId(attributeData.getParentDetailId());
			/*
			 *  if("DTT16".equalsIgnoreCase(attributeData.getDataTypeId())){
            	serverData.setPredefinedValues(attributeData.getPredefinedValues());
                }DTT01 
			 */
			
			if("DTT01".equalsIgnoreCase(attributeData.getDataTypeId())){
				dictionaryParameterDetailData.setPredefinedValues(attributeData.getPredefinedValues());	
			}
			
			dictionaryParameterDetailData.setRadiusRFCDictionaryParameterId(attributeData.getRadiusRFCDictionaryParameterId());
			dictionaryParameterDetailData.setUsageType(attributeData.getUsageType());
			dictionaryParameterDetailData.setVendorId(attributeData.getVendorId());
			dictionaryParameterDetailData.setVendorParameterId(attributeData.getVendorParameterId());
			dictionaryParameterDetailData.setVendorParameterOveridden(attributeData.getVendorParameterOveridden());

			DataTypeData clientDataTypeData = attributeData.getDataType();

			dictionaryParameterDetailData.setDataType(convertToServerData(clientDataTypeData));


			Collection<AttributeData> childSet = attributeData.getChildAttributeList();

			if(childSet != null && !childSet.isEmpty()){
				Iterator<AttributeData> iterator = childSet.iterator();
				List<DictionaryParameterDetailData> nestedDictionaryParameterDetailList = new ArrayList<DictionaryParameterDetailData>();
				while (iterator.hasNext()) {
					AttributeData childParameterDetailData = (AttributeData) iterator.next();
					DictionaryParameterDetailData parameterDetailData = convertToServerData(childParameterDetailData);				
					nestedDictionaryParameterDetailList.add(parameterDetailData);
				}
				dictionaryParameterDetailData.setNestedParameterDetailList(nestedDictionaryParameterDetailList);	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return dictionaryParameterDetailData;

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
		/*
		 * get dictionary as xml format
		 */
		String xmlString=new String();
		try{

			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();

			com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData dictionaryData= dictionaryBLManager.getDictionaryById(strdictionaryId);
			xmlString=dictionaryBLManager.convertAsXml(dictionaryData);

		}catch (Exception e) {

		}
		return xmlString;
	}





}

