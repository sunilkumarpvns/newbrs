package com.elitecore.nvsmx.system.interceptor;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.util.ReflectionUtil;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.NewGroupPredicate;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Authorization Interceptor will defines action performed by user is authorized or not. It authorize the staff based on roles provided
 * for specific groups.
 * @author ishani bhatt
 * 
 */

public class AuthorizationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "AUTHORIZATION-INTCPTR";
	private static final String BLOCK_IP_CANNOT_PROCEED = "blocked.ip.cannot.proceed";

	private String ip;
	private HttpServletRequest request;

    @Override
    public String doIntercept(ActionInvocation actionInvocation) throws Exception {
		final ActionContext context = actionInvocation.getInvocationContext();
		request = (HttpServletRequest) context.get(StrutsStatics.HTTP_REQUEST);
	
		ip = request.getRemoteAddr();
		
		ValidationAware action = (ValidationAware) actionInvocation.getAction();
		if (isIPBlocked()) {
		    action.addActionError(((ActionSupport) action).getText(BLOCK_IP_CANNOT_PROCEED));
		    return Results.LOGIN.getValue();
		}
		RequestURI requestURI = RequestURIFactory.createRequestURI(request);
		String username = (String) request.getSession().getAttribute(Attributes.STAFF_USERNAME);


		if (NVSMXCommonConstants.ADMIN.equalsIgnoreCase(username) == false) {
		    // check the authorization/access-rights if the user is not Admin
			if (requestURI.isEligible() == true && requestURI.isAuthorized() == false) {

				    String lastURI = (String)request.getSession().getAttribute(Attributes.LAST_URI);
					Object object = actionInvocation.getAction();
					List<Method> setActionChainUrlMethodList = ReflectionUtil.getDeclaredMethodsAnnotatedWith(object.getClass(),ActionChain.class);
					for(Method method : setActionChainUrlMethodList){
					object = method.invoke(object, lastURI);
					}
				return Results.DISPATCH_TO_PARENT.getValue();

			    }
		    }
		if (requestURI != null && (requestURI.getRequestURIPath().contains("view") == true || (requestURI.getRequestURIPath().contains("init")) == true || requestURI.getRequestURIPath().contains("search") == true)) {
		    requestURI.setRequestURIPath(requestURI.getRequestURIPath().replace(request.getContextPath() + "/", ""));
		    request.getSession().setAttribute(Attributes.LAST_URI,requestURI.getRequestURIPath());
		}

		return actionInvocation.invoke();
    }

	private boolean isIPBlocked() {
		Long releaseTime = getBlockedIPreleaseTimeMap().get(ip);
		boolean flag = true;
		if (releaseTime == null) {
			flag = false;
		} else if (releaseTime > System.currentTimeMillis()) {
			flag = true;
		} else {
			getBlockedIPreleaseTimeMap().remove(ip);
			request.getSession().setAttribute(Attributes.CAPTCHA_FAILURE_COUNT, 0);
			flag = false;
		}
		return flag;
	}
	private Map<String, Long> getBlockedIPreleaseTimeMap(){
		return (Map<String, Long>) request.getServletContext().getAttribute(Attributes.BLOCKED_IP_RELEASE_TIME_MAP);
	}

	/**
	 * This class will authorize a single action performed by user like create, update, single export and import
	 */
	private static class RequestURISingle extends RequestURI {
		List<String> groups;

		public RequestURISingle(HttpServletRequest request){
			super(request);
			groups = Collectionz.newArrayList();
		}

		@Override
		protected boolean isAuthorizedAction(ACLModules module) {

			List<String> entityGroups = getGroups();
			List<String> oldGroups = CommonConstants.COMMA_SPLITTER.split(request.getParameter(Attributes.ENTITY_OLD_GROUPS));

			List<String> notAllowedGroups = findNotAllowedGroups(module, methodName, entityGroups, oldGroups);

			if (Collectionz.isNullOrEmpty(notAllowedGroups) == false) {
				String groupNames = GroupDAO.getGroupNames(notAllowedGroups);
				String message = module.getDisplayLabel() + " '" + methodName + "' not allowed for '" + groupNames + "' Group";
				LogManager.getLogger().warn(AuthorizationInterceptor.MODULE, message);
				request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, message);
				return false;
			}

			return true;
		}

		private List<String> findNotAllowedGroups(ACLModules module, String methodName,
												  List<String> entityGroups,
												  List<String> oldGroups) {

			List<String> notAllowedGroups = Collectionz.newArrayList();
			SingleOperationAuthorizedHandler authorizedHandler = SingleOperationAuthorizedHandler.getHandler();

			//Remove init check after all initCreate and initUpdate methods are merged. - jaidip trivedi
			if (actualMethodName.contains("init") == false && actualMethodName.contains("exportAll") == false && actualMethodName.contains("import") == false && actualMethodName.contains("manageOrder") == false ) {

				if (Collectionz.isNullOrEmpty(oldGroups) == false) {

					if (authorizedHandler.isAuthorizedForAnyGroup(oldGroups, getStaffBelongingRoleMap(), module.name(), methodName) == false) {
						return oldGroups;
					}
					List<GroupData> staffBelongingGroupDatas = (List<GroupData>) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP);
					notAllowedGroups.addAll(authorizedHandler.checkOldGroups(staffBelongingGroupDatas, entityGroups, oldGroups));

				}

				List<String> newGroups = Lists.copy(entityGroups, new NewGroupPredicate(oldGroups));
				notAllowedGroups.addAll(authorizedHandler.isAuthorizedForAllGroup(newGroups, getStaffBelongingRoleMap(), module.name(), methodName));

			}
			return notAllowedGroups;
		}

		/**
		 * Get groups from request.
		 * @return
		 */
		public List<String> getGroups(){

			String[] groupNameArray = request.getParameterValues(Attributes.GROUPIDS);
			List<String> groups = Collectionz.newArrayList();
			if(Arrayz.isNullOrEmpty(groupNameArray) == false){
				if(groupNameArray.length == 1){
					groups = CommonConstants.COMMA_SPLITTER.split(groupNameArray[0]);
				} else{
					groups = Arrays.asList(groupNameArray);
				}
			} else if (actualMethodName.contains("init") || actualMethodName.contains("exportAll") || actualMethodName.contains("import")) {
				String groupNames = getStaffBelongingGroups();
				groups = CommonConstants.COMMA_SPLITTER.split(groupNames);
			}else{
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}

			return groups;
		}

	}


	/**
	 * This class is used to authorize action based on multiple entity provided. For Multiple Delete, export, this class is used.
	 */
	private static class RequestURIMultiple extends RequestURI {

		Map<String, List<String>> groups;

		public RequestURIMultiple(HttpServletRequest request){
			super(request);
			groups = new HashMap<String, List<String>>();
		}

		@Override
		protected boolean isAuthorizedAction(ACLModules modules) {
			Map<String,String> notAllowedGroupNames = new HashMap<String, String>();
			boolean isAuthorizedAction= MultipleOperationAuthorizedHandler.getHandler().doAuthorize(getGroups(),getStaffBelongingGroups(),getStaffBelongingRoleMap(),getStaffBelongingGroupData(),modules.name(),methodName,notAllowedGroupNames);
			if(isAuthorizedAction){
				return true;
			}
			String message = modules.getDisplayLabel() + " '" + methodName + "' not allowed for '" + notAllowedGroupNames.values() + "' Group";
			LogManager.getLogger().warn(AuthorizationInterceptor.MODULE, message);
			request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, message);
			return false;
		}

		private Map<String, List<String>> getGroups() {
			String[] ids = request.getParameterValues("ids");
			if (ids != null && ids.length > 0) {
				for (String id : ids) {
					List<String> groupList = new ArrayList<String>();
					String groupId = request.getParameter(id);
					if ("undefined".equalsIgnoreCase(groupId) == false) {
						groupList.add(groupId);
					}
					groups.put(id, groupList);
				}
			}else{
				String[] groupNameArray = request.getParameterValues(Attributes.GROUPIDS);
				if(Arrayz.isNullOrEmpty(groupNameArray)==false){
					List<String> groupList = new ArrayList<String>();
					groupList.add(Strings.join(",",groupNameArray));
					groups.put("GROUPS",groupList);
				}
			}
			return groups;
		}

	}

	/**
	 * A factory that will instantiate the Object based on the method provided in RequestURI.
	 */
	private static class RequestURIFactory {

		public static RequestURI createRequestURI(HttpServletRequest request) {
			if (request.getRequestURI().contains("delete")  || request.getRequestURI().contains("export")) {
				return new RequestURIMultiple(request);
			}else{
				return new RequestURISingle(request);
			}
		}
	}
}





