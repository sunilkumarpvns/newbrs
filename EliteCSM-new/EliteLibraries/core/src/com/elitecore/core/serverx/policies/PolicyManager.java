package com.elitecore.core.serverx.policies;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBException;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadCacheFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.core.serverx.manager.cache.CacheDetailProvider;
import com.elitecore.core.serverx.manager.cache.Cacheable;
import com.elitecore.core.serverx.policies.accesstime.EliteAccessTimePolicy;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.policies.data.PolicyData;
import com.elitecore.core.serverx.policies.data.PolicyGroupConfiguration;
import com.elitecore.core.serverx.policies.data.PolicyGroupData;
import com.elitecore.core.serverx.policies.data.PolicyOverrideData;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.core.serverx.policies.data.TimeSlotData;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;

/**
 * @author subhashpunani
 * @version 1.0
 * @created 20-Jan-2009 5:29:01 PM
 */
public abstract class PolicyManager {
	
	private static final String MODULE = "C-PM";
	
	//protected Map<String,IPolicyData> policiesDataMap;
	protected List<IPolicyData> policyDataList;
	private Map<String, ElitePolicyTreeData> policiesTreeDataMap;
	protected Map<String,ElitePolicyParseTree> policiesTreeMap; 
	private boolean bPolicyCachingEnabled = false;
	boolean bContinueOnPolicyNotFound = true;
	boolean bRejectOnCheckItemNotFound = false;
	boolean bRejectOnRejectItemNotFound = false;
	private String serverHome;
	private boolean isInitializedFromDB = false;

	protected static final int ACCEPT = 1;
	protected static final int REJECT = 0;
	
	private PolicyGroupConfiguration policyGroupConfiguration = new PolicyGroupConfiguration();

	private ServerContext serverContext;
	
	public void initCache(ServerContext serverContext, boolean bApplyCheckItemForPortalRequest) throws ManagerInitialzationException{
		this.serverContext = serverContext;
		this.isInitializedFromDB  = true;
		this.serverHome = serverContext.getServerHome() ;
		List<IPolicyData> policyDataList=null;
		
		try {
			policyDataList = readFromDB();
			this.policyGroupConfiguration.getPolicyGroupDataList().addAll(readPolicyGroupFromDB());
			this.policyGroupConfiguration.postRead();
		} catch(LoadCacheFailedException e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
			LogManager.getLogger().trace(e);
			try {
				policyDataList = deSerializePolicies(getSerializeFileNameForPolicy());
				this.policyGroupConfiguration = ConfigUtil.deserialize(getSerializeFileNameForPolicyGroup(), PolicyGroupConfiguration.class);
				this.policyGroupConfiguration.postRead();
			} catch (Exception e1) {
				//If the Manager Fails to initialize, it will be initialized with no policy
				initCache(policyDataList);
				this.policyGroupConfiguration = new PolicyGroupConfiguration();
				throw new ManagerInitialzationException("Error in initializing Policy Manager", e1);
			}
		}
		
		initCache(policyDataList);
		
		createLastKnownGoodConfiguration();
	}

	private void createLastKnownGoodConfiguration() {
		serializePolicies();
		serializePolicyGroup();
	}

	protected abstract String getSerializeFileNameForPolicy();
	protected abstract String getSerializeFileNameForPolicyGroup();
	
	private int initCache(List<IPolicyData> policyDataList){		
		int resultCode = CacheConstants.SUCCESS;
		if(policyDataList == null)
			policyDataList = new ArrayList<IPolicyData>();
		
		//Add default policy into list
		IPolicyData defaultPolicy = new PolicyData();
		defaultPolicy.setPolicyName("*");
		defaultPolicy.setDescription("Default Policy");
		defaultPolicy.setCheckItem(null);
		defaultPolicy.setRejectItem(null);
		defaultPolicy.setReplyItem(null);
		policyDataList.add(defaultPolicy);

		
		Map<String, ElitePolicyTreeData> policiesTreeDataMap = new HashMap<String, ElitePolicyTreeData>();
		for(int i=0;i< policyDataList.size();i++){
			IPolicyData policyData= policyDataList.get(i);
			ElitePolicyTreeData policyTreeData;
			try{
				policyTreeData=parsePolicy(policyData);
				if(policyTreeData!=null){
					policiesTreeDataMap.put(policyData.getPolicyName(), policyTreeData);					
				}else{
					policyData.setParseble(false);
					resultCode = CacheConstants.INTRIM;
				}
			}catch(Exception e){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Skipping the policy " + policyData.getPolicyName() +". Reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE,e);
				policyData.setParseble(false);
				resultCode = CacheConstants.INTRIM;
			}
		}
		this.policiesTreeMap = new HashMap<String, ElitePolicyParseTree>();
		this.policiesTreeDataMap = policiesTreeDataMap;
		this.policyDataList = policyDataList;
		
		// Policy will not be FAIL as atleast default Policy will remain in the map.  
		return resultCode;
	}
	
	public void setRejectOnCheckItemNotFound(boolean bRejectOnCheckItemNotFound){
		this.bRejectOnCheckItemNotFound = bRejectOnCheckItemNotFound;
	}

	public boolean getRejectOnCheckItemNotFound(){
		return bRejectOnCheckItemNotFound;
	}

	public void setRejectOnRejectItemNotFound(boolean bRejctOnRejectItemNotFound){
		this.bRejectOnRejectItemNotFound = bRejctOnRejectItemNotFound;
	}
	public boolean getRejectOnRejectItemNotFound(){
		return bRejectOnRejectItemNotFound;
	}

	public void setContinueOnPolicyNotFound(boolean bContinueOnPolicyNotFound){
		this.bContinueOnPolicyNotFound = bContinueOnPolicyNotFound;
	}

	public boolean getContinueOnPolicyNotFound(){
		return bContinueOnPolicyNotFound;
	}

	public ElitePolicyTreeData parsePolicy(IPolicyData policyData) throws ParserException{

		if(policyData==null){
				throw new ParserException("Invalid Policy");
		}
		
		ElitePolicyTreeData policyTreeData = new ElitePolicyTreeData();
		policyTreeData.setPolicyName(policyData.getPolicyName());
		String strCheckItem = policyData.getCheckItem();
		String strRejectItem = policyData.getRejectItem();
		String strReplyItem = policyData.getReplyItem();
		String strAddItem = policyData.getAddItem();
		List<TimeSlotData> timeSlotData = policyData.getTimeSlots();
		
		if(strCheckItem != null){
			try{
				List<String> checkItems = ParserUtility.convertToPostFixNotation(strCheckItem);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Parsed Check Items: " + checkItems);
				ElitePolicyParseTree policyParseTree = buildParseTree(checkItems, true, PolicyType.CHECK_ITEM_POLICY, policyData.getPolicyName());
				policyTreeData.setCheckItemTree(policyParseTree);	
			}catch(Exception e){
				throw new ParserException("Error in parsing Check-Item for policy " + policyTreeData.getPolicyName(),e);
			}
		}

		if(strRejectItem != null){
			try{
				List<String> rejectItems = ParserUtility.convertToPostFixNotation(strRejectItem);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Parsed Reject Items: " + rejectItems);
				ElitePolicyParseTree policyParseTree = buildParseTree(rejectItems, true, PolicyType.REJECT_ITEM_POLICY, policyData.getPolicyName());
				policyTreeData.setRejectItemTree(policyParseTree);
			}catch(Exception e){
				throw new ParserException("Error in parsing Reject-Item for policy " + policyTreeData.getPolicyName(),e);
			}
		}

		try{
			IReplyItems replyItem = getReplyItemNode(strReplyItem);
			replyItem.parseReplyItems();
			policyTreeData.setReplyItem(replyItem);
		}catch(Exception e){
			throw new ParserException("Error in parsing Reply-Item for policy " + policyTreeData.getPolicyName(),e);
		}
		
		try{
			IReplyItems addItem = getReplyItemNode(strAddItem);
			addItem.parseReplyItems();
			policyTreeData.setAddItem(addItem);
		}catch(Exception e){
			throw new ParserException("Error in parsing Add-Item for policy " + policyTreeData.getPolicyName(),e);
		}
		

		try{
			policyTreeData.setAccessTimePolicy(getAccessPolicy(timeSlotData));
		}catch (Exception ex) {
			throw new ParserException("Error in parsing Time Slots for policy " + policyTreeData.getPolicyName(),ex);
		}
		
		return policyTreeData;
	}
	
	protected abstract EliteAccessTimePolicy getAccessPolicy(List<TimeSlotData> timeSlotData) throws ParserException;
	
	protected abstract IReplyItems getReplyItemNode(String replyItem) ;

	/**
	 * 
	 * @param requestPacket
	 * @param responsePacket
	 * @param customerAccountData
	 * @throws ParserException 
	 * @throws PolicyNotFoundException 
	 */
	
	public ElitePolicyParseTree buildParseTree(List<String>policyTokens,boolean bOROptimization,
			PolicyType policyType, String policyName) throws ParserException, PolicyNotFoundException{
		Stack<PolicyParseTreeNode> parseTreeStack = new Stack<PolicyParseTreeNode>();
		ElitePolicyParseTree policyParseTree = null;
		//If any single policy found from cache then that policy will be applied. 
		//And if no policies found to apply, Policy Not Found Exception will be thrown.
		if(policyTokens!=null && !policyTokens.isEmpty()){
			try{
				for(int i=0;i<policyTokens.size();i++){
					String strPolicyToken = policyTokens.get(i);
					if("&&".equals(strPolicyToken)|| "||".equals(strPolicyToken)|| "^".equals(strPolicyToken)){
						PolicyParseTreeNode operatorNode = new PolicyParseTreeNode(strPolicyToken,bOROptimization);
						PolicyParseTreeNode rightNode = parseTreeStack.pop();
						PolicyParseTreeNode leftNode = parseTreeStack.pop();
						operatorNode.setLeftNode(leftNode);
						operatorNode.setRightNode(rightNode);
						parseTreeStack.push(operatorNode);
					}else{
						switch(policyType){
						case CUSTOMER_POLICY:
							ElitePolicyTreeData policyTreeData =null;
							if(policiesTreeDataMap.get(strPolicyToken)!=null){  
								policyTreeData = policiesTreeDataMap.get(strPolicyToken);
							}
							parseTreeStack.push(getPolicyParseTreeNode(policyTreeData));
							break;
						default:
							parseTreeStack.push(getAttributeParseTreeNode(strPolicyToken, policyType, policyName));
						}		
					}
				}
			}catch (EmptyStackException e) {
				throw new ParserException("Invalid Expression", e);
			}
			policyParseTree = new ElitePolicyParseTree();
			policyParseTree.setRootNode(parseTreeStack.pop());
			if(!parseTreeStack.empty())
				throw new ParserException("Invalid Expression");
		}
		return policyParseTree;
	}
	
	public boolean isPoliciesExists(String policyExpression) throws ParserException{
		List<String> policyTokens = ParserUtility.convertToPostFixNotation(policyExpression);
		if(policyTokens != null){
			for(int i=0;i<policyTokens.size();i++){
				String currentToken = policyTokens.get(i);
				if(!"&&".equals(currentToken) && !"||".equals(currentToken)){
					if(policiesTreeDataMap.get(currentToken)==null)  
						return false;
				}
			}
			return true;
		}
		return false;
	}

	public abstract PolicyParseTreeNode getAttributeParseTreeNode(String strExpr, PolicyType policyType, String policyName);
	public abstract PolicyParseTreeNode getPolicyParseTreeNode(ElitePolicyTreeData policyTreeData);

	public void setPolicyCachingEnabled(boolean bPolicyCachingEnabled) {
		this.bPolicyCachingEnabled = bPolicyCachingEnabled;
	}

	public boolean isPolicyCachingEnabled() {
		return bPolicyCachingEnabled;
	}

	protected abstract void applyReplyItems(PolicyTreeData treeData,List<String> policies,Map<String,Map<String,Map<String,ArrayList<String>>>> overridedReplyItems);
	
	protected void applyReplyItems(PolicyTreeData treeData, List<String> policies){
		if(policies == null)
			return;
		String strOverridedReplyItem = treeData.getOverridedReplyItems();
		Map<String,Map<String,Map<String,ArrayList<String>>>> customerLevelOverrideValues = null;
		try{
			if(strOverridedReplyItem != null)
				customerLevelOverrideValues = ParserUtility.parseCustomerReplyItems(strOverridedReplyItem);
			applyReplyItems(treeData, policies, customerLevelOverrideValues);
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	protected abstract void applyAddItems(PolicyTreeData treeData,List<String> policies,Map<String,Map<String,Map<String,ArrayList<String>>>> overridedAddItems);
	
	protected void applyAddItems(PolicyTreeData treeData, List<String> policies){
		if(policies == null)
			return;
		String strOverridedAddItem = treeData.getOverridedAddItems();
		Map<String,Map<String,Map<String,ArrayList<String>>>> customerLevelOverrideValues = null;
		try{
			if(strOverridedAddItem != null)
				customerLevelOverrideValues = ParserUtility.parseCustomerReplyItems(strOverridedAddItem);
			applyAddItems(treeData, policies, customerLevelOverrideValues);
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	protected List<String> applyCheckItems(PolicyTreeData treeData) throws ParserException, PolicyFailedException{
		String strPolicies = treeData.getPoliciesToBeApplied();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Applying Check-Items/Reject-Items for policies : " + strPolicies);
		
		if(strPolicies != null && strPolicies.trim().length()>0)
			strPolicies = "* && ("+strPolicies + ")";
		else
			strPolicies = "*";
		String strOverridedCheckItem = treeData.getOverridedCheckItems();
		String strOverridedRejectItem = treeData.getOverridedRejectItems();

		int iResult = PolicyConstants.GENERAL_FAILER;
		ArrayList<String> satisfiedPolicies = new ArrayList<String>();
		PolicyOverrideData policyOverrideData = new PolicyOverrideData();
		
		try{
			if(strOverridedCheckItem!=null && strOverridedCheckItem.trim().length()>0){
				try{
					Map<String,Map<String,String>> overrideValues = ParserUtility.convertToCustomerLevelPolicyMap(strOverridedCheckItem);
					policyOverrideData.setCheckItem(overrideValues);
				}catch(Exception e){
					policyOverrideData.setErrorInParsingCheckItem(true);
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			if(strOverridedRejectItem!=null && strOverridedRejectItem.trim().length()>0){
				try{
					Map<String,Map<String,String>>overrideValues = ParserUtility.convertToCustomerLevelPolicyMap(strOverridedRejectItem);
					policyOverrideData.setRejectItem(overrideValues);
				}catch(Exception e){
					policyOverrideData.setErrorInParsingRejectItem(true);
					LogManager.getLogger().trace(MODULE, e);
				}
			}
				
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"Policy Override Data : " + policyOverrideData);
			treeData.setPolicyOverrideData(policyOverrideData);
			//Convert expression to post fix notation
			
			ElitePolicyParseTree policyParseTree = policiesTreeMap.get(strPolicies);
			if(policyParseTree == null){
				policyParseTree = getPolicyParseTree(strPolicies);
				if(isPolicyCachingEnabled() || policiesTreeMap.size()<100){
					policiesTreeMap.put(strPolicies, policyParseTree);
				}
			}
			iResult = policyParseTree.getRootNode().evaluate(treeData,satisfiedPolicies);
		}catch(Exception e){
			iResult=PolicyConstants.GENERAL_FAILER;
			LogManager.getLogger().trace(MODULE, e);
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Policy Result : " + PolicyConstants.stringify(iResult));
		if(iResult > PolicyConstants.SUCCESS){
			throw new PolicyFailedException("Policy Failed");
		}
		
		return satisfiedPolicies;
	}
	protected List<String> applyPolicies(PolicyTreeData treeData) throws ParserException, PolicyFailedException{
		List<String> satisfiedPolicies = applyCheckItems(treeData);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Applying Add-Items");
		applyAddItems(treeData, satisfiedPolicies);
		return satisfiedPolicies;
	}
	
	private ElitePolicyParseTree getPolicyParseTree(String strPolicies) throws ParserException, PolicyNotFoundException{

		List<String> policyTokens = ParserUtility.convertToPostFixNotation(strPolicies);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE,"Authorization Policies in user profile: " + policyTokens);

		//Build Parse tree from post fix notation
		ElitePolicyParseTree policyParseTree = buildParseTree(policyTokens,false,PolicyType.CUSTOMER_POLICY, null);
		return policyParseTree;
	}

	abstract protected List<IPolicyData> readFromDB() throws LoadCacheFailedException;
	abstract protected List<IPolicyData> readFromFile(File source) throws LoadCacheFailedException;
	
	protected void serializePolicies(){
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(new FileOutputStream(getSerializeFileNameForPolicy())); //NOSONAR - Reason: Resources should be closed
			oos.writeObject(policyDataList);
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Failed to Serialize the Policy Data Object. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} finally {
			Closeables.closeQuietly(oos);
		}
	}
	
	protected void serializePolicyGroup() {
		try {
			ConfigUtil.serialize(new File(getSerializeFileNameForPolicyGroup()), PolicyGroupConfiguration.class, policyGroupConfiguration);
		} catch (JAXBException e) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Error while serializing policy group configuration, Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(e);
		} catch (IOException e) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Error while serializing policy group configuration, Reason: "+e.getMessage());
			}
			LogManager.getLogger().trace(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> deSerializePolicies(String fileName) throws IOException, ClassNotFoundException{
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(fileName)); //NOSONAR
				ArrayList<T> policyDataList = (ArrayList<T>)ois.readObject();
				return policyDataList;
			} finally {
				Closeables.closeQuietly(ois);
			}
			
	}
	
	protected void setPoliciesTreeDataMap(Map<String, ElitePolicyTreeData> policiesTreeDataMap) {
		this.policiesTreeDataMap = policiesTreeDataMap;
	}

	protected Map<String, ElitePolicyTreeData> getPoliciesTreeDataMap() {
		return policiesTreeDataMap;
	}
	
	protected ElitePolicyTreeData getPolicyTreeData(String policyName){
		return policiesTreeDataMap.get(policyName);
	}
	
	protected String getServerHome(){
		return serverHome;
	}
	protected abstract String getKey();
	protected abstract String getPolicyGroupKey();
	
	private CacheDetail reloadPolicies() {
		CacheDetailProvider cacheDetail = new CacheDetailProvider();
		cacheDetail.setName(getKey());

		List<IPolicyData> policyDataList=null;
		
		if(this.isInitializedFromDB){			
			cacheDetail.setSource("AAA_Server_DS");			
			try{
				policyDataList=readFromDB();
				cacheDetail.setResultCode(initCache(policyDataList));		
				serializePolicies();
			}catch(LoadCacheFailedException e) {
				cacheDetail.setResultCode(CacheConstants.FAIL);
			}
		}else{
			File policyFile = new File(getSerializeFileNameForPolicy());
			cacheDetail.setSource(policyFile.getAbsolutePath());			
			try{
				policyDataList=readFromFile(policyFile);
				cacheDetail.setResultCode(initCache(policyDataList));		
				serializePolicies();
			}catch(LoadCacheFailedException e) {
				cacheDetail.setResultCode(CacheConstants.FAIL);
			}
			
		}	
		
		return cacheDetail;
	}	
	
	private CacheDetail reloadPolicyGroups() {
		CacheDetailProvider policyGroupCache = new CacheDetailProvider();
		policyGroupCache.setName(getPolicyGroupKey());
		
		if (isInitializedFromDB) {
			policyGroupCache.setSource("AAA_Server_DS");			
			try {
				PolicyGroupConfiguration newPolicyGroupConfiguration = new PolicyGroupConfiguration();
				List<PolicyGroupData> policyGroupData = readPolicyGroupFromDB();

				newPolicyGroupConfiguration.getPolicyGroupDataList().addAll(policyGroupData);
				newPolicyGroupConfiguration.postRead();
				this.policyGroupConfiguration = newPolicyGroupConfiguration;
				policyGroupCache.setResultCode(CacheConstants.SUCCESS);
				serializePolicyGroup();
			} catch (LoadCacheFailedException e) { 
				policyGroupCache.setResultCode(CacheConstants.FAIL);
				ignoreTrace(e);
			}
		} else {
			File policyGroupFile = new File(getSerializeFileNameForPolicyGroup());
			policyGroupCache.setSource(policyGroupFile.getAbsolutePath());			
			try{
				PolicyGroupConfiguration newPolicyGroupConfiguration = ConfigUtil.deserialize(policyGroupFile, PolicyGroupConfiguration.class);
				newPolicyGroupConfiguration.postRead();
				this.policyGroupConfiguration = newPolicyGroupConfiguration;
				policyGroupCache.setResultCode(CacheConstants.SUCCESS);
			} catch (FileNotFoundException e) { 
				policyGroupCache.setResultCode(CacheConstants.FAIL);
				ignoreTrace(e);
			} catch (JAXBException e) { 
				policyGroupCache.setResultCode(CacheConstants.FAIL);
				ignoreTrace(e);
			}
		}
		return policyGroupCache;
	}
	
	abstract protected @Nonnull List<PolicyGroupData> readPolicyGroupFromDB() throws LoadCacheFailedException;
	
	public String getExpressionFrom(String groupPolicyName) {
		return this.policyGroupConfiguration.getExpressionByGroupName(groupPolicyName);
	}
}