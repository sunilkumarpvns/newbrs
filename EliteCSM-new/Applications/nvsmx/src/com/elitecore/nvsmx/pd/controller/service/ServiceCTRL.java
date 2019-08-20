package com.elitecore.nvsmx.pd.controller.service;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.ResourceStatus;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
import com.elitecore.corenetvertex.sm.gateway.ServiceGuidingData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Order;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_ITALIC_TEXT_TAG;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/service")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "service" }),

})
public class ServiceCTRL extends RestGenericCTRL<ServiceData> {

	private static final long serialVersionUID = -1514438069544331696L;
	private static final String DATA_SERVICE_ID = "DATA";
	private static final String VOLUME_SERVICE_ID = "VOICE";

	private String productOffersAssociationListAsJson;
	private String diameterProfilesAssociationListAsJson;
	private String radiusProfilesAssociationListAsJson;

	@Override
	public ACLModules getModule() {
		return ACLModules.SERVICE;
	}

	@Override
	public ServiceData createModel() {
		return new ServiceData();
	}

	@Override
	public HttpHeaders create() {
		ServiceData serviceData = (ServiceData) getModel();
		serviceData.setStatus(PkgStatus.ACTIVE.name());
		return super.create();
	}

	@Override
	public HttpHeaders update() {
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(getLogModule(),"Method called update()");
		}
		try {

			String result = authorize();
			ServiceData serviceData = (ServiceData) getModel();
			if(result.equals(SUCCESS) == false){
				setActionChainUrl(getRedirectURL(METHOD_EDIT));
				return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
			}

			if(Objects.isNull(ResourceStatus.fromVal(serviceData.getStatus()))){
				addFieldError("status", "Invalid status");
				getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ serviceData.getId()+". Reason: Invalid status");
				return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
			}

