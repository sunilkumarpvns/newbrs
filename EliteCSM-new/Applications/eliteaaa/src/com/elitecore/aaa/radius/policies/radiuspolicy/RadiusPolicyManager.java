/*

 *  EliteRadius
 *    
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 16th June 2009
 *  Created By Subhash Punani
 *  
 */
package com.elitecore.aaa.radius.policies.radiuspolicy;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.policies.AAAAccessTimePolicy;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.aaa.radius.policies.radiuspolicy.data.RadiusPolicyTreeData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadCacheFailedException;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.serverx.policies.ElitePolicyTreeData;
import com.elitecore.core.serverx.policies.IReplyItems;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.PolicyManager;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.accesstime.EliteAccessTimePolicy;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.policies.data.PolicyData;
import com.elitecore.core.serverx.policies.data.PolicyGroupData;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.core.serverx.policies.data.TimeSlotData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RadiusPolicyManager extends PolicyManager{
	
	private static final String MODULE = "Radius Policy Manager";
	private String Key;
	private static Map<String,RadiusPolicyManager> policyManagerInstances;
		
	private static final String SYSTEM_FOLDER = "system";
	private static final String TIME_SLOT_QUERY = "SELECT MONTHOFYEAR,DAYOFMONTH,DAYOFWEEK,TIMEPERIOD FROM TBLMRADIUSPOLICYTIMEPERIOD WHERE RADIUSPOLICYID = ?";
	private static final String RADIUS_POLICYGROUP_FILENAME = "_sys.radiuspolicygroup";

	public RadiusPolicyManager(String key) {
		this.Key = key;
	}
	public static RadiusPolicyManager getInstance(String strServiceId) {
		if (policyManagerInstances == null) {
			synchronized (RadiusPolicyManager.class) {
				if (policyManagerInstances == null)
					policyManagerInstances = new HashMap<String, RadiusPolicyManager>();
			}
		}
		
		RadiusPolicyManager policyManagerInstance = policyManagerInstances.get(strServiceId);
		if(policyManagerInstance == null){
			synchronized (policyManagerInstances) {
				if(policyManagerInstances.get(strServiceId) == null){
					policyManagerInstance = new RadiusPolicyManager(strServiceId);
					policyManagerInstances.put(strServiceId, policyManagerInstance);
				}else{
					policyManagerInstance = policyManagerInstances.get(strServiceId);
				}
			}
		}
		return policyManagerInstance;
	}	 
	
	@Override
	public PolicyParseTreeNode getAttributeParseTreeNode(String strExpr, PolicyType policyType, String policyName) {
		String[] strSplitedExpression = ParserUtility.splitKeyAndValue(strExpr);
		
		RadiusParseTreeNode radiusParseTreeNode = new RadiusParseTreeNode(strSplitedExpression,true,policyType, policyName);
		return radiusParseTreeNode;
	}

	@Override
	public PolicyParseTreeNode getPolicyParseTreeNode(ElitePolicyTreeData policyTreeData) {
		RadiusPolicyParseTreeNode radiusPolicyParseTreeNode = new RadiusPolicyParseTreeNode(policyTreeData,false);
		return radiusPolicyParseTreeNode;
	}
	
	public static ArrayList<String> getPolicyManagerInstances(){
		ArrayList<String> radiusPolicyManagers = new ArrayList<String>();
 		if(policyManagerInstances != null){
			radiusPolicyManagers.addAll(policyManagerInstances.keySet());
			return radiusPolicyManagers;
 		}
 		return null;
 	}
	
	public static ArrayList<String> getRadiusPolicies(String policyManagerInstanceName){
		RadiusPolicyManager policyManager = policyManagerInstances.get(policyManagerInstanceName);
		ArrayList<String> policiesList = new ArrayList<String>();
		if(policyManager != null){
			if(policyManager.policyDataList != null){
				for(IPolicyData policyData:policyManager.policyDataList)
					policiesList.add(policyData.getPolicyName());
			}
		}
		return policiesList;
	}
	
	public static IPolicyData getRadiusPolicy(String strPolicyManager, String strPolicyName){
		RadiusPolicyManager policyManager = policyManagerInstances.get(strPolicyManager);
		if(policyManager != null){
 			try {
				if(policyManager.policyDataList != null){
					for(IPolicyData policyData:policyManager.policyDataList){
						if(policyData.getPolicyName().equals(strPolicyName))
							return (IPolicyData) policyData.clone();
	 				}
				}
			} catch (CloneNotSupportedException e) {
				LogManager.getLogger().trace(MODULE, e);
 			}
 		}
		return null;
 	}
	
	public String getName() {
		return Key;
	}
		
	protected List<IPolicyData> readFromFile(File filePolicyName) throws LoadCacheFailedException{

		//Add default policy for customer level check,reject and reply item
		List<IPolicyData> policyDataList = new ArrayList<IPolicyData> ();
		IPolicyData policyData = new PolicyData();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(false);

		DocumentBuilder documentBuilder;
		try {
			documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(filePolicyName);
			if(document==null ){
				//TODO Take proper action
				return null;
			}
			NodeList radiusPolicyList= document.getElementsByTagName("policies");

			for(int i=0;i<radiusPolicyList.getLength();i++) {
				NodeList nodeListing = radiusPolicyList.item(i).getChildNodes();

				for(int j=0;j<nodeListing.getLength();j++) {
					Node subNodes = nodeListing.item(j);

					if(subNodes.getNodeName().equalsIgnoreCase("policy-list")){
						NodeList ndList=subNodes.getChildNodes();
						for(int t=0;t<ndList.getLength();t++){
							Node nsubNode = ndList.item(t);

							if(nsubNode.getNodeName().equalsIgnoreCase("policy")){ 
								policyData = new PolicyData();
								NamedNodeMap namedNodeMap = nsubNode.getAttributes();
								policyData.setPolicyName(namedNodeMap.getNamedItem("name").getNodeValue());
								policyData.setDescription(namedNodeMap.getNamedItem("description").getNodeValue());
								NodeList radiusNodeList = nsubNode.getChildNodes();

								for(int n = 0 ; n < radiusNodeList.getLength() ; n++){
									Node detailNode = radiusNodeList.item(n);

									if(detailNode.getNodeName().equalsIgnoreCase("check-items")){
										policyData.setCheckItem(detailNode.getTextContent());
									}else if(detailNode.getNodeName().equalsIgnoreCase("reply-items")){
										policyData.setReplyItem(detailNode.getTextContent());
									}else if(detailNode.getNodeName().equalsIgnoreCase("reject-items")){
										policyData.setRejectItem(detailNode.getTextContent());
									}else if(detailNode.getNodeName().equalsIgnoreCase("add-items")){
										policyData.setAddItem(detailNode.getTextContent());
									}
								}
								policyDataList.add(policyData);
							}
						}
					}else if (subNodes.getNodeName().equalsIgnoreCase("action-on-check-item-not-found")){ 
						int actionOnCheckItemNotFound = ACCEPT;
						try{
							actionOnCheckItemNotFound = Integer.parseInt(subNodes.getTextContent().trim());
						}catch(NumberFormatException ex){
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Invalid value " + subNodes.getTextContent() + ", configured for Action On Check Item Not Found in hotline policies. Using default value : ACCEPT");
							}
						}
						if(actionOnCheckItemNotFound == ACCEPT){
							setRejectOnCheckItemNotFound(false);
						}else{
							setRejectOnCheckItemNotFound(true);
						}
						
					}else if (subNodes.getNodeName().equalsIgnoreCase("action-on-reject-item-not-found")){ 
						int actionOnRejectItemNotFound = ACCEPT;
						try{
							actionOnRejectItemNotFound = Integer.parseInt(subNodes.getTextContent().trim());
						}catch(NumberFormatException ex){
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Invalid value " + subNodes.getTextContent() + ", configured for Action On Reject Item Not Found in hotline policies. Using default value : ACCEPT");
							}
						}
						if(actionOnRejectItemNotFound == ACCEPT){
							setRejectOnRejectItemNotFound(false);
						}else{
							setRejectOnRejectItemNotFound(true);
						}
						
					}else if (subNodes.getNodeName().equalsIgnoreCase("action-on-policy-not-found")){ 
						int actionOnPolicyNotFound = ACCEPT;
						try{
							actionOnPolicyNotFound = Integer.parseInt(subNodes.getTextContent().trim());
						}catch(NumberFormatException ex){
							if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Invalid value " + subNodes.getTextContent() + ", configured for Action On Policy Not Found in hotline policies. Using default value : ACCEPT");
							}
						}
						if(actionOnPolicyNotFound == ACCEPT){
							setContinueOnPolicyNotFound(true);
						}else{
							setContinueOnPolicyNotFound(false);
						}
					}
				}
			}
		} catch (ParserConfigurationException e1) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		return policyDataList;	
	}

	
	@Override
	protected List<IPolicyData> readFromDB() throws LoadCacheFailedException{								
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet =null;

		List<IPolicyData> policyDataList = new ArrayList<IPolicyData> ();
		IPolicyData policyData = new PolicyData();
		
		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement("SELECT RADIUSPOLICYID,NAME,DESCRIPTION,CHECKITEM,REPLYITEM,REJECTITEM,ADDITEM FROM TBLMRADIUSPOLICY WHERE COMMONSTATUSID = '"+CommonConstants.DATABASE_POLICY_STATUS_ACTIVE+"'");
			resultSet =preparedStatement.executeQuery();
			while(resultSet.next()){
				policyData = new PolicyData();
				String radiusPolicyID = resultSet.getString("RADIUSPOLICYID");
				policyData.setPolicyName(resultSet.getString("NAME"));
				policyData.setDescription(resultSet.getString("DESCRIPTION"));
				policyData.setCheckItem(resultSet.getString("CHECKITEM"));
				policyData.setRejectItem(resultSet.getString("REJECTITEM"));
				policyData.setReplyItem(resultSet.getString("REPLYITEM"));

				policyData.setAddItem(resultSet.getString("ADDITEM"));

				policyData.addTimeSlots(readTimeSlots(radiusPolicyID,connection));
				policyDataList.add(policyData);
			}
		} catch (DataSourceException e) {
			throw new LoadCacheFailedException("Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage() + ". Caching policy from Exported file", e);
		}catch(Exception e) {
			throw new LoadCacheFailedException("Problem reading policy from the datasource, Reason: " + e.getMessage(),e);
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		return policyDataList;
	}

	private List<TimeSlotData> readTimeSlots(String radiusPolicyID, Connection connection) throws SQLException{
		PreparedStatement timeSlotPS = null;
		try {
			timeSlotPS = connection.prepareStatement(TIME_SLOT_QUERY);
		timeSlotPS.setString(1, radiusPolicyID);
		ResultSet resultSet = timeSlotPS.executeQuery();
		List<TimeSlotData> timeSlots = new ArrayList<TimeSlotData>();
		while(resultSet.next()){
			String monthOfYear = resultSet.getString("MONTHOFYEAR");
			String dayOfMonth = resultSet.getString("DAYOFMONTH");
			String dayOfWeek = resultSet.getString("DAYOFWEEK");
			String timePeriod = resultSet.getString("TIMEPERIOD");
			timeSlots.add(new TimeSlotData(monthOfYear, dayOfMonth, dayOfWeek, timePeriod));
		}
		return timeSlots;
		} finally {
			DBUtility.closeQuietly(timeSlotPS);
	}
	}
	
	@Override
	protected IReplyItems getReplyItemNode(String replyItem) {
		return new RadiusReplyItems(replyItem);
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void applyReplyItems(PolicyTreeData treeData,List<String> policies, 
			Map<String,Map<String,Map<String,ArrayList<String>>>> overridedReplyItems) {
		RadServiceResponse radiusServiceResponse = ((RadiusPolicyTreeData)treeData).getRadiusServiceResponse();
		for(String strPolicy:policies){
			Map<String,Map<String,ArrayList<String>>> overideValues = null;
			if(overridedReplyItems != null)
				overideValues = overridedReplyItems.get(strPolicy); 
			ElitePolicyTreeData policyTreeData = getPolicyTreeData(strPolicy);
			IReplyItems replyItems = policyTreeData.getReplyItem();
			if(replyItems != null){
				ArrayList<IRadiusAttribute> replyAttributes = (ArrayList<IRadiusAttribute>) replyItems.getReplyItems(treeData, overideValues);
				for(IRadiusAttribute radiusAttribute:replyAttributes){
						radiusServiceResponse.addAttribute(radiusAttribute);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void applyAddItems(PolicyTreeData treeData,List<String> policies, 
			Map<String,Map<String,Map<String,ArrayList<String>>>> overridedAddItems) {
		RadServiceRequest radiusServiceRequest = ((RadiusPolicyTreeData)treeData).getRadiusServiceRequest();
		
		for(String strPolicy:policies){
			Map<String,Map<String,ArrayList<String>>> overideValues = null;
			if(overridedAddItems != null)
				overideValues = overridedAddItems.get(strPolicy); 
			ElitePolicyTreeData policyTreeData = getPolicyTreeData(strPolicy);
			IReplyItems addItems = policyTreeData.getAddItem();
			if(addItems != null){
				ArrayList<IRadiusAttribute> addAttributes = (ArrayList<IRadiusAttribute>) addItems.getReplyItems(treeData, overideValues);
				for(IRadiusAttribute radiusAttribute:addAttributes){
					radiusServiceRequest.addInfoAttribute(radiusAttribute);
				}
			}
		}
	}
	
	public List<String> applyPolicies(RadServiceRequest request,RadServiceResponse response,
			String strPoliciesToBeApplied) throws ParserException, PolicyFailedException{
		
		RadiusPolicyTreeData treeData = new RadiusPolicyTreeData(request, response);
		treeData.setPoliciesToBeApplied(strPoliciesToBeApplied);
		treeData.setRejectOnCheckItemNotFound(this.getRejectOnCheckItemNotFound());
		treeData.setRejectOnRejectItemNotFound(this.getRejectOnRejectItemNotFound());
		treeData.setContinueOnPolicyNotFound(this.getContinueOnPolicyNotFound());
		
		return super.applyPolicies(treeData);
	}
	public List<String> applyPolicies(RadAuthRequest request,RadAuthResponse response,
			String strPoliciesToBeApplied,int iVendorType,String strOverridedCheckItems,
			String strOverridedRejectItems,	String strOverridedReplyItems,boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound,boolean bContinueOnPolicyNotFound,boolean bApplyCheckItemForPortalRequest) 
			throws ParserException, PolicyFailedException{
		
		RadiusPolicyTreeData treeData = new RadiusPolicyTreeData(request, response);
		
		treeData.setPoliciesToBeApplied(strPoliciesToBeApplied);
		treeData.setClientType(iVendorType);
		treeData.setOverridedCheckItems(strOverridedCheckItems);
		treeData.setOverridedRejectItems(strOverridedRejectItems);
		treeData.setOverridedReplyItems(strOverridedReplyItems);
		treeData.setRejectOnCheckItemNotFound(bRejectOnCheckItemNotFound);
		treeData.setRejectOnRejectItemNotFound(bRejectOnRejectItemNotFound);
		treeData.setContinueOnPolicyNotFound(bContinueOnPolicyNotFound);
		treeData.setbApplyCheckItemForPortalRequest(bApplyCheckItemForPortalRequest);
		
		return super.applyPolicies(treeData);		
	}
	public List<String> applyPolicies(RadAuthRequest request,RadAuthResponse response,
			String strPoliciesToBeApplied,int iVendorType,String strOverridedCheckItems,
			String strOverridedRejectItems,	String strOverridedReplyItems,boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound,boolean bContinueOnPolicyNotFound) 
			throws ParserException, PolicyFailedException{
				
		return 	applyPolicies(request,response,strPoliciesToBeApplied, iVendorType,strOverridedCheckItems,strOverridedRejectItems,strOverridedReplyItems,bRejectOnCheckItemNotFound,
				bRejectOnRejectItemNotFound,bContinueOnPolicyNotFound,false);
	}
	
	public void applyReplyItems(RadServiceRequest request,RadServiceResponse response,List<String> policies,String strOverridedReplyItem,boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound,boolean bContinueOnPolicyNotFound) {
		
		RadiusPolicyTreeData treeData = new RadiusPolicyTreeData(request, response);
		treeData.setOverridedReplyItems(strOverridedReplyItem);
		treeData.setRejectOnCheckItemNotFound(bRejectOnCheckItemNotFound);
		treeData.setRejectOnRejectItemNotFound(bRejectOnRejectItemNotFound);
		treeData.setContinueOnPolicyNotFound(bContinueOnPolicyNotFound);
		applyReplyItems(treeData, policies);
	}
	
	
	@Override
	protected String getSerializeFileNameForPolicy() {
		return getServerHome() + File.separator + SYSTEM_FOLDER + File.separator + "_system.radiuspolicy";
	}
	@Override
	protected String getKey() {
		return Key;
	}
	@Override
	protected EliteAccessTimePolicy getAccessPolicy(List<TimeSlotData> timeSlotData) throws ParserException {
		AAAAccessTimePolicy aaaAccessTimePolicy = new AAAAccessTimePolicy();
		if(timeSlotData != null && timeSlotData.size() > 0){
			List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
			try{
				for(int i= 0; i < timeSlotData.size(); i++){
					TimeSlotData tsData = timeSlotData.get(i);
					TimeSlot ts = TimeSlot.getTimeSlot(tsData.getMOY(),tsData.getDOM(),tsData.getDOW(),tsData.getTimePeriod());
					timeSlots.add(ts);
				}
				aaaAccessTimePolicy.setTimeSlots(timeSlots);
			}catch (Exception ex) {
				throw new ParserException(ex);
			}
		}
		return aaaAccessTimePolicy;
	}
	@Override
	protected String getSerializeFileNameForPolicyGroup() {
		return getServerHome() + File.separator + SYSTEM_FOLDER + File.separator + RADIUS_POLICYGROUP_FILENAME;
	}
	
	@Override
	protected List<PolicyGroupData> readPolicyGroupFromDB() throws LoadCacheFailedException {
		List<PolicyGroupData> policyGroupList = new ArrayList<PolicyGroupData>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			stmt = connection.prepareStatement(getQueryForPolicyGroup());
			resultSet = stmt.executeQuery();
			while (resultSet.next()){
				String name = resultSet.getString("POLICYNAME");
				String expression = resultSet.getString("EXPRESSION");
				PolicyGroupData policyGroupData = new PolicyGroupData();
				policyGroupData.setName(name);
				policyGroupData.setExpression(expression);
				policyGroupList.add(policyGroupData);
			}
		} catch (DataSourceException e) {
			throw new LoadCacheFailedException(e.getMessage());
		} catch (SQLException e) {
			throw new LoadCacheFailedException(e.getMessage());
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(stmt);
			DBUtility.closeQuietly(connection);
		}
		return policyGroupList;
	}
	
	private String getQueryForPolicyGroup() {
		return "SELECT * FROM TBLMRADIUSPOLICYGROUP";
	}
	
	@Override
	protected String getPolicyGroupKey() {
		return "RADIUS_POLICY_GROUP";
	}
}
