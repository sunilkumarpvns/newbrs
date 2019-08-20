package com.elitecore.aaa.diameter.policies.diameterpolicy;

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
import com.elitecore.aaa.diameter.policies.diameterpolicy.data.DiameterPolicyTreeData;
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
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

public class DiameterPolicyManager extends PolicyManager {
	private static final String MODULE = "Diameter Policy Manager";
	private String Key;
	private static Map<String,DiameterPolicyManager> policyManagerInstances;
		
	private static final String SYSTEM_FOLDER = "system";
	public static final String DIAMETER_AUTHORIZATION_POLICY = "DIAMETER_AUTHORIZATION_POLICY";
	private static final String DIAMETER_POLICYGROUP_FILENAME = "_system.diameterpolicygroup";

	public DiameterPolicyManager(String key) {
		this.Key = key;
	}
	
	static {
		policyManagerInstances = new HashMap<String, DiameterPolicyManager>();
	}
	public static DiameterPolicyManager getInstance(String strServiceId) {
		
		DiameterPolicyManager policyManagerInstance = policyManagerInstances.get(strServiceId);
		if(policyManagerInstance == null){
			synchronized (policyManagerInstances) {
				if(policyManagerInstances.get(strServiceId) == null){
					policyManagerInstance = new DiameterPolicyManager(strServiceId);
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
		
		DiameterParseTreeNode parseTreeNode = new DiameterParseTreeNode(strSplitedExpression,true,policyType, policyName);
		return parseTreeNode;
	}

	@Override
	public PolicyParseTreeNode getPolicyParseTreeNode(ElitePolicyTreeData policyTreeData) {
		DiameterPolicyParseTreeNode policyParseTreeNode = new DiameterPolicyParseTreeNode(policyTreeData,false);
		return policyParseTreeNode;
	}
	
	public static ArrayList<String> getPolicyManagerInstances(){
		ArrayList<String> policyManagers = new ArrayList<String>();
 		if(policyManagerInstances != null){
			policyManagers.addAll(policyManagerInstances.keySet());
			return policyManagers;
 		}
 		return null;
 	}
	
	public static ArrayList<String> getRadiusPolicies(String policyManagerInstanceName){
		DiameterPolicyManager policyManager = policyManagerInstances.get(policyManagerInstanceName);
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
		DiameterPolicyManager policyManager = policyManagerInstances.get(strPolicyManager);
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
								LogManager.getLogger().warn(MODULE, "Invalid value configured for Action On Check Item Not Found in hotline policies. Using default value : ACCEPT");
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
								LogManager.getLogger().warn(MODULE, "Invalid value configured for Action On Reject Item Not Found in hotline policies. Using default value : ACCEPT");
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
								LogManager.getLogger().warn(MODULE, "Invalid value configured for Action On Policy Not Found in hotline policies. Using default value : ACCEPT");
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
			preparedStatement = connection.prepareStatement("SELECT DIAMETERPOLICYID,NAME,DESCRIPTION,CHECKITEM,REPLYITEM,REJECTITEM FROM TBLMDIAMETERPOLICY WHERE COMMONSTATUSID = '"+CommonConstants.DATABASE_POLICY_STATUS_ACTIVE+"'");
			resultSet =preparedStatement.executeQuery();
			while(resultSet.next()){
				policyData = new PolicyData();
				String diameterPolicyID = resultSet.getString("DIAMETERPOLICYID");
				policyData.setPolicyName(resultSet.getString("NAME"));
				policyData.setDescription(resultSet.getString("DESCRIPTION"));
				policyData.setCheckItem(resultSet.getString("CHECKITEM"));
				policyData.setRejectItem(resultSet.getString("REJECTITEM"));
				policyData.setReplyItem(resultSet.getString("REPLYITEM"));
				
				List<TimeSlotData> timeSlotDataList = readTimeSlots(diameterPolicyID, connection);
				policyData.addTimeSlots(timeSlotDataList);
				
				policyDataList.add(policyData);
			}
		} catch (DataSourceException e) {
			throw new LoadCacheFailedException("Connection to datasource: " + EliteAAADBConnectionManager.ELITE_AAADB_CACHE + " is unavailable, Reason: " + e.getMessage(), e);
		}catch(Exception e) {
			throw new LoadCacheFailedException("Problem reading policy from the datasource, Reason: " + e.getMessage(),e);
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		return policyDataList;
	}

	private List<TimeSlotData> readTimeSlots(String diameterPolicyID, Connection connection) throws SQLException {
		
		String queryForTimeSlotData = "SELECT MONTHOFYEAR,DAYOFMONTH,DAYOFWEEK,TIMEPERIOD FROM TBLMDIAMETERPOLICYTIMEPERIOD WHERE DIAMETERPOLICYID = ?";
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(queryForTimeSlotData);
			statement.setString(1, diameterPolicyID);
			ResultSet resultSet = statement.executeQuery();

			List<TimeSlotData> timeSlotDataList = new ArrayList<TimeSlotData>();
			while (resultSet.next()) {
				String monthOfYear = resultSet.getString("MONTHOFYEAR");
				String dayOfMonth = resultSet.getString("DAYOFMONTH");
				String dayOfWeek = resultSet.getString("DAYOFWEEK");
				String timePeriod = resultSet.getString("TIMEPERIOD");
				TimeSlotData data = new TimeSlotData(monthOfYear, dayOfMonth, dayOfWeek, timePeriod);
				timeSlotDataList.add(data);
			}

			return timeSlotDataList;
		} finally {
			DBUtility.closeQuietly(statement);
		}
	}
	
	@Override
	protected IReplyItems getReplyItemNode(String replyItem) {
		
		return new DiameterReplyItems(replyItem);
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void applyReplyItems(PolicyTreeData treeData,List<String> policies, 
			Map<String,Map<String,Map<String,ArrayList<String>>>> overridedReplyItems) {
		DiameterAnswer answer = ((DiameterPolicyTreeData)treeData).getDiameterAnswer();
		for(String strPolicy:policies){
			Map<String,Map<String,ArrayList<String>>> overideValues = null;
			if(overridedReplyItems != null)
				overideValues = overridedReplyItems.get(strPolicy); 
			ElitePolicyTreeData policyTreeData = getPolicyTreeData(strPolicy);
			IReplyItems replyItems = policyTreeData.getReplyItem();
			if(replyItems != null){
				ArrayList<IDiameterAVP> replyAttributes = (ArrayList<IDiameterAVP>) replyItems.getReplyItems(treeData, overideValues);
				for(IDiameterAVP attribute:replyAttributes){
					answer.addAvp(attribute);
				}
			}
		}
	}
	
	
	public void applyReplyItems(DiameterRequest request,DiameterAnswer answer,
		List<String> policies,String strOverridedReplyItem) {
		
		DiameterPolicyTreeData treeData = new DiameterPolicyTreeData(request, answer);
		treeData.setOverridedReplyItems(strOverridedReplyItem);		
		applyReplyItems(treeData, policies);
	}
	
	@Override
	protected void applyAddItems(PolicyTreeData treeData,List<String> policies,	Map<String,Map<String,Map<String,ArrayList<String>>>> overridedAddItems) {
//		for(String strPolicy:policies){
//			IPolicyData policyData = policiesDataMap.get(strPolicy);
//			Map<String,Map<String,ArrayList<String>>> overideValues = null;
//			if(overridedAddItems != null)
//				overideValues = overridedAddItems.get(strPolicy); 
//			ElitePolicyTreeData policyTreeData = policyData.getElitePolicyTreeData();
//			IReplyItems addItems = policyTreeData.getAddItem();
//			if(addItems != null){
//				IRadiusPacket radiusRequestPacket = (IRadiusPacket) requestPacket;
//				ArrayList<IRadiusAttribute> addAttributes = (ArrayList<IRadiusAttribute>) addItems.getReplyItems(requestPacket, responsePacket, overideValues);
//				for(IRadiusAttribute radiusAttribute:addAttributes){
//					radiusRequestPacket.addAttribute(radiusAttribute);
//				}
//				radiusRequestPacket.refreshInfoPacketHeader();
//			}
//		}
	}
	public List<String> applyPolicies(DiameterRequest request,DiameterAnswer response,
			String strPoliciesToBeApplied) throws ParserException, PolicyFailedException{
		
		DiameterPolicyTreeData treeData = new DiameterPolicyTreeData(request, response);
		treeData.setPoliciesToBeApplied(strPoliciesToBeApplied);
		treeData.setRejectOnCheckItemNotFound(this.getRejectOnCheckItemNotFound());
		treeData.setRejectOnRejectItemNotFound(this.getRejectOnRejectItemNotFound());
		treeData.setContinueOnPolicyNotFound(this.getContinueOnPolicyNotFound());
		
		return super.applyPolicies(treeData);
	}
	public List<String> applyPolicies(DiameterRequest request,DiameterAnswer response,
			String strPoliciesToBeApplied, String strOverridedCheckItems,
			String strOverridedRejectItems,	String strOverridedReplyItems,boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound,boolean bContinueOnPolicyNotFound,boolean bApplyCheckItemForPortalRequest) 
			throws ParserException, PolicyFailedException{
		
		DiameterPolicyTreeData treeData = new DiameterPolicyTreeData(request, response);
		
		treeData.setPoliciesToBeApplied(strPoliciesToBeApplied);
		//treeData.setClientType(iVendorType);
		treeData.setOverridedCheckItems(strOverridedCheckItems);
		treeData.setOverridedRejectItems(strOverridedRejectItems);
		treeData.setOverridedReplyItems(strOverridedReplyItems);
		treeData.setRejectOnCheckItemNotFound(bRejectOnCheckItemNotFound);
		treeData.setRejectOnRejectItemNotFound(bRejectOnRejectItemNotFound);
		treeData.setContinueOnPolicyNotFound(bContinueOnPolicyNotFound);
		treeData.setbApplyCheckItemForPortalRequest(bApplyCheckItemForPortalRequest);
		
		return super.applyPolicies(treeData);		
	}
	public List<String> applyPolicies(DiameterRequest request,DiameterAnswer response,
			String strPoliciesToBeApplied, String strOverridedCheckItems,
			String strOverridedRejectItems,	String strOverridedReplyItems,boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound,boolean bContinueOnPolicyNotFound) 
			throws ParserException, PolicyFailedException{
		
		return applyPolicies(request,response,strPoliciesToBeApplied,strOverridedCheckItems,
				strOverridedRejectItems,strOverridedReplyItems,bRejectOnCheckItemNotFound,
				bRejectOnRejectItemNotFound,bContinueOnPolicyNotFound,false);		
	}
	
	@Override
	protected String getSerializeFileNameForPolicy() {
		return getServerHome() + File.separator + SYSTEM_FOLDER + File.separator + "_system.diameterpolicy";
	}
	@Override
	protected String getKey() {
		return Key;
	}
	@Override
	protected EliteAccessTimePolicy getAccessPolicy(
			List<TimeSlotData> timeSlotData) throws ParserException {
		AAAAccessTimePolicy aaaAccessTimePolicy = new AAAAccessTimePolicy();
		if(timeSlotData != null){
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
		return getServerHome() + File.separator + SYSTEM_FOLDER + File.separator + DIAMETER_POLICYGROUP_FILENAME;
	}
	@Override
	protected List<PolicyGroupData> readPolicyGroupFromDB()
			throws LoadCacheFailedException {
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
		return "SELECT * FROM TBLMDIAMETERPOLICYGROUP";
	}
	
	@Override
	protected String getPolicyGroupKey() {
		return "DIAMETER_POLICY_GROUP";
	}
}
