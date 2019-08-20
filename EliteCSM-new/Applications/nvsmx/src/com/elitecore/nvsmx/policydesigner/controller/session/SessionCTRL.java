package com.elitecore.nvsmx.policydesigner.controller.session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.policydesigner.model.session.SessionData;
import com.elitecore.nvsmx.policydesigner.model.session.SessionDataUtility;
import com.elitecore.nvsmx.policydesigner.model.session.SessionSearchField;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.remotecommunications.BroadCastCompletionResult;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroup;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.ws.sessionmanagement.blmanager.SessionUtil;
import com.elitecore.nvsmx.ws.util.ReAuthUtil;
import com.google.gson.Gson;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.ws.util.SessionInformationComparator.getSessionInformationComparatorBasedOnCreationTime;

/**
 *
 * Control active session operations
 * @author Dhyani.Raval
 *
 */
public class SessionCTRL extends EliteGenericCTRL<SessionData>{

	private static final long serialVersionUID = 1L;
	private static final String MODULE = SessionCTRL.class.getSimpleName();
	private static final SessionUtil sessionUtil = new SessionUtil(EndPointManager.getInstance(), RMIGroupManager.getInstance());
	private static final String SUBSCRIBER_VIEW = "policydesigner/subscriber/Subscriber/view";
	public static final String CORE_SESSION_ID_OR_INSTANCE_ID_NOT_FOUND = "CoreSessionId Or InstanceId not found.";
	private SessionData sessionData;
	private SessionSearchField sessionSearchField;
	private String actionChainUrl;
	public static final String SESSION_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.sessionInfo,dataList\\[\\d+\\]\\.sessionInfo.CS.SessionID,dataList\\[\\d+\\]\\.sessionInfo.Sub.SubscriberIdentity,dataList\\[\\d+\\]\\.sessionInfo.CS.SessionIPv4,dataList\\[\\d+\\]\\.sessionInfo.CS.SessionType,dataList\\[\\d+\\]\\.creationTime,dataList\\[\\d+\\]\\.lastUpdateTime,dataList\\[\\d+\\]\\.instanceData,dataList\\[\\d+\\]\\.instanceData.name,dataList\\[\\d+\\]\\.instanceData.netServerCode,dataList\\[\\d+\\]\\.instanceData.id,dataList\\[\\d+\\]\\.sessionInfo.CS.CoreSessionID";
	public static final String SESSION_INCLUDE_ALL_PARAMETERS = ",dataList\\[\\d+\\]\\.*";

	private static final Predicate<RMIResponse<String>> SUCCESS_FAIL_RESULT_PREDICATE = rmiResponse -> {
        if (rmiResponse.isErrorOccurred()) {
            return false;
        }
        String response = rmiResponse.getResponse();
        if (JMXConstant.SUCCESS.equalsIgnoreCase(response) == false || JMXConstant.PARTIAL_SUCCESS.equalsIgnoreCase(response) == false) {
            return false;
        }
        return true;
    };

