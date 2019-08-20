package com.elitecore.nvsmx.pd.controller.cleanup;

import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import com.elitecore.nvsmx.pd.model.cleanup.CleanupData;
import com.elitecore.nvsmx.pd.model.cleanup.PCCEntities;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/cleanup")
@Results({ @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME, "clean-up" }),

})
public class CleanUpCTRL extends RestGenericCTRL<CleanupData> {


    private static final String MODULE = "CLEAN-UP-CTRL";

    @Override
    public ACLModules getModule() {
        return ACLModules.DATAPKG;
    }

    @Override
    public CleanupData createModel() {
        return new CleanupData();
    }

    @Override
    @SkipValidation
    public HttpHeaders destroy() {
        try {
            deleteEntity();
        }catch (Exception e){
            getLogger().error(MODULE,"Error while deleting entities. Reason: " + e.getMessage());
            getLogger().trace(MODULE,e );
        }
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
    }

    private void deleteEntity() throws Exception {

        if(Objects.isNull(getModel())){
            getLogger().error(MODULE, "No clean up can be performed. Reason: Clean up data is not received");
            return ;
        }
        CleanupData cleanupData = (CleanupData) getModel();
        String entityName = cleanupData.getEntityName();
        if(StringUtils.isEmpty(entityName)){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No clean up can be performed. Reason: Clean up entity name is not received");
            }
            return;
        }
        PCCEntities pccEntities = PCCEntities.fromEntityName(entityName);
        if(Objects.isNull(pccEntities)){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "No clean up can be performed. Reason: invalid entity type: "+ entityName +" received");
            }
            return ;
        }
        if(CollectionUtils.isEmpty(cleanupData.getIds())){
            getLogger().error(MODULE,"No clean up can be performed. Reason: No id received");
            return;
        }

        for(String id:cleanupData.getIds()){
            Object entity = CRUDOperationUtil.get(pccEntities.getEntityClass(), id);
            if(Objects.isNull(entity)){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "No entity "+ entityName +" exist with given id: "+id);
                }
                continue;
            }
            if(PCCEntities.DATA_PACKAGE == pccEntities){
                ImportExportCRUDOperationUtil.removePackageHierarchy((PkgData) entity, HibernateSessionFactory::getSession);
                continue;
            }
            CRUDOperationUtil.delete(entity);
            CRUDOperationUtil.flushSession();
        }
    }


    @Override
    public String authorize() throws Exception {
        return SUCCESS;
    }
}
