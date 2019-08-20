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

import com.elitecore.elitesm.blmanager.diameter.dictionary.DiameterDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class SearchDiameterAttributesServlet
 */
public class SearchDiameterAttributesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="SearchDiameterAttributesServlet";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchDiameterAttributesServlet() {
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
		List<DiameterdicParamDetailData> dictionaryParamList = null;
		List<String> attributeIdNameList = null;
		String attrIdNameStr = "";
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
 		try {
 			DiameterDictionaryBLManager diameterDictionaryBLManager = new DiameterDictionaryBLManager();
 			List<DiameterdicData> dictionaryList = diameterDictionaryBLManager.getOnlyDiameterDictionaryDataList();
 			Map<Long,DiameterdicData> dictionaryMap = new HashMap<Long,DiameterdicData>(); 
 			if(dictionaryList!=null && !dictionaryList.isEmpty()){
 				for (Iterator iterator = dictionaryList.iterator(); iterator.hasNext();) {
					DiameterdicData dictionaryData = (DiameterdicData) iterator.next();
					dictionaryMap.put(dictionaryData.getVendorId(), dictionaryData);	
				}
 			}
 			
			dictionaryParamList = (ArrayList<DiameterdicParamDetailData>)diameterDictionaryBLManager.getOnlyDiameterDictionaryParameterList(searchNameOrAttributeId);
			attributeIdNameList = new ArrayList<String>();
			
			if(dictionaryParamList != null) {
				for(DiameterdicParamDetailData diameterDictionaryParamDetail : dictionaryParamList) {
					attrIdNameStr = diameterDictionaryParamDetail.getAttributeId() +","+diameterDictionaryBLManager.getAttributeFullName(diameterDictionaryParamDetail,dictionaryMap)+"[" + diameterDictionaryParamDetail.getAttributeId() + "]#";
					
					if(!diameterDictionaryParamDetail.getDataTypeId().equalsIgnoreCase("DTT11")){
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
	}

}