	@SkipValidation
	@Override
	public String search() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called search()");
		}

		try {

			if (getSessionSearchField() != null) {
			setCriteriaJson(GsonFactory.defaultInstance().toJson(getSessionSearchField()));
			}

			if(sessionSearchField!=null) {
				SessionSearchAttributes sessionSearchAttributes = sessionSearchField.getSessionAttribute();
				String sessionAttribtueValue = sessionSearchField.getAttributeValue();


					if(Strings.isNullOrBlank(sessionAttribtueValue)==false) {
						switch(sessionSearchAttributes) {
							case SUBSCIBER_IDENTITY:
								SubscriberDAO.getInstance().checkForUserAuthorizationBySubscriberIdentity(sessionAttribtueValue, getStaffData(), ACLAction.SEARCH);
								break;

							case ALTERNATE_IDENTITY:
								SubscriberDAO.getInstance().checkForUserAuthorizationByAlternateIdentity(sessionAttribtueValue, getStaffData(), ACLAction.SEARCH);
								break;

							case CORE_SESSION_ID:
							List<SessionData> sessionDatas = findSessionFromCoreSessionId(getAttributeValueToSet(sessionAttribtueValue,true));
								String subscriberIdentity = getSubscriberIdentityFromSessionData(sessionDatas);
								SubscriberDAO.getInstance().checkForUserAuthorizationBySubscriberIdentity(subscriberIdentity, getStaffData(), ACLAction.SEARCH);
								break;

							case SESSION_IP:
								sessionDatas = convertSessionDataImplToSessionData(sessionUtil.getSessionBySessionIP(sessionAttribtueValue));
								subscriberIdentity = getSubscriberIdentityFromSessionData(sessionDatas);
								SubscriberDAO.getInstance().checkForUserAuthorizationBySubscriberIdentity(subscriberIdentity, getStaffData(), ACLAction.SEARCH);
								break;
						default:
							break;
						}
					}
			}

				} catch (OperationFailedException e) {
					setCriteriaJson(null);
					getLogger().info(MODULE, "Failure in searching Session Information. Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
					getLogger().trace(MODULE, e);
			}
		} catch(UnauthorizedActionException ex){
			setCriteriaJson(null);
			getLogger().error(MODULE, "Session search cannot be performed. Session search operation is not Allowed to :"+getStaffData().getUserName());
			getLogger().trace(MODULE,ex);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, ex.getMessage());

		} catch (Exception e) {
			setCriteriaJson(null);
			addActionError(Discriminators.SESSION + " " +getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			getLogger().error(MODULE,"Failed to set search criteria. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}
		return Results.LIST.getValue();
	}


	private String getSubscriberIdentityFromSessionData(List<SessionData> sessionDatas){
		if( Collectionz.isNullOrEmpty(sessionDatas) == false ){
			for(SessionData data : sessionDatas) {
				if (data != null) {
					Map<String, String> sessionInfo = data.getSessionInfo();
					if (Maps.isNullOrEmpty(sessionInfo) == false) {
						String subscriberIdentity = sessionInfo.get(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal());
						if(subscriberIdentity!=null){
							return subscriberIdentity;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	protected List<SessionData> getSearchResult (String criteriaJson,Class<SessionData> beanType, int startIndex, int maxRecords,String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception {
		List<SessionData> sessionDatas = Collectionz.newArrayList();
		if (Strings.isNullOrEmpty(criteriaJson)) {
			if(getLogger().isDebugLogLevel()){
			    getLogger().debug(MODULE,"Unable to find Session Data.Reason: No criteria found");
			}
			return sessionDatas;
		}
		Gson gson = GsonFactory.defaultInstance();
		SessionSearchField sessionSearchFields = gson.fromJson(criteriaJson, SessionSearchField.class);
		Boolean isPlainText = Boolean.parseBoolean(request.getParameter("isPlainText"));

		if (sessionSearchFields == null || Strings.isNullOrEmpty(sessionSearchFields.getAttributeValue())) {
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE,"Unable to find Session Data. Reason: No Attribute found in Criteria");
			}
			return sessionDatas;
		}

		switch (sessionSearchFields.getSessionAttribute()) {
			case ALTERNATE_IDENTITY:
				return getSessionDataByAlternateId(sessionDatas, sessionSearchFields);
			case CORE_SESSION_ID:
				return findSessionFromCoreSessionId(getAttributeValueToSet(sessionSearchFields.getAttributeValue(),isPlainText));
			case SESSION_IP:
				return convertSessionDataImplToSessionData(sessionUtil.getSessionBySessionIP(sessionSearchFields.getAttributeValue()));
			case SUBSCIBER_IDENTITY:
				return convertSessionDataImplToSessionData(sessionUtil.getSessionBySubscriberIdentity(SubscriberDAO.getInstance().getStrippedSubscriberIdentity(sessionSearchFields.getAttributeValue()), null));
		}

		return sessionDatas;

	}

	private List<SessionData> getSessionDataByAlternateId(List<SessionData> sessionDatas, SessionSearchField sessionSearchFields) throws OperationFailedException {
		StaffData staffData = (StaffData) request.getSession().getAttribute(Attributes.STAFF_DATA);
		String subscriberId = SubscriberDAO.getInstance().getSubscriberIdByAlternateId(sessionSearchFields.getAttributeValue(), staffData);
		if (Strings.isNullOrEmpty(subscriberId)) {
            return sessionDatas;
        }
		return convertSessionDataImplToSessionData(sessionUtil.getSessionBySubscriberIdentity(subscriberId, null));
	}

	private HashSet<String> getAttributeValueToSet(String attributeValue, Boolean isActualValue) throws UnsupportedEncodingException {

		String value = attributeValue;
		List<String> attrbuteValues =  new ArrayList<String>();

		if(isActualValue == false) {

			//Required Decoding when Load IMS Session (In Load IMS Session we are encoding coreSessionIds , so need to decode it while searching)
			value = URLDecoder.decode(value, CommonConstants.UTF_8);
			attrbuteValues = CommonConstants.PIPE_SPLITTER.split(value);
		} else {
			attrbuteValues.add(value);
		}

		//Converting into set because do not need to display duplicate session value.
		return new HashSet<String>(attrbuteValues);

	}


	private List<SessionData> findSessionFromCoreSessionId(HashSet<String> attributeValueSet) {
		List<SessionData> sessionDatas = Collectionz.newArrayList();

		for (String coreSessionId : attributeValueSet) {
			RMIResponse<SessionInformation> rmiResponse = sessionUtil.getSessionByCoreSessionId(coreSessionId);

			if (rmiResponse == null) {
				continue;
			}
			SessionData data = SessionDataUtility.from(rmiResponse.getResponse());
			data.setInstanceData(rmiResponse.getInstanceData());
			sessionDatas.add(data);
		}
		return sessionDatas;
	}

	@Override
	public String getIncludeProperties(){

		return SESSION_INCLUDE_PARAMETERS;
	}

	private List<SessionData> convertSessionDataImplToSessionData (Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationRMIResponseMap) {

		List<SessionData> sessions = new ArrayList<SessionData>();
		if(Maps.isNullOrEmpty(sessionInformationRMIResponseMap)){
			return sessions;
		}
		for (Map.Entry<SessionInformation,RMIResponse<Collection<SessionInformation>>> sessionDataEntry : sessionInformationRMIResponseMap.entrySet()) {
			SessionInformation sessionInformation = sessionDataEntry.getKey();
			SessionData data = SessionDataUtility.from(sessionInformation);
			data.setInstanceData(sessionDataEntry.getValue().getInstanceData());
			sessions.add(data);
		}
		return sessions;
	}

	@SkipValidation
	public String viewDetail() {
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called viewDetail()");
		}
		try{

			String tableId = request.getParameter(Attributes.TABLE_ID);
			String rowData = request.getParameter(Attributes.ROW_DATA+tableId);

			if(isValidRowData(rowData)){
				return Results.SUBTABLEURL.getValue();
			}
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE,"String received at viewDetail at: "+rowData);
			}
			Gson gson = GsonFactory.defaultInstance();
			sessionData = gson.fromJson(rowData, SessionData.class);
			String coreSessionId = sessionData.getSessionInfo().get(PCRFKeyConstants.CS_CORESESSION_ID.getVal());
			sessionSearchField = new SessionSearchField();
			sessionSearchField.setSessionAttribute(SessionSearchAttributes.CORE_SESSION_ID);
			sessionSearchField.setAttributeValue(coreSessionId);

			List<SessionData> sessionDatas = findSessionFromCoreSessionId(getAttributeValueToSet(sessionSearchField.getAttributeValue(),true));
			if(Collectionz.isNullOrEmpty(sessionDatas)) {
				sessionData = new SessionData();
			} else {
				sessionData = sessionDatas.get(0);
			}
			//Here need to set because need to display all the information of session
			setIncludeSearchProperties(SESSION_INCLUDE_ALL_PARAMETERS);
			return Results.SUBTABLEURL.getValue();
		}catch(Exception e){
			getLogger().error(MODULE,"Error while fetching session data. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			return Results.ERROR.getValue();
		}

	}

	private boolean isValidRowData(String rowData) {
		return Strings.isNullOrEmpty(rowData) || CommonConstants.NULL_STRING.equalsIgnoreCase(rowData.trim()) || CommonConstants.UNDEFINED_STRING.equalsIgnoreCase(rowData.trim());
	}

	public String disconnect() {

		if (getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Method called disconnect()");

		String fromSubscriberView = request.getParameter(Attributes.FROM_VIEW_PAGE);
		String instanceIdCode = request.getParameter(Attributes.INSTANCE_ID_CODE);
		String coreSessionID = request.getParameter(Attributes.CORE_SESSION_ID);

		try {

			if (Strings.isNullOrBlank(coreSessionID) == false && Strings.isNullOrBlank(instanceIdCode) == false) {

				checkAuthorization(coreSessionID, ACLAction.DISCONNECT);

				RMIResponse<String> disconnectSessionByCoreSessionIdResponse = RemoteMessageCommunicator.callSync(new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.SESSIONS_DISCONNECT_BY_CORE_SESSION_ID, coreSessionID, HTTPMethodType.GET),
						RMIGroupManager.getInstance().getRMIGroupFromServerCode(instanceIdCode), instanceIdCode);


				if (disconnectSessionByCoreSessionIdResponse != null) {
					addActionMessage("Session Disconnected Successfully");
					if (getLogger().isInfoLogLevel()) {
						getLogger().info(MODULE, "Session Disconnected Successfully");
					}
				}
			} else {
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, CORE_SESSION_ID_OR_INSTANCE_ID_NOT_FOUND);
			}

		} catch(UnauthorizedActionException ex){
			getLogger().error(MODULE, "Session cannot be disconnect for coreSessionID: "+coreSessionID+". Session disconnect operation is not Allowed to :"+getStaffData().getUserName());
			getLogger().trace(MODULE,ex);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, ex.getMessage());

		} catch (Exception e) {
			getLogger().error(MODULE,"Error while disconnecting session data. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
		}

		if(Strings.isNullOrBlank(fromSubscriberView) == false){
			setActionChainUrl(SUBSCRIBER_VIEW + "#section3");
			return Results.REDIRECT_ACTION.getValue();
		}

		return Results.LIST.getValue();
	}

	private void checkAuthorization(String coreSessionID, ACLAction aclAction) throws OperationFailedException, UnauthorizedActionException, UnsupportedEncodingException {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Checking Authorization for action: " + aclAction.getVal());
		}

		SessionSearchField searchField = new SessionSearchField();
		searchField.setSessionAttribute(SessionSearchAttributes.CORE_SESSION_ID);
		searchField.setAttributeValue(coreSessionID);

		List<SessionData> sessionDatas = findSessionFromCoreSessionId(getAttributeValueToSet(searchField.getAttributeValue(), true));
		String subscriberIdentity = getSubscriberIdentityFromSessionData(sessionDatas);

        SubscriberDAO.getInstance().checkForUserAuthorizationBySubscriberIdentity(subscriberIdentity, getStaffData(), aclAction);

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, aclAction.getVal() + " action is Permitted");
		}
	}

	public String delete() {

		if (getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Method called delete()");

		String fromSubscriberView = request.getParameter(Attributes.FROM_VIEW_PAGE);
		String instanceIdCode = request.getParameter(Attributes.INSTANCE_ID_CODE);
		String coreSessionID = request.getParameter(Attributes.CORE_SESSION_ID);

		try {

			if (Strings.isNullOrBlank(coreSessionID) == false && Strings.isNullOrBlank(instanceIdCode) == false) {

				checkAuthorization(coreSessionID, ACLAction.DELETE);

				RMIResponse<String> removeSessionByCoreSessionIdResponse = RemoteMessageCommunicator.callSync(new RemoteMethod(RemoteMethodConstant.NETVERTEX_SESSION_REST_BASE_URI_PATH, RemoteMethodConstant.REMOVE_SESSION_BY_CORE_SESSION_ID, coreSessionID, HTTPMethodType.GET),
						RMIGroupManager.getInstance().getRMIGroupFromServerCode(instanceIdCode), instanceIdCode);

				if (removeSessionByCoreSessionIdResponse != null) {
					addActionMessage("Session Deleted Successfully");
					if (getLogger().isInfoLogLevel()) {
						getLogger().info(MODULE, "Session Deleted Successfully");
					}
				}

			} else {
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, CORE_SESSION_ID_OR_INSTANCE_ID_NOT_FOUND);
			}

		} catch(UnauthorizedActionException ex){
			getLogger().error(MODULE, "Session cannot be deleted for coreSessionID: "+coreSessionID+". Session deleted operation is not Allowed to :"+getStaffData().getUserName());
			getLogger().trace(MODULE,ex);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, ex.getMessage());

		} catch (Exception e) {
			getLogger().error(MODULE,"Error while deleting session data. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
		}

		if(Strings.isNullOrBlank(fromSubscriberView) == false){
			setActionChainUrl(SUBSCRIBER_VIEW + "#section3");
			return Results.REDIRECT_ACTION.getValue();
		}

		return Results.LIST.getValue();

	}


	public String reauth() {

		if (getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Method called reauth()");

		String instanceIdCode = request.getParameter(Attributes.INSTANCE_ID_CODE);
		String coreSessionID = request.getParameter(Attributes.CORE_SESSION_ID);
		try {

			if (Strings.isNullOrBlank(coreSessionID) == false && Strings.isNullOrBlank(instanceIdCode) == false) {
				checkAuthorization(coreSessionID, ACLAction.REAUTH);
				ReAuthUtil.doReAuthByCoreSessionId(coreSessionID,instanceIdCode);
			} else {
				if (getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, CORE_SESSION_ID_OR_INSTANCE_ID_NOT_FOUND);
			}

		}catch (OperationFailedException e){
			getLogger().error(MODULE,"Error while ReAuth Session. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(e.getMessage());
		}catch(UnauthorizedActionException ex){
			getLogger().error(MODULE, "Session cannot be ReAuth for coreSessionID: "+coreSessionID+". Session ReAuth operation is not Allowed to :"+getStaffData().getUserName());
			getLogger().trace(MODULE,ex);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, ex.getMessage());

		} catch (Exception e) {
			getLogger().error(MODULE,"Error while ReAuth Session. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
		}
		return Results.LIST.getValue();
	}

	@Override
	public SessionData getModel() {
		return sessionData;
	}

	public SessionData getSessionData() {
		return sessionData;
	}

	public void setSessionData(SessionData sessionData) {
		this.sessionData = sessionData;
	}


	public SessionSearchField getSessionSearchField() {
		return sessionSearchField;
	}

	public void setSessionSearchField(SessionSearchField sessionSearchField) {
		this.sessionSearchField = sessionSearchField;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}
}
