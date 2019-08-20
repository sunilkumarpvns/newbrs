package com.elitecore.nvsmx.sm.controller.routingtable;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingAction;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableGatewayRelData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by vikas on 22/9/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/routingtable")
@Results({
        @Result(name = SUCCESS, type = "redirectAction", params = {"actionName", "routing-table"}),
})
public class RoutingTableCTRL extends RestGenericCTRL<RoutingTableData> {

    private static final long serialVersionUID = 1L;
    private List<MccMncGroupData> mccMncGroupList = new ArrayList<>();
    private List<DiameterGatewayData> diameterGatewayList = new ArrayList<>();

    private static final String ROUTINGTABLE_GATEWAY_REL_DATA_LIST = "routingTableGatewayRelDataList";
    private static final String DIAMETER_GATEWAY_DATA_ID = "diameterGatewayData.id";
    private static final String WEIGHTAGE = "weightage";

    private static final Predicate<? super RoutingTableGatewayRelData> EMPTY_ROUTING_TABLE_PREDICATE = routingTableGatewayRelData -> {
        if (routingTableGatewayRelData == null || routingTableGatewayRelData.getDiameterGatewayData() == null) {
            return false;
        }
        return true;
    };

    @Override
    public ACLModules getModule() {
        return ACLModules.ROUTINGTABLE;
    }

    @Override
    public RoutingTableData createModel() {
        return new RoutingTableData();
    }

    @Override
    public HttpHeaders create() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called create()");
        }

        loadRoutingTableGatewayRelData();
        return super.create();
    }

    @Override
    public HttpHeaders update() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }

        loadRoutingTableGatewayRelData();
        return super.update();
    }

    private void loadRoutingTableGatewayRelData() {

        RoutingTableData routingTableData = (RoutingTableData) getModel();
        if (Collectionz.isNullOrEmpty(routingTableData.getRoutingTableGatewayRelDataList()) == false) {
            Collectionz.filter(routingTableData.getRoutingTableGatewayRelDataList(), EMPTY_ROUTING_TABLE_PREDICATE);

            if (RoutingAction.PROXY.getDisplayValue().equals(routingTableData.getAction())) {
                routingTableData.getRoutingTableGatewayRelDataList().forEach(routingTableGatewayRelData -> routingTableGatewayRelData.setRoutingTableData(routingTableData));
                loadRoutingTableGatewayRelDatas(routingTableData.getRoutingTableGatewayRelDataList());
            } else {
                routingTableData.getRoutingTableGatewayRelDataList().clear();
            }
        }

    }

    private void loadRoutingTableGatewayRelDatas(List<RoutingTableGatewayRelData> routingTableGatewayRelDataList) {
        for (RoutingTableGatewayRelData routingTableGatewayRelData : routingTableGatewayRelDataList) {
            DiameterGatewayData diameterGatewayData = CRUDOperationUtil.get(DiameterGatewayData.class, routingTableGatewayRelData.getDiameterGatewayData().getId());
            routingTableGatewayRelData.getDiameterGatewayData().setName(diameterGatewayData.getName());
        }
    }

    @Override
    public void validate() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }

        RoutingTableData routingTableData = (RoutingTableData) getModel();

        validateMccMncGroupData(routingTableData);

        if (RoutingAction.PROXY.getDisplayValue().equals(routingTableData.getAction())) {
            if (Collectionz.isNullOrEmpty(routingTableData.getRoutingTableGatewayRelDataList())) {
                addFieldError("action", getText("routingtable.gateway.required"));
            } else {
                validateRoutingTableGatewayRel(routingTableData.getRoutingTableGatewayRelDataList());
            }
        }

        super.validate();
    }

    private void validateMccMncGroupData(RoutingTableData routingTableData) {

        if (RoutingType.MCC_MNC_BASED.getValue().equals(routingTableData.getType())) {
            if (routingTableData.getMccMncGroupData() == null) {
                addFieldError("mccMncGroupData", getText("routingtable.mccMncGroupData.required"));
            } else {
                MccMncGroupData mccMncGroupData = CRUDOperationUtil.get(MccMncGroupData.class, routingTableData.getMccMncGroupId());
                if (mccMncGroupData == null) {
                    addFieldError("mccMncGroupData", getText("routingtable.mccMncGroupData.invalid"));
                }else{
                    routingTableData.setMccMncGroupData(mccMncGroupData);
                }
            }
        }
    }

    private void validateRoutingTableGatewayRel(List<RoutingTableGatewayRelData> routingTableGatewayRelDataList) {

        Set<String> gatewayIds = Collectionz.newHashSet();
        int i = 0;
        for (RoutingTableGatewayRelData routingTableGatewayRelData : routingTableGatewayRelDataList) {
            DiameterGatewayData diameterGatewayData = routingTableGatewayRelData.getDiameterGatewayData();
            if (diameterGatewayData == null) {
                addFieldError(ROUTINGTABLE_GATEWAY_REL_DATA_LIST + "[" + i + "]" + "." + DIAMETER_GATEWAY_DATA_ID, getText("routingtable.gateway.select.error"));
            } else {
                if (gatewayIds.contains(diameterGatewayData.getId())) {
                    addFieldError(ROUTINGTABLE_GATEWAY_REL_DATA_LIST + "[" + i + "]" + "." + DIAMETER_GATEWAY_DATA_ID, getText("routingtable.gateway.duplicate"));
                } else {
                    DiameterGatewayData diameterGatewayDataFromDb = CRUDOperationUtil.get(DiameterGatewayData.class, diameterGatewayData.getId());
                    if (diameterGatewayDataFromDb == null) {
                        addFieldError(ROUTINGTABLE_GATEWAY_REL_DATA_LIST + "[" + i + "]" + "." + DIAMETER_GATEWAY_DATA_ID, getText("routingtable.gateway.invalid"));
                    } else {
                        gatewayIds.add(diameterGatewayData.getId());
                    }
                }
            }

            Integer weightage = routingTableGatewayRelData.getWeightage();
            if (weightage == null || weightage < 0 || weightage > 10) {
                addFieldError(ROUTINGTABLE_GATEWAY_REL_DATA_LIST + "[" + i + "]" + "." + WEIGHTAGE, getText("routingtable.weightage.invalid"));
            }
            i++;
        }

    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setMccMncGroupList(CRUDOperationUtil.findAll(MccMncGroupData.class));
        setDiameterGatewayList(CRUDOperationUtil.findAll(DiameterGatewayData.class));
    }

    public List<MccMncGroupData> getMccMncGroupList() {
        return mccMncGroupList;
    }

    public void setMccMncGroupList(List<MccMncGroupData> mccMncGroupList) {
        this.mccMncGroupList = mccMncGroupList;
    }

    public List<DiameterGatewayData> getDiameterGatewayList() {
        return diameterGatewayList;
    }

    public void setDiameterGatewayList(List<DiameterGatewayData> diameterGatewayList) {
        this.diameterGatewayList = diameterGatewayList;
    }


}