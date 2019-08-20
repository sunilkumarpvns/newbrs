package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.generic.GenericSearchData;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * The {@link EliteGenericCTRL} is used as a base class for the search
 * functionality.
 * 
 * @author Dhyani.Raval
 *
 */
public abstract class EliteGenericCTRL<T> extends ActionSupport implements ServletRequestAware, ServletResponseAware ,ModelDriven<T>, Preparable {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = EliteGenericCTRL.class.getSimpleName();
	private GenericSearchData genericSearchData = new GenericSearchData();
	private String jspUrl = "/view/generic/Generic";
	private List<T> dataList = Collectionz.newArrayList();
	private String beanType;
	private int draw;
	private String recordsTotal;
	private String recordsFiltered;
	private String criteriaJson;
	private String includeSearchProperties;
	private int pages;
	public String groupIds = new String();
	private List<GroupInfo> groupInfoList;
	protected HttpServletRequest request;
	
	private String parentIdKey;
	private String parentIdValue;

	protected HttpServletResponse response;
	protected final Splitter SPLITTER = Strings.splitter(',').trimTokens();
	protected static final String INVALID_ENTITY_MESSAGE = "invalidEntityMessage";

	public String getParentIdValue() {
		return parentIdValue;
	}

	public void setParentIdValue(String parentIdValue) {
		this.parentIdValue = parentIdValue;
	}

	public String getParentIdKey() {
		return parentIdKey;
	}

	public void setParentIdKey(String parentIdKey) {
		this.parentIdKey = parentIdKey;
	}

