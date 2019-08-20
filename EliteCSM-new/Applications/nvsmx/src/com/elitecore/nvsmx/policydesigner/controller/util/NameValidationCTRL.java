package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pd.lrn.LrnData;
import com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData;
import com.elitecore.corenetvertex.pd.prefix.PrefixData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.sm.location.area.AreaData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.nvsmx.policydesigner.model.pkg.dataservicetype.DataServiceTypeDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice.IMSPkgServiceDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.syquota.SyQuotaProfileDAO;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.keys.ValidationKeys;
import com.elitecore.nvsmx.system.keys.ValidationMessage;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.MessageFormat;

public class NameValidationCTRL extends ActionSupport implements ServletRequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "NAME-VALIDATION-CTRL";
	protected HttpServletRequest request;
	protected String name;
	protected ValidationMessage validationMessage = new ValidationMessage();
	private String mode;
	private String id;
	private String className;
	private String property;
	
	/**
	 * parent id under which uniqueness is required (e.g for quota/qos profile package will be parent)
	 */
	private String parentId; 
	                           

	
  
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request; 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ValidationMessage getValidationMessage() {
		return validationMessage;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		
	}
	
	public String  doValidation(){
		if(LogManager.getLogger().isDebugLogLevel()){
		  LogManager.getLogger().debug(MODULE,"Method called doValidation()");
		}
		
		try{
			validationMessage.setMessageCode(ValidationMessage.ERROR);

			if(Strings.isNullOrBlank(getProperty())){
				setProperty("name");
			}
			if( Strings.isNullOrEmpty(name) == true ){
				Object [] args = {getProperty()};
				MessageFormat messageFormat = new MessageFormat(getText(ValidationKeys.EMPTY_NAME.key));
				
				validationMessage.setMessage(messageFormat.format(args));
			}else if("name".equals(getProperty()) && (name.length() < 2 || name.length() > 100)){
				validationMessage.setMessage(getText("error.length.name"));
			}else if("name".equals(getProperty()) && name.matches(getText(ValidationKeys.NAME_VALIDATION_REGEX.key)) == false){
				validationMessage.setMessage(getText("invalid.name"));
			}else{

	            Class<?> clazz = getClassType();
				boolean isAlreadyExist =false;

				if(Strings.isNullOrEmpty(getParentId())==false && clazz !=null){
					if(clazz.equals(QuotaProfileData.class)){
						isAlreadyExist = QuotaProfileDAO.isDuplicateName(getMode(), getId(), getName(),getParentId());  	
					}else if(clazz.equals(QosProfileData.class)){
						isAlreadyExist = QosProfileDAO.isDuplicateName(getMode(), getId(), getName(),getParentId());	
					}else if(clazz.equals(SyQuotaProfileData.class)){
						isAlreadyExist = SyQuotaProfileDAO.isDuplicateName(getMode(), getId(), getName(),getParentId());	
					}else if(clazz.equals(IMSPkgServiceData.class)){
						isAlreadyExist = IMSPkgServiceDAO.isDuplicateName(getMode(),getId(),getName(),getParentId());
					}else if(clazz.equals(RegionData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RegionData.class,getMode(),getId(),getName(),getParentId(),"countryData");

					}else if(clazz.equals(CityData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(CityData.class, getMode(), getId(), getName(), getParentId(), "regionData");
					}else if(clazz.equals(AreaData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(AreaData.class,getMode(),getId(),getName(),getParentId(),"cityData");
					}else if(clazz.equals(DataRateCardData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(DataRateCardData.class,getMode(),getId(),getName(),getParentId(),"pkgData");
					}else if(clazz.equals(RateCardData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RateCardData.class,getMode(),getId(),getName(),getParentId(),"rncPackageData");
					}else if(clazz.equals(RateCardGroupData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RateCardGroupData.class,getMode(),getId(),getName(),getParentId(),"rncPackageData");
					}
				}else if(clazz!=null){
					if(clazz.equals(ServerInstanceData.class) || clazz.equals(ServerGroupData.class) || clazz.equals(DiameterGatewayData.class)){
						isAlreadyExist = CRUDOperationUtil.isDuplicateProperty(getMode(), clazz, getId(), getName(), getProperty());
					} else  if(clazz.equals(DataServiceTypeData.class)){
						isAlreadyExist = DataServiceTypeDAO.isDuplicateValue(getMode(), clazz, getId(), getName(), getProperty());
					} else if(clazz.equals(RatingGroupData.class)) {
						isAlreadyExist = DataServiceTypeDAO.isDuplicateIdentifierForRatingGroup(getMode(), clazz, getId(), getName(), getProperty());
					}else if(clazz.equals(ChargingRuleDataServiceTypeData.class)) {
						isAlreadyExist = CRUDOperationUtil.isDuplicateProperty("create", Class.forName(PCCRuleData.class.getCanonicalName()), getId(), getName(), getProperty());

						if( isAlreadyExist == false ) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateProperty(getMode(), clazz, getId(), getName(), getProperty());
						}
					} else if(clazz.equals(PCCRuleData.class)) {

						if("monitoringKey".equalsIgnoreCase(getProperty())) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateProperty("create", Class.forName(ChargingRuleDataServiceTypeData.class.getCanonicalName()), getId(), getName(), getProperty());
						}

						if( isAlreadyExist == false ) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateName(getMode(), clazz, getId(), getName(), getProperty());
						}

					} else if(clazz.equals(PrefixData.class)) {
						if("prefix".equalsIgnoreCase(getProperty())) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateProperty(getMode(), Class.forName(PrefixData.class.getCanonicalName()), getId(), getName(), getProperty());
						}

						if( isAlreadyExist == false ) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateName(getMode(), clazz, getId(), getName(), getProperty());
						}
					} else if(clazz.equals(LrnData.class)) {
						if("lrn".equalsIgnoreCase(getProperty())) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateProperty(getMode(), Class.forName(LrnData.class.getCanonicalName()), getId(), getName(), getProperty());
						}
					} else if(clazz.equals(MonetaryRechargePlanData.class)) {
						if("name".equalsIgnoreCase(getProperty())) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateName(getMode(), clazz, getId(), getName(),getProperty());
						}
						if( "price".equalsIgnoreCase(getProperty())) {
							isAlreadyExist = CRUDOperationUtil.isDuplicateProperty(getMode(), Class.forName(MonetaryRechargePlanData.class.getCanonicalName()), getId(), getName(), getProperty());
						}
					} else if(clazz.equals(RevenueDetailData.class)) {
						isAlreadyExist = CRUDOperationUtil.isDuplicateProperty(getMode(), Class.forName(RevenueDetailData.class.getCanonicalName()), getId(), getName(), getProperty());
					} else{
						isAlreadyExist = CRUDOperationUtil.isDuplicateName(getMode(), clazz, getId(), getName(),getProperty());
					}
				}

				if( isAlreadyExist ){	
					Object [] args = {getProperty()};
					MessageFormat messageFormat = new MessageFormat(getText(ValidationKeys.NAME_ALREADY_EXIST.key));
					validationMessage.setMessage(StringUtils.capitalize(messageFormat.format(args)));
				}else{				
					validationMessage.setMessage(getText(ValidationKeys.VALID_NAME.key));
					validationMessage.setMessageCode(ValidationMessage.SUCCESS);				
				}			
			}
	
			return Results.SUCCESS.getValue();
		
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Failed to do name validation. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			addActionError(getText(ValidationKeys.NAME_VALIDATION_FAILED.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String classType) {
		this.className = classType;
		
	}
	
	
	private Class<?> getClassType(){
		  Class<?> clazz=null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
		    LogManager.getLogger().error(MODULE, "failed to validate name for type: "+className+" Reason. "+e.getMessage());
		    LogManager.getLogger().trace(MODULE, e);
		}
		  return clazz;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String pkgId) {
		this.parentId = pkgId;
		
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
		
	}

	
}