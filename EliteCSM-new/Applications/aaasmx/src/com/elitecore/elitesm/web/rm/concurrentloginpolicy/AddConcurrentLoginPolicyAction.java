package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyForm;

public class AddConcurrentLoginPolicyAction extends BaseDictionaryAction {
	protected static final String MODULE = "CONCURRENTLOGINPOLICY ACTION";
	private static final String INIT_FORWARD = "createConcurrentLoginPolicy";
	private static final String NEXT_FORWARD = "addConcLoginPolSerWise";
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "create";
	private static final String ACTION_ALIAS = "CREATE_CONCURRENT_LOGIN_POLICY_ACTION";
	private static final long STANDARD_VENDOR_ID=0;
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			AddConcurrentLoginPolicyForm addConcurrentLoginPolicyForm = (AddConcurrentLoginPolicyForm)form;
			String action = request.getParameter("action");
			if(action != null){
				if(action.equalsIgnoreCase("new") ){
					addConcurrentLoginPolicyForm = (AddConcurrentLoginPolicyForm) form;
					addConcurrentLoginPolicyForm.setConcurrentLoginPolicy("Individual");
					addConcurrentLoginPolicyForm.setMaxLogin("Limited");
					addConcurrentLoginPolicyForm.setStatus("1");
					addConcurrentLoginPolicyForm.setConcurrentLoginPolicyMode("Service Wise");
					String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
					addConcurrentLoginPolicyForm.setDescription(getDefaultDescription(userName));
					RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
					
					DictionaryData dictionaryData = dictionaryBLManager.getDictionaryDataByVendor(STANDARD_VENDOR_ID);
					if(dictionaryData != null){
						List<DictionaryParameterDetailData> dictionaryParameterDetailList = dictionaryData.getDictionaryParameterDetailList();
						Collections.sort(dictionaryParameterDetailList);
						addConcurrentLoginPolicyForm.setDictionaryParameterDetailList(dictionaryParameterDetailList);
					}
					
					request.getSession().setAttribute("addConcurrentLoginPolicyForm",addConcurrentLoginPolicyForm);
					return mapping.findForward(INIT_FORWARD);
					
				}else if (action.equalsIgnoreCase("next")){
					
					addConcurrentLoginPolicyForm.setConcurrentLoginPolicyDetail(new ArrayList<String>());
					String status =request.getParameter("status");
					if(status == null || status.equalsIgnoreCase("")){
						addConcurrentLoginPolicyForm.setStatus(BaseConstant.HIDE_STATUS);
					}else{
						addConcurrentLoginPolicyForm.setStatus(BaseConstant.SHOW_STATUS);
					}
					
					if(addConcurrentLoginPolicyForm.getConcurrentLoginPolicyMode().equalsIgnoreCase("Service Wise")){
					//List supportedValueList;
					RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
					DictionaryParameterDetailData dictionaryParameterDetailData = new DictionaryParameterDetailData();
					dictionaryParameterDetailData.setName(addConcurrentLoginPolicyForm.getAttribute());
					List lstPreDefineValues = dictionaryBLManager.getDictionaryParameterDetailList(dictionaryParameterDetailData);
					
					StringTokenizer tokenizer ;
					String strPredefineValues;
				
					Map<String,String> supportedValueMap = new LinkedHashMap<String, String>();
					if(lstPreDefineValues!=null && lstPreDefineValues.size() > 0){
						dictionaryParameterDetailData= (DictionaryParameterDetailData) lstPreDefineValues.get(0);
						strPredefineValues = dictionaryParameterDetailData.getPredefinedValues();
						if(strPredefineValues!=null){
							tokenizer = new StringTokenizer(strPredefineValues,",:");

							//KeyValueBean keyValueBean;
							while(tokenizer.hasMoreTokens()){
								//keyValueBean = new KeyValueBean();
								String value = tokenizer.nextToken();
								String key = tokenizer.nextToken();
					
								supportedValueMap.put(key,value);

							}
							supportedValueMap.put("*","others");
						}
					}
				
					request.getSession().setAttribute("supportedValueMap",supportedValueMap);
						return mapping.findForward(NEXT_FORWARD);
	
					}else {
						addConcurrentLoginPolicyForm.setAttribute(null);
						return mapping.findForward(CREATE_FORWARD);
						
					}
				}
				
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
}
