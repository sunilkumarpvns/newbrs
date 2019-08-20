package com.elitecore.nvsmx.sm.controller.mccmncgroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by vikas on 14/9/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/mccmncgroup")
@Results({
        @Result(name = SUCCESS, type = "redirectAction", params = {"actionName", "mcc-mnc-group"}),

})
public class MccMncGroupCTRL extends RestGenericCTRL<MccMncGroupData> {

    private static final long serialVersionUID = 1L;
    private List<BrandData> brandList = new ArrayList<>();
    private List<NetworkData> networkList = new ArrayList<>();


    public List<NetworkData> getNetworkList() {
        return networkList;
    }

    public void setNetworkList(List<NetworkData> networkList) {
        this.networkList = networkList;
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.MCCMNCGROUP;
    }

    @Override
    public MccMncGroupData createModel() {
        return new MccMncGroupData();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setBrandList(CRUDOperationUtil.findAll(BrandData.class));
    }

    @Override
    public HttpHeaders create(){
        MccMncGroupData mccMncGroupData = (MccMncGroupData) getModel();
        setNetworkDataFromId(mccMncGroupData);
        return super.create();
    }

    @Override
    public HttpHeaders update(){
        MccMncGroupData mccMncGroupData = (MccMncGroupData) getModel();
        setNetworkDataFromId(mccMncGroupData);
        return super.update();
    }

    private void setNetworkDataFromId(MccMncGroupData mccMncGroupData) {
        List<NetworkData> networkDatas = mccMncGroupData.getNetworkDatas();
        List<NetworkData> networkDataList = Collectionz.newArrayList();
        if(Collectionz.isNullOrEmpty(networkDatas) == false){

            mccMncGroupData.getNetworkDatas().forEach(networkData -> {
                        if (networkData != null && Strings.isNullOrBlank(networkData.getId()) == false) {
                            NetworkData networkDataFromDB = CRUDOperationUtil.get(NetworkData.class,networkData.getId());
                            if(networkDataFromDB != null){
                                networkDataList.add(networkDataFromDB);
                            }
                        }
                    }

            );

        }
        mccMncGroupData.setNetworkDatas(null);
        mccMncGroupData.setNetworkDatas(networkDataList);
    }

    @Override
    public boolean prepareAndValidateDestroy(MccMncGroupData mccMncGroupData) {
        List<RoutingTableData> attachedRoutingTables = getAttachedRoutingTables(mccMncGroupData.getId());
        if (Collectionz.isNullOrEmpty(attachedRoutingTables) == false) {
            List<String> attachedRoutingTableNames = attachedRoutingTables.stream().map(RoutingTableData::getName).collect(Collectors.toList());
            addActionError("Unable to delete '" + mccMncGroupData.getName() + "'. <br/>Reason :  " + ACLModules.MCCMNCGROUP.getDisplayLabel() + " is configured with one or more " + ACLModules.ROUTINGTABLE.getDisplayLabel());
            LogManager.getLogger().error(getLogModule(), "Unable to delete " + mccMncGroupData.getName() + " DataServiceType is configured with packages: " + attachedRoutingTableNames);
            return false;
        }
        return true;
    }

    @Override
    public void validate() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }
        MccMncGroupData mccMncGroupData = (MccMncGroupData) getModel();

        if (Strings.isNullOrBlank(mccMncGroupData.getBrandId()) == false) {
            BrandData brandData = CRUDOperationUtil.get(BrandData.class, mccMncGroupData.getBrandId());
            if (brandData == null) {
                addFieldError("brandId", getText("mccmncgroup.brandid.invalid"));
            }
        } else {
            addFieldError("brandId", getText("mccmncgroup.brandid.invalid"));
        }

        super.validate();
    }

    public List<BrandData> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<BrandData> brandList) {
        this.brandList = brandList;
    }

    public List<RoutingTableData> getAttachedRoutingTables(String mccmncGroupId) {
        MccMncGroupData mccMncGroupData = new MccMncGroupData();
        mccMncGroupData.setId(mccmncGroupId);
        Restrictions.eq("mccMncGroupData", mccMncGroupData);
        return CRUDOperationUtil.findAll(RoutingTableData.class,Restrictions.eq("mccMncGroupData", mccMncGroupData));
    }
}