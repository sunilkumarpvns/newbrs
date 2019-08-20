package com.elitecore.elitesm.web.diameter.diameterdicmgmt.client;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.AttributeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DataTypeData;
import com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared.DictionaryData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("diamiterdic")
public interface DiameterdicMgmtService extends RemoteService {
	
	List<AttributeData> getAttributeList(String dictionaryId) throws IllegalArgumentException;
	DictionaryData getDictionaryData(String dictionaryId) throws IllegalArgumentException;
	ArrayList<String> getStaticStringTree();
	//DictionaryData saveDictionary(String xmlString, String strDictionaryId);
	List<DataTypeData> getDataTypeList();
	DictionaryData updateDictionary(DictionaryData dictionaryData) ;
	String getDictionaryAsXML(String strDictionaryId);
	List<DictionaryData> getDictionaryDataList();	
}

