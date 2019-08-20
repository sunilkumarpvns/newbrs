package com.elitecore.corenetvertex.pm.bod;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.apache.commons.lang.SystemUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BoDQosMultiplier implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double sessionMultiplier;
    private Map<Long, BoDServiceMultiplier> dataServiceIdToServiceMultipliers;

    public static final ToStringStyle BOD_QOS_MULTIPLIER_TO_STRING_STYLE = new BoDQosMultiplierToStringStyle();

    public BoDQosMultiplier(Double sessionMultiplier) {
        this.sessionMultiplier = sessionMultiplier;
        this.dataServiceIdToServiceMultipliers = new HashMap<>();
    }

    public BoDQosMultiplier addDataServiceIdToServiceMultiplier(Long serviceIdentifier, BoDServiceMultiplier serviceMultiplier){
        this.dataServiceIdToServiceMultipliers.put(serviceIdentifier, serviceMultiplier);
        return this;
    }

    public Double getSessionMultiplier(){
        return sessionMultiplier;
    }

    public Map<Long, BoDServiceMultiplier> getDataServiceIdToServiceMultipliers() {
        return dataServiceIdToServiceMultipliers;
    }


    @Override
    public String toString(){
        ToStringBuilder builder = new ToStringBuilder(this, BOD_QOS_MULTIPLIER_TO_STRING_STYLE);
        builder.append("Session Multiplier", sessionMultiplier);

        builder.append("Service Multipliers:");
        if(Maps.isNullOrEmpty(this.dataServiceIdToServiceMultipliers)){
            builder.append("\tNo Service Multipliers found.");
        } else{
            for(Map.Entry<Long, BoDServiceMultiplier> entry : dataServiceIdToServiceMultipliers.entrySet()){
                builder.append("\tData Service ID", entry.getValue().getDataServiceTypeID());
                builder.append("\tData Service Name", entry.getValue().getDataServicetyTypeName());
                builder.append("\tService Multiplier", entry.getValue());
                builder.append(SystemUtils.LINE_SEPARATOR);
            }
        }

        return builder.toString();
    }

    private static final class BoDQosMultiplierToStringStyle extends ToStringStyle.CustomToStringStyle {
        private static final long serialVersionUID = 1L;
        BoDQosMultiplierToStringStyle() {
            super();
            this.setContentStart(SystemUtils.LINE_SEPARATOR);
            this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getTabs(2));
        }
    }
}
