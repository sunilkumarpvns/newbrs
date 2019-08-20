package com.elitecore.nvsmx.sm.controller.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.ddf.DdfData;
import com.elitecore.corenetvertex.sm.ddf.DdfSprRelData;
import com.elitecore.corenetvertex.sm.spinterface.SpInterfaceData;
import com.elitecore.corenetvertex.sm.spr.SprData;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Used to manage all the operation related to SPR
 *@author dhyani.raval
 */
@ParentPackage(value = "sm")
@Namespace("/sm/spr")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","spr"}),

})
public class SprCTRL extends RestGenericCTRL<SprData> {

    private List<DatabaseData> databaseDataList;
    private List<SpInterfaceData> spInterfaceDataList;
    private String dbNameDbUrlMap;

    @Override
    public ACLModules getModule() {
        return ACLModules.SPR;
    }

    @Override
    public SprData createModel() {
        return new SprData();
    }

    public List<DatabaseData> getDatabaseDataList() {
        return databaseDataList;
    }

    public void setDatabaseDataList(List<DatabaseData> databaseDataList) {
        this.databaseDataList = databaseDataList;
    }

    public void setDbNameDbUrlMap(List<DatabaseData> dbDataLst) {
        Gson gson = GsonFactory.defaultInstance();
        this.dbNameDbUrlMap = gson.toJson(dbDataLst.stream().collect(Collectors.toMap(DatabaseData::getName, DatabaseData::getConnectionUrl)));
    }

    public String getDbNameDbUrlMap() {return dbNameDbUrlMap; }

    public List<SpInterfaceData> getSpInterfaceDataList() {
        return spInterfaceDataList;
    }

    public void setSpInterfaceDataList(List<SpInterfaceData> spInterfaceDataList) {
        this.spInterfaceDataList = spInterfaceDataList;
    }

    public String getAlternateIdFieldSuggestions() {
        List<String> alternateIdFieldSuggestionList = Collectionz.newArrayList();
        for (SPRFields sprFields : SPRFields.values()) {
            alternateIdFieldSuggestionList.add(sprFields.name());
        }
        Gson gson = GsonFactory.defaultInstance();
        return gson.toJson(alternateIdFieldSuggestionList);
    }

    @Override
    public void validate() {
        SprData sprData = (SprData) getModel();
        validateDatabaseDataSource(sprData);
        validateSpInterface(sprData);
        super.validate();
    }

    private void validateDatabaseDataSource(SprData sprData) {
        String databaseId = sprData.getDatabaseId();
        if(Strings.isNullOrBlank(databaseId) == false){
            DatabaseData databaseExists = CRUDOperationUtil.get(DatabaseData.class, databaseId);
            if(databaseExists == null){
                addFieldError("databaseId",getText("invalid.field.value"));
            }else{
                sprData.setDatabaseData(databaseExists);
            }
        }
    }

    private void validateSpInterface(SprData sprData) {
        String spInterfaceId = sprData.getSpInterfaceId();
        if(Strings.isNullOrBlank(spInterfaceId) == false){
            SpInterfaceData spInterfaceDataExist = CRUDOperationUtil.get(SpInterfaceData.class, spInterfaceId);
            if(spInterfaceDataExist == null){
                addFieldError("spInterfaceId",getText("invalid.field.value"));
            }else{
                sprData.setSpInterfaceData(spInterfaceDataExist);
            }
        }
    }

    @Override
    public void prepareValuesForSubClass(){
        List<DatabaseData> dbDataLst = CRUDOperationUtil.findAll(DatabaseData.class);
        setDatabaseDataList(dbDataLst);
        setSpInterfaceDataList(CRUDOperationUtil.findAll(SpInterfaceData.class));
        setDbNameDbUrlMap(dbDataLst);
    }

    @Override
    protected boolean prepareAndValidateDestroy(SprData sprData) {
        List associatedList = findAssociationWithDdf(sprData);

        if(Collectionz.isNullOrEmpty(associatedList)) {
            associatedList = findAssociationWithDdfSprRelation(sprData) ;
        }

        if (Collectionz.isNullOrEmpty(associatedList) == false) {
           addActionError("SPR '"+sprData.getName()+"' is associated with DDF");
            String associatedAreas = Strings.join(",", associatedList);
            getLogger().error(getLogModule(), "Error while deleting SPR " + sprData.getName() + ".Reason: SPR is associated with " + associatedAreas);
            return false;
        }

        return true;
    }

    public  List findAssociationWithDdf(SprData sprData) {
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(DdfData.class);
            criteria.add(Restrictions.eq("defaultSprData",sprData));
            return CRUDOperationUtil.findAllByDetachedCriteria(criteria);
        }catch (Exception e){
            LogManager.getLogger().error(getLogModule(), "Error while fetching associated DDF. Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
        }
        return null;
    }

    public  List findAssociationWithDdfSprRelation(SprData sprData) {
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(DdfSprRelData.class);
            criteria.add(Restrictions.eq("sprData",sprData));
            return CRUDOperationUtil.findAllByDetachedCriteria(criteria);

        }catch (Exception e){
            LogManager.getLogger().error(getLogModule(), "Error while fetching associated DDF. Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
        }
        return null;
    }
}
