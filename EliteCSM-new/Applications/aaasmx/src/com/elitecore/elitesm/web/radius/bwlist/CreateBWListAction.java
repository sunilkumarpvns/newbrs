package com.elitecore.elitesm.web.radius.bwlist;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.blmanager.radius.bwlist.BWListBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.ErrorBean;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.bwlist.forms.CreateBWListForm;

public class CreateBWListAction extends BaseWebAction {
	private static final String MODULE = "CREATE BWLIST ACTION";
	private static final String ACTION_ALIAS_CREATE = ConfigConstant.CREATE_BLACKLIST_CANDIDATES_ACTION;
	private static final String ACTION_ALIAS_UPLOAD = ConfigConstant.UPLOAD_BLACKLIST_CANDIDATES_ACTION;
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
	private static final String FAILURE = "failure";
	private static final String SUCCESS = "success";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Logger.logTrace(MODULE,"Entered execute method of "+getClass().getName());
		try {

			IStaffData staffData =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			CreateBWListForm createBWListForm = (CreateBWListForm)form;

			if("create".equalsIgnoreCase(createBWListForm.getInputMode())){

				checkActionPermission(request, ACTION_ALIAS_CREATE);

				BWListBLManager bwlistBLManager = new BWListBLManager();
				BWListData bwlistData = convertFormToBean(createBWListForm);
			
                
				bwlistBLManager.create(bwlistData);
				doAuditing(staffData ,ACTION_ALIAS_CREATE);
				
				request.setAttribute("responseUrl","/searchBWList.do");
				ActionMessage message = new ActionMessage("bwlist.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);



			}else if("upload".equalsIgnoreCase(createBWListForm.getInputMode())){
				checkActionPermission(request, ACTION_ALIAS_UPLOAD);

				BWListBLManager bwlistBLManager = new BWListBLManager();
				FormFile file = createBWListForm.getFileUpload();

				if(file!=null && file.getFileName().toLowerCase().endsWith(".csv")){
					List<ErrorBean> errorList = bwlistBLManager.addCSVDataToDatabase(createBWListForm.getFileUpload());


					if(errorList!=null  && errorList.size()>0){

						Object errorElements[] = errorList.toArray();
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("bwlist.csv.upload.errormessage");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE);

					}else{

						doAuditing(staffData, ACTION_ALIAS_UPLOAD);
						request.setAttribute("responseUrl","/initSearchBWList.do");
						ActionMessage message = new ActionMessage("bwlist.csv.upload.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request,messages);

					}

				}
				else{
					//Object errorElements[] = errorList.toArray();
					//request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("bwlist.csv.upload.invalidfile");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}


			}

			return mapping.findForward(SUCCESS);
		}catch(ActionNotPermitedException e){
			ActionMessages messages = new ActionMessages();
			Logger.logError(MODULE, "Action is not permitted.");
			Logger.logTrace(MODULE, e);
			ActionMessage message = new ActionMessage("general.user.restricted");
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);

		}catch (Exception managerExp) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Logger.logTrace(MODULE,managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("bwlist.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}
		return mapping.findForward(FAILURE);
	}
	
	private BWListData convertFormToBean(CreateBWListForm createBWListForm){
		BWListData bwlistData = new BWListData();
		bwlistData.setAttributeId(createBWListForm.getAttributeId());
		bwlistData.setAttributeValue(createBWListForm.getAttributeValue());
		bwlistData.setTypeId("LST00");
		if(createBWListForm.getActiveStatus()!=null){
			if(createBWListForm.getActiveStatus().equalsIgnoreCase("1")){
				bwlistData.setCommonStatusId("CST01");
			}else{
				bwlistData.setCommonStatusId("CST02");					
			}
		}else{
			bwlistData.setCommonStatusId("CST02");				
		}

		bwlistData.setValidity(createBWListForm.getValidity());
		bwlistData.setTypeName(createBWListForm.getTypeName());
		return bwlistData;
	}


}
