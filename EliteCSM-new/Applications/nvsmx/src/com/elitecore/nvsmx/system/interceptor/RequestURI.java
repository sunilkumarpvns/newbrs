package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class that contains the information related to Request URI passed from UI. It contains action name, method name, requestURI path
 */
public abstract class RequestURI {
	private static final String MODULE = "REQ-URI";
	public static final String SUBSCRIBER = "SUBSCRIBER";
	public static final String SESSION = "SESSION";
	protected String[] uriParts;
	protected String methodName;
	protected String moduleName;
	protected String requestURIPath;
	protected String actualMethodName;
	protected HttpServletRequest request;
	private String defaultPermittedActions = "VIEW,LOGIN,EXECUTE,SEARCH,LOGOUT,WELCOME,INITLOGIN,GETDEFAULTSERVICEFLOW,SETSEARCHCRITERIA,INIT";

	public RequestURI() {

	}

	public RequestURI(HttpServletRequest request) {
		this.request = request;
		this.requestURIPath = request.getRequestURI();
		this.uriParts = requestURIPath.split("/");
		this.methodName = ACLAction.fromVal(uriParts[getLastIndex()].replace(".action", "").toUpperCase());
	}


	public String getMethodName() {
		return this.methodName;
	}

	public int getLastIndex() {
		if (uriParts != null && uriParts.length >= 2) {
			return uriParts.length - 1;
		}
		return -1;
	}


	public String getModuleName() {
		moduleName = uriParts[getLastIndex() - 1];
		actualMethodName = uriParts[getLastIndex()];
		return moduleName;
	}

	public String getRequestURIPath() {
		return this.requestURIPath;
	}

	public void setRequestURIPath(String requestURIPath) {
		this.requestURIPath = requestURIPath;
	}

	public String getActualMethodName() {
		return actualMethodName;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public boolean isEligible() {

		if ( Strings.isNullOrEmpty(getRequestURIPath()) == true ) {
			return false;
		}

		if ( getModuleName().equalsIgnoreCase(SUBSCRIBER) == true ) {
			return false;
		}

		if ( getModuleName().equalsIgnoreCase(SESSION) == true ) {
			return false;
		}

		return true;
	}

	protected abstract boolean isAuthorizedAction(ACLModules module);

	public boolean isAuthorized() {
		try {
			if (getLastIndex() != -1) {
				String requestMethodName = getMethodName();
				String requestModuleName = getModuleName();
				List<String> defaultPermittedActionList = CommonConstants.COMMA_SPLITTER.split(defaultPermittedActions);
				if (defaultPermittedActionList.contains(requestMethodName)) {
					LogManager.getLogger().info(MODULE, "Default Actions permitted : " + requestMethodName);
					return true;
				}

				/* currently check is provided on service type. Service type CRUD Operation is restricted for all the users. Service
				* Type will be managed by super admin only*/
				String serviceType = "DataServiceType";
				if(Strings.isNullOrBlank(requestModuleName) == false && serviceType.equalsIgnoreCase(requestModuleName)){
					String message = Discriminators.DATA_SERVICE_TYPE +" " + this.methodName + " is allowed for Super Admin only";
					LogManager.getLogger().warn(MODULE, message);
					request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, message);
					return false;
				}

				ACLModules modconstant = ACLModules.fromModuleName(requestModuleName);

				if (modconstant == null) {
					LogManager.getLogger().warn(MODULE, "Allowing for Module " + requestModuleName + " as no ACL define with this name");
					return true;
				}

				//FIXME need to provide via requestURI -- ishani.bhatt
				if(("ATTACHPCCRULE").equals(requestMethodName) || ("ADDAPPLICABLEQOSPROFILES").equals(requestMethodName) || ("ATTACHCHARGINGRULEBASENAME").equals(requestMethodName) || ("REMOVECHARGINGRULEBASENAME").equals(requestMethodName)){
					this.methodName = ACLAction.UPDATE.name();
				}

				boolean isAuthorizedAction = isAuthorizedAction(modconstant);
				if (isAuthorizedAction == false) {
					return false;
				}
			}
			return true;

		} catch (Exception ex) {
			request.setAttribute(Attributes.ACTION, NVSMXCommonConstants.WELCOME_URL);
			LogManager.getLogger().warn(MODULE, "Error while Authorization check. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return false;
		}


	}
	protected Map<String,RoleData> getStaffBelongingRoleMap(){
		return (Map<String,RoleData>) request.getSession().getAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP);
	}


	protected Set<GroupData> getStaffBelongingGroupData(){

		return getStaffData().getGroupDatas();
	}

	private StaffData getStaffData() {
		return  (StaffData) request.getSession().getAttribute(Attributes.STAFF_DATA);
	}

	protected String getStaffBelongingGroups(){
		return (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
	}
}
