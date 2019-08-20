package com.elitecore.nvsmx.sm.controller.systemparameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.systemparameter.OfflineRnCSystemParameterConfiguration;
import com.elitecore.corenetvertex.sm.systemparameter.OfflineRncSystemParameterValuePool;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameterData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/systemparameter")
@Results({
        @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {"actionName", "offline-rnc-system-parameter/*/refresh"}),
})
public class OfflineRncSystemParameterCTRL extends CreateNotSupportedCTRL<OfflineRnCSystemParameterConfiguration> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String OFFLINE_RNC_SYSTEM_PARAMETER_EDIT = "sm/systemparameter/offline-rnc-system-parameter/edit";
	private static final String  ALIAS = "alias";
	private Map<String, List<OfflineRncSystemParameterValuePool>> offlineRncSystemParameterValuePoolMap = OfflineRncSystemParameterValuePool.getValueMap();
	private List<String> isoCodeList = Collectionz.newArrayList();
	private List<String> timeZoneList = TimezoneFormate.getTimeZone();

	@Override
	public ACLModules getModule() {
		return ACLModules.SYSTEM_PARAMETER;
	}

	@Override
	public OfflineRnCSystemParameterConfiguration createModel() {
		return new OfflineRnCSystemParameterConfiguration();
	}

	@Override
	public HttpHeaders show() {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called show()");

		}
		try {
			setActionChainUrl(getRedirectURL(METHOD_SHOW));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
		} catch (Exception e) {
			addActionError("Error while viewing " + getModule().getDisplayLabel() + " data for id ");
			getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " data. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
		}

		addActionError(getModule().getDisplayLabel() + " Not Found");
		return new DefaultHttpHeaders("error").withStatus(400);

	}

	@Override
	public String edit() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called edit()");
		}
		try {
			setActionChainUrl(OFFLINE_RNC_SYSTEM_PARAMETER_EDIT);
			return NVSMXCommonConstants.REDIRECT_URL;
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Error while performing Update Operation");
			return ERROR;
		}

	}

	@Override
	public HttpHeaders update() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called update()");
		}

		try {
			OfflineRnCSystemParameterConfiguration offlineRnCSystemParameterConfiguration = (OfflineRnCSystemParameterConfiguration) getModel();
			JsonArray difference = new JsonArray();
			for (SystemParameterData fileParameter : offlineRnCSystemParameterConfiguration.getFileSystemParameters()) {
				 SystemParameterData fileSystemParameterDataFromDB = getSystemParameterData(fileParameter);
	             if(fileSystemParameterDataFromDB != null) {
	                 setSystemParameterDiff(difference, fileSystemParameterDataFromDB.toJson(),fileParameter.toJson());
	             }
				CRUDOperationUtil.merge(fileParameter);
			}
			for (SystemParameterData ratingParameter : offlineRnCSystemParameterConfiguration.getRatingSystemParameters()) {
				 SystemParameterData ratingSystemParameterDataFromDB = getSystemParameterData(ratingParameter);
	             if(ratingSystemParameterDataFromDB != null) {
	                 setSystemParameterDiff(difference, ratingSystemParameterDataFromDB.toJson(),ratingParameter.toJson());
	             }
				CRUDOperationUtil.merge(ratingParameter);
			}

			String message = getModule().getDisplayLabel() + " <b><i>" + offlineRnCSystemParameterConfiguration.getResourceName() + "</i></b> " + "Updated";
            if(difference.size() > 0) {
                CRUDOperationUtil.audit(offlineRnCSystemParameterConfiguration, offlineRnCSystemParameterConfiguration.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, offlineRnCSystemParameterConfiguration.getHierarchy(), message);
            }
			addActionMessage(getModule().getDisplayLabel() + " updated successfully");
			return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();

		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Error while performing Update Operation");
		}
		return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
	}

	@SkipValidation
	public HttpHeaders refresh() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called refresh()");
		}
		try {
			SystemParameterDAO.refresh();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(getLogModule(), "System Parameter Refresh Successful");
			}
		} catch (InitializationFailedException e) {
			getLogger().error(getLogModule(), "Error while refreshing " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Error while performing Refresh Operation");
		}
		setActionChainUrl(getRedirectURL(METHOD_SHOW));
		return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
	}

	public void prepareShow() throws Exception{
		getRequest().setAttribute(NVSMXCommonConstants.TYPE, NVSMXCommonConstants.OFFLINE_RNC_SYSTEM_PARAMETER);
		prepareValuesForSubClass();
	}
	
	public void prepareRefresh() throws Exception{
		getRequest().setAttribute(NVSMXCommonConstants.TYPE, NVSMXCommonConstants.OFFLINE_RNC_SYSTEM_PARAMETER);
		prepareValuesForSubClass();
	}

	@Override
	public void prepareValuesForSubClass() throws Exception {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called prepareValuesForSubClass()");
		}
		if (Collectionz.isNullOrEmpty(SystemParameterDAO.getOrderedOfflineRnCFileSystemParameterData()) || Collectionz.isNullOrEmpty(SystemParameterDAO.getOrderedOfflineRnCRatingSystemParameterData())) {
			SystemParameterDAO.initializeSystemParameterList();
		}

		OfflineRnCSystemParameterConfiguration offlineRnCSystemParameterConfiguration = (OfflineRnCSystemParameterConfiguration) getModel();
		offlineRnCSystemParameterConfiguration.setFileSystemParameters(SystemParameterDAO.getOrderedOfflineRnCFileSystemParameterData());
		offlineRnCSystemParameterConfiguration.setRatingSystemParameters(SystemParameterDAO.getOrderedOfflineRnCRatingSystemParameterData());
		
		Set<String> isoList = new HashSet<>();
        Locale[] locales = Locale.getAvailableLocales();

        for(Locale loc : locales) {
        	try {
        		Currency currency = Currency.getInstance(loc);
	
		        if ( currency != null ) {
		        	
		        	isoList.add(currency.getCurrencyCode());
		        }
        	}catch(Exception ex) {
        		getLogger().error(getLogModule(), ex.getMessage());
        		getLogger().trace(ex);
        	}
        }
		isoCodeList.addAll(isoList);
	}
	
	private SystemParameterData getSystemParameterData(SystemParameterData systemParameter) {
        Criterion criterion = Restrictions.eq(ALIAS,systemParameter.getAlias());
        List<SystemParameterData> systemParams = CRUDOperationUtil.findAll(SystemParameterData.class,criterion);
        SystemParameterData systemParameterDataFromDB = null;
        if(Collectionz.isNullOrEmpty(systemParams) == false){
            systemParameterDataFromDB = systemParams.get(0);
        }
        return systemParameterDataFromDB;
    }

    private void setSystemParameterDiff(JsonArray difference, JsonObject oldJsonObject, JsonObject newJsonObject) {
            JsonArray diff = ObjectDiffer.diff(oldJsonObject, newJsonObject);
            if(diff.size() > 0) {
                difference.add(diff.get(0));
            }
    }
    
    public Map<String, List<OfflineRncSystemParameterValuePool>> getOfflineRncSystemParameterValuePoolMap() {
		return offlineRncSystemParameterValuePoolMap;
	}
    
    public List<String> getIsoCodeList() {
		return isoCodeList;
	}
    
    public List<String> getTimeZoneList() {
		return timeZoneList;
	}
}