			ServiceData serviceDataFromDB = CRUDOperationUtil.get(ServiceData.class, serviceData.getId());
			if(serviceDataFromDB == null){
				getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ serviceData.getId()+". Reason: Not found");
				return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
			}

			serviceData.setId(serviceDataFromDB.getId());
			serviceData.setModifiedDateAndStaff(getStaffData());

			CRUDOperationUtil.merge(serviceData);
			String message = getModule().getDisplayLabel() +"  "+ START_BOLD_TEXT_TAG + START_ITALIC_TEXT_TAG +serviceData.getResourceName() +  CLOSE_BOLD_TEXT_TAG + CLOSE_ITALIC_TEXT_TAG  + " Updated";
			CRUDOperationUtil.audit(serviceData,serviceData.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),serviceData.getHierarchy(),message);
			addActionMessage(getModule().getDisplayLabel()+" updated successfully");
			setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(serviceData.getId()));
			CRUDOperationUtil.flushSession();
			return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
		}catch (ConstraintViolationException cve){
			getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +" information. Reason: "+cve.getMessage());
			getLogger().trace(getLogModule(),cve);
			addActionError("Fail to perform update Operation.Reason: constraint "+cve.getConstraintName()+" violated");
		}catch (GenericJDBCException e){
			getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
			getLogger().trace(getLogModule(),e);
			addActionError("Fail to perform Update Operation. Reason: " + e.getSQLException().getMessage());
		} catch (Exception e){
			getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
			getLogger().trace(getLogModule(),e);
			addActionError("Fail to perform Update Operation. Reason: " + e.getMessage());
		}
		return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

	}

	@SkipValidation
	public HttpHeaders viewAssociation(){
		ServiceData serviceData = (ServiceData) getModel();
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(getLogModule(),"Method called viewAssociation()");
		}

		try{
			List<ProductOfferData> productOffersAssociationList = new ArrayList<>();
			List<DiameterGatewayProfileData> diameterProfilesAssociationList = new ArrayList<>();
			List<RadiusGatewayProfileData> radiusProfilesAssociationList = new ArrayList<>();

			List<ProductOfferData> productOfferList = CRUDOperationUtil.get(ProductOfferData.class, Order.asc("name"));

			for(ProductOfferData productOffer : productOfferList){
				List<ProductOfferServicePkgRelData> productOfferServicePkgRelList = productOffer.getProductOfferServicePkgRelDataList();
				for(ProductOfferServicePkgRelData productOfferServicePkgRel : productOfferServicePkgRelList) {
					if (productOfferServicePkgRel.getServiceId().equals(serviceData.getServiceId())) {
						productOffersAssociationList.add(productOffer);
					}
				}
			}

			List<ServiceGuidingData> serviceGuidings = getServiceGuidingData();
			for(ServiceGuidingData serviceGuidingData : serviceGuidings){
				if(serviceGuidingData.getServiceData().getId().equalsIgnoreCase(serviceData.getId())){
					if(Objects.nonNull(serviceGuidingData.getDiameterGatewayProfileData())){
						diameterProfilesAssociationList.add(serviceGuidingData.getDiameterGatewayProfileData());
					}else{
						radiusProfilesAssociationList.add(serviceGuidingData.getRadiusGatewayProfileData());
					}
				}
			}
			setServiceAssociationDataAsJson(productOffersAssociationList, diameterProfilesAssociationList, radiusProfilesAssociationList);

			setActionChainUrl(getRedirectURL("view-association"));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();

		}catch(Exception ex){
			getLogger().error(getLogModule(),"Failed to view service. Reason: "+ex.getMessage());
			getLogger().trace(getLogModule(),ex);
			return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
		}
	}

	private void setServiceAssociationDataAsJson(List<ProductOfferData> productOffersAssociationList, List<DiameterGatewayProfileData> diameterProfilesAssociationList, List<RadiusGatewayProfileData> radiusProfilesAssociationList) {
		Gson gson = GsonFactory.defaultInstance();
		setProductOffersAssociationListAsJson(gson.toJsonTree(productOffersAssociationList).getAsJsonArray().toString());
		setDiameterProfilesAssociationListAsJson(gson.toJsonTree(diameterProfilesAssociationList).getAsJsonArray().toString());
		setRadiusProfilesAssociationListAsJson(gson.toJsonTree(radiusProfilesAssociationList).getAsJsonArray().toString());
	}

	private void setRadiusProfilesAssociationListAsJson(String radiusProfilesAssociationListAsJson) {
		this.radiusProfilesAssociationListAsJson = radiusProfilesAssociationListAsJson;
	}

	private void setDiameterProfilesAssociationListAsJson(String diameterProfilesAssociationListAsJson) {
		this.diameterProfilesAssociationListAsJson = diameterProfilesAssociationListAsJson;
	}

	private void setProductOffersAssociationListAsJson(String productOffersAssociationListAsJson) {
		this.productOffersAssociationListAsJson = productOffersAssociationListAsJson;
	}

	public String getProductOffersAssociationListAsJson() {
		return productOffersAssociationListAsJson;
	}

	public String getDiameterProfilesAssociationListAsJson() {
		return diameterProfilesAssociationListAsJson;
	}

	public String getRadiusProfilesAssociationListAsJson() {
		return radiusProfilesAssociationListAsJson;
	}

	@Override
	public boolean prepareAndValidateDestroy(ServiceData serviceData) {
		if(Objects.equals(DATA_SERVICE_ID, serviceData.getId()) || Objects.equals(VOLUME_SERVICE_ID, serviceData.getId())){
				addActionError("Can not perform delete operation. Reason: System generated service " + serviceData.getName() + " can not be deleted.");
				return false;
		}
		List<ServiceGuidingData> serviceGuidings = getServiceGuidingData();
		for(ServiceGuidingData serviceGuidingData : serviceGuidings){
			if(serviceGuidingData.getServiceData().getId().equalsIgnoreCase(serviceData.getId())){
				addActionError("Can not perform delete operation. Reason: Service is associated with Diameter/RADIUS Gateway Profile");
				return false;
			}
		}
		return true;
	}

	private List<ServiceGuidingData> getServiceGuidingData() {
		List<ServiceGuidingData> serviceGuidingData = new ArrayList<>();
		List<DiameterGatewayProfileData> diameterProfiles = CRUDOperationUtil.get(DiameterGatewayProfileData.class, Order.asc("name"));
		List<RadiusGatewayProfileData> radiusProfiles = CRUDOperationUtil.get(RadiusGatewayProfileData.class,Order.asc("name"));
		if(Collectionz.isNullOrEmpty(diameterProfiles) == false){
			diameterProfiles.forEach(diameterProfile -> serviceGuidingData.addAll(diameterProfile.getServiceGuidingDatas()));
		}
		if(Collectionz.isNullOrEmpty(radiusProfiles) == false){
			radiusProfiles.forEach(radiusProfile -> serviceGuidingData.addAll(radiusProfile.getServiceGuidingDatas()));
		}
		return serviceGuidingData;
	}
}