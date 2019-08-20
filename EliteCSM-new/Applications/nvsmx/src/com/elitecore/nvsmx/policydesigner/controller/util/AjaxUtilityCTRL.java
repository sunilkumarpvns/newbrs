package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionFactory;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.datasource.IConnectionDataSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.policydesigner.controller.util.ProductOfferUtility.doesBelongToGroup;

/**
 * common utility controller for ajax call
 * @author Dhyani.Raval
 *
 */
public class AjaxUtilityCTRL extends ActionSupport implements ServletRequestAware  , ServletResponseAware{

	private static final long serialVersionUID = 1L;
	private static final int PROCEDURE_CALL_TIMEOUT = 1000;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private static final String MODULE = AjaxUtilityCTRL.class.getSimpleName();
	private List<NetworkData> networkList = new ArrayList<>();
	private PkgData pkgData = new PkgData();

	/**
	 * remove the session attribute for unauthorized user 
	 */
	public void removeSesionAttribute(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called removeSesionAttribute()");
		}
		try{
			request.getSession().removeAttribute(Attributes.UNAUTHORIZED_USER);
			request.removeAttribute(Attributes.UNAUTHORIZED_USER);
		}catch(Exception e){
			getLogger().error(MODULE, "Error while removing session attribute for unauthorized user");
			getLogger().trace(MODULE,e);
		}
	}

	public void checkDatabaseConnection() throws Exception{
		Connection connection = null;
		ResultSet resultSet = null;
		PrintWriter out = null;
		Client client = null;
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called checkDatabaseConnection()");
		}

		Gson gson = GsonFactory.defaultInstance();

		try {
			out = response.getWriter();
			String connectionUrl = request.getParameter("connectionUrl");
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			String plainTextPassword = request.getParameter("isPlainTextPassword");

			if(Strings.toBoolean(plainTextPassword) == false) {
				//if call is from create then password will be plain text so no need to decrypt it
				password = PasswordUtility.getDecryptedPassword(password);
			}

			if(Strings.isNullOrBlank(connectionUrl) == false && Strings.isNullOrBlank(userName) == false && Strings.isNullOrBlank(password) == false ) {

				if (connectionUrl.contains(CommonConstants.VOLTDB)) {
					ClientConfig config = new ClientConfig(connectionUrl, password);

					config.setProcedureCallTimeout(PROCEDURE_CALL_TIMEOUT);
					config.setTopologyChangeAware(true);
					config.setReconnectOnConnectionLoss(true);
					client = ClientFactory.createClient(config);

					addConnectionToClient(client, connectionUrl);
				} else {
					IConnectionDataSource dataBaseConnection = new DBConnectionFactory().createDataBaseConnection(connectionUrl,userName, password, 1, 1, new HashMap<>());
					dataBaseConnection.init();
					connection = dataBaseConnection.getConnection();
				}

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("sqlstate","200");
				jsonObject.addProperty("message","");
				out.print(gson.toJson(jsonObject));
			}
			out.flush();

		} catch (DataSourceException e){
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("sqlstate",e.getSQLState());
			jsonObject.addProperty("message",e.getMessage());
			out.print(gson.toJson(jsonObject));
			getLogger().error(MODULE, "Error while checking database connection. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);

		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("sqlstate", ResultCode.SERVICE_UNAVAILABLE.name);
			jsonObject.addProperty("message","Error while checking database connection. Reason: "+e.getMessage());
			out.print(gson.toJson(jsonObject));
			getLogger().error(MODULE, "Error while checking database connection. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
		} finally {
			Closeables.closeQuietly(out);
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(connection);
			client.close();
		}

	}




	public void fetchTableNamesByDatabaseId() {
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called fetchTableNamesByDatabaseId()");
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String databaseId = request.getParameter("databaseId");

			if(Strings.isNullOrBlank(databaseId) == false) {
				Gson gson = GsonFactory.defaultInstance();
				out.print(gson.toJson(CRUDOperationUtil.fetchTableNameOrColumnNameByDatabase(getDatabaseData(databaseId),null)));
			}
			out.flush();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching column name from database. Reason: "+e.getMessage());
			getLogger().trace(MODULE,e);

		} finally {
			Closeables.closeQuietly(out);
		}

	}

	public DatabaseData getDatabaseData(String id) {
		return  CRUDOperationUtil.get(DatabaseData.class, id);
	}
	
	/*this method find partner name by account id*/
	
	public void findPartnerNameById(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called findPartnerNameById()");
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String accountId = request.getParameter("id");
			
			if(Strings.isNullOrBlank(accountId) == false) {
				AccountData accountData = CRUDOperationUtil.get(AccountData.class, accountId);
				if( accountData != null){
					out.print(accountData.getPartnerData().getName());
				}
			}else{
				if(getLogger().isDebugLogLevel()){
					getLogger().debug(MODULE, "Account Id null");
				}
			}
			out.flush();
		}
		catch (Exception e) {
			getLogger().error(MODULE, "Error  Reason: "+e.getMessage());
			getLogger().trace(MODULE,e);

		} finally {
			Closeables.closeQuietly(out);
		}
	 
	}

	

	public void fetchColumnNamesByTableName() {
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called fetchColumnNamesByTableName()");
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String databaseId = request.getParameter("databaseId");
			String tableName = request.getParameter("tableName");

			if(Strings.isNullOrBlank(databaseId) == false) {
				Gson gson = GsonFactory.defaultInstance();
				out.print(gson.toJson(CRUDOperationUtil.fetchTableNameOrColumnNameByDatabase(getDatabaseData(databaseId),tableName)));
			}
			out.flush();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching column name from database. Reason: "+e.getMessage());
			getLogger().trace(MODULE,e);

		} finally {
			Closeables.closeQuietly(out);
		}

	}
	public String pkgData(){
		LogManager.getLogger().debug(MODULE, "Called pkgData()");
		try {
			String pkgId = request.getParameter("id");
			PkgData pkgDataFromDB = CRUDOperationUtil.get(PkgData.class, pkgId);
			setPkgData(pkgDataFromDB);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching network Data for brand: Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return "ajax_pkgdata_jsp";
	}
	public String networkData(){
		LogManager.getLogger().debug(MODULE, "Called networkData()");
		try {
			String brandId = request.getParameter("id");
			BrandData brandData = new BrandData();
			brandData.setId(brandId);
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MccMncGroupData.class)
					.createAlias("networkDatas", "network")
					.add(Restrictions.eq("brandData", brandData))
					.setProjection(Property.forName("network.id"));
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(NetworkData.class, "networkData")
					.add(Restrictions.eq("brandData", brandData))
					.add(Subqueries.propertyNotIn("networkData.id", detachedCriteria));
			setNetworkList(criteria.list());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching network Data for brand: Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
		return "ajax_success_jsp";
	}

	public void childAttributeForParent(){
		LogManager.getLogger().debug(MODULE, "Called childAttributeForParent()");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String parentAttributeKey =  request.getParameter("attribute");
			List<AttributeData> attributeDatas = CRUDOperationUtil.get(AttributeData.class, Order.asc("name"));
			Predicate<AttributeData> attributePredicate = attributeData -> CommunicationProtocol.RADIUS.name().equalsIgnoreCase(attributeData.getDictionaryType()) ? true : false;
			Collectionz.filter(attributeDatas,attributePredicate);

			out.print(getChildAttributes(parentAttributeKey,attributeDatas));
			out.flush();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while fetching network Data for brand: Reason: "+e.getMessage());
			getLogger().trace(MODULE,e);
		}finally{
			Closeables.closeQuietly(out);
		}
	}

	private String getChildAttributes(String parentAttributeKey, List<AttributeData> attributeDatas) {
		JsonObject json = new JsonObject();
		for(AttributeData attributeData : attributeDatas) {
			String key = getKey(attributeData.getVendorInformation().getVendorId(), attributeData.getAttributeId());
			if(parentAttributeKey.equalsIgnoreCase(key)){
				getChildData(key,attributeData.getChildAttributes(),json);
			} else if(parentAttributeKey.contains(key)) {
				checkForChildAttributeMatched(parentAttributeKey, key, attributeData.getChildAttributes(), json);
			}
		}
        return json.toString();

	}

	private void getChildData(String key,List<AttributeData> childAttributes,JsonObject json) {
		if (Collectionz.isNullOrEmpty(childAttributes) == false) {
				for (AttributeData childAttr : childAttributes) {
					String childAttrKey = getKey(key,childAttr.getAttributeId());
					json.addProperty(childAttrKey,childAttr.getName());
				}
			}
	}


	private void checkForChildAttributeMatched(String parentAttributeKey,String key, List<AttributeData> childAttributes,JsonObject json){
		if(Collectionz.isNullOrEmpty(childAttributes) == false){
			for(AttributeData childAttribute : childAttributes){
				String childAttKey = getKey(key ,childAttribute.getAttributeId());
				if(childAttKey.equalsIgnoreCase(parentAttributeKey)){
					getChildData(childAttKey,childAttribute.getChildAttributes(),json);
				}else if(parentAttributeKey.contains(childAttKey)){
					checkForChildAttributeMatched(parentAttributeKey,childAttKey,childAttribute.getChildAttributes(),json);

				}
			}
		}

	}

	private String getKey(String parentAttributeKey, String attributeId) {
		StringBuilder sb = new StringBuilder();
		sb.append(parentAttributeKey);
		sb.append(CommonConstants.COLON);
		sb.append(attributeId);
		return sb.toString();
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public List<NetworkData> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(List<NetworkData> networkList) {
		this.networkList = networkList;
	}

	public void getRateUnitsUoms() throws Exception{
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called getRateUnitsUoms()");
		}
		PrintWriter out = null;
		Gson gson = GsonFactory.defaultInstance();

		try {
			out = response.getWriter();
			String uomName = request.getParameter("pulseUnit");

			List<Uom> uomsOfSameTypeFromName = Collectionz.newArrayList();
			uomsOfSameTypeFromName.add(Uom.PERPULSE);
			uomsOfSameTypeFromName.addAll(Uom.getUomsOfSameTypeFromName(uomName));
			JsonArray jsonArray = gson.toJsonTree(uomsOfSameTypeFromName, new TypeToken<List<Uom>>() {}.getType()).getAsJsonArray();
			out.print(gson.toJson(jsonArray));

			out.flush();

		} catch (Exception e) {
			getLogger().error(MODULE, "Error fetch Uom list for Rate Unit. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
		} finally {
			Closeables.closeQuietly(out);
		}
	}

	public void getPackages() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called getPackages()");
		}
		PrintWriter out = null;
		Gson gson = GsonFactory.defaultInstance();

		try {
			out = response.getWriter();
			String pkgType = request.getParameter("pkgType");
			if (Strings.isNullOrBlank(pkgType)) {
				pkgType = PkgType.BASE.name();
			}
			final String currency = request.getParameter("currency");

			JsonArray jsonArray = null;

			Map<String, PkgData> recentPackage = getIdToPackagesFromDb();
			if (PkgType.BASE.name().equalsIgnoreCase(pkgType)) {
				List<Package> packages = PolicyManager.getInstance().getAllPackageDatas().stream()
						.filter(dataPkg -> PkgType.BASE.name().equals(dataPkg.getType()) && dataPkg.getStatus() != PolicyStatus.FAILURE
								&& doesBelongToGroup(dataPkg.getGroupIds(), getStaffBelongingGroups())
								&& dataPkg.getMode().getOrder() >= PkgMode.TEST.getOrder()
								&& (currency==null || dataPkg.getCurrency().equals(currency))
								&& isPackageDeleted(recentPackage.get(dataPkg.getId()))==false)
						.filter(dataPkg -> doesBelongToGroup(dataPkg.getGroupIds(), getGroupIdsFromRequest())).collect(Collectors.toList());
				jsonArray = gson.toJsonTree(packages, new TypeToken<List<Package>>() {
				}.getType()).getAsJsonArray();
			} else if (PkgType.ADDON.name().equalsIgnoreCase(pkgType)) {
				List<AddOn> addOns = PolicyManager.getInstance().getActiveLiveAddOnDatas().stream()
						.filter(addOnPkg -> doesBelongToGroup(addOnPkg.getGroupIds(), getStaffBelongingGroups())
								&& addOnPkg.getMode().getOrder() >= PkgMode.TEST.getOrder() && addOnPkg.getStatus() != PolicyStatus.FAILURE
								&& (currency==null || addOnPkg.getCurrency().equals(currency))
								&& isPackageDeleted(recentPackage.get(addOnPkg.getId()))==false)
						.filter(addOnPkg -> doesBelongToGroup(addOnPkg.getGroupIds(), getGroupIdsFromRequest())).collect(Collectors.toList());
				jsonArray = gson.toJsonTree(addOns).getAsJsonArray();
			}
			out.print(gson.toJson(jsonArray));
			out.flush();
		} catch (Exception e) {
			getLogger().error(MODULE, "Error fetching package list. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		} finally {
			Closeables.closeQuietly(out);
		}
	}

	private Map<String, PkgData> getIdToPackagesFromDb(){
		List<PkgData> dataPackages = HibernateReader.readAll(PkgData.class, HibernateSessionFactory.getSession());
		return dataPackages.stream().collect(
				Collectors.toMap(x -> x.getId(), x-> x));
	}

	private boolean isPackageDeleted(PkgData pkgData){

		if(pkgData == null){
			return true;
		}

		return CommonConstants.STATUS_DELETED.equals(pkgData.getStatus());
	}

	private void addConnectionToClient(Client client, String connectionUrl) throws IOException {

		String commaSeperatedIpAddressList = connectionUrl.split("//")[1];

		String[] ipAddressList = commaSeperatedIpAddressList.split(",");

		for (String ipAddress : ipAddressList) {
			String[] ipPort = ipAddress.split(":");
			client.createConnection(ipPort[0], Integer.parseInt(ipPort[1]));
		}
	}

	private List<String> getStaffBelongingGroups() {
		String groups = (String)request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);

		if(groups==null){
			groups = "";
		}

		return CommonConstants.COMMA_SPLITTER.split(groups);
	}

	private List<String> getGroupIdsFromRequest() {
		String groups = request.getParameter(Attributes.GROUPIDS);

		if(groups==null){
			groups = "";
		}

		return CommonConstants.COMMA_SPLITTER.split(groups);
	}

	public PkgData getPkgData() {
		return pkgData;
	}

	public void setPkgData(PkgData pkgData) {
		this.pkgData = pkgData;
	}
}
