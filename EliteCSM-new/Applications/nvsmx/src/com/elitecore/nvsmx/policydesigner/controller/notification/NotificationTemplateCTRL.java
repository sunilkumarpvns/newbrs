
package com.elitecore.nvsmx.policydesigner.controller.notification;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.policydesigner.model.pkg.notification.NotificationTemplateDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import java.text.MessageFormat;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class NotificationTemplateCTRL extends EliteGenericCTRL<NotificationTemplateData>{

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "NOTF-TMPLT-CTRL";
	private NotificationTemplateData notificationTemplateData = new NotificationTemplateData();
	private String actionChainUrl;
	private static final String ACTION = "policydesigner/notification/NotificationTemplate/search";
	private static final String ACTION_TEMPLATE_VIEW ="policydesigner/notification/NotificationTemplate/view";
	private List<String> selectedGroupsForTemplate = Collectionz.newArrayList();
	Object []messageParameter = {Discriminators.NOTIFICATION_TEMPLATE};
	public static final String NOTIFICATION_TEMPLATE_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.subject,dataList\\[\\d+\\]\\.templateType,dataList\\[\\d+\\]\\.groups";


	@Override
	public String search() {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called search()");
		}

		try{
			return Results.LIST.getValue();
		} catch (HibernateDataException e) {
			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			getLogger().error(MODULE,"Failed to search Notification Templates. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			request.setAttribute(Attributes.ACTION, ACTION);
			return Results.ERROR.getValue();
		}
	}

	public String searchCriteria(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called searchCriteria()");
		}

	    try {
	    	String json = GsonFactory.defaultInstance().toJson(notificationTemplateData);
	    	request.setAttribute(Attributes.CRITERIA, json);
	    	return Results.LIST.getValue();
	    }catch(Exception e){
			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			getLogger().error(MODULE, "Failed to search Notification Template. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			return Results.LIST.getValue();
	    }
	}

	private String getNotificationTemplateId() {

		String pkgId = request.getParameter(Attributes.NOTIFICATION_TEMPLATE_ID);
		if (Strings.isNullOrBlank(pkgId)) {
			pkgId = (String) request.getAttribute(Attributes.NOTIFICATION_TEMPLATE_ID);
			if (Strings.isNullOrBlank(pkgId)) {
				pkgId = request.getParameter("notificationTemplateData.id");
			}
		}
		return pkgId;
	}


	public String init() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called init()");
		}

		request.setAttribute(Attributes.ACTION, ACTION);
		String notificationTemplateId = getNotificationTemplateId();
		if (Strings.isNullOrBlank(notificationTemplateId)) {
		return Results.CREATE.getValue();
	}
		try {
			setActionChainUrl(ACTION_TEMPLATE_VIEW);
	
			notificationTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, notificationTemplateId);
			if (notificationTemplateData == null) {
				addActionError(Discriminators.NOTIFICATION_TEMPLATE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				return Results.LIST.getValue();
			}
			setSelectedGroupsForTemplate(Strings.splitter(',').trimTokens().split(notificationTemplateData.getGroups()));
			request.setAttribute(Attributes.NOTIFICATION_TEMPLATE_ID, notificationTemplateId);
			return Results.UPDATE.getValue();

		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching Notification Tempalte data for update operation. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		}
	}

	public String create(){
		getLogger().debug(MODULE,"Method called create()");
		
		response.setContentType("text/html;charset=UTF-8");
		setActionChainUrl(Results.VIEW.getValue());
		try{
			notificationTemplateData.setCreatedDateAndStaff(getStaffData());
			if(Strings.isNullOrBlank(getGroupIds())){
				notificationTemplateData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				notificationTemplateData.setGroups(getGroupIds());
			}
			CRUDOperationUtil.save(notificationTemplateData);
			String message = Discriminators.NOTIFICATION_TEMPLATE + " <b><i>" + notificationTemplateData.getName() + "</i></b> " + "Created";  
			CRUDOperationUtil.audit(notificationTemplateData,notificationTemplateData.getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), notificationTemplateData.getHierarchy(), message);
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			request.setAttribute(Attributes.NOTIFICATION_TEMPLATE_ID, notificationTemplateData.getId());
			return Results.DISPATCH_VIEW.getValue();
			
 		} catch(Exception e){
			getLogger().error(MODULE,"Failed to create Notification Template. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.NOTIFICATION_TEMPLATE + " "+ getText(ActionMessageKeys.CREATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		} 
	}

	public String delete(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called delete()");
		}
	    String[] templateIds = request.getParameterValues("ids");
	    String fromViewPage = request.getParameter("fromViewPage");
	    try {
	    	if( templateIds != null){
	    		for(String templateId: templateIds){

	    			notificationTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, templateId);
	    			String attachedPackages = NotificationTemplateDAO.attachedWithPackages(notificationTemplateData);
					
					if(Strings.isNullOrBlank(attachedPackages) == false){
						addActionError(getText("notification.template.delete.error",new String[]{notificationTemplateData.getName()}));
						addActionError(getText("note.refer.logs"));
						getLogger().error(MODULE,"Can not perform delete operation. Reason: "+notificationTemplateData.getName() +"  is associated to packages " + attachedPackages );

						if(Strings.isNullOrBlank(fromViewPage) == false){
							return Results.VIEW.getValue();
						}else{
							return Results.LIST.getValue();
						}
					}
	    			notificationTemplateData.setStatus(CommonConstants.STATUS_DELETED);
	    			CRUDOperationUtil.update(notificationTemplateData);
	    			String message = Discriminators.NOTIFICATION_TEMPLATE + " <b><i>" + notificationTemplateData.getName() + "</i></b> " + "Deleted";
	    			CRUDOperationUtil.audit(notificationTemplateData,notificationTemplateData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), notificationTemplateData.getHierarchy(), message);
	    		}
	    		MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
				addActionMessage(messageFormat.format(messageParameter));
	    		setActionChainUrl(ACTION);
	    		return Results.REDIRECT_ACTION.getValue();
	    	} else {
			    addActionError(Discriminators.NOTIFICATION_TEMPLATE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			    addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			    if(Strings.isNullOrBlank(fromViewPage) == false){
					return Results.VIEW.getValue();
				}else{
					return Results.LIST.getValue();
				}
	    	}
 	    }catch (Exception e) {			
			getLogger().error(MODULE,"Error while fetching Notification Template data for delete operation. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.NOTIFICATION_TEMPLATE + " " +getText(ActionMessageKeys.DELETE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
	    }						
	}


	public String view(){
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called view()");
		}
		
		request.setAttribute(Attributes.ACTION, ACTION);
		response.setContentType("text/html;charset=UTF-8");
		String notificationTemplateId = getNotificationTemplateId();

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"View template of Id: "+ notificationTemplateId);
		}
		
		try {

			notificationTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class, notificationTemplateId);
			notificationTemplateData.setGroupNames(GroupDAO.getGroupNames(Strings.splitter(',').trimTokens().split((notificationTemplateData.getGroups()))));
			
			if (notificationTemplateData == null) {
				addActionError("Failed to display full details of Notification Template");
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				return Results.LIST.getValue();
			}

 		} catch (Exception e) {			
			getLogger().error(MODULE,"Error while fetching Notification Template data for view operation. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			
			addActionError(Discriminators.NOTIFICATION_TEMPLATE + " " + getText(ActionMessageKeys.VIEW_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		} 
		return Results.VIEW.getValue();
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called update()");
		}
		
		response.setContentType("text/html;charset=UTF-8");
		String notificationTemplateId = notificationTemplateData.getId();
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "NotificationTempalte Id: " + notificationTemplateId);
			}
			if(Strings.isNullOrBlank(getGroupIds())){
				notificationTemplateData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				notificationTemplateData.setGroups(getGroupIds());
			}
			notificationTemplateData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(notificationTemplateData);
			String message = Discriminators.NOTIFICATION_TEMPLATE + " <b><i>" + notificationTemplateData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(notificationTemplateData,notificationTemplateData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), notificationTemplateData.getHierarchy(), message);
			request.setAttribute(Attributes.NOTIFICATION_TEMPLATE_ID, notificationTemplateId);
			
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			getLogger().error(MODULE, "Error while updating Notification Template of Id: '"+notificationTemplateId+"'. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.NOTIFICATION_TEMPLATE + " " +getText(ActionMessageKeys.UPDATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		}	
	}

	public void setNotificationTemplateData(NotificationTemplateData notificationTemplateData) {
		this.notificationTemplateData = notificationTemplateData;
	}



	@Override
	public NotificationTemplateData getModel() {
		return notificationTemplateData;
	}
	
	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	public NotificationTemplateData getNotificationTemplateData() {
		return notificationTemplateData;
	}

	public List<String> getSelectedGroupsForTemplate() {
		return selectedGroupsForTemplate;
	}

	public void setSelectedGroupsForTemplate(
			List<String> selectedGroupsForTemplate) {
		this.selectedGroupsForTemplate = selectedGroupsForTemplate;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List<NotificationTemplateData> getSearchResult(String criteriaJson, Class beanType, int startIndex,int maxRecords, String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception {
		return super.getSearchResult(criteriaJson, beanType, startIndex, maxRecords,sortColumnName, sortColumnOrder, staffBelongingGroups);
	}


	@Override
	public String getIncludeProperties(){
		return NOTIFICATION_TEMPLATE_INCLUDE_PARAMETERS;
	}

}
