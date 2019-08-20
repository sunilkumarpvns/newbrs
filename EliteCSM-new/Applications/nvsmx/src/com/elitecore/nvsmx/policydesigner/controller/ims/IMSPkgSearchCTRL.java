package com.elitecore.nvsmx.policydesigner.controller.ims;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.nvsmx.system.constants.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice.IMSPkgServiceDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice.IMSPkgServiceWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice.IMSPkgServiceWrapper.IMSPkgServiceWrapperBuilder;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Perform operations related to IMS Packages
 * @author Dhyani.Raval
 *
 */
public class IMSPkgSearchCTRL extends ActionSupport implements ServletRequestAware{

	private static final long serialVersionUID = 1L;
	private IMSPkgServiceData imsPkgServiceData = new IMSPkgServiceData(); 
	private HttpServletRequest request;
	private List<IMSPkgServiceWrapper> imsPkgServiceWrappers;
	private static final String MODULE = IMSPkgSearchCTRL.class.getSimpleName();
	
	public String getImsPkgServiceDataList(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called getImsPkgServiceDataList()");
		}
		String imsPkgId = request.getParameter("imsPkgId");
		try{
			List<IMSPkgServiceData> imsPkgServiceDatas = IMSPkgServiceDAO.getIMSServiceByIMSPkg(imsPkgId);
			imsPkgServiceWrappers = Collectionz.newArrayList();
			for(IMSPkgServiceData imsPkgServiceData : imsPkgServiceDatas){
				IMSPkgServiceWrapper imsPkgServiceWrapper = new IMSPkgServiceWrapperBuilder().withIMSPkgServiceDatas(imsPkgServiceData).build();
				imsPkgServiceWrappers.add(imsPkgServiceWrapper);
			}
		}catch(Exception e){
			getLogger().error(MODULE,"Failed to get IMS Package Services. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}
		return Results.PKG_SUCCESS.getValue();
	}
	
	public String imsPkgServiceViewDetail(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called imsPkgServiceViewDetail()");
		}
		try{
			String tableId = request.getParameter(Attributes.TABLE_ID);
			String rowData = request.getParameter(Attributes.ROW_DATA+tableId);
			Gson gson = GsonFactory.defaultInstance();
			IMSPkgServiceWrapper imsPkgServiceWrapper = gson.fromJson(rowData, IMSPkgServiceWrapper.class);
			setImsPkgServiceData(CRUDOperationUtil.get(IMSPkgServiceData.class, imsPkgServiceWrapper.getId()));
		}catch(Exception e){
			getLogger().error(MODULE,"Failed to get IMS Package PCC attributes. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}
		return Results.SUBTABLE_SUCCESS.getValue();
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public IMSPkgServiceData getImsPkgServiceData() {
		return imsPkgServiceData;
	}

	public void setImsPkgServiceData(IMSPkgServiceData imsPkgServiceData) {
		this.imsPkgServiceData = imsPkgServiceData;
	}

	public List<IMSPkgServiceWrapper> getImsPkgServiceWrappers() {
		return imsPkgServiceWrappers;
	}

	public void setImsPkgServiceWrappers(List<IMSPkgServiceWrapper> imsPkgServiceWrappers) {
		this.imsPkgServiceWrappers = imsPkgServiceWrappers;
	}
	
}
