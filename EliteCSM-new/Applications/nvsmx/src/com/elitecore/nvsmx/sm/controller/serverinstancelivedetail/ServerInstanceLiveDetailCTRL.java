package com.elitecore.nvsmx.sm.controller.serverinstancelivedetail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.DataSourceInfo;
import com.elitecore.corenetvertex.GatewayStatusInfo;
import com.elitecore.corenetvertex.GlobalListenersInfo;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.ServiceInfo;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceLiveDetailData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.remotecommunications.EndPoint;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceMethods;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by Ashish Kothari on 14/8/18.
 */
@ParentPackage(value = NVSMXCommonConstants.REST_PARENT_PKG_SM)
@Namespace("/sm/serverinstancelivedetail")
@org.apache.struts2.convention.annotation.Results({
		@Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {NVSMXCommonConstants.ACTION_NAME, "server-instance-live-detail"}),
})
public class ServerInstanceLiveDetailCTRL extends CreateNotSupportedCTRL<ServerInstanceLiveDetailData> {

	private ServerInstanceData serverInstanceData;

	@Override
	public HttpHeaders index() {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called index()");
		}
		getLogger().error(getLogModule(), "Index operation Not Supported");
		addActionError(getText("method.not.allowed"));
		getResponse().addHeader(ALLOWED_METHOD_HEADER, NON_CREATEABLE_RESOURCE_ALLOWED_METHOD);
		return new DefaultHttpHeaders(ERROR).withStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	public HttpHeaders show() { // View

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called show()");
		}

		try {

			ServerInstanceLiveDetailData serverInstanceLiveDetailData = (ServerInstanceLiveDetailData) getModel();
			serverInstanceData = CRUDOperationUtil.get(ServerInstanceData.class, serverInstanceLiveDetailData.getId());

			setGroupDetails(serverInstanceLiveDetailData);

			EndPoint netvertexEndPoint = EndPointManager.getInstance().getByServerCode(serverInstanceData.getId());

			if (netvertexEndPoint != null && netvertexEndPoint.isAlive()) {
				liveDetail(serverInstanceLiveDetailData);
				setActionChainUrl(super.getRedirectURL(METHOD_SHOW));
				return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
			} else {
				addActionError("No Live server found. Live detail not Available.");
				setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(serverInstanceData.getId()));
				return new DefaultHttpHeaders(REDIRECT_ACTION);
			}

		} catch (Exception e) {
			addActionError("Error while viewing " + getModule().getDisplayLabel());
			getLogger().error(getLogModule(), "Error while viewing" + getModule().getDisplayLabel() + ". Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
		}

		addActionError(getModule().getDisplayLabel() + " Not Found");
		return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
	}

	private void setGroupDetails(ServerInstanceLiveDetailData serverInstanceLiveDetailData) {
		String belonginsGroupNames = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(serverInstanceData.getGroups()));
		serverInstanceLiveDetailData.setGroups(serverInstanceData.getGroups());
		serverInstanceLiveDetailData.setGroupNames(belonginsGroupNames);
	}

	@Override
	public ACLModules getModule() {
		return ACLModules.SERVER_INSTANCE_LIVE_DETAIL;
	}

	@Override
	public ServerInstanceLiveDetailData createModel() {
		return new ServerInstanceLiveDetailData();
	}

	public void liveDetail(ServerInstanceLiveDetailData serverInstanceLiveDetailData) {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called liveDetail()");
		}
		ServerInstanceLiveDetailProvider liveDetailProvider = new ServerInstanceLiveDetailProvider(serverInstanceData);
		serverInstanceLiveDetailData.setServerInfo(getServerInfo(liveDetailProvider));
		serverInstanceLiveDetailData.setServiceInfoList(getServiceInfosIntoList(liveDetailProvider));
		serverInstanceLiveDetailData.setGatewayStatusInfoList(getGatewayStatusInfosIntoList(liveDetailProvider));
		serverInstanceLiveDetailData.setGlobalListenersInfoList(getGlobalListenersInfosIntoList(liveDetailProvider));
		serverInstanceLiveDetailData.setDataSourceInfoList(getDatasourceInfosIntoList(liveDetailProvider));
		serverInstanceLiveDetailData.setPolicyDetailList(getPolicyStatusInfoIntoList(liveDetailProvider));
		setModel(serverInstanceLiveDetailData);
	}

	private ArrayList<PolicyDetail> getPolicyStatusInfoIntoList(ServerInstanceLiveDetailProvider liveDetailProvider) {
		ArrayList<PolicyDetail> policyDetailList = Collectionz.newArrayList();
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(getLogModule(), "Method called getPolicyStatusInfoIntoList()");
			}
			String policyStatusInfoJson = liveDetailProvider.getLiveDataFor(WebServiceMethods.NETVERTEX_GET_POLICY_STATUS_INFO.name());
			if (Strings.isNullOrBlank(policyStatusInfoJson) == false) {
				Type type = new TypeToken<List<PolicyDetail>>() {
				}.getType();
				List<PolicyDetail> policyDetailListTemp = GsonFactory.defaultInstance().fromJson(policyStatusInfoJson, type);
				if (Collectionz.isNullOrEmpty(policyDetailListTemp) == false) {
					policyDetailList.addAll(policyDetailListTemp);
				}
			}
		} catch (Exception ex) {
			getLogger().error(getLogModule(), "Error while preparing policyStatusList. Reason: " + ex.getMessage());
			getLogger().trace(getLogModule(), ex);
		}
		return policyDetailList;
	}

	private List<GlobalListenersInfo> getGlobalListenersInfosIntoList(ServerInstanceLiveDetailProvider liveDetailProvider) {
		List<GlobalListenersInfo> globalListenersInfoList = Collectionz.newArrayList();
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(getLogModule(), "Method called getGlobalListenersInfosIntoList()");
			}
			String globalListenerInfoJson = liveDetailProvider.getLiveDataFor(WebServiceMethods.NETVERTEX_GET_GLOBAL_LISTENERS_INFO.name());
			if (Strings.isNullOrBlank(globalListenerInfoJson) == false) {
				Type type = new TypeToken<Map<String, GlobalListenersInfo>>() {
				}.getType();
				Map<String, GlobalListenersInfo> globalListenersInfoMap = GsonFactory.defaultInstance().fromJson(globalListenerInfoJson, type);
				if (Maps.isNullOrEmpty(globalListenersInfoMap) == false) {
					globalListenersInfoList.addAll(globalListenersInfoMap.values());
				}
			}
		} catch (Exception ex) {
			getLogger().error(getLogModule(), "Error while preparing GlobalListenersInfo List. Reason: " + ex.getMessage());
			getLogger().trace(getLogModule(), ex);
		}
		return globalListenersInfoList;
	}

	private ArrayList<GatewayStatusInfo> getGatewayStatusInfosIntoList(ServerInstanceLiveDetailProvider liveDetailProvider) {
		ArrayList<GatewayStatusInfo> gatewayStatusInfoList = Collectionz.newArrayList();
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(getLogModule(), "Method called getGatewayStatusInfosIntoList()");
			}
			String gatewaysInfoJson = liveDetailProvider.getLiveDataFor(WebServiceMethods.NETVERTEX_GET_GATEWAY_STATUS_INFO.name());
			if (Strings.isNullOrBlank(gatewaysInfoJson) == false) {
				Type type = new TypeToken<Map<String, List<GatewayStatusInfo>>>() {
				}.getType();
				Map<String, List<GatewayStatusInfo>> gatewayStatusInfoMap = GsonFactory.defaultInstance().fromJson(gatewaysInfoJson, type);
				for (List<GatewayStatusInfo> list : gatewayStatusInfoMap.values()) {
					gatewayStatusInfoList.addAll(list);
				}
			}
		} catch (Exception ex) {
			getLogger().error(getLogModule(), "Error while preparing GatewayStatusInfo List. Reason: " + ex.getMessage());
			getLogger().trace(getLogModule(), ex);
		}
		return gatewayStatusInfoList;
	}

	private List<ServiceInfo> getServiceInfosIntoList(ServerInstanceLiveDetailProvider liveDetailProvider) {
		List<ServiceInfo> serviceInfoList = Collectionz.newArrayList();
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(getLogModule(), "Method called getServiceInfosIntoList()");
			}
			String pcrfServiceInfoJson = liveDetailProvider.getLiveDataFor(WebServiceMethods.NETVERTEX_GET_SERVICES_INFO.name());

			if (Strings.isNullOrBlank(pcrfServiceInfoJson) == false) {
				Type type = new TypeToken<Map<String, ServiceInfo>>() {
				}.getType();
				Map<String, ServiceInfo> serviceInfoMap = GsonFactory.defaultInstance().fromJson(pcrfServiceInfoJson, type);
				if (Maps.isNullOrEmpty(serviceInfoMap) == false) {
					serviceInfoList.addAll(serviceInfoMap.values());
				}
			}

		} catch (Exception ex) {
			getLogger().error(getLogModule(), "Error while preparing ServiceInfo List. Reason: " + ex.getMessage());
			getLogger().trace(getLogModule(), ex);
		}
		return serviceInfoList;
	}

	private List<DataSourceInfo> getDatasourceInfosIntoList(ServerInstanceLiveDetailProvider liveDetailProvider) {
		List<DataSourceInfo> dataSourceInfoList = Collectionz.newArrayList();
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(getLogModule(), "Method called getDatasourceInfosIntoList()");
			}
			String dataSourceInfoJson = liveDetailProvider.getLiveDataFor(WebServiceMethods.NETVERTEX_GET_DATASOURCE_INFO.name());
			if (Strings.isNullOrBlank(dataSourceInfoJson) == false) {
				Type type = new TypeToken<Map<String, DataSourceInfo>>() {
				}.getType();
				Map<String, DataSourceInfo> dataSourceInfoMap = GsonFactory.defaultInstance().fromJson(dataSourceInfoJson, type);
				if (Maps.isNullOrEmpty(dataSourceInfoMap) == false) {
					dataSourceInfoList.addAll(dataSourceInfoMap.values());
				}
			}
		} catch (Exception ex) {
			getLogger().error(getLogModule(), "Error while preparing DataSourceInfo List. Reason: " + ex.getMessage());
			getLogger().trace(getLogModule(), ex);
		}
		return dataSourceInfoList;
	}

	private ServerInfo getServerInfo(ServerInstanceLiveDetailProvider liveDetailProvider) {
		ServerInfo serverInfo = null;
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(getLogModule(), "Method called getServerInfo()");
			}

			String serverInfoJson = liveDetailProvider.getLiveDataFor(WebServiceMethods.NETVERTEX_GET_SERVER_INFO.name());
			if (Strings.isNullOrBlank(serverInfoJson) == false) {
				serverInfo = GsonFactory.defaultInstance().fromJson(serverInfoJson, ServerInfo.class);
				serverInfo.setLastConfigurationReloadTime();
			}

		} catch (Exception ex) {
			getLogger().error(getLogModule(), "Error while preparing ServerInfo. Reason: " + ex.getMessage());
			getLogger().trace(getLogModule(), ex);
		}
		return serverInfo;
	}

	public String getPolicyDetailListAsJson() {
		ServerInstanceLiveDetailData model = (ServerInstanceLiveDetailData) getModel();
		JsonArray policyDetailJsonArray = GsonFactory.defaultInstance().toJsonTree(model.getPolicyDetailList(), new TypeToken<List<PolicyDetail>>() {
		}.getType()).getAsJsonArray();
		return policyDetailJsonArray.toString();
	}

	public String getServiceInfoListAsJson() {
		ServerInstanceLiveDetailData model = (ServerInstanceLiveDetailData) getModel();
		JsonArray serviceInfoListJsonArray = GsonFactory.defaultInstance().toJsonTree(model.getServiceInfoList(), new TypeToken<List<ServiceInfo>>() {
		}.getType()).getAsJsonArray();
		return serviceInfoListJsonArray.toString();
	}

	public String getGatewayStatusInfoListAsJson() {
		ServerInstanceLiveDetailData model = (ServerInstanceLiveDetailData) getModel();
		JsonArray gatewayStatusInfoListJsonArray = GsonFactory.defaultInstance().toJsonTree(model.getGatewayStatusInfoList(), new TypeToken<List<GatewayStatusInfo>>() {
		}.getType()).getAsJsonArray();
		return gatewayStatusInfoListJsonArray.toString();
	}

	public String getGlobalListenersInfoListAsJson() {
		ServerInstanceLiveDetailData model = (ServerInstanceLiveDetailData) getModel();
		JsonArray globalListenersInfoListJsonArray = GsonFactory.defaultInstance().toJsonTree(model.getGlobalListenersInfoList(), new TypeToken<List<GlobalListenersInfo>>() {
		}.getType()).getAsJsonArray();
		return globalListenersInfoListJsonArray.toString();
	}

	public String getDataSourceInfoListAsJson() {
		ServerInstanceLiveDetailData model = (ServerInstanceLiveDetailData) getModel();
		JsonArray dataSourceInfoListJsonArray = GsonFactory.defaultInstance().toJsonTree(model.getDataSourceInfoList(), new TypeToken<List<DataSourceInfo>>() {
		}.getType()).getAsJsonArray();
		return dataSourceInfoListJsonArray.toString();
	}

	@Override
	public String getRedirectURL(String id) {
		StringBuilder sb = new StringBuilder();
		sb.append(ACLModules.SERVER_INSTANCE.getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
				.append(ACLModules.SERVER_INSTANCE.getActionURL()[0]).append(CommonConstants.FORWARD_SLASH)
				.append(id);
		return sb.toString();
	}
}

