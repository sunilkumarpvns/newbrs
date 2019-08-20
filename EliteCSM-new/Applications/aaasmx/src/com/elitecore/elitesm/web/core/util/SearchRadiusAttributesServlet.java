package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class SearchAttributesServlet
 */
public class SearchRadiusAttributesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="SearchRadiusAttributesServlet";
  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchRadiusAttributesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String searchNameOrAttributeId = request.getParameter("searchNameOrAttributeId");
		String[] searchNameArray = searchNameOrAttributeId.split(",");
		searchNameOrAttributeId = searchNameArray[searchNameArray.length-1].trim();
		List<DictionaryParameterDetailData> dictionaryParamList = null;
		List<String> attributeIdNameList = null;
		String attrIdNameStr = "";
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
 		try {
 			RadiusDictionaryBLManager radDictionaryBLManager = new RadiusDictionaryBLManager();
 			List<DictionaryData> dictionaryList = radDictionaryBLManager.getOnlyDictionaryDataList();
 			Map<Long,DictionaryData> dictionaryMap = new HashMap<Long,DictionaryData>(); 
 			if(dictionaryList!=null && !dictionaryList.isEmpty()){
 				for (Iterator iterator = dictionaryList.iterator(); iterator.hasNext();) {
					DictionaryData dictionaryData = (DictionaryData) iterator.next();
					dictionaryMap.put(dictionaryData.getVendorId(), dictionaryData);	
				}
 			}
 			
			dictionaryParamList = (ArrayList<DictionaryParameterDetailData>)radDictionaryBLManager.getOnlyDictionaryParameterList(searchNameOrAttributeId);
			attributeIdNameList = new ArrayList<String>();
			
			if(dictionaryParamList != null) {
				for(DictionaryParameterDetailData radDictionaryParamDetail : dictionaryParamList) {
					attrIdNameStr = radDictionaryParamDetail.getAttributeId() +","+radDictionaryBLManager.getAttributeFullName(radDictionaryParamDetail,dictionaryMap)+"[" + radDictionaryParamDetail.getAttributeId() + "]#";
					
					if(!radDictionaryParamDetail.getDataTypeId().equalsIgnoreCase("DTT11")){
						attributeIdNameList.add(attrIdNameStr);
					}
					
				}
			}
			if(attributeIdNameList.size()>0){
				String temp = attributeIdNameList.get(attributeIdNameList.size()-1);
				temp = temp.substring(0,temp.length()-1);
				attributeIdNameList.set(attributeIdNameList.size()-1,temp);
			}
 		}catch(DataManagerException e) {
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(Exception e) {
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	out.println(attributeIdNameList);
	out.close();
	}

}
