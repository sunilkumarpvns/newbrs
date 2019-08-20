package com.elitecore.nvsmx.sm.controller.audit;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.audit.AuditData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.audit.AuditDAO;
import com.elitecore.nvsmx.sm.model.audit.AuditDataWrapper;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Validateable;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.RestActionSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Audit for Server Manager will be managed by this class.
 * It is rest based controller so its rest services are also published by default
 * @author ishani bhatt
 *
 */
@ParentPackage(value = "sm")
@Namespace("/sm/audit")
@org.apache.struts2.convention.annotation.Results({
		@Result(name= SUCCESS, type="redirectAction",params = {"actionName","audit"}),

})
@InterceptorRef(value = "restStack")
public class AuditCTRL extends RestActionSupport implements ModelDriven<Object>,Validateable,ServletRequestAware {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = AuditCTRL.class.getSimpleName();
	private HttpServletRequest request;
	private Collection<AuditData> list;
	private String actionChainUrl;
	private AuditData auditData = new AuditData();
	private String auditableResourceName;
	private String refererUrl;
	private String actualId;


	@SkipValidation
	public String show(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called show()");
		}
		try{
			String actualId = auditData.getId();
			String auditableId = request.getParameter(Attributes.AUDITABLE_ID);
			if(Strings.isNullOrBlank(auditableId)){
				auditableId = actualId;
			}
			setActualId(actualId);
			setAuditableResourceName(request.getParameter("auditableResourceName"));
			setRefererUrl(request.getParameter("refererUrl"));
			list = AuditDAO.searchHistory(auditableId,actualId);
			setActionChainUrl("sm/audit/audit/show");
			return NVSMXCommonConstants.REDIRECT_URL;
 		}catch(Exception e){
			LogManager.getLogger().error(MODULE,"Error while fetching Audit data. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return Results.ERROR.getValue();
		}
	}

	@SkipValidation
	public String viewDetail() {
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called detailData()");
		}
		request.setAttribute(Attributes.ACTION, "sm/audit/audit/show");
		try{
			String pageId = request.getParameter("pageId");
			String tableId = request.getParameter(Attributes.TABLE_ID);
			String rowData = request.getParameter(Attributes.ROW_DATA+tableId);
			Gson gson = GsonFactory.defaultInstance();
			AuditDataWrapper auditDataWrapper = gson.fromJson(rowData, AuditDataWrapper.class);
			AuditData auditData =  CRUDOperationUtil.get(AuditData.class, auditDataWrapper.getId());
			request.setAttribute(Attributes.AUDIT_DATA, auditData);
			request.setAttribute("pageId", pageId);
			return "viewDetail";
 		}catch(Exception e){
			LogManager.getLogger().error(MODULE,"Error while fetching Audit data. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return Results.ERROR.getValue();
		}
		
	}

	public Collection<AuditData> getList() {
		return list;
	}

	public void setList(Collection<AuditData> list) {
		this.list = list;
	}

	@Override
	public Object getModel() {
		return list == null ? auditData : list;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	public String getAuditDataListAsJson() {
		List<AuditDataWrapper> auditDataWrappers = Collectionz.newArrayList();
		for(AuditData auditData : list){
			AuditDataWrapper auditDataWrapper = new AuditDataWrapper.AuditDataWrapperBuilder().withAuditDatas(auditData).build();
			auditDataWrappers.add(auditDataWrapper);
		}
		Gson gson = GsonFactory.defaultInstance();
		JsonArray auditDataJson = gson.toJsonTree(auditDataWrappers).getAsJsonArray();
		return auditDataJson.toString();
	}


	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}

	@Override
	public void setServletRequest(HttpServletRequest httpServletRequest) {
		this.request = httpServletRequest;
	}

	public String getAuditableResourceName() {
		return auditableResourceName;
	}

	public void setAuditableResourceName(String auditableResourceName) {
		this.auditableResourceName = auditableResourceName;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	public void setActualId(String actualId){
		this.actualId = actualId;
	}
	public String getActualId(){
		return this.actualId;
	}
}
