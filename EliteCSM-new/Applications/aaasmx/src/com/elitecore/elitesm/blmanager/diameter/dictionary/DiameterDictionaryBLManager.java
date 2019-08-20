package com.elitecore.elitesm.blmanager.diameter.dictionary;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.HibernateException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.elitecore.commons.base.Strings;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.diameter.dictionary.DictionaryDataManager;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.exception.AttributeNotFoundException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData;
import com.elitecore.elitesm.datamanager.radius.dictionary.exception.DuplicateDictionaryConstraintExcpetion;
import com.elitecore.elitesm.util.logger.Logger;
public class DiameterDictionaryBLManager extends BaseBLManager {


	private static final String MODULE = "DIAMETER DICTIONARY";
	public DictionaryDataManager getDictionaryDataManager( IDataManagerSession session ) {
		DictionaryDataManager dictionaryDataManager = (DictionaryDataManager) DataManagerFactory.getInstance().getDataManager(DictionaryDataManager.class, session);
		return dictionaryDataManager;
	}


	public Hashtable getDatatype() throws DataManagerException {
		IDataManagerSession session = null;
		List lstDataType;
		Hashtable htab_strName_strDataTypeId = new Hashtable();
		try {

			session = DataManagerSessionFactory.getInstance().getDataManagerSession();

			DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session); 

			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			DataTypeData iDatatypeData = null;
			lstDataType = dictionaryDataManager.getDatatype();
			if (lstDataType != null) {
				for ( int i = 0; i < lstDataType.size(); i++ ) {
					iDatatypeData = (DataTypeData) lstDataType.get(i);
					htab_strName_strDataTypeId.put(iDatatypeData.getName(), iDatatypeData.getDataTypeId());
				}
			}
			return htab_strName_strDataTypeId;
		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}finally{
			closeSession(session);
		}
	}


	public void create(DiameterdicData diameterdicData) throws  DataManagerException,DataValidationException,ConstraintViolationException,AttributeNotFoundException {

		IDataManagerSession session = null;
		session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session); 
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{

			session.beginTransaction();
			DiameterdicData data= new DiameterdicData();
			data.setVendorId(diameterdicData.getVendorId());
			data.setApplicationId(diameterdicData.getApplicationId());
			List<DiameterdicData> dictionaryList=dictionaryDataManager.getAllList(data);
			if (dictionaryList == null || dictionaryList.size() <= 0) {
				dictionaryDataManager.create(diameterdicData);
				session.commit();
			}else{
				throw new DuplicateDictionaryConstraintExcpetion("Dictionary " + diameterdicData.getVendorName() + " with Vendor Number " + diameterdicData.getVendorId() + " and Application Id"+diameterdicData.getApplicationId()+" already exists");
			}


		}catch (ConstraintViolationException hExp){
			session.rollback();
    		throw new DataManagerException(hExp.getMessage(), hExp);
		}catch (HibernateException hExp) {
			session.rollback();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception exp) {
			exp.printStackTrace();
			session.rollback();
			throw new DataManagerException(exp.getMessage(), exp);
		}finally{
			closeSession(session);
		}

	}


	public List getAllDictionaryList() throws DataManagerException {
		IDataManagerSession session = null;
		session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session); 
		List lstDictionaryList=null;
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			DiameterdicData data = new DiameterdicData();
			lstDictionaryList= dictionaryDataManager.getAllList(data);

		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}
		

		return lstDictionaryList;
	}


	public ArrayList<DiameterdicParamDetailData> getDictionaryParameterDetailListById(String dictionaryId) throws DataManagerException {
		
		ArrayList<DiameterdicParamDetailData> arrList=new ArrayList<DiameterdicParamDetailData>();
		List<DiameterdicParamDetailData> list=null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		if(dictionaryId != null) {
			list = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryId);
		}   
		try{
		//     printParameters(list,null);
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				DiameterdicParamDetailData  parameterDetailData = (DiameterdicParamDetailData) iterator.next();
				arrList.add(parameterDetailData);	
	
			}
			
		}catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}finally{
			closeSession(session);
		}
		return arrList;
	}


	public DiameterdicData getDictionaryById(DiameterdicData data) throws DataManagerException {
		List<DiameterdicData> lstDictionaryList=new ArrayList<DiameterdicData>();
		IDataManagerSession session = null;
		session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session); 
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			lstDictionaryList= dictionaryDataManager.getAllList(data);

			if(lstDictionaryList == null)
				throw new DataManagerException("Dictionary Not Found..");
			data=(DiameterdicData)lstDictionaryList.get(0);

		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}

		return data;

	}


	public void updateDictionary(DiameterdicData serverData) throws DataManagerException {
		IDataManagerSession session = null;
		session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session); 
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			dictionaryDataManager.updateDictionary(serverData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}
	}


	public void updateStatus(List<String> listSelectedIDs, String commonStatusId) throws DataManagerException,
	DataValidationException,ConstraintViolationException 
	{

		IDataManagerSession session = null;
		session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session); 
		Date currentDate = new Date();

		try {
			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			if (listSelectedIDs != null) {
				updateStatusValidate(listSelectedIDs, commonStatusId);
				session.beginTransaction();
				for ( int i = 0; i < listSelectedIDs.size(); i++ ) {
					if (listSelectedIDs.get(i)  != null){
						String dictionaryId = listSelectedIDs.get(i);
						dictionaryDataManager.updateStatus(dictionaryId, commonStatusId, new Timestamp(currentDate.getTime()));

					}
				}
				session.commit();
			} else {
				session.rollback();
				throw new DataManagerException("Data Manager implementation not found for ");
			}
		}
		catch (DataManagerException e) {
			session.rollback();
			throw new DataManagerException("Action failed : " + e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public void updateStatusValidate( List lstdictionaryId ,String commonStatusId ) throws DataValidationException {

		if (EliteGenericValidator.isBlankOrNull(commonStatusId)) { throw (new NullValueException("Invalid Dictionary Status", (MODULE + "." + "status").toLowerCase())); }

	}


	public void delete(List<String> listSelectedIDs)throws DataManagerException, ConstraintViolationException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			session.beginTransaction();
			if (listSelectedIDs != null) {
				for ( int i = 0; i < listSelectedIDs.size(); i++ ) {
					if (listSelectedIDs.get(i) != null) {
						String dictionaryId = listSelectedIDs.get(i);
						/*List radiusPolicyDictionaryMapDataList = dictionaryDataManager.getRadiusPolicyDictionaryMapDataByDictionaryId(dictionaryId);

                                for ( int j = 0; j < radiusPolicyDictionaryMapDataList.size(); j++ ) {
                                    RadiusPolicyDictionaryMapData radiusPolicyDictionaryMapData = (RadiusPolicyDictionaryMapData) radiusPolicyDictionaryMapDataList.get(j);
                                    dictionaryDataManager.deleteRadiusPolicyDictionaryMapDataByDictionaryId(radiusPolicyDictionaryMapData);
                                }*/

						dictionaryDataManager.delete(dictionaryId);
					}
				}
			} else {
				throw new DataManagerException("Empty Dictionary to be deleted");
			}
			session.commit();
		} catch (DataManagerException exp) {
			exp.printStackTrace();
			Logger.logTrace(MODULE, "Error While deleting Dictionary"+ exp.getMessage());
			session.rollback();
			//Logger.logTrace(MODULE, "Error While deleting Dictionary" + exp.getMessage());
			throw new DataManagerException("Delete Dictionary Action failed : " + exp.getMessage());
		} catch (Exception e) {
			session.rollback();
			Logger.logTrace(MODULE, "Error While deleting Dictionary" + e.getMessage());
			throw new DataManagerException("Delete Dictionary Action failed : " + e.getMessage());
		} finally {
			closeSession(session);
		}
	}


	public List getAllDictionaryById(List<String> listSelectedIDs) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
 
	   List<DiameterdicData> lstDictionaryData= new ArrayList<DiameterdicData>();
       try{
		
		Iterator<String> itr = listSelectedIDs.iterator();
		DiameterdicData diameterDicData;
		while (itr.hasNext()) {
			diameterDicData= new DiameterdicData();
			String dictionaryId =  itr.next();
			if (Strings.isNullOrEmpty(dictionaryId) == false) {
				diameterDicData.setDictionaryId(dictionaryId);
				diameterDicData=(DiameterdicData)dictionaryDataManager.getAllList(diameterDicData).get(0);
				lstDictionaryData.add(diameterDicData);
			}
		}
       } catch (DataManagerException exp) {
			exp.printStackTrace();
			Logger.logTrace(MODULE, "Error While fetching All Dictionaries"+ exp.getMessage());
			session.rollback();
			throw new DataManagerException("List Dictionary Action failed : " + exp.getMessage());
		} catch (Exception e) {
			session.rollback();
			Logger.logTrace(MODULE, "Error While fetching All Dictionaries"+ e.getMessage());
			throw new DataManagerException("List Dictionary Action failed : " + e.getMessage());
		} finally {
			closeSession(session);
		}


		return lstDictionaryData;
	}


	public String convertAsXml(DiameterdicData diameterdicData) throws DataManagerException {
        
		String xmlString = null;
		ArrayList<DiameterdicParamDetailData> diameterdicParamDetailList=getDictionaryParameterDetailListById(diameterdicData.getDictionaryId());
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			
			/*
			 * NamedNodeMap childAttribute = datasourceNode.getAttributes();
			Attr idAttr = document.createAttribute(DATASOURCE_ID);
			idAttr.setValue(String.valueOf(getDataSourceId()));
			childAttribute.setNamedItem(idAttr);
			 */

			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			//root elements attribute-list
			Document doc = docBuilder.newDocument();

			Element attributeList = doc.createElement("attribute-list");
			NamedNodeMap attributeListChildAttribute = attributeList.getAttributes();
			
			addAttrToElement("vendorid",diameterdicData.getVendorId().toString(),attributeListChildAttribute,doc);
			addAttrToElement("applicationid",diameterdicData.getApplicationId().toString(),attributeListChildAttribute,doc);
			addAttrToElement("application-name",diameterdicData.getApplicationName(),attributeListChildAttribute,doc);
			addAttrToElement("vendor-name",diameterdicData.getVendorName(),attributeListChildAttribute,doc);
			
			

			/*
			 * generate XML for Attributes
			 */

			for (Iterator iterator = diameterdicParamDetailList.iterator(); iterator.hasNext();)
			{
				DiameterdicParamDetailData diameterdicParamDetailData = (DiameterdicParamDetailData) iterator.next();
				Element attribute = doc.createElement("attribute");
                NamedNodeMap attrChildAttribute=attribute.getAttributes();
                
				/*id-required*/
				//attribute.setAttribute("id",diameterdicParamDetailData.getVendorParameterId().toString());
				addAttrToElement("id",diameterdicParamDetailData.getVendorParameterId().toString(),attrChildAttribute,doc);

				/*name-required*/
				//attribute.setAttribute("name",diameterdicParamDetailData.getName());
				addAttrToElement("name",diameterdicParamDetailData.getName(),attrChildAttribute,doc);
				/*Description-optional*/

				if(diameterdicParamDetailData.getDescription() != null){
					//attribute.setAttribute("description",diameterdicData.getDescription());
					addAttrToElement("description",diameterdicData.getDescription(),attrChildAttribute,doc);
				}
				/*Mandatory - optional*/

				if(diameterdicParamDetailData.getMandatory() != null){
					//attribute.setAttribute("mandatory",diameterdicParamDetailData.getMandatory());
					addAttrToElement("mandatory",diameterdicParamDetailData.getMandatory(),attrChildAttribute,doc);
				}
				/*Protected- optional*/

				if(diameterdicParamDetailData.getStrProtected() != null){
					//attribute.setAttribute("protected",diameterdicParamDetailData.getStrProtected());
					addAttrToElement("protected",diameterdicParamDetailData.getStrProtected(),attrChildAttribute,doc);
				}
				/*Encryption- optional*/

				if(diameterdicParamDetailData.getEncryption() != null){
					//attribute.setAttribute("encryption",diameterdicParamDetailData.getEncryption());
					addAttrToElement("encryption",diameterdicParamDetailData.getEncryption(),attrChildAttribute,doc);
				}

				/* type-required*/

				//attribute.setAttribute("type",diameterdicParamDetailData.getDataType().getName());
				addAttrToElement("type",diameterdicParamDetailData.getDataType().getName(),attrChildAttribute,doc);
				/* data type is grouped*/

				if("DTT12".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){

					Map<String, AvpRule> fixedGroupedAttribute=diameterdicParamDetailData.getFixedGroupedAttribute();
					Map<String, AvpRule> requiredGroupedAttribute=diameterdicParamDetailData.getRequiredGroupedAttribute();
					Map<String, AvpRule> optionalGroupedAttribute=diameterdicParamDetailData.getOptionalGroupedAttribute();

					/* create Grouped Element*/
					Element grouped = doc.createElement("grouped");

					/* set Fixed Attriutes*/

					if(fixedGroupedAttribute != null && !fixedGroupedAttribute.isEmpty()){

						Element fixed = doc.createElement("fixed");
						createAttributeRuleElements(fixedGroupedAttribute,fixed,doc);
						grouped.appendChild(fixed);   

					}
					/* set Required Attriutes*/  
					if(requiredGroupedAttribute != null && !requiredGroupedAttribute.isEmpty()){
						Element required=doc.createElement("required");
						createAttributeRuleElements(requiredGroupedAttribute,required,doc);
						grouped.appendChild(required);

					}
					/* set Optional Attriutes*/
					if(optionalGroupedAttribute != null && !optionalGroupedAttribute.isEmpty()){

						Element optional=doc.createElement("optional");
						createAttributeRuleElements(optionalGroupedAttribute,optional,doc);
						grouped.appendChild(optional);
					}

					attribute.appendChild(grouped);

				}
				/* data type is Enumerated*/
				else if("DTT16".equalsIgnoreCase(diameterdicParamDetailData.getDataTypeId())){

					String predefinedValues=diameterdicParamDetailData.getPredefinedValues();
					Element supportedValues=doc.createElement("supported-values");
					createSupportedValueElements(predefinedValues,supportedValues,doc);
					attribute.appendChild(supportedValues);
					
				}
				attributeList.appendChild(attribute);
			}
			doc.appendChild(attributeList);
			/*doc build complete*/
			xmlString = format(doc);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}catch(IOException iExp){
			iExp.printStackTrace();
		}catch(Exception iExp){
			iExp.printStackTrace();
		}
		return xmlString;


	}


	


	private void addAttrToElement(String attrName,String attrValue,NamedNodeMap attributeListChildAttribute, Document doc) {
		Attr attr = doc.createAttribute(attrName);
		attr.setValue(attrValue);
		attributeListChildAttribute.setNamedItem(attr);
	}


	private void createSupportedValueElements(String predefinedValues,Element el, Document doc) {
		String[] supportedValues = predefinedValues.split(",");
		if(supportedValues != null && supportedValues.length>0){
			for (int i = 0; i < supportedValues.length; i++) {

				String[] supportedValue=supportedValues[i].split(":");
				Element value=doc.createElement("value");
				NamedNodeMap valueChildAttribute=value.getAttributes();
				addAttrToElement("id",supportedValue[1], valueChildAttribute, doc);
				addAttrToElement("name",supportedValue[0], valueChildAttribute, doc);
				el.appendChild(value);  

			}

		}

	}


	private void createAttributeRuleElements(Map<String, AvpRule> groupedAttribute,Element el, Document doc) {
		Iterator<String> itr=groupedAttribute.keySet().iterator();
		
		while(itr.hasNext()){
			
			AvpRule serverAVPData=groupedAttribute.get(itr.next());
			
			Element attributeRule = doc.createElement("attributerule");
			NamedNodeMap ruleChildAttribute=attributeRule.getAttributes();
			
			addAttrToElement("maximum", serverAVPData.getMaximum().trim(), ruleChildAttribute, doc);
			addAttrToElement("minimum", serverAVPData.getMinimum().trim(), ruleChildAttribute, doc);
			addAttrToElement("name", serverAVPData.getName().trim(), ruleChildAttribute, doc);
			
			if(serverAVPData.getVendorId() == -1){
				addAttrToElement("vendor-id", " * ", ruleChildAttribute, doc);
			}else{
				addAttrToElement("vendor-id", String.valueOf(serverAVPData.getVendorId()).trim(), ruleChildAttribute, doc);
			}
			if(serverAVPData.getAttrId() == -1){
				addAttrToElement("id", " * ", ruleChildAttribute, doc);
			}else{
				addAttrToElement("id", String.valueOf(serverAVPData.getAttrId()).trim(), ruleChildAttribute, doc);	
			}
			
            el.appendChild(attributeRule);			

		}
		
		
	}
	
	public List<DiameterdicData> getOnlyDiameterDictionaryDataList() throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        List<DiameterdicData> lstDictionaryDataList = null;
        
		try {

			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			lstDictionaryDataList = dictionaryDataManager.getOnlyDiameterDictionaryDataList();
			
		} catch (DataManagerException dataManagerException) {
			dataManagerException.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("List Dictionary Action failed : " + dataManagerException.getMessage());
		} catch ( Exception exception ){
			exception.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("List Dictionary Action failed : " + exception.getMessage());
		} finally {
			closeSession(session);
		}
        return lstDictionaryDataList;
    }
	
	public List<DiameterdicParamDetailData> getOnlyDiameterDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        List<DiameterdicParamDetailData> dictionaryParameterList = null;
         
        try{
			
        	if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			dictionaryParameterList = dictionaryDataManager.getOnlyDiameterDictionaryParameterList(searchNameOrAttributeId);
        } catch (DataManagerException dataManagerException) {
			dataManagerException.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("List Dictionary Action failed : " + dataManagerException.getMessage());
		} catch ( Exception exception ){
			exception.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("List Dictionary Action failed : " + exception.getMessage());
		} finally {
			closeSession(session);
		}
        return dictionaryParameterList;
    }
	
	public String getAttributeFullName(DiameterdicParamDetailData dictionaryParameterDetailData,Map<Long,DiameterdicData> dictionaryMap) throws DataManagerException{
		String attributeFullName = null;
		DiameterdicData dictionaryData = dictionaryMap.get(dictionaryParameterDetailData.getVendorId());
		if(dictionaryData!=null){
			attributeFullName = dictionaryData.getVendorName() + ":" + dictionaryParameterDetailData.getName();
		}			
		return attributeFullName;
	}
}
