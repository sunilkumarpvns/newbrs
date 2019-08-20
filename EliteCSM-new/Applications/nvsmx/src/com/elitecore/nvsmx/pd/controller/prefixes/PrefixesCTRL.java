package com.elitecore.nvsmx.pd.controller.prefixes;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.account.AccountPrefixMasterRelationData;
import com.elitecore.corenetvertex.pd.account.PrefixListMasterData;
import com.elitecore.corenetvertex.pd.prefixes.PrefixesData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;

/**
 * @author Ajay Pandey on 17/12/17
 */
@ParentPackage(value = "pd")
@Namespace("/pd/prefixes")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "prefixes" }),

})
public class PrefixesCTRL extends RestGenericCTRL<PrefixesData> {

	private static final long serialVersionUID = -1342976327159618815L;

	private boolean checkMaster;
	private String accountIds;
	private String masterPrefixIds;

	@Override
	public ACLModules getModule() {

		return ACLModules.PREFIXES;
	}

	@Override
	public PrefixesData createModel() {

		return new PrefixesData();
	}

	@SkipValidation
	@Override
	public void prepareValuesForSubClass() throws Exception {
		String accountId = getRequest().getParameter(NVSMXCommonConstants.ACCOUNT_ID);
		String masterPrefix = getRequest().getParameter(NVSMXCommonConstants.MASTER_PREFIX);
		
		if(Strings.isNullOrBlank(accountId) == false){
			setAccountIds(accountId);
		}
		
		if(Strings.isNullOrBlank(masterPrefix) == false){
			setMasterPrefixIds(masterPrefix);
		}
	}

