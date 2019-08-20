package com.elitecore.elitesm.web.diameter.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.dictionary.DiameterDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.FormatInvalidException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidFileExtensionException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.ValueOutOfRangeException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.EnvironmentNotSupportedException;
import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.dictionary.forms.ListDiameterdicForm;

public class MiscDiameterdicAction extends BaseWebAction {
    
	private static final String SUCCESS_FORWARD = "success";
    private static final String LIST_FORWARD    = "listDiameterdic";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS = ConfigConstant.CHANGE_DIAMETER_DICTIONARY_STATUS_ACTION;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_DIAMETER_DICTIONARY_ACTION;
	private static final String DOWNLOAD_DIAMETER_DICTIONARY=ConfigConstant.DOWNLOAD_DIAMETER_DICTIONARY;
    
    public ActionForward execute( ActionMapping mapping ,ActionForm form ,HttpServletRequest request , HttpServletResponse response ) throws Exception {
    	if(checkAccess(request, ACTION_ALIAS) || checkAccess(request,ACTION_ALIAS_DELETE)){
    		Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
    		String actionMessage = null;
    		try {
    			DiameterDictionaryBLManager dictionaryBLManager = new DiameterDictionaryBLManager();
    			ListDiameterdicForm lstForm = (ListDiameterdicForm) form;

    			if (lstForm.getAction() != null) {
    				String[] strSelectedIds = request.getParameterValues("select");
    			
    				List<String> listSelectedIDs = new ArrayList<String>();
    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
    				
    				if (strSelectedIds != null) {
    					for(int i=0;i<strSelectedIds.length;i++){
    						listSelectedIDs.add(strSelectedIds[i]);
    					}

    					if (lstForm.getAction().equalsIgnoreCase("download")) {
    						List lstDictionaries = dictionaryBLManager.getAllDictionaryById(listSelectedIDs);
    						downloadAsXml(request, response, (DiameterdicData) lstDictionaries.get(0));
    						doAuditing(staffData, DOWNLOAD_DIAMETER_DICTIONARY);
    					} else if (lstForm.getAction().equalsIgnoreCase("show")) {
    						checkActionPermission(request, ACTION_ALIAS);
    						actionMessage = "dictionary.update.status.success";
    						String actionAlias = ACTION_ALIAS;

    						dictionaryBLManager.updateStatus(listSelectedIDs, DictionaryConstant.SHOW_STATUS_ID);
    						doAuditing(staffData, actionAlias);
    						actionMessage = "dictionary.update.status.failure";
    					} else if (lstForm.getAction().equalsIgnoreCase("hide")) {
    						checkActionPermission(request, ACTION_ALIAS);
    						actionMessage = "dictionary.update.status.success";

    						String actionAlias = ACTION_ALIAS;

    						dictionaryBLManager.updateStatus(listSelectedIDs, DictionaryConstant.HIDE_STATUS_ID);
    						doAuditing(staffData, actionAlias);
    						actionMessage = "dictionary.update.status.failure";
    					} else if (lstForm.getAction().equalsIgnoreCase("delete")) {
    						checkActionPermission(request, ACTION_ALIAS_DELETE);
    						actionMessage = "dictionary.remove.failure";

    						String actionAlias = ACTION_ALIAS_DELETE;

    						dictionaryBLManager.delete(listSelectedIDs);
    						doAuditing(staffData, actionAlias);
    						request.setAttribute("responseUrl","/listDiameterdic.do");
    						ActionMessage message = new ActionMessage("diameter.dictionary.delete.success");
    						ActionMessages messages1 = new ActionMessages();
    						messages1.add("information",message);
    						saveMessages(request,messages1);

    						System.out.println("MiscDictionary Action");
    						//return mapping.findForward(LIST_FORWARD);
    						return mapping.findForward(SUCCESS_FORWARD);
    					}
    				}
    			}
    			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName());   
    			return mapping.findForward(LIST_FORWARD);
    		}catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }
    		catch (AuthorizationException authExp) {

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +authExp.getMessage());
    			Logger.logTrace(MODULE,authExp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
    			request.setAttribute("errorDetails", errorElements);

    		}catch (EnvironmentNotSupportedException envException) {

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +envException.getMessage());
    			Logger.logTrace(MODULE,envException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(envException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch (CommunicationException connException) {


    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +connException.getMessage());
    			Logger.logTrace(MODULE,connException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(connException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch(OperationFailedException opException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +opException.getMessage());
    			Logger.logTrace(MODULE,opException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(opException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch(ConstraintViolationException conException)
    		{

    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +conException.getMessage());
    			Logger.logTrace(MODULE,conException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(conException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch(FormatInvalidException forException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +forException.getMessage());
    			Logger.logTrace(MODULE,forException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(forException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch(NullValueException nullValueException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +nullValueException.getMessage());
    			Logger.logTrace(MODULE,nullValueException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nullValueException);
    			request.setAttribute("errorDetails", errorElements);


    		}
    		catch(InvalidFileExtensionException invException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invException.getMessage());
    			Logger.logTrace(MODULE,invException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch(InvalidValueException invalidValueException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +invalidValueException.getMessage());
    			Logger.logTrace(MODULE,invalidValueException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(invalidValueException);
    			request.setAttribute("errorDetails", errorElements);


    		}
    		catch(ValueOutOfRangeException valueOutOfRangeException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +valueOutOfRangeException.getMessage());
    			Logger.logTrace(MODULE,valueOutOfRangeException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(valueOutOfRangeException);
    			request.setAttribute("errorDetails", errorElements);


    		}
    		catch(DataValidationException validationException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +validationException.getMessage());
    			Logger.logTrace(MODULE,validationException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(validationException);
    			request.setAttribute("errorDetails", errorElements);

    		}
    		catch(DataManagerException mgrException)
    		{
    			Logger.logError(MODULE, "Error during Data Manager operation , reason :" +mgrException.getMessage());
    			Logger.logTrace(MODULE,mgrException);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(mgrException);
    			request.setAttribute("errorDetails", errorElements);

    		}

    		catch (Exception exp){
    			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
    			Logger.logTrace(MODULE,exp);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
    			request.setAttribute("errorDetails", errorElements);

    		}


    		// TODO : Baiju - set error message and then forward to error page.
    		ActionMessages messages = new ActionMessages();
    		ActionMessage message = new ActionMessage(actionMessage);
    		messages.add("information", message);
    		saveErrors(request, messages);
    		return mapping.findForward(FAILURE_FORWARD);
    	}else{
    		Logger.logError(MODULE, "Error during Data Manager operation ");
    		ActionMessage message = new ActionMessage("general.user.restricted");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveErrors(request, messages);

    		return mapping.findForward(INVALID_ACCESS_FORWARD);	
    	}
   }

	private void downloadAsXml(HttpServletRequest request,HttpServletResponse response, DiameterdicData diameterdicData) throws IOException, DataManagerException {
		


    	ServletOutputStream out = null;
    	try{
    		response.setHeader("Content-Disposition", "attachment;filename=\"" + diameterdicData.getVendorName().toLowerCase() + ".xml\"");
    		out = response.getOutputStream();
    		DiameterDictionaryBLManager dictionaryBLManger = new DiameterDictionaryBLManager();
    		String xmlString =dictionaryBLManger.convertAsXml(diameterdicData);
    		Logger.logDebug(MODULE, "Dictionary XML:\n"+xmlString);
    		out.println(xmlString);
    		out.flush();
    		out.close();
    	}finally{
    		if(out!=null){
    			out.close();
    		}
    	}

    
		
	}
    
    
    /**
         * @author KaushikVira
         * @return void
         * @throws DataManagerException
         * @purpose This method is downlado Dictionary in xml format
         */
    /*private void downloadAsXml( HttpServletRequest request ,
            HttpServletResponse response ,
            IDictionaryData dictionaryData ) throws IOException{

    	ServletOutputStream out = null;
    	try{
    		response.setHeader("Content-Disposition", "attachment;filename=\"" + dictionaryData.getName().toLowerCase() + ".xml\"");
    		out = response.getOutputStream();
    		DictionaryBLManager dictionaryBLManger = new DictionaryBLManager();
    		String xmlString =dictionaryBLManger.convertAsXml(dictionaryData);
    		Logger.logDebug(MODULE, "Dictionary XML:\n"+xmlString);
    		out.println(xmlString);
    		out.flush();
    		out.close();
    	}finally{
    		if(out!=null){
    			out.close();
    		}
    	}

    }*/
}
 /*    private void downloadAsXml( HttpServletRequest request ,
                                HttpServletResponse response ,
                                IDictionaryData dictionaryData ) {
        Hashtable htab_strAttributeName_strPredefinedValues = null;
        ServletOutputStream out = null;
        Enumeration enumeration = null;
        Collection colDictionaryParameterDetailData = null;
        Iterator itLstDicParamList = null;
        DictionaryParameterDetailData dictionaryParameterDetailData = null;
        Hashtable htab_strDataTypeId_strName = new Hashtable();
        String strAttributeName = null;
        String strPredefinedValues = null;
        String strFileData = null;
        StringTokenizer stComma = null;
        StringTokenizer stColon = null;
        
        try {
            htab_strAttributeName_strPredefinedValues = new Hashtable();
            response.setHeader("Content-Disposition", "attachment;filename=\"" + dictionaryData.getName().toLowerCase() + ".xml\"");
            out = response.getOutputStream();
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("");
            out.println("<attribute-list vendorid=\"" + dictionaryData.getVendorId() + "\" vendor-name=\"" + dictionaryData.getName() + "\">");
            colDictionaryParameterDetailData = dictionaryData.getDictionaryParameterDetail();
            if (colDictionaryParameterDetailData != null && colDictionaryParameterDetailData.isEmpty() == false) {
                
            	
                List lstDicParamList = Arrays.asList(colDictionaryParameterDetailData.toArray()); 
                //Collections.sort(lstDicParamList);
                
                
                itLstDicParamList = lstDicParamList.iterator();
                while (itLstDicParamList.hasNext()) {
                    dictionaryParameterDetailData = (DictionaryParameterDetailData) itLstDicParamList.next();
                    if(dictionaryParameterDetailData.getParentDetailId() == null){
                   
                    boolean hasChildAttribue = false;    
                    if(dictionaryParameterDetailData.getNestedParameterDetailSet()!= null && !dictionaryParameterDetailData.getNestedParameterDetailSet().isEmpty())
                      hasChildAttribue =true;
                    
                    String endTag = "\">";
                    Collection<DictionaryParameterDetailData> nestedAttributeList = dictionaryParameterDetailData.getNestedParameterDetailSet();
                    if (dictionaryParameterDetailData.getPredefinedValues() != null && dictionaryParameterDetailData.getPredefinedValues().trim().equalsIgnoreCase("") == false) {
                    	String hasTag = dictionaryParameterDetailData.getHasTag();
                    	
                    	if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
                    		strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
					    						+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
					    						+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
					    						+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag())
					    						+ "\">";
                    			
                    	}else{
                    		strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
                        						+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
                        						+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
                        						+ "\">";
    											
                    	}
                        out.println(strFileData);
                        strAttributeName = (String) dictionaryParameterDetailData.getName();
                        strPredefinedValues = (String) dictionaryParameterDetailData.getPredefinedValues();
                        stComma = new StringTokenizer(strPredefinedValues, ",");
                        out.println("                 <supported-values>");
                        String valueId = null;
                        String valueName = null;
                        stComma = new StringTokenizer(strPredefinedValues, ",");
                        while (stComma.hasMoreTokens()) {
                            stColon = new StringTokenizer(stComma.nextToken(), ":");
                            valueName = stColon.nextToken();
                            valueId = stColon.nextToken();
                            strFileData = "                          <value id=\"" + valueId + "\" name=\"" + valueName + "\"/>";
                            out.println(strFileData);
                        }
                        out.println("                 </supported-values>");
                        if(!hasChildAttribue){
                        out.println("        </attribute>");
                        }
                    } else {
                        strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) + "\" name=\"" + (dictionaryParameterDetailData.getName()) + "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()) + "\"/>";
                    	String hasTag = dictionaryParameterDetailData.getHasTag();
                    	if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
                    		strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
												+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
												+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
												+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag());
												//+ "\"/>";
                    		if(hasChildAttribue){
                    			strFileData +=endTag;
                    				
                    		}else{
                    			strFileData +="\"/>";
                    		}
                    	}else{
                    		strFileData ="        <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
                    							+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
                    							+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()); 
                    							//+ "\"/>";
                    		if(hasChildAttribue){
                    			strFileData +=endTag;
                    				
                    		}else{
                    			strFileData +="\"/>";
                    		}
                    	}
 
                        out.println(strFileData);
                    }
                    
                    // for child attribute...
                    if(hasChildAttribue){
                		
                		for (Iterator iterator = nestedAttributeList.iterator(); iterator.hasNext();) {
							
                			DictionaryParameterDetailData dictionaryParameterDetailData2 = (DictionaryParameterDetailData) iterator.next();
                			recursivePrintNestedAttributeAsXml(out, dictionaryParameterDetailData2,dictionaryParameterDetailData.getDictionaryParameterDetailId());
						}
                		out.println("        </attribute>");	
                		
                	}
                    
                    
                    
                   } 
                }
                     
             }
            out.println("</attribute-list>");
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
//    private void recursivePrintNestedAttributeAsXml(ServletOutputStream out,DictionaryParameterDetailData dictionaryParameterDetailData,long parentId) throws IOException {
//    	
//    	long parentDetailId = dictionaryParameterDetailData.getParentDetailId();
//    	if(parentId == parentDetailId){
//    		
//    		String strAttributeName = null;
//            String strPredefinedValues = null;
//            String strFileData = null;
//            StringTokenizer stComma = null;
//            StringTokenizer stColon = null;
//            
//            boolean hasChildAttribue = false;    
//            if(dictionaryParameterDetailData.getNestedParameterDetailSet()!= null && !dictionaryParameterDetailData.getNestedParameterDetailSet().isEmpty())
//              hasChildAttribue =true;
//            
//            String endTag = "\">";
//            Collection<DictionaryParameterDetailData> nestedAttributeList = dictionaryParameterDetailData.getNestedParameterDetailSet();
//            if (dictionaryParameterDetailData.getPredefinedValues() != null && dictionaryParameterDetailData.getPredefinedValues().trim().equalsIgnoreCase("") == false) {
//            	String hasTag = dictionaryParameterDetailData.getHasTag();
//            	
//            	if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
//            		strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
//			    						+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
//			    						+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
//			    						+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag())
//			    						+ "\">";
//            			
//            	}else{
//            		strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
//                						+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
//                						+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
//                						+ "\">";
//										
//            	}
//                out.println(strFileData);
//                strAttributeName = (String) dictionaryParameterDetailData.getName();
//                strPredefinedValues = (String) dictionaryParameterDetailData.getPredefinedValues();
//                stComma = new StringTokenizer(strPredefinedValues, ",");
//                out.println("                                  <supported-values>");
//                String valueId = null;
//                String valueName = null;
//                stComma = new StringTokenizer(strPredefinedValues, ",");
//                while (stComma.hasMoreTokens()) {
//                    stColon = new StringTokenizer(stComma.nextToken(), ":");
//                    valueName = stColon.nextToken();
//                    valueId = stColon.nextToken();
//                    strFileData = "                                                    <value id=\"" + valueId + "\" name=\"" + valueName + "\"/>";
//                    out.println(strFileData);
//                }
//                out.println("                                  </supported-values>");
//                if(!hasChildAttribue){
//                out.println("                </attribute>");
//                }
//            } else {
//                strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) + "\" name=\"" + (dictionaryParameterDetailData.getName()) + "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()) + "\"/>";
//            	String hasTag = dictionaryParameterDetailData.getHasTag();
//            	if(hasTag!=null && hasTag.trim().equalsIgnoreCase("yes") ){
//            		strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
//										+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
//										+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName())
//										+ "\" has-tag=\""+ (dictionaryParameterDetailData.getHasTag());
//										//+ "\"/>";
//            		if(hasChildAttribue){
//            			strFileData +=endTag;
//            				
//            		}else{
//            			strFileData +="\"/>";
//            		}
//            	}else{
//            		strFileData ="                <attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) 
//            							+ "\" name=\"" + (dictionaryParameterDetailData.getName()) 
//            							+ "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()); 
//            							//+ "\"/>";
//            		if(hasChildAttribue){
//            			strFileData +=endTag;
//            				
//            		}else{
//            			strFileData +="\"/>";
//            		}
//            	}
//
//                out.println(strFileData);
//            }
//            
//            // for child attribute...
//            if(hasChildAttribue){
//        		
//        		for (Iterator iterator = nestedAttributeList.iterator(); iterator.hasNext();) {
//					
//        			DictionaryParameterDetailData dictionaryParameterDetailData2 = (DictionaryParameterDetailData) iterator.next();
//        			recursivePrintNestedAttributeAsXml(out, dictionaryParameterDetailData2,dictionaryParameterDetailData.getDictionaryParameterDetailId());
//				}
//        		out.println("                </attribute>");	
//        		
//        	}
//            
//            
//            
//           }   
//          
//    
//    }
        

	

		
	


	private void download( HttpServletRequest request ,
                           HttpServletResponse response ,
                           IDictionaryData dictionaryData ) {
        Hashtable htab_strAttributeName_strPredefinedValues = null;
        ServletOutputStream out = null;
        Enumeration enumeration = null;
        Collection colDictionaryParameterDetailData = null;
        Iterator iteColDictionaryParameterDetailData = null;
        DictionaryParameterDetailData dictionaryParameterDetailData = null;
        Hashtable htab_strDataTypeId_strName = new Hashtable();
        String strAttributeName = null;
        String strPredefinedValues = null;
        String strFileData = null;
        StringTokenizer stComma = null;
        StringTokenizer stColon = null;
        
        try {
            htab_strAttributeName_strPredefinedValues = new Hashtable();
            response.setHeader("Content-Disposition", "attachment;filename=\"dictionary." + dictionaryData.getName().toLowerCase() + "\"");
            out = response.getOutputStream();
            out.println("#");
            out.println("# Dictionary Name : " + dictionaryData.getName().toLowerCase());
            out.println("#");
            if (dictionaryData.getVendorId() != 0.0) {
                out.println("");
                out.println(DictionaryConstant.VENDOR + "\t" + dictionaryData.getName() + "\t" + (new Double(dictionaryData.getVendorId())).intValue());
            }
            out.println("");
            out.println(DictionaryConstant.BEGIN + "\t" + dictionaryData.getName());
            out.println("");
            colDictionaryParameterDetailData = dictionaryData.getDictionaryParameterDetail();
            if (colDictionaryParameterDetailData != null && colDictionaryParameterDetailData.isEmpty() == false) {
                iteColDictionaryParameterDetailData = colDictionaryParameterDetailData.iterator();
                while (iteColDictionaryParameterDetailData.hasNext()) {
                    dictionaryParameterDetailData = (DictionaryParameterDetailData) iteColDictionaryParameterDetailData.next();
                    strFileData =
                            (DictionaryConstant.ATTRIBUTE) + "\t"
                                    + (dictionaryParameterDetailData.getName())
                                    + "\t"
                                    + (dictionaryParameterDetailData.getVendorParameterId())
                                    + "\t"
                                    +
                                    // ((dictionaryParameterDetailData.getDataTypeId() == null ? ((String)htab_strDataTypeId_strName.get(DictionaryConstant.DATA_TYPE_ID_STRING)).toLowerCase() :
                                    // (dictionaryParameterDetailData.getDataTypeId().trim().equalsIgnoreCase("") == true ?
                                    // ((String)htab_strDataTypeId_strName.get(DictionaryConstant.DATA_TYPE_ID_STRING)).toLowerCase() :
                                    // ((String)htab_strDataTypeId_strName.get(dictionaryParameterDetailData.getDataTypeId())).toLowerCase()))) + "\t" +
                                    (dictionaryParameterDetailData.getDataType().getName())
                                    + "\t"
                                    + (dictionaryData.getName())
                                    + ((dictionaryParameterDetailData.getAvPair() == null ? "" : (dictionaryParameterDetailData.getAvPair().trim().equalsIgnoreCase("") == true ? ""
                                            : (dictionaryParameterDetailData.getAvPair().trim().equalsIgnoreCase(DictionaryConstant.YES) == true ? ("\t" + DictionaryConstant.AVPAIR_ALIAS) : ""))));
                    out.println(strFileData);
                    if (dictionaryParameterDetailData.getPredefinedValues() != null && dictionaryParameterDetailData.getPredefinedValues().trim().equalsIgnoreCase("") == false) {
                        htab_strAttributeName_strPredefinedValues.put(dictionaryParameterDetailData.getName(), dictionaryParameterDetailData.getPredefinedValues());
                    }
                }
                out.println("");
                if (htab_strAttributeName_strPredefinedValues != null && htab_strAttributeName_strPredefinedValues.isEmpty() == false) {
                    enumeration = htab_strAttributeName_strPredefinedValues.keys();
                    while (enumeration.hasMoreElements()) {
                        strAttributeName = (String) enumeration.nextElement();
                        strPredefinedValues = (String) htab_strAttributeName_strPredefinedValues.get(strAttributeName);
                        stComma = new StringTokenizer(strPredefinedValues, ",");
                        while (stComma.hasMoreTokens()) {
                            stColon = new StringTokenizer(stComma.nextToken(), ":");
                            strFileData = (DictionaryConstant.VALUE) + "\t" + strAttributeName + "\t" + stColon.nextToken() + "\t" + stColon.nextToken();
                            out.println(strFileData);
                        }
                    }
                    
                }
            }
            out.println("");
            out.println(DictionaryConstant.END + "\t" + dictionaryData.getName());
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}*/
