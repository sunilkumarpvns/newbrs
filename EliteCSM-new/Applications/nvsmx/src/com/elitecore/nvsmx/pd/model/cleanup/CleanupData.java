package com.elitecore.nvsmx.pd.model.cleanup;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;

import java.util.List;


public class CleanupData extends ResourceData {

   private String entityName;
   private List<String> ids;


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String getResourceName() {
        return CommonConstants.EMPTY_STRING;
    }
}
