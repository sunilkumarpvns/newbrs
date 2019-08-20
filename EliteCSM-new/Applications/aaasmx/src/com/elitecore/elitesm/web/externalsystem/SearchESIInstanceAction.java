package com.elitecore.elitesm.web.externalsystem;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.externalsystem.forms.SearchESIInstanceForm;

public class SearchESIInstanceAction extends BaseWebAction{


	private static final String SUCCESS_FORWARD = "SearchESIInstance";
	private static final String SUCCESS_FORWARD_DELETE = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_EXTERNAL_SYSTEM;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_EXTERNAL_SYSTEM;
	private static final String MODULE = "SEARCH_ESI_INSTANCE_ACTION";
	private static boolean isESIBounded = false;
	

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS) || checkAccess(request,ACTION_ALIAS_DELETE)){
			SearchESIInstanceForm searchESIInstanceForm = (SearchESIInstanceForm)form;
			ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
			ESITypeAndInstanceData esiInstanceData = new ESITypeAndInstanceData(); 

			try{
				String[]esiInstanceIds = request.getParameterValues("select");
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchESIInstanceForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo = 1;

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

				long currentPageNumber = 0;
				if(searchESIInstanceForm.getAction() != null) {
					if(searchESIInstanceForm.getAction().equals("delete")){
						checkActionPermission(request, ACTION_ALIAS_DELETE);
						
						/**
						 *  This code is for to check whether any driver is bonded with radius service policy or not
						 *  If any driver is bound with radius service policy then it throws an exception and not allow user to delete driver.
						 */
						
						ServicePolicyBLManager  blManager = new ServicePolicyBLManager();
		    			List<RadServicePolicyData>  radServicePolicyDatasList = blManager.searchRadiusServicePolicy(staffData);
		    			
		    			isESIBounded = false;
		    			
		    			if(radServicePolicyDatasList != null && !(radServicePolicyDatasList.isEmpty())){
		    				for(RadServicePolicyData radServicePolicyData : radServicePolicyDatasList){

		 						String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
		 						
		 						if( xmlDatas != null && !(xmlDatas.isEmpty()) ){
		 	 						
		 	 						List<String> esiIdsValues = Arrays.asList(esiInstanceIds);
		 	 						boolean isEsiBind = searchElementFromXml(xmlDatas.trim(),esiIdsValues);
		 	 						
		 	 						if(isEsiBind){
		 	 							throw new org.hibernate.exception.ConstraintViolationException("Child Record Found - Delete Radius service policy first !!!",null ,"ConstraintViolationException");
		 	 						}
		 						}
		        			}
		    			}
		    			
						if( !isESIBounded ){
						    esiBLManager.deleteByInstanceId(Arrays.asList(esiInstanceIds),staffData);
						    isESIBounded=false;
						}
						
						int strSelectedIdsLen = esiInstanceIds.length;
						currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchESIInstanceForm.getPageNumber(),searchESIInstanceForm.getTotalPages(),searchESIInstanceForm.getTotalRecords());

						request.setAttribute("responseUrl","/searchESIInstance.do?name="+searchESIInstanceForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchESIInstanceForm.getTotalPages()+"&totalRecords="+searchESIInstanceForm.getTotalRecords());
						ActionMessage message = new ActionMessage("esi.delete.success");
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
						return mapping.findForward(SUCCESS_FORWARD_DELETE);
					}
				}

				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");
				
				String strPolicyName =request.getParameter("name");
				if(strPolicyName != null && !("".equals(strPolicyName))){
					esiInstanceData.setName(strPolicyName);
				}else if(searchESIInstanceForm.getName() != null){
					esiInstanceData.setName(searchESIInstanceForm.getName());
				}else{
					esiInstanceData.setName("");
				}

				String strTypeId = request.getParameter("esiTypeId");
				if(strTypeId != null && !("".equals(strTypeId))) {
					esiInstanceData.setEsiTypeId(Long.parseLong(strTypeId));
				}else if (Strings.isNullOrBlank(searchESIInstanceForm.getEsiTypeId()) == false) {
					esiInstanceData.setEsiInstanceId(searchESIInstanceForm.getEsiTypeId());
				}else {
					esiInstanceData.setEsiTypeId(0);
				}

				PageList pageList = esiBLManager.searchESIInstance(esiInstanceData,requiredPageNo, pageSize,staffData);

				searchESIInstanceForm.setPageNumber(pageList.getCurrentPage());
				searchESIInstanceForm.setTotalPages(pageList.getTotalPages());
				searchESIInstanceForm.setTotalRecords(pageList.getTotalItems());
				
				request.setAttribute("externalSysInterfaceList",pageList.getListData());

				request.setAttribute("searchESIInstance",searchESIInstanceForm);//setting the form 
				searchESIInstanceForm.setAction("list");// setting the action

				List<ExternalSystemInterfaceTypeData> esiTypeList = esiBLManager.getListOfESIType();
				searchESIInstanceForm.setEsiTypeList(esiTypeList);
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	

			}catch (ConstraintViolationException e) {			
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("esi.search.childrecord",e.getCause());
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);

			}catch(DataManagerException de){

				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE,de);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(de);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("esi.search.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

	}

	private boolean searchElementFromXml(String stringReader,final List<String> esiInstanceids) throws XPathExpressionException {
		 
		try{
			 InputSource myInputSource=new InputSource(new StringReader(stringReader));
			 
			 SAXParserFactory factory = SAXParserFactory.newInstance();
		     SAXParser saxParser = factory.newSAXParser();
		     
		     /* Define a handler for the parser. The handler would just dump the value of tags
		        as fetched from the oracle table */
		     DefaultHandler handler = new DefaultHandler() {
		         boolean bid = false;
		         String communicatorIds = "";
		         public void startElement(String uri, String localName,String qName, Attributes attributes)
		         throws SAXException {
		                 if (qName.equalsIgnoreCase("communicator")) {
		                   bid = true;
		                   communicatorIds = attributes.getValue("id");
		                  }
		            }
		            public void characters(char ch[], int start, int length)
		            throws SAXException {
		                if (bid) {
		                	
		                	for(int i=0;i<esiInstanceids.size();i++){
		                		   int esiInstanceId = Integer.parseInt(esiInstanceids.get(i));
		                		   int esiId = Integer.parseInt(communicatorIds);
		                			if(esiId  == esiInstanceId){
		                				isESIBounded = true;
		                				System.out.println(esiInstanceId + " : "+esiId);
		                			}
		                	}
		                    bid = false;
		                    communicatorIds="";
		                }
		            }
		        };
		        /* Call parse method to parse the input */
		        saxParser.parse(myInputSource, handler);
		        /* Close all DB related objects */
		}catch(Exception e){
			e.fillInStackTrace();
		}
		return isESIBounded;
	}
}