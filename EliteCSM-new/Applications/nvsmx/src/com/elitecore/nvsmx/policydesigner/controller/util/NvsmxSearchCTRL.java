package com.elitecore.nvsmx.policydesigner.controller.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.corenetvertex.sm.audit.AuditData;
import com.elitecore.nvsmx.system.constants.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditCriteriaData;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.audit.AuditDAO;
import com.elitecore.nvsmx.commons.model.audit.AuditDataWrapper;
import com.elitecore.nvsmx.commons.model.audit.AuditDataWrapper.AuditDataWrapperBuilder;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileWrapper.QuotaProfileWrapperBuilder;
import com.elitecore.nvsmx.system.ConfigurationProvider;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author kirpalsinh.raj Common controller class for the search feature of all
 *         modules.
 * 
 */
public class NvsmxSearchCTRL extends ActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = NvsmxSearchCTRL.class.getSimpleName();
	private HttpServletRequest request;
	private List dataList = Collectionz.newArrayList();
	private int draw;
	private String recordsTotal;
	private String recordsFiltered;
	private String beanType;
	private String errorMessage;
	private int pages;


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String execute() {
	    LogManager.getLogger().debug(MODULE, "Method called execute()");
	    
	    String drawParam = request.getParameter("draw");
	    String startParam = request.getParameter("start");
	    String lengthParam = request.getParameter("length");
	    String sortColumnOrder = request.getParameter("sortColumnOrder");	
	    String sortColumnName = request.getParameter("sortColumnName");
	    
	    String criteria = request.getParameter("criteriaVal");
	    String fupLevel = request.getParameter("fupLevel");
	    String quotaProfileId = request.getParameter("quotaProfileId");
	    String showPagination = request.getParameter("showPagination");
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
			if(showPagination!=null && showPagination.equalsIgnoreCase("true")){
				maxRecords = Numbers.parseInt(lengthParam, rows * pages);
				startIndex = Numbers.parseInt(startParam, 0);
			}
			
			Class beanType = Class.forName(getBeanType());
			LogManager.getLogger().debug(MODULE,"BeanType : " + beanType.getSimpleName());
			
			String staffBelongingGroups = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
			
			if(beanType.getSimpleName().contains(QosProfileDetailWrapper.class.getSimpleName())) {
			   qosProfilesList(sortColumnOrder, sortColumnName);
			   
			} else if(beanType.getSimpleName().contains(QuotaProfileWrapper.class.getSimpleName())) {
			    quotaProfilesList(sortColumnOrder, sortColumnName);
			    
			} else if (beanType.getSimpleName().contains(PccRuleWrapper.class.getSimpleName())) {
				getPccRuleList();
				
			} else if((Strings.isNullOrEmpty(fupLevel) == false) && (Strings.isNullOrEmpty(quotaProfileId) == false)){
				
			    QuotaProfileData quotaprofile = new QuotaProfileData();
			    quotaprofile.setId(quotaProfileId);
			    List<QuotaProfileDetailData> quotaProfileDetails = getQuotaProfileDetails(QuotaProfileDAO.getFupLevelQuotaProfileDetails(beanType,fupLevel,quotaprofile));
			    dataList = new ArrayList();
			    for (QuotaProfileDetailData quotaProfileDetail : quotaProfileDetails) {
			    	quotaProfileDetail.setServiceId(quotaProfileDetail.getDataServiceTypeData().getName());
			    	dataList.add(quotaProfileDetail);
			    }
			    
			} else if (beanType.getSimpleName().contains(AuditDataWrapper.class.getSimpleName())){
				
				List<AuditData> auditDatas = Collectionz.newArrayList();
				if(Strings.isNullOrEmpty(criteria) == false){
					Gson gson = GsonFactory.defaultInstance();
					AuditCriteriaData auditCriteriaData = gson.fromJson(criteria, AuditCriteriaData.class);
					auditDatas = AuditDAO.searchCriteria(auditCriteriaData, startIndex, maxRecords, sortColumnName, sortColumnOrder);
				}else{
					auditDatas = AuditDAO.findAll(startIndex, maxRecords, sortColumnName, sortColumnOrder);
				}
				getAuditDatas(auditDatas,startIndex,maxRecords,sortColumnName,sortColumnOrder);
				
			} else {
				
				if(Strings.isNullOrEmpty(criteria) == false){
					Gson gson = GsonFactory.defaultInstance();
					Object object = gson.fromJson(criteria, beanType);
					dataList = CRUDOperationUtil.searchByCriteria(object, startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
				}else{
					dataList = CRUDOperationUtil.findAllWhichIsNotDeleted(beanType, startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
				}
				
			}
			
			int listSize = dataList.size();
			draw = drawTemp;
			recordsTotal = String.valueOf(maxRecords);

			int totalFileterRecord = startIndex + listSize;
			recordsFiltered = String.valueOf(totalFileterRecord);
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE,"Failed to search data. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);

			addActionError(getText(ActionMessageKeys.SEARCH_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.ERROR.getValue();
		}

		return Results.PKG_SUCCESS.getValue();
	}

	/**
	 * It will convert all the audit data list into AuditDataWrapper
	 * @param auditDatas
	 * @param startIndex
	 * @param maxRecords
	 * @param sortColumnName
	 * @param sortColumnOrder
	 */
	private void getAuditDatas(List<AuditData> auditDatas,int startIndex, int maxRecords , String sortColumnName , String sortColumnOrder) {
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called getAuditDatas()");
		}
		List<AuditDataWrapper> auditDataWrappers = Collectionz.newArrayList();
		for(AuditData auditData : auditDatas){
			AuditDataWrapper auditDataWrapper = new AuditDataWrapperBuilder().withAuditDatas(auditData).build();
			auditDataWrappers.add(auditDataWrapper);
		}
		dataList = auditDataWrappers;
	}

	/**
	 * get PccrRules of respective QosProfileDetail from database  
	 */
	private void getPccRuleList() {
		 LogManager.getLogger().info(MODULE, "Method called getPccRuleList()");
		 String qosProfileDetailId = request.getParameter("qosProfileDetailId");
		 if(Strings.isNullOrEmpty(qosProfileDetailId) == false){
			 QosProfileDetailData qosProfileDetail =  new QosProfileDetailData();
			 qosProfileDetail.setId(qosProfileDetailId);
			 dataList = QosProfileDAO.getPccRules(qosProfileDetail);
		 }
	}

	private void qosProfilesList(String sortColumnOrder, String sortColumnName) {
	    LogManager.getLogger().info(MODULE, "Method called qosProfilesList()");
	    
	    String pkgId= request.getParameter("pkgId");
	    
	    if (Strings.isNullOrEmpty(pkgId) == false ){
		PkgData pkgData = new PkgData();
	    	pkgData.setId(pkgId);	
	    	dataList = QosProfileDAO.getQosProfileDetailWrappers(pkgData, sortColumnName, sortColumnOrder);	    
	    }	    
	}
	
	/**
	 * get QuotaProfileDetails of which service type is not "Default-Service" or aggregation key is not "Billing Cycle " 
	 * @param quotaProfileDetails
	 * @return List<QuotaProfileDetail>
	 */
	private List<QuotaProfileDetailData> getQuotaProfileDetails(List<QuotaProfileDetailData> quotaProfileDetails) {
		List<QuotaProfileDetailData> profileDetails = new ArrayList<QuotaProfileDetailData>();
		for (QuotaProfileDetailData quotaProfileDetail : quotaProfileDetails) {
			String Key = quotaProfileDetail.getAggregationKey();
			String serviceId = quotaProfileDetail.getDataServiceTypeData().getId();
			if ((AggregationKey.BILLING_CYCLE.name().equalsIgnoreCase(Key) == false) || (CommonConstants.ALL_SERVICE_ID.equalsIgnoreCase(serviceId) == false)) {
				profileDetails.add(quotaProfileDetail);
			}
		}
		return profileDetails;
	}
	
	private void quotaProfilesList(String sortColumnOrder, String sortColumnName) {
	    String pkgId= request.getParameter("pkgId");	    
	    LogManager.getLogger().info(MODULE, "Method called  quotaProfilesList()");

	    if (Strings.isNullOrEmpty(pkgId) == false ){
		PkgData pkgData = new PkgData();
	    	pkgData.setId(pkgId);
	    	
        	List<QuotaProfileData> quotaProfiles=QuotaProfileDAO.getQuotaProfile(pkgData, sortColumnName, sortColumnOrder);
        	List<QuotaProfileWrapper> quotaProfileWrappers = Collectionz.newArrayList();
        	
        	for(QuotaProfileData quotaProfile:quotaProfiles){
        	    	QuotaProfileWrapper wrapper = new QuotaProfileWrapperBuilder(quotaProfile.getId(), quotaProfile.getName()).withQuotaProfileDetails(quotaProfile.getQuotaProfileDetailDatas(),0).build();					
        	    	quotaProfileWrappers.add(wrapper);
        	}
        	dataList = quotaProfileWrappers;
	    }
	}
	
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(String recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
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

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public String getBeanType() {
		return beanType;
	}

	public void setBeanType(String beanType) {
		this.beanType = beanType;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

}