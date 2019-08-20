package com.elitecore.nvsmx.sm.controller.lrn;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pd.lrn.LrnData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.ResourceDataGroupPredicate;
import com.google.gson.Gson;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author saloni.shah
 */
@ParentPackage(value = "sm")
@Namespace("/sm/lrn")
@Results({@Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {NVSMXCommonConstants.ACTION_NAME, "lrn"}),
})
public class LrnCTRL extends RestGenericCTRL<LrnData> {

	private static final long serialVersionUID = -1342976327159618815L;
	private List<NetworkData> networkList = new ArrayList<>();
	private List<OperatorData> operatorList = new ArrayList<>();

	private String operatorNetworkRelations;

	@Override
	public ACLModules getModule() {

		return ACLModules.LRN;
	}

	@Override
	public LrnData createModel() {

		return new LrnData();
	}

	@Override
	public void prepareValuesForSubClass() throws Exception {
		setOperatorList(CRUDOperationUtil.findAll(OperatorData.class));
		setNetworkList(CRUDOperationUtil.findAll(NetworkData.class));
		prepareOperationNetworkRelation();
	}

	private void prepareOperationNetworkRelation() {
		setNetworkBelongingToOperatorJson();
	}

	@Override
	public void validate() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called validate()");
		}
		LrnData lrnData = (LrnData) getModel();

		if(lrnData.getNetworkId() == null){
			lrnData.setNetworkData(null);
		} else {
			NetworkData networkData = CRUDOperationUtil.get(NetworkData.class, lrnData.getNetworkId());
			if(networkData==null){
				addFieldError("networkId",getText("lrn.network.not.exist"));
			}else{
				lrnData.setNetworkData(networkData);
			}
		}

		if(lrnData.getOperatorData() == null){
			addFieldError("operatorId", getText("lrn.operator.required"));
		} else {
			OperatorData operatorData = CRUDOperationUtil.get(OperatorData.class, lrnData.getOperatorId());
			if (operatorData == null) {
				addFieldError("operatorId", getText("lrn.operator.not.exist"));
			}else{
				lrnData.setOperatorData(operatorData);
			}
		}

		validateUniqueEntity(lrnData);
	}

	public List<NetworkData> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(List<NetworkData> networkList) {
		this.networkList = networkList;
	}

	public List<OperatorData> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(List<OperatorData> operatorList) {
		this.operatorList = operatorList;
	}

	public String getOperatorNetworkRelations() {
		return operatorNetworkRelations;
	}

	public void setOperatorNetworkRelations(String operatorNetworkRelations) {
		this.operatorNetworkRelations = operatorNetworkRelations;
	}

	public void validateUniqueEntity(LrnData lrnData) {
		boolean isAlreadyExist = isDuplicateEntity("lrn", lrnData.getResourceName() ,getMethodName(), lrnData);
		if(isAlreadyExist){
			addFieldError("lrn","LRN already exists");
		}
	}


	protected boolean isDuplicateEntity(String propertyName, String value, String mode, LrnData lrnData){
		try {

			return CRUDOperationUtil.isDuplicateProperty(mode, lrnData.getClass(), lrnData.getId(), value, propertyName);
		} catch (Exception e) {
			addActionError("Fail to perform duplicate entity check");
			getLogger().error(getLogModule(),"Fail to perform duplicate entity check.Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
		}
		return false;
	}

	public void setNetworkBelongingToOperatorJson() {

		List<NetworkData> networkDataList = CRUDOperationUtil.findAllWhichIsNotDeleted(NetworkData.class);
		Map<String, List<NetworkData>> networkDataMap = Maps.newHashMap();

		for (NetworkData networkData : networkDataList) {
			OperatorData operatorData = networkData.getOperatorData();
			String operatorId = operatorData.getId();

			if (networkDataMap.containsKey(operatorId)) {

				networkDataMap.get(operatorId).add(networkData);
			} else {
				List<NetworkData> networkDataListForOperatorID = new ArrayList<>();
				networkDataListForOperatorID.add(networkData);
				networkDataMap.put(operatorId, networkDataListForOperatorID);
			}
		}

		Gson gson = GsonFactory.defaultInstance();
		this.operatorNetworkRelations = gson.toJson(networkDataMap);
	}

}
