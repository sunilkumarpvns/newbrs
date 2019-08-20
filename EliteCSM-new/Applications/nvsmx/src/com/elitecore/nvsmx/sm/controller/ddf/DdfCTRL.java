package com.elitecore.nvsmx.sm.controller.ddf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.ddf.DdfData;
import com.elitecore.corenetvertex.sm.ddf.DdfSprRelData;
import com.elitecore.corenetvertex.sm.spr.SprData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Used to manage DDF Configuration
 * @author dhyani.raval
 */
@ParentPackage(value = "sm")
@Namespace("/sm/ddf")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","ddf"}),

})
public class DdfCTRL extends CreateNotSupportedCTRL<DdfData> {

    private List<SprData> sprDataList;

    @Override
    public ACLModules getModule() {
        return ACLModules.DDF;
    }

    @Override
    public DdfData createModel() {
        return new DdfData();
    }


    @Override
    public HttpHeaders index() {
        return show();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setSprDataList(CRUDOperationUtil.findAll(SprData.class));
    }

    @Override
    public HttpHeaders show() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called show()");
        }

        DdfData  ddfData = (DdfData) getModel();

        if (ddfData == null){
            setActionChainUrl(getRedirectURL("show"));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        }

        if (Strings.isNullOrBlank(ddfData.getId()) || "*".equals(ddfData.getId())) {
            List all = CRUDOperationUtil.findAll(getModel().getClass());
            if(Collectionz.isNullOrEmpty(all) == false){
                ddfData = (DdfData) all.get(0);
                setModel(ddfData);
            }
        } else {
            setModel(CRUDOperationUtil.get(ddfData.getClass(), ddfData.getId()));
        }
        setActionChainUrl(getRedirectURL("show"));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
    }

    /**
     * Override because don't want to check duplicate entry
     * @return
     */
    @Override
    public HttpHeaders update() {

        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called update()");
        }
        try {
            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL("edit"));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(500);
            }
            DdfData ddfData = (DdfData) getModel();
            ddfData.setDefaultSprData(CRUDOperationUtil.get(SprData.class,ddfData.getDefaultSprDataId()));
            int i = 1;
            filterEmptyDdfSprRelDataList(ddfData.getDdfSprRelDatas());
            for(DdfSprRelData ddfSprRelData : ddfData.getDdfSprRelDatas()) {
                if(ddfSprRelData != null) {
                    ddfSprRelData.setSprData(CRUDOperationUtil.get(SprData.class,ddfSprRelData.getSprDataId()));
                    ddfSprRelData.setOrderNo(i);
                    i++;
                }

            }
            ddfData.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.merge(ddfData);

            String message = getModule().getDisplayLabel() + " <b><i>" + ddfData.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(ddfData,ddfData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(),ddfData.getHierarchy(), message);

            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();

        }catch (Exception e){
            LogManager.getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    private void filterEmptyDdfSprRelDataList(List<DdfSprRelData> ddfSprRelDataList) {

        Collectionz.filter(ddfSprRelDataList, ddfSprRelData -> ddfSprRelData == null ? false : true);
    }

    public String getDdfSprRelationDatasAsJson() {
        Gson gson = GsonFactory.defaultInstance();
        DdfData ddfData = (DdfData) getModel();
        return gson.toJsonTree(ddfData.getDdfSprRelDatas(),new TypeToken<List<DdfSprRelData>>() {}.getType()).getAsJsonArray().toString();
    }

    public List<SprData> getSprDataList() {
        return sprDataList;
    }

    public void setSprDataList(List<SprData> sprDataList) {
        this.sprDataList = sprDataList;
    }

    @Override
    public void validate() {

        DdfData ddfData = (DdfData) getModel();

        for(DdfSprRelData ddfSprRelData : ddfData.getDdfSprRelDatas()) {
            if(ddfSprRelData != null) {

                if(Strings.isNullOrBlank(ddfSprRelData.getIdentityPattern())) {
                    addFieldError("identityPattern","Identity Pattern can not be empty");
                }
                if(Strings.isNullOrBlank(ddfSprRelData.getSprDataId())) {
                    addFieldError("sprDataId","SPR can not be empty");
                }
            }

        }
    }
}
