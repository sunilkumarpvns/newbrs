package com.elitecore.netvertexsm.web.servermgr.spr.ddf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData;
import com.elitecore.corenetvertex.spr.ddf.DDFEntryData;
import com.elitecore.corenetvertex.spr.ddf.DDFTableData;
import com.elitecore.netvertexsm.blmanager.servermgr.ddf.DDFBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.sprdriver.SPRBLManager;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.servermgr.spr.form.DDFTableDataForm;

public class DDFTableDataAction extends BaseWebDispatchAction implements IUpdateAction {

	private static final String MODULE = "DDF-TABLE-DATA-ACTION";
	private static final String DEFAULT_DDF_ID = "DDF_ID_1";

	private static final String VIEW_FORWARD = "viewDDF";
	private static final String EDIT_FORWARD = "editDDF";
	private static final String FAILURE_FORWARD = "failure";

	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DDF;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DDF;
	private Map<String, SubscriberRepositoryData> subscriberRepositoryDataById;

	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (LogManager.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in InitUpdate Method of " + getClass().getName());
		}

		if ((checkAccess(request, UPDATE_ACTION_ALIAS))) {

			DDFTableDataForm ddfForm = (DDFTableDataForm) form;

			try {
				DDFBLManager ddfblManager = DDFBLManager.getInstance();
				DDFTableData ddfTableData = ddfblManager.getDDFTableData();

				SPRBLManager sprblManager = new SPRBLManager();
				List<SPRData> sprDataList = sprblManager.getSPRDataList();

				// FIXME remove this conversion when SubscriberRepositoryData is
				// switched with SPRData --Chetan
				List<SubscriberRepositoryData> subscriberRepositoryDatas = convertSPRDataToSubscriberRepositoryData(sprDataList);

				ddfForm.setSubscriberRepositoryDatas(subscriberRepositoryDatas);
				convertBeanToForm(ddfForm, ddfTableData);
				request.setAttribute("ddfTableData", ddfTableData);
				request.setAttribute("ddfTableDataForm", ddfForm);
				return mapping.findForward(EDIT_FORWARD);
			} catch (Exception e) {

				Logger.logError(MODULE, "Returning error forward from: " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("ddf.update.failure"));
				saveErrors(request, messages);

				ActionMessages errorHeadingMessage = new ActionMessages();
				errorHeadingMessage.add("errorHeading", new ActionMessage("ddf.error.heading", "Updating"));
				saveMessages(request, errorHeadingMessage);
				return mapping.findForward(FAILURE);
			}
		} else {
			Logger.logError(MODULE, "No Access on UPDATE DDF Operation");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage("spr.error.heading", "updating");
			errorHeadingMessage.add("errorHeading", message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private void convertBeanToForm(DDFTableDataForm ddfForm, DDFTableData ddfTableData) {
		ddfForm.setId(ddfTableData.getId());
		ddfForm.setDdfEntries(ddfTableData.getDdfEntries());
		if (ddfTableData.getDefaultSPR() != null) {
			ddfForm.setDefaultSprId(ddfTableData.getDefaultSPR().getId());
			ddfForm.setDefaultSubscriberRepository(ddfTableData.getDefaultSPR());
		}
		ddfForm.setStripPrefixes(ddfTableData.getStripPrefixes());
	}

	private List<SubscriberRepositoryData> convertSPRDataToSubscriberRepositoryData(List<SPRData> sprDataList) {

		List<SubscriberRepositoryData> repositoryDatas = new ArrayList<SubscriberRepositoryData>();
		subscriberRepositoryDataById = new HashMap<String, SubscriberRepositoryData>(sprDataList.size());
		for (SPRData sprData : sprDataList) {
			SubscriberRepositoryData repositoryData = new SubscriberRepositoryData();
			repositoryData.setId(sprData.getId());
			repositoryData.setName(sprData.getSprName());
			repositoryDatas.add(repositoryData);
			subscriberRepositoryDataById.put(sprData.getId(), repositoryData);
		}

		return repositoryDatas;
	}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (LogManager.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in UPDATE Method of " + getClass().getName());
		}

		if ((checkAccess(request, UPDATE_ACTION_ALIAS))) {

			DDFTableDataForm ddfForm = (DDFTableDataForm) form;

			try {
				DDFBLManager ddfblManager = DDFBLManager.getInstance();
				DDFTableData ddfTableData = new DDFTableData();

				convertFormToBean(ddfForm, ddfTableData);

				String[] identityPatterns = request.getParameterValues("identityPatterns");
				String[] sprIds = request.getParameterValues("sprIds");

				if (Arrayz.isNullOrEmpty(identityPatterns) == false) {
					List<DDFEntryData> entryDatas = new ArrayList<DDFEntryData>(identityPatterns.length);
					for (int i = 0; i < identityPatterns.length; i++) {
						DDFEntryData entryData = new DDFEntryData();
						entryData.setOrderNo(i + 1);
						entryData.setIdentityPattern(identityPatterns[i]);
						entryData.setSubscriberRepositoryData(subscriberRepositoryDataById.get(sprIds[i]));
						entryData.setDdfTableData(ddfTableData);
						entryDatas.add(entryData);
					}
					ddfTableData.setDdfEntries(entryDatas);
				}

				// TODO pass Audit related data
				ddfblManager.updateDDFTableData(ddfTableData, null, null);

				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("ddf.update.success", "Updating"));
				saveMessages(request, messages);
				request.setAttribute("responseUrl", "/ddfTableData.do?method=view");
				return mapping.findForward(SUCCESS);
			} catch (Exception e) {
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("ddf.update.failure"));
				saveErrors(request, messages);
				ActionMessages errorHeadingMessage = new ActionMessages();
				errorHeadingMessage.add("errorHeading", new ActionMessage("ddf.error.heading", "updating"));
				saveMessages(request, errorHeadingMessage);
				return mapping.findForward(FAILURE);
			}

		} else {
			Logger.logError(MODULE, "No Access on UPDATE DDF Operation");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage("ddf.error.heading", "updating");
			errorHeadingMessage.add("errorHeading", message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

	}

	private void convertFormToBean(DDFTableDataForm ddfForm, DDFTableData ddfTableData) {
		ddfTableData.setStripPrefixes(ddfForm.getStripPrefixes());
		ddfTableData.setId(ddfForm.getId());
		ddfTableData.setDefaultSPR(subscriberRepositoryDataById.get(ddfForm.getDefaultSprId()));

	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (LogManager.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in View Method of " + getClass().getName());
		}

		if (checkAccess(request, VIEW_ACTION_ALIAS)) {

			try {
				DDFBLManager blManager = DDFBLManager.getInstance();
				DDFTableData ddfTableData = blManager.getDDFTableData();

				if (ddfTableData == null) {
					ddfTableData = new DDFTableData();
					ddfTableData.setId(DEFAULT_DDF_ID);
					blManager.createDDFTableData(ddfTableData);
					ddfTableData = blManager.getDDFTableData();
				}

				request.setAttribute("ddfTableData", ddfTableData);
				return mapping.findForward(VIEW_FORWARD);
			} catch (Exception e) {
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();

				messages.add("information", new ActionMessage("ddf.view.failure"));
				saveErrors(request, messages);

				ActionMessages errorHeadingMessage = new ActionMessages();
				ActionMessage message = new ActionMessage("ddf.error.heading", "viewing");
				errorHeadingMessage.add("errorHeading", message);
				saveMessages(request, errorHeadingMessage);
				return mapping.findForward(FAILURE_FORWARD);
			}

		} else {
			Logger.logWarn(MODULE, "No Access on this View DDF Operation");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage("ddf.error.heading", "viewing");
			errorHeadingMessage.add("errorHeading", message);
			saveMessages(request, errorHeadingMessage);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	
}
