package com.elitecore.elitesm.web.rm.concurrentloginpolicy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyDetailForm;
import com.elitecore.elitesm.web.rm.concurrentloginpolicy.forms.AddConcurrentLoginPolicyForm;

public class AddConcurrentLoginPolicyDetailAction  extends BaseWebAction{
	protected static final String MODULE = "ADDCONCURRENTLOGINPOLICYDETAIL ACTION";
	private static final String NEXT_FORWARD = "addConcLoginPolicyDetail";
	private static final String CREATE_FORWARD = "create";
	private static final String PREVIOUS_FORWARD = "previous";
	private static final String LIMITED = "Limited";
	private static final String ACTION_ALIAS = "CREATE_CONCURRENT_LOGIN_POLICY_ACTION";
	
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				Logger.logTrace(MODULE,"Entered execute method of " + getClass().getName());
				String action = request.getParameter("action");

				AddConcurrentLoginPolicyDetailForm addConcurrentLoginPolicyDetailForm =  (AddConcurrentLoginPolicyDetailForm)form;

				if(action != null){
					if(action.equalsIgnoreCase("add") ){
						((AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm")).getConcurrentLoginPolicyDetail().add(addConcurrentLoginPolicyDetailForm);
						request.removeAttribute("addConcurrentLoginPolicyDetailForm");

						List<AddConcurrentLoginPolicyDetailForm> concurrentLoginPolicyDetailList = ((AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm")).getConcurrentLoginPolicyDetail();
						Map<String,Integer> attributeLoginMap = getAttributeLoginMap(concurrentLoginPolicyDetailList);
						Logger.logDebug(MODULE, "attributeLoginMap [add] : "+attributeLoginMap);
						request.setAttribute("attributeLoginMap", attributeLoginMap);
						 
					}else if(action.equalsIgnoreCase("create")){
						return mapping.findForward(CREATE_FORWARD);

					}else if(action.equalsIgnoreCase("remove")){
						 
						((AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm")).getConcurrentLoginPolicyDetail().remove(addConcurrentLoginPolicyDetailForm.getItemIndex());
						request.removeAttribute("addConcurrentLoginPolicyDetailForm");	

						List<AddConcurrentLoginPolicyDetailForm> concurrentLoginPolicyDetailList = ((AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm")).getConcurrentLoginPolicyDetail();
						Map<String,Integer> attributeLoginMap = getAttributeLoginMap(concurrentLoginPolicyDetailList);
						Logger.logDebug(MODULE, "attributeLoginMap [remove] : "+attributeLoginMap);
						request.setAttribute("attributeLoginMap", attributeLoginMap);

					}else if(action.equalsIgnoreCase("next")){
						addConcurrentLoginPolicyDetailForm.setMaxNumLogin(0);
						addConcurrentLoginPolicyDetailForm.setMaxLogin(LIMITED);
						
						List<AddConcurrentLoginPolicyDetailForm> concurrentLoginPolicyDetailList = ((AddConcurrentLoginPolicyForm)request.getSession().getAttribute("addConcurrentLoginPolicyForm")).getConcurrentLoginPolicyDetail();
						Map<String,Integer> attributeLoginMap = getAttributeLoginMap(concurrentLoginPolicyDetailList);
						Logger.logDebug(MODULE, "attributeLoginMap [next] : "+attributeLoginMap);
						request.setAttribute("attributeLoginMap", attributeLoginMap);
						
					}else if(action.equalsIgnoreCase("previous")){
						
						return mapping.findForward(PREVIOUS_FORWARD);

					}
				}
				return mapping.findForward(NEXT_FORWARD);

			} catch (Exception e) {
				Logger.logError(MODULE,e.getMessage());
				e.printStackTrace();
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}

			return mapping.findForward("failure");
			
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private Map<String,Integer> getAttributeLoginMap(List<AddConcurrentLoginPolicyDetailForm> concurrentLoginPolicyDetailList){
		 Map<String,Integer> attributeLoginMap = new HashMap<String,Integer>();
		 if(concurrentLoginPolicyDetailList!=null){
			 for (Iterator<AddConcurrentLoginPolicyDetailForm> iterator = concurrentLoginPolicyDetailList.iterator(); iterator.hasNext();) {
				 AddConcurrentLoginPolicyDetailForm temp =  iterator.next();
				 if(attributeLoginMap.containsKey(temp.getAttributeValue())){
					 Integer value = (Integer)attributeLoginMap.get(temp.getAttributeValue());
					 attributeLoginMap.put(temp.getAttributeValue(), new Integer( value.intValue() + temp.getMaxNumLogin()));
				 }else{
					 attributeLoginMap.put(temp.getAttributeValue(), new Integer(temp.getMaxNumLogin()));
				 }
			}
		 }
		 return attributeLoginMap;
	}
}