	@Override
	public HttpHeaders index() { // list or search
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called index()");
		}
		List<PrefixesData> list = null;
		try {
			list = CRUDOperationUtil.findAll((Class<PrefixesData>) PrefixesData.class);
			List<PrefixesData> tempList = new ArrayList<>();
			for (PrefixesData prefixData : list) {
				if (prefixData.getPrefixMaster().getId().equals(CommonConstants.DEFAULT_PREFIX_LIST_MASTER_ID)) {
					tempList.add(prefixData);
				}
			}
			setList(tempList);
			setActionChainUrl(getRedirectURL(METHOD_INDEX));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while fetching " + getModule().getDisplayLabel()
					+ " information. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Fail to perform Search Operation");
		}
		return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
	}

	@Override
	public HttpHeaders create() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called create()");
		}
		PrefixesData model = (PrefixesData) getModel();
        try {
            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            if (model != null) {
                StaffData staffData = getStaffData();
                model.setCreatedDateAndStaff(staffData);
                
                
                
                if(Strings.isNullOrEmpty(model.getMasterPrefixId()) == false){
                	CRUDOperationUtil.save(model);
        			setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId())+"?masterPrefix="+
        			model.getMasterPrefixId());
                }
                
                if (Strings.isNullOrBlank(masterPrefixIds) == false) {
        			PrefixListMasterData prefixMaster = new PrefixListMasterData();
        			prefixMaster.setId(CommonConstants.DEFAULT_PREFIX_LIST_MASTER_ID);
        			model.setPrefixMaster(prefixMaster);
        			setModel(model);
                
        			CRUDOperationUtil.save(model);
        			setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId())+"?masterPrefix="+
        			masterPrefixIds);
        		}

        		if (Strings.isNullOrBlank(accountIds) == false) {
        			List<AccountPrefixMasterRelationData> accountPrefixMasterRelationDataList = CRUDOperationUtil
        					.findAll(AccountPrefixMasterRelationData.class);

        			if (accountPrefixMasterRelationDataList.isEmpty() == false) {
        				getAccountPrefixMasterRelationData(accountPrefixMasterRelationDataList, accountIds);
        				if (accountPrefixMasterRelationDataList.isEmpty() == false) {
        					model.setPrefixMaster(accountPrefixMasterRelationDataList.get(0).getPrefixListMasterData());
        				} else {
        					setPrefixdata(model, accountIds);
        				}
        			} else {
        				setPrefixdata(model, accountIds);
        			}
        			setModel(model);
        			CRUDOperationUtil.save(model);
        			setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId())+"?accountId"+"="+
                			accountIds);
        		}
            }
            String message = getModule().getDisplayLabel()  + " <b><i>" + model.getResourceName() + "</i></b> " + "Created";
            CRUDOperationUtil.audit(model,model.getResourceName(),AuditActions.CREATE,getStaffData(),getRequest().getRemoteAddr(),model.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" created successfully");
            return new DefaultHttpHeaders(REDIRECT_ACTION).setLocationId(model.getId());
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform create Operation");
        }
        return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
	}

	@Override
	public HttpHeaders update() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called update()");
		}
		PrefixesData model = (PrefixesData) getModel();

		try {

			String result = authorize();
			if (result.equals(SUCCESS) == false) {
				setActionChainUrl(getRedirectURL(METHOD_EDIT));
				return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL)
						.withStatus(ResultCode.INTERNAL_ERROR.code);
			}

			if (isEntityExists(model.getId()) == false) {
				return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
			}

			model.setModifiedDateAndStaff(getStaffData());
			 
           
			 
            if(Strings.isNullOrEmpty(model.getMasterPrefixId()) == false){
            	CRUDOperationUtil.save(model);
    			setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId())+"?masterPrefix="+
    			model.getMasterPrefixId());
            }
			
			if (Strings.isNullOrBlank(masterPrefixIds) == false) {
				PrefixListMasterData prefixMaster = new PrefixListMasterData();
				prefixMaster.setId(CommonConstants.DEFAULT_PREFIX_LIST_MASTER_ID);
				model.setPrefixMaster(prefixMaster);
    			
    			setModel(model);
				setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId())+"?masterPrefix="+
	        			masterPrefixIds);
			}
			if (Strings.isNullOrBlank(accountIds) == false) {
				List<AccountPrefixMasterRelationData> accountPrefixMasterRelationDataList = CRUDOperationUtil
						.findAll(AccountPrefixMasterRelationData.class);

				getAccountPrefixMasterRelationData(accountPrefixMasterRelationDataList, accountIds);
				model.setPrefixMaster(accountPrefixMasterRelationDataList.get(0).getPrefixListMasterData());

				setModel(model);
				setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId())+"?accountId"+"="+
            			accountIds);
			}

			CRUDOperationUtil.merge(model);
			String message = getModule().getDisplayLabel() + " <b><i>" + model.getResourceName() + "</i></b> "
					+ "Updated";
			CRUDOperationUtil.audit(model, model.getResourceName(), AuditActions.UPDATE, getStaffData(),
					getRequest().getRemoteAddr(), model.getHierarchy(), message);
			addActionMessage(getModule().getDisplayLabel() + " updated successfully");
			return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel()
					+ " information. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Fail to perform Update Operation");
		}
		return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
	}

	@Override
	public HttpHeaders show() { // show
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called show()");
		}

		if (Strings.isNullOrBlank(getRequest().getParameter(NVSMXCommonConstants.MASTER_PREFIX)) == false) {
			setMasterPrefixIds(getRequest().getParameter(NVSMXCommonConstants.MASTER_PREFIX));
		} 
		if (Strings.isNullOrBlank(getRequest().getParameter("accountId")) == false) {
			setAccountIds(getRequest().getParameter("accountId"));
		} 
		return super.show();
	}

	@Override
	public HttpHeaders destroy() {// delete
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called destroy()");
		}
		super.destroy();
		
		
		if (Strings.isNullOrBlank(getRequest().getParameter(NVSMXCommonConstants.MASTER_PREFIX)) == false) {
			return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
		} 
		else{
			setActionChainUrl(CommonConstants.FORWARD_SLASH + "pd/account/account/"
					+ (getRequest().getParameter(NVSMXCommonConstants.ACCOUNT_ID)));
			return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
		} 
	}

	private void setPrefixdata(PrefixesData prefixesData, String accountId) {
		AccountPrefixMasterRelationData accountPrefixMasterRelationData = new AccountPrefixMasterRelationData();

		AccountData accountData = new AccountData();
		accountData.setId(accountId);

		accountPrefixMasterRelationData.setAccountData(accountData);
		PrefixListMasterData prefixListMasterData = new PrefixListMasterData();
		CRUDOperationUtil.save(prefixListMasterData);

		accountPrefixMasterRelationData.setPrefixListMasterData(prefixListMasterData);
		CRUDOperationUtil.save(accountPrefixMasterRelationData);
		prefixesData.setPrefixMaster(accountPrefixMasterRelationData.getPrefixListMasterData());
	}

	private List<AccountPrefixMasterRelationData> getAccountPrefixMasterRelationData(
			List<AccountPrefixMasterRelationData> accountPrefixMasterRelationDatas, String accountId) {
		filterAccountPrefixMasterRelationData(accountPrefixMasterRelationDatas, accountId);
		return accountPrefixMasterRelationDatas;
	}

	private void filterAccountPrefixMasterRelationData(
			List<AccountPrefixMasterRelationData> accountPrefixMasterRelationDatas, String accountId) {

		Collectionz.filter(accountPrefixMasterRelationDatas, accountPrefixMasterRelationData -> {
			if (accountPrefixMasterRelationData != null
					&& accountPrefixMasterRelationData.getAccountData().getId().equals(accountId)) {
				return true;
			} else {
				return false;
			}
		});
	}

	public boolean isCheckMaster() {
		return checkMaster;
	}

	public void setCheckMaster(boolean checkMaster) {
		this.checkMaster = checkMaster;
	}

	public String getAccountIds() {
		return accountIds;
	}

	public void setAccountIds(String accountIds) {
		this.accountIds = accountIds;
	}

	public String getMasterPrefixIds() {
		return masterPrefixIds;
	}

	public void setMasterPrefixIds(String masterPrefixIds) {
		this.masterPrefixIds = masterPrefixIds;
	}

	@Override
	public void validate() {
		super.validate();
		
		PrefixesData model = (PrefixesData)getModel();
		if(model !=null && Strings.isNullOrEmpty(model.getMasterPrefixId()) == false && CommonConstants.DEFAULT_PREFIX_LIST_MASTER_ID.equals(model.getMasterPrefixId()) == false){
			addActionError(getText("global.prefixid.invalid"));
		}
	}
}
