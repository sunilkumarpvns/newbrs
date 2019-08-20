package com.elitecore.nvsmx.commons.controller.audit;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.corenetvertex.sm.audit.AuditData;
import com.elitecore.nvsmx.system.constants.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditCriteriaData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.audit.AuditDAO;
import com.elitecore.nvsmx.commons.model.audit.AuditDataWrapper;
import com.elitecore.nvsmx.commons.model.audit.AuditDataWrapper.AuditDataWrapperBuilder;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.validation.SkipValidation;

/**
 * 
 * @author Dhayni.Raval
 *
 */
public class AuditCTRL extends ActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = AuditCTRL.class.getSimpleName();
	private static final Object action = "/commons/audit/Audit/search";
	private HttpServletRequest request;
	AuditCriteriaData auditCriteriaData = new AuditCriteriaData();
	
	@SkipValidation
	public String search(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE,"Method called search()");
		}
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String searchCriteria(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE,"Method called searchCriteria()");
		}
		request.setAttribute(Attributes.ACTION, action);
		try {
			String json = GsonFactory.defaultInstance().toJson(getAuditCriteriaData());
	    	request.setAttribute(Attributes.CRITERIA, json);
			return Results.LIST.getValue();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch AuditData based on criteria. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			return Results.ERROR.getValue();
		}
	}
	
	@SkipValidation
	public String view(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called view()");
		}
		request.setAttribute(Attributes.ACTION, action);
		try{
			String actualId = request.getParameter(Attributes.ACTUAL_ID);
			String auditableId = request.getParameter(Attributes.AUDITABLE_ID);
			String auditPageHeadingName = request.getParameter(Attributes.AUDIT_PAGE_HEADING_NAME);	
			String refererUrl = request.getParameter("refererUrl");
			List<AuditData> auditDatas = AuditDAO.searchHistory(auditableId,actualId);
			
			List<AuditDataWrapper> auditDataWrappers = Collectionz.newArrayList();
			for(AuditData auditData : auditDatas){
				AuditDataWrapper auditDataWrapper = new AuditDataWrapperBuilder().withAuditDatas(auditData).build();
				auditDataWrappers.add(auditDataWrapper);
			}
			
			Gson gson = GsonFactory.defaultInstance();
			JsonArray auditDataJson = gson.toJsonTree(auditDataWrappers).getAsJsonArray();
			request.setAttribute(Attributes.VIEW_HISTORY, auditDataJson);
			request.setAttribute(Attributes.AUDIT_PAGE_HEADING_NAME, auditPageHeadingName);
			request.setAttribute(Attributes.ACTUAL_ID, actualId);
			request.setAttribute("refererUrl", refererUrl);
			return Results.AUDIT.getValue();
 		}catch(Exception e){
			LogManager.getLogger().error(MODULE,"Error while fetching Audit data. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return Results.ERROR.getValue();
		}
	}
	
	public AuditCriteriaData getAuditCriteriaData() {
		return auditCriteriaData;
	}

	public void setAuditCriteriaData(AuditCriteriaData auditCriteriaData) {
		this.auditCriteriaData = auditCriteriaData;
	}
	
	/**
	 * Used to display the detail data of audit logs
	 * @return String
	 */
	@SkipValidation
	public String viewDetail() {
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called detailData()");
		}
		request.setAttribute(Attributes.ACTION, action);
		try{
			String pageId = request.getParameter("pageId");
			String tableId = request.getParameter(Attributes.TABLE_ID);
			String rowData = request.getParameter(Attributes.ROW_DATA+tableId);
			Gson gson = GsonFactory.defaultInstance();
			AuditDataWrapper auditDataWrapper = gson.fromJson(rowData, AuditDataWrapper.class);
			AuditData auditData =  CRUDOperationUtil.get(AuditData.class, auditDataWrapper.getId());
			request.setAttribute(Attributes.AUDIT_DATA, auditData);
			request.setAttribute("pageId", pageId);
			return Results.AUDIT_DATA_DETAIL.getValue();
 		}catch(Exception e){
			LogManager.getLogger().error(MODULE,"Error while fetching Audit data. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return Results.ERROR.getValue();
		}
		
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
