package com.elitecore.elitesm.web.core.system.license;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.core.system.license.SMLicenseBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.license.forms.LicenseForm;
import com.elitecore.elitesm.web.core.system.license.forms.ServerInstanceStatus;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.nfv.AllocatedLicenseRecordInfo;
import com.elitecore.license.nfv.CentralizedLicenseCoordinator;
import com.elitecore.license.nfv.DaoException;
import com.elitecore.license.nfv.PresentableLicenseData;

/**
 * 
 * @author Tejas.P.Shah
 *
 */
public class ViewServerLicenseAction extends BaseDispatchAction {
	public static final String DEREGISTERED = "DEREGISTERED";
	public static final String REGISTERED = "REGISTERED";
	private static final String VIEW_FORWARD = "viewServerLicense";
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
	private static final String DEREGISTER_INSTANCE = ConfigConstant.DEREGISTER_INSTANCE;

	public ActionForward getServerLicenceInformation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			LicenseForm uploadForm = (LicenseForm) form;
			SMLicenseBLManager smLicenseBlManager = new SMLicenseBLManager();
			List<AllocatedLicenseRecordInfo> serverLicenseList = new java.util.ArrayList<AllocatedLicenseRecordInfo>();
			CentralizedLicenseCoordinator coordinator = new CentralizedLicenseCoordinator(smLicenseBlManager);

			List<AllocatedLicenseRecordInfo> allRecords = coordinator.getLicensedInstances();

			List<PresentableLicenseData> license = new ArrayList<PresentableLicenseData>();
			List<ServerInstanceStatus> serverInstanceStatusList = new ArrayList<ServerInstanceStatus>();

			NetServerBLManager netServerBlManager = new NetServerBLManager();
			List<NetServerInstanceData> aaaServerInstanceList = netServerBlManager.getNetServerInstanceList();

			for (NetServerInstanceData info : aaaServerInstanceList) {
				ServerInstanceStatus instanceStatus = new ServerInstanceStatus();
				final String instanceName = info.getName();
				if (Collectionz.isNullOrEmpty(allRecords) == false) {
					for (AllocatedLicenseRecordInfo data : allRecords) {
						if (data.getName().equals(instanceName) && data.getStatus().equals(REGISTERED)) {
							instanceStatus.setId(data.getId());
							instanceStatus.setName(data.getName());
							instanceStatus.setStatus(data.getStatus());
							break;
						} else {
							instanceStatus.setName(instanceName);
						}
					}
					serverInstanceStatusList.add(instanceStatus);
				} else {
					instanceStatus.setName(instanceName);
					serverInstanceStatusList.add(instanceStatus);
				}
			}

			uploadForm.setSmLicenceData(serverInstanceStatusList);
			uploadForm.setPresentableLicenseDatas(license);
			request.setAttribute("uploadForm", uploadForm);
			request.setAttribute("serverlicenselist", serverLicenseList);

		} catch (Exception e) {
			Logger.logError(MODULE, "Error during Server Instance fetching, reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("license.serverinstance.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			e.printStackTrace();
			return mapping.findForward(FAILURE_FORWARD);
		}
		return mapping.findForward(VIEW_FORWARD);
	}

	public ActionForward doDeregister(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		try {

			LicenseForm deregisterForm = (LicenseForm) form;
			checkActionPermission(request, DEREGISTER_INSTANCE);
			
			NetServerBLManager netServerBlManager = new NetServerBLManager();

			String instanceName = deregisterForm.getInstanceName();
			String ip = null, port = null;

			NetServerInstanceData serverInstanceData = null;

			serverInstanceData = netServerBlManager.getNetServerInstanceByName(instanceName);

			CentralizedLicenseCoordinator coordinator = new CentralizedLicenseCoordinator(new SMLicenseBLManager());

			coordinator.deregister(instanceName);
			ip = serverInstanceData.getAdminHost();
			port = String.valueOf(serverInstanceData.getAdminPort());

			Object[] objArgValues = {};
			String[] strArgTypes = {};
			String method = "deregisterLicense";

			try {
				EliteUtility.doRemoteCommunication(MBeanConstants.LICENSE, method, ip, port, objArgValues, strArgTypes);
			} catch (Exception e) {
				Logger.getLogger().error(MODULE, "Server Communication failed. Connection can not established.");
			}

			List<AllocatedLicenseRecordInfo> allRecords = coordinator.getLicensedInstances();

			List<PresentableLicenseData> license = null;
			deregisterForm.setPresentableLicenseDatas(license);

			List<ServerInstanceStatus> serverInstanceStatusList = new ArrayList<ServerInstanceStatus>();
			deregisterForm.setSmLicenceData(serverInstanceStatusList);

			request.setAttribute("uploadForm", deregisterForm);
			request.setAttribute("serverlicenselist", allRecords);
			request.setAttribute("responseUrl", "/viewServerLicenceAction?method=getServerLicenceInformation");

			ActionMessage message = new ActionMessage("license.deregister.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS_FORWARD);

		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (UnidentifiedServerInstanceException commExp) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Logger.logTrace(MODULE, commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/viewServerLicenceAction?method=getServerLicenceInformation");
			ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			messages.add("information", messageReason);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (CommunicationException commExp) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Logger.logTrace(MODULE, commExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/viewServerLicenceAction?method=getServerLicenceInformation");
			ActionMessage messageReason = new ActionMessage("servermgr.server.deregister.liveserver.failure.reason");
			ActionMessages messages = new ActionMessages();
			messages.add("information", messageReason);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (Exception managerExp) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Logger.logTrace(MODULE, managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/viewServerLicenceAction?method=getServerLicenceInformation");
			ActionMessage message = new ActionMessage("license.deregister.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}

	public ActionForward fetchLicense(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws DataManagerException {
		String id = request.getParameter("id");
		LicenseForm deregisterForm = (LicenseForm) form;

		CentralizedLicenseCoordinator coordinator;
		List<PresentableLicenseData> license = null;
		try {
			coordinator = new CentralizedLicenseCoordinator(new SMLicenseBLManager());

			license = coordinator.getLicense(id);
			deregisterForm.setPresentableLicenseDatas(license);

			request.setAttribute("showData", new String("showData"));
			request.setAttribute("uploadForm", deregisterForm);
		} catch (InvalidLicenseKeyException e) {
			Logger.logError(MODULE, "Error during license fetching, reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("license.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (DaoException e) {
			Logger.logError(MODULE, "Error during license fetching, reason : " + e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/viewServerLicenceAction?method=getServerLicenceInformation");
			ActionMessage message = new ActionMessage("license.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} catch (Exception managerExp) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
			Logger.logTrace(MODULE, managerExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			request.setAttribute("responseUrl", "/viewServerLicenceAction?method=getServerLicenceInformation");
			ActionMessage message = new ActionMessage("license.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}

		return mapping.findForward(VIEW_FORWARD);
	}
}
