package com.elitecore.elitesm.web.radius.dictionarymgmt.client;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.AttributeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DataTypeData;
import com.elitecore.elitesm.web.radius.dictionarymgmt.shared.DictionaryData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DictionaryMgmtServiceAsync {

	
	void getDictionaryData(String dictionaryId,AsyncCallback<DictionaryData> callback);
	void getStaticStringTree(AsyncCallback<ArrayList<String>> callback);
	void saveDictionary(String xmlString, String strDictionaryId,AsyncCallback<DictionaryData> asyncCallback);
	void getAttributeList(String dictionaryId,AsyncCallback<List<AttributeData>> callback);
	void getDataTypeList(AsyncCallback<List<DataTypeData>> callback);
	void updateDictionary(DictionaryData dictionaryData,AsyncCallback<DictionaryData> callback);
	void getDictionaryAsXML(String strDictionaryId,AsyncCallback<String> callback);
	

}
