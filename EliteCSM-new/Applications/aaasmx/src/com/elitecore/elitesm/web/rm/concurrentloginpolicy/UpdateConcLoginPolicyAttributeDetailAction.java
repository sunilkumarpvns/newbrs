package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.UpdateConcLoginPolicyAttributeDetailForm;

public class UpdateConcLoginPolicyAttributeDetailAction  extends BaseDictionaryAction{

	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_FORWARD  = "updateConcLoginAttributeDet";
	private static final String VIEW_FORWARD    = "viewConcurrentLoginPolicyDetail";
	private static final String  MODULE         = "UPDATE CONCURRENT LOGIN POLICY ATTRIBUTE DETAIL";
	private static final String strUpdateConcLoginAttributeDet = "updateConcLoginAttributeDet";
	private static final String LIMITED = "Limited";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_CONCURRENT_POLICY_ATTRIBUTE_DETAIL_ACTION;
	private static final String SERVICE_WISE_MODE_ID = "SMS0150";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();
			UpdateConcLoginPolicyAttributeDetailForm concLoginPolicyAttributeDetailForm= (UpdateConcLoginPolicyAttributeDetailForm)form;
			
			String strConcurrentLoginPolicyId = request.getParameter("concurrentLoginPolicyId");

			try {
	
				String concurrentLoginPolicyId=null;
				if(strConcurrentLoginPolicyId == null){
					
					concurrentLoginPolicyId = concLoginPolicyAttributeDetailForm.getConcurrentLoginId();
					Logger.logTrace(MODULE,"After submit setting concurrentLoginPolicyId:"+concurrentLoginPolicyId);
				}else{
					concurrentLoginPolicyId = strConcurrentLoginPolicyId;
					
				}
				Logger.logDebug(MODULE,"concurrentLoginPolicyId :"+concurrentLoginPolicyId);
				concLoginPolicyAttributeDetailForm.setConcurrentLoginId(concurrentLoginPolicyId);
				
				String action = concLoginPolicyAttributeDetailForm.getAction();
				
					if( Strings.isNullOrEmpty(concurrentLoginPolicyId) == false ){
						ConcurrentLoginPolicyData concurrentLoginPolicyData = new ConcurrentLoginPolicyData();
						
						concurrentLoginPolicyData= concurrentLoginPolicyBLManager.getConcurrentLoginPolicyById(concurrentLoginPolicyId);
						
						request.removeAttribute("concurrentLoginPolicyData");
						request.setAttribute("concurrentLoginPolicyData",concurrentLoginPolicyData);
						
						if(concurrentLoginPolicyData.getConcurrentLoginPolicyModeId().equalsIgnoreCase(SERVICE_WISE_MODE_ID)){
							RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
							DictionaryParameterDetailData dictionaryParameterDetailData = new DictionaryParameterDetailData();
							dictionaryParameterDetailData.setName(concurrentLoginPolicyData.getAttribute());
							List lstPreDefineValues = dictionaryBLManager.getDictionaryParameterDetailList(dictionaryParameterDetailData);
							
							StringTokenizer tokenizer ;
							String strPredefineValues;
							Map<String,String> supportedValueMap = new LinkedHashMap<String, String>();
							
							if(lstPreDefineValues!=null && lstPreDefineValues.size() > 0){
								dictionaryParameterDetailData= (DictionaryParameterDetailData) lstPreDefineValues.get(0);
								strPredefineValues = dictionaryParameterDetailData.getPredefinedValues();
								if(strPredefineValues!=null){
								tokenizer = new StringTokenizer(strPredefineValues,",:");
								
								while(tokenizer.hasMoreTokens()){
									String value = tokenizer.nextToken();
									String key = tokenizer.nextToken();
									supportedValueMap.put(key,value);
								}
								supportedValueMap.put("*","others");
								}
							}
							request.getSession().setAttribute("supportedValueMap",supportedValueMap);	
						}
						request.setAttribute("action",strUpdateConcLoginAttributeDet);
						
						if(action!=null){
							if(action.equalsIgnoreCase("add")){
								Logger.logTrace(MODULE,"add NAS Detail called");
								int iNextSrNo=1;
								if(concurrentLoginPolicyData.getConcurrentLoginPolicyDetail() !=null){
								iNextSrNo = concurrentLoginPolicyData.getConcurrentLoginPolicyDetail().size()+1;
								}
								IConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData = new ConcurrentLoginPolicyDetailData();
								concurrentLoginPolicyDetailData.setConcurrentLoginId(concLoginPolicyAttributeDetailForm.getConcurrentLoginId());
								concurrentLoginPolicyDetailData.setSerialNumber(iNextSrNo);
								concurrentLoginPolicyDetailData.setLogin(concLoginPolicyAttributeDetailForm.getMaxNumLogin());
								concurrentLoginPolicyDetailData.setAttributeValue(concLoginPolicyAttributeDetailForm.getAttributeValue());
								((List)request.getSession().getAttribute("loginPolicyDetail")).add(concurrentLoginPolicyDetailData);
								Logger.logDebug(MODULE,"after adding size:"+((List)request.getSession().getAttribute("loginPolicyDetail")).size());						
								request.setAttribute("action",strUpdateConcLoginAttributeDet);
								
								List lstLoginPolicyDetail = (List)request.getSession().getAttribute("loginPolicyDetail");
								Map<String,Integer> attributeLoginMap = getAttributeLoginMap(lstLoginPolicyDetail);
								Logger.logDebug(MODULE, "Nas Port Type Map [add] : "+attributeLoginMap);
								request.setAttribute("attributeLoginMap", attributeLoginMap);

								return mapping.findForward(UPDATE_FORWARD);
								
							}else if(action.equalsIgnoreCase("update")){
								IConcurrentLoginPolicyData tempConcurrentLoginPolicyData = new ConcurrentLoginPolicyData();
								
								concurrentLoginPolicyData= concurrentLoginPolicyBLManager.getConcurrentLoginPolicyById(concurrentLoginPolicyId);
								
								List lstLoginPolicyDetail = (List)request.getSession().getAttribute("loginPolicyDetail");
								Logger.logDebug(MODULE,"size before update :"+((List)request.getSession().getAttribute("loginPolicyDetail")).size());
								
								for(int i = 0; i<lstLoginPolicyDetail.size() ; i++){
									IConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData= (IConcurrentLoginPolicyDetailData)lstLoginPolicyDetail.get(i);
									Logger.logDebug(MODULE,"old serial:"+concurrentLoginPolicyDetailData.getSerialNumber());
									concurrentLoginPolicyDetailData.setSerialNumber(i+1);
								}
								
								List<ConcurrentLoginPolicyDetailData> tempSet = (List<ConcurrentLoginPolicyDetailData>) request.getSession().getAttribute("loginPolicyDetail");
								
								for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : tempSet) {
									if (concurrentLoginPolicyDetail.getLogin() == -1) {
										concurrentLoginPolicyDetail.setLoginLimit("Unlimited");
									} else {
										concurrentLoginPolicyDetail.setLoginLimit("Limited");
									}
								}
								
								Logger.logDebug(MODULE,"Set size before update :"+tempSet.size());
								concurrentLoginPolicyData.setConcurrentLoginPolicyDetail(tempSet);
								
								IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
								String actionAlias = ACTION_ALIAS;
								
								staffData.setAuditId(concurrentLoginPolicyData.getAuditUId());
								staffData.setAuditName(concurrentLoginPolicyData.getName());
								
								concurrentLoginPolicyBLManager.updateAttributeDetail(concurrentLoginPolicyData,concurrentLoginPolicyId,staffData,actionAlias);
								request.removeAttribute("action");
								return mapping.findForward(VIEW_FORWARD);   
								
							}else if(action.equalsIgnoreCase("remove")){
								List lstLoginPolicyDetail = (List)request.getSession().getAttribute("loginPolicyDetail");
								if(lstLoginPolicyDetail != null){
									Logger.logDebug(MODULE,"about to remove ");
									lstLoginPolicyDetail.remove(concLoginPolicyAttributeDetailForm.getItemIndex());
									
								}
								
								request.getSession().removeAttribute("loginPolicyDetail");
								request.getSession().setAttribute("loginPolicyDetail",lstLoginPolicyDetail);
								Logger.logDebug(MODULE,"Size After Remove : "+((List)request.getSession().getAttribute("loginPolicyDetail")).size());			        		
								request.setAttribute("action",strUpdateConcLoginAttributeDet);
								
								Map<String,Integer> attributeLoginMap = getAttributeLoginMap(lstLoginPolicyDetail);
								Logger.logDebug(MODULE, "attributeLoginMap [remove] : "+attributeLoginMap);
								request.setAttribute("attributeLoginMap", attributeLoginMap);
								
								return mapping.findForward(UPDATE_FORWARD);
								
							}
						}else{
							
						List lstConcurrentLoginPolicyDetails = concurrentLoginPolicyData.getConcurrentLoginPolicyDetail();
							
						Logger.logDebug(MODULE,"initial size:"+lstConcurrentLoginPolicyDetails.size());
						request.getSession().setAttribute("loginPolicyDetail",lstConcurrentLoginPolicyDetails);
						
						List lstLoginPolicyDetail = (List)request.getSession().getAttribute("loginPolicyDetail");
						Map<String,Integer> attributeLoginMap = getAttributeLoginMap(lstLoginPolicyDetail);
						Logger.logDebug(MODULE, "attributeLoginMap[add] : "+attributeLoginMap);
						request.setAttribute("attributeLoginMap", attributeLoginMap);
						
						request.setAttribute("action",strUpdateConcLoginAttributeDet);
						concLoginPolicyAttributeDetailForm.setMaxLogin(LIMITED);
						return mapping.findForward(UPDATE_FORWARD);
						}
					}
					
			} catch (Exception e) {
				Logger.logError(MODULE,e.getMessage());
				e.printStackTrace();
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			} 
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
	
	private UpdateConcLoginPolicyAttributeDetailForm convertBeanToForm(IConcurrentLoginPolicyData concurrentLoginPolicyData){
		UpdateConcLoginPolicyAttributeDetailForm updateConcLoginPolicyAttributeDetailForm =null;
		if(concurrentLoginPolicyData != null){
			updateConcLoginPolicyAttributeDetailForm = new UpdateConcLoginPolicyAttributeDetailForm();
			updateConcLoginPolicyAttributeDetailForm.setConcurrentLoginId(concurrentLoginPolicyData.getConcurrentLoginId());
			List lstNasPortTypeDetail = concurrentLoginPolicyData.getConcurrentLoginPolicyDetail();

			updateConcLoginPolicyAttributeDetailForm.setConcurrentLoginPolicyDetail(lstNasPortTypeDetail);
			updateConcLoginPolicyAttributeDetailForm.setAuditUId(concurrentLoginPolicyData.getAuditUId());
		}
		return updateConcLoginPolicyAttributeDetailForm;	
	}
	

	private Map<String,Integer> getAttributeLoginMap(List<IConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetailList){
		 Map<String,Integer> attributeLoginMap = new HashMap<String,Integer>();
		 if(concurrentLoginPolicyDetailList!=null){
			 for (Iterator<IConcurrentLoginPolicyDetailData> iterator = concurrentLoginPolicyDetailList.iterator(); iterator.hasNext();) {
				 IConcurrentLoginPolicyDetailData temp =  iterator.next();
				 if(attributeLoginMap.containsKey(temp.getAttributeValue())){
					 Integer value = (Integer)attributeLoginMap.get(temp.getAttributeValue());
					 attributeLoginMap.put(temp.getAttributeValue(), new Integer( value.intValue() + temp.getLogin()));
				 }else{
					 attributeLoginMap.put(temp.getAttributeValue(), new Integer(temp.getLogin()));
				 }
			}
		 }
		 return attributeLoginMap;
	}

}