	@SkipValidation
	public String setSearchCriteria() {
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE,"Method called setSearchCriteria()");
		}
		try {
			setCriteriaJson(GsonFactory.defaultInstance().toJson(getModel()));
			if(GenericSearchData.class.getSimpleName().equals(getModel().getClass().getSimpleName())) {
				GenericSearchData tempGlobalSearchData = (GenericSearchData) getModel();
				setJspUrl(tempGlobalSearchData.getPolicyDesignerModules().getJspUrl());
			}
			return Results.LIST.getValue();
		}catch(Exception e){
			addActionError(Discriminators.PACKAGE + " " +getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			LogManager.getLogger().error(MODULE,"Failed to set search criteria. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			return Results.LIST.getValue();
		}
	}

	public String searchData() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called searchData()");
		}

		String drawParam = request.getParameter("draw");
		String startParam = request.getParameter("start");
		String lengthParam = request.getParameter("length");
		String sortColumnOrder = request.getParameter("sortColumnOrder");
		String sortColumnName = request.getParameter("sortColumnName");
		String showPagination = request.getParameter("showPagination");
		criteriaJson = request.getParameter("criteriaVal");
		String cachedPages = request.getParameter("pages");
		if(Strings.isNullOrBlank(cachedPages)){
			pages = -1;
		}else{
			pages = Integer.parseInt(cachedPages);
		}

		try {
			int rows = ConfigurationProvider.getInstance().getPageRowSize();
			int drawTemp = Numbers.parseInt(drawParam, 0);

			int maxRecords = 0;
			int startIndex = 0;
			if (showPagination != null && showPagination.equalsIgnoreCase("true")) {
				maxRecords = Numbers.parseInt(lengthParam, rows * pages);
				startIndex = Numbers.parseInt(startParam, 0);
			}
			Class<T> beanType = (Class<T>) Class.forName(getBeanType());

			String staffBelongingGroups = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);

			setDataList(getSearchResult(criteriaJson, beanType, startIndex,maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups));

			if(Strings.isNullOrBlank(getIncludeProperties()) == false){
				setIncludeSearchProperties(NVSMXCommonConstants.DATATABLE_PARAMETERS + getIncludeProperties());
			}

			int listSize = dataList.size();
			draw = drawTemp;
			recordsTotal = String.valueOf(maxRecords);
			int totalFileterRecord = startIndex + listSize;
			recordsFiltered = String.valueOf(totalFileterRecord);

		} catch (Exception e) {
			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			getLogger().error(MODULE,"Error while fetching data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return Results.SEARCH_SUCCESS.getValue();

	}

	@SuppressWarnings({ "unchecked"})
	protected List<T> getSearchResult(String criteriaJson, Class<T> beanType,int startIndex, int maxRecords, String sortColumnName,String sortColumnOrder, String staffBelongingGroups) throws Exception{
		if (Strings.isNullOrEmpty(criteriaJson) == false) {
			Gson gson = GsonFactory.defaultInstance();
			Object object = gson.fromJson(criteriaJson, beanType);
			return (List<T>) CRUDOperationUtil.searchByCriteria(object,startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		} else {
			return (List<T>) CRUDOperationUtil.findAllWhichIsNotDeleted(beanType,startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		}
	}
	
	public String showProgressBar() {
		try {
			String redirectUrl = request.getParameter(Attributes.REDIRECT_URL);
			request.setAttribute(Attributes.REDIRECT_URL, redirectUrl);
		} catch (Exception e) {
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			getLogger().error(MODULE,"Error while showing progressbar. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return Results.PROGRESS_BAR.getValue();
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getBeanType() {
		return beanType;
	}

	public void setBeanType(String beanType) {
		this.beanType = beanType;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public String getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(String recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public String getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(String recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public String getCriteriaJson() {
		return criteriaJson;
	}

	public void setCriteriaJson(String criteriaJson) {
		this.criteriaJson = criteriaJson;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public String getIncludeSearchProperties() {
		return includeSearchProperties;
	}

	public void setIncludeSearchProperties(String includeSearchProperties) {
		this.includeSearchProperties = includeSearchProperties;
	}

	public abstract String getIncludeProperties();

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}

	public GenericSearchData getGenericSearchData() {
		return genericSearchData;
	}

	public void setGenericSearchData(GenericSearchData genericSearchData) {
		this.genericSearchData = genericSearchData;
	}
	
	@SkipValidation
	public String search() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called search()");
		}
		return Results.LIST.getValue();
	}

	public String getJspUrl() {
		return jspUrl;
	}

	public void setJspUrl(String jspUrl) {
		this.jspUrl = jspUrl;
	}

	protected boolean checkForImportExportOperation() {
		return  (Boolean) request.getServletContext().getAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING);

    }

	@SuppressWarnings("unchecked")
	@Override
	public T getModel() {
		return (T) genericSearchData;
	}

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
	protected String redirectError(String discriminator,String key,String result) {
		return redirectError(discriminator,key,result,true);
	}
	protected String redirectError(String discriminator,String key,String result,boolean displayLogText) {
		addActionError(discriminator + " " + getText(key));
		if(displayLogText){
			addActionError(getText("note.refer.logs"));
		}
		return result;
	}
	protected String redirectError(String discriminator,String key) {
		return   redirectError(discriminator, key, Results.REDIRECT_ERROR.getValue());
	}

	protected String generateErrorLogsAndRedirect(String discriminator, Exception e, String message, String key, String result) {
		getLogger().error(MODULE, message+" Reason: " + e.getMessage());
		getLogger().trace(MODULE, e);
		return redirectError(discriminator, key, result);
	}

	public String getStaffBelongingGroups(){
		return (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
	}

	public Map<String,RoleData> getStaffBelogingRoleMap(){
		return (HashMap<String, RoleData>) request.getSession().getAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP);
	}

	protected Set<GroupData> getStaffBelongingGroupDataSet(){
         return getStaffData().getGroupDatas();

	}

	protected StaffData getStaffData(){
		return (StaffData) request.getSession().getAttribute(Attributes.STAFF_DATA);
	}


	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	@Override
	public final void prepare() throws Exception {
		prepareForSubClass();
	}

	/**
	 * Override this method instead of implementing Preparable
	 * @throws Exception
	 */
	public void prepareForSubClass() throws Exception{

	}

	private List<String> getGroupsFromRequest() {
		if (request != null) {
			String[] groupNameArray = request.getParameterValues(Attributes.GROUPIDS);
			if (Arrayz.isNullOrEmpty(groupNameArray) == false) {
				if (groupNameArray.length == 1) {
					return com.elitecore.corenetvertex.constants.CommonConstants.COMMA_SPLITTER.split(groupNameArray[0]);
				} else {
					return Arrays.asList(groupNameArray);
				}
			}
		}
		return null;
	}


	public List<GroupInfo> getGroupInfoList() {
		if (groupInfoList == null) {
			List<String> groups = getGroupsFromRequest();
			if (Collectionz.isNullOrEmpty(groups)) {
				String groupids = getGroupIds();
				groups = com.elitecore.corenetvertex.constants.CommonConstants.COMMA_SPLITTER.split(groupids);
			}

			groupInfoList = GroupInfoSelectionUtil.getCombinedGroupInfoList((List<GroupData>) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP), groups);
		}
		return groupInfoList;
	}

	public void setGroupInfoList(List<GroupInfo> groupInfoList) {
		this.groupInfoList = groupInfoList;
	}

	protected String getGroupNames(ResourceData resourceData) {
		StringBuilder groupNames = new StringBuilder();
		List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(resourceData.getGroups());
		if(groupIds != null) {
			groupIds.sort((o1, o2) -> (o1.compareTo(o2)));
			for (String groupId : groupIds) {
				GroupData groupData = CRUDOperationUtil.get(GroupData.class, groupId.trim());
				if (Objects.isNull(groupData)) {
					addActionError("Group Doesn't exist for Id: " + groupId);
					return null;
				}
				groupNames.append(groupData.getName()).append(",");
			}
			groupNames.deleteCharAt(groupNames.lastIndexOf(","));
		}
		return groupNames.toString();
	}
}