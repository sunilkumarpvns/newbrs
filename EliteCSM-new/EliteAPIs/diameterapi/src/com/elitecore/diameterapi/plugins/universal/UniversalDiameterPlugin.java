package com.elitecore.diameterapi.plugins.universal;
		
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.translator.operations.data.DiameterHeaderFields;
import com.elitecore.diameterapi.plugins.BaseDiameterPlugin;
import com.elitecore.diameterapi.plugins.DiameterParamDetail;
import com.elitecore.diameterapi.plugins.DiameterUniversalPluginPolicyDetail;
import com.elitecore.diameterapi.plugins.universal.conf.DiameterUniversalPluginDetails;
import com.elitecore.diameterapi.plugins.universal.conf.UniversalDiameterPluginConf;
import com.elitecore.diameterapi.plugins.universal.constant.UniversalDiameterPluginConstant;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class UniversalDiameterPlugin extends BaseDiameterPlugin {
	
	public static final String MODULE = "UNIVERSAL_DIAMETER_PLUGIN";
	private List<DiameterUniversalPluginPolicyDetail> inPolicyList;
	private List<DiameterUniversalPluginPolicyDetail> outPolicyList;
	
	public static final int IN_PACKET = 1;
	public static final int OUT_PACKET = 2;
	private DiameterUniversalPluginDetails data;
	
	public UniversalDiameterPlugin(PluginContext pluginContext,
			PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
	}
	
	

	public UniversalDiameterPlugin(PluginContext context, DiameterUniversalPluginDetails pluginDetail) {
		super(context, pluginDetail.getPluginInfo());
		this.data = pluginDetail;
		this.inPolicyList = new ArrayList<DiameterUniversalPluginPolicyDetail>(data.getInPluginList());
		this.outPolicyList = new ArrayList<DiameterUniversalPluginPolicyDetail>(data.getOutPluginList());
	}


	@Override
	public void init() throws InitializationFailedException {
		if(data == null){
			throw new InitializationFailedException("Universal Plugin configuration is null");
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Universal Diameter Plugin: " + data.getName());
		}

		Collectionz.filter(inPolicyList, new Predicate<DiameterUniversalPluginPolicyDetail>() {

			@Override
			public boolean apply(DiameterUniversalPluginPolicyDetail policyDetail) {
				return policyDetail.isEnabled();
			}
		});
			
		Collectionz.filter(outPolicyList, new Predicate<DiameterUniversalPluginPolicyDetail>() {

			@Override
			public boolean apply(DiameterUniversalPluginPolicyDetail policyDetail) {
				return policyDetail.isEnabled();
			}
		});	
	}

	@Override
	public void reInit() throws InitializationFailedException {
		init();
	}
	
	@Override
	public void handleInMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Apply universal diameter plugin: " + data.getName());
		}
		applyUniversalPluginPolicies(diameterRequest,diameterAnswer, inPolicyList);
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Universal Diameter In Plugin policy handled successfully.");
		}
	}

	@Override
	public void handleOutMessage(DiameterPacket diameterRequest,
			DiameterPacket diameterAnswer, ISession session, String argument, PluginCallerIdentity callerID) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Apply universal diameter plugin: " + data.getName());
		}
		applyUniversalPluginPolicies(diameterRequest,diameterAnswer, outPolicyList);
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Universal Diameter Out Plugin policy handled successfully.");
		}
		
	}

	@Override
	public String toString() {
			return ((UniversalDiameterPluginConf)getPluginConfiguration()).toString();
    }
	
	private void alterPacket(DiameterPacket diameterRequest, DiameterPacket diameterAnswer, 
			List<DiameterParamDetail> parameterList) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Executing Add, Filter or Dynamic Assign Item"
					+ " for Diameter Packet");
		}
		
		DiameterParamDetail diameterParamsDetail;
		for (int i = 0; i < parameterList.size(); i++) {
			diameterParamsDetail = parameterList.get(i);

			if (diameterParamsDetail.isActive() == false) {
				continue;
			}
			int ipacketType = diameterParamsDetail.getPacket_type();
			DiameterPacket diameterPacket = getRequestPacket(ipacketType, diameterRequest, diameterAnswer);

			if (diameterPacket == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Skipping item: " +diameterParamsDetail.getParameter_usage() 
							+ " with Attribute-Id: "+ diameterParamsDetail.getAttr_id() 
							+ ", Reason: Packet-Type: " 
							+ (diameterParamsDetail.getPacket_type() == 1 ? "Request" : "Answer")  
							+ " not found.");
				}
				continue;
			}
			alterPacket(diameterPacket, diameterParamsDetail, new DiameterAVPValueProvider(diameterPacket));
		}

	}

	private boolean alterPacket(DiameterPacket diameterPacket,
			DiameterParamDetail diameterParamsDetail, 
			DiameterAVPValueProvider valueProvider) {

		String value;
		try {
			value = diameterParamsDetail.getParameterExpression()
					.getStringValue(valueProvider);
		} catch (Exception e) { 
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping item: " + diameterParamsDetail.getParameter_usage() 
						+ " with Attribute-Id: "+ diameterParamsDetail.getAttr_id() 
						+ ", Reason: " + e.getMessage());
			}
			ignoreTrace(e);
			return false;
		} 
		if (UniversalDiameterPluginConstant.FILTER_ITEM.equalsIgnoreCase(diameterParamsDetail.getParameter_usage())) {
			return filter(diameterParamsDetail.getAttr_id(), value, diameterPacket);
		} 
		
		if (UniversalDiameterPluginConstant.REPLY_ITEM.equalsIgnoreCase(diameterParamsDetail.getParameter_usage())) {
			return add(diameterParamsDetail.getAttr_id(), value, diameterPacket);
		}
		
		if (UniversalDiameterPluginConstant.VALUE_REPLACE_ITEM.equalsIgnoreCase(diameterParamsDetail.getParameter_usage())) {
			DiameterHeaderFields diameterHeaderField = DiameterHeaderFields
					.getHeaderField(diameterParamsDetail.getAttr_id());
			
			if (diameterHeaderField != null) {
				return diameterHeaderField.apply(diameterPacket, value);
			}
			return replaceValue(diameterParamsDetail.getAttr_id(), 
					value, diameterPacket);
		} 

		if (UniversalDiameterPluginConstant.ASSIGN_ITEM.equalsIgnoreCase(diameterParamsDetail.getParameter_usage())) {
			return dynamicallyAssignValue(diameterParamsDetail.getAttr_id(),
					value, diameterPacket);
		}
		return true;
	}

	private boolean add(String attributeId, String value, DiameterPacket diameterPacket) {
		try {
			IDiameterAVP avp = DiameterUtility.createAvp(attributeId, value);
			diameterPacket.addAvp(avp);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Added Attribute: " + attributeId 
						+ " in packet with value: " + value);
			}
			return true;
		} catch (Exception e) { 
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Error occured in adding Attribute: " + attributeId 
						+ " with value: " + value + ", Reason: " + e.getMessage());
			}
			ignoreTrace(e);
			return false;
		}
	}

	private DiameterPacket getRequestPacket(int requestType , DiameterPacket diameterRequest, DiameterPacket diameterAnswer){
		
		final int NOT_DEFINE = 0; 
		DiameterPacket diameterPacket = null;
		if(requestType == NOT_DEFINE){
			diameterPacket = diameterRequest;
		// Support for Request
		}else if(requestType == IN_PACKET ){
			diameterPacket = diameterRequest;

		// Support for Answer
		}else if(requestType == OUT_PACKET){
			diameterPacket = diameterAnswer;
		}
		return diameterPacket;
	}
	
	private boolean dynamicallyAssignValue(String attributeId, String value,DiameterPacket diameterPacket) {

		IDiameterAVP avp = diameterPacket.getAVP(attributeId, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);

		try{

			if (avp != null) {
				avp.setStringValue(value);
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Attribute: " + attributeId 
							+ " updated with value: " + value);
				}
				return true;
			}
			avp = DiameterUtility.createAvp(attributeId, value);
			diameterPacket.addAvp(avp);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Attribute: " + attributeId 
						+ " not arrived in packet, added attribute to packet with value: " + value);
			}
			return true;
		} catch (Exception ex) { 
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Error occured while updating value of Attribute: " 
						+ attributeId + " with value: " + value 
						+ ", Reason: " + ex.getMessage());
			}
			ignoreTrace(ex);
			return false;
		}
	}
	
	private boolean replaceValue(String attributIdStr,
			String strAttrValue, DiameterPacket diameterPacket) {
		
		IDiameterAVP diameterAVP = diameterPacket.getAVP(attributIdStr, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
		if (diameterAVP == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Not applying Value-Replace Item, Reason: Attribute: " 
						+ attributIdStr + " not found in packet");
			}
			return false;
		}
		try {
			diameterAVP.setStringValue(strAttrValue);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Attribute: " + attributIdStr 
						+ " updated with value: " + strAttrValue);
			}
			return true;
		} catch (Exception ex) { 
			
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Error occured while updating Attribute: " 
						+ attributIdStr + "with value: " 
						+ strAttrValue + ", Reason: " + ex.getMessage());
			}
			ignoreTrace(ex);
			return false;
		}
	}
	
	private boolean filter(String attributId,
			String value, DiameterPacket diameterPacket) {

		List<IDiameterAVP> diameterAVPs = diameterPacket.getAVPList(attributId, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
		if (Collectionz.isNullOrEmpty(diameterAVPs)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Not applying Filter Item, Reason: Attribute: " 
						+ attributId + " not found in packet");
			}
			return true;
		}

		if ("*".equals(value)) {
			diameterPacket.removeAllAVPs(diameterAVPs, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Attribute(s): " + attributId + 
						" filtered from packet");
			}
			return true;
		}
		boolean matchFound = false;
		for (IDiameterAVP avp : diameterAVPs) {
			if (avp.getStringValue().equals(value)) {
				diameterPacket.removeAVP(avp, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Attribute: " + attributId + 
							" with Value: " + value +
							", is filtered from packet");
				}
				matchFound = true;
			} else if (DiameterUtility.matches(avp.getStringValue(), value)) {
				diameterPacket.removeAVP(avp, DiameterPacket.INCLUDE_INFO_ATTRIBUTE);
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Attribute: " + attributId + 
							" matched Value: " + value + 
							", is filtered from packet");
				}
				matchFound = true;
			}
		}
		if (matchFound == false) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Not applying Filter Item for Attribute: "
						+ attributId
						+ ", Reason: filter expression evaluation failed."); 
			}
		}
		return matchFound;
	}
	
	public void applyUniversalPluginPolicies(DiameterPacket diameterRequest,DiameterPacket diameterAnswer,List<DiameterUniversalPluginPolicyDetail> policyList) {

		if (Collectionz.isNullOrEmpty(policyList)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No plugin policy found.");
			}
			return;
		}
		
		for (DiameterUniversalPluginPolicyDetail pluginPolicyDetail : policyList) {

			List<DiameterParamDetail> parameterList = pluginPolicyDetail.getParameterDetailsForPlugin();
			if (Collectionz.isNullOrEmpty(parameterList)) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Skipping Policy: "+ pluginPolicyDetail.getName()
							+ " No parameter list found for policy");
				}
				continue;
			}
			if (applyCheckAndRejectItems(diameterRequest, diameterAnswer, parameterList)) {

				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Applying Policy: " + pluginPolicyDetail.getName() 
							+ ", Reason: Check and Reject item(s) Result: Success");
				}
				alterPacket(diameterRequest, diameterAnswer, parameterList);															

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE,"Policy: " + pluginPolicyDetail.getName()
							+ " applied successfully.");
				}

				if(pluginPolicyDetail.getPolicyAction() == UniversalDiameterActionConstant.STOP.value){
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Skipping furthur processing, Reason: Action " 
								+ UniversalDiameterActionConstant.STOP.name
								+ " defined for Policy: " + pluginPolicyDetail.getName());
					}
					return;
				}

			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Policy: " + pluginPolicyDetail.getName() 
							+ " will not be applied, Reason: Check and Reject item(s) Result: Failure");
				}
				continue;
			}
		}
	}
	
	
	//checking whether the policy follows required things
	private boolean applyCheckAndRejectItems(DiameterPacket diameterRequest,DiameterPacket diameterAnswer,
			List<DiameterParamDetail> paramDetailList) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Applying check and reject items.");
		}
		
		DiameterParamDetail diameterParamDetail;
		for (int i = 0; i < paramDetailList.size(); i++) {
			diameterParamDetail = paramDetailList.get(i);

			/*
			 * Skipping INACTIVE Parameters
			 */
			if (diameterParamDetail.isActive() == false) {
				continue;
			}
			DiameterPacket diameterPacket = getRequestPacket(diameterParamDetail.getPacket_type(), 
					diameterRequest, diameterAnswer);
			
			/*
			 * If expected packet not found
			 * then policy fails
			 */
			if (diameterPacket == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Skipping item: " + diameterParamDetail.getParameter_usage() + 
							", Reason: Packet-Type: " 
							+ (diameterParamDetail.getPacket_type() == 1 ? "Request" : "Answer")  
							+ " not found.");
				}
				return false;
			}
			String strParameterUsage = diameterParamDetail.getParameter_usage();

			if (UniversalDiameterPluginConstant.CHECK_ITEM.equalsIgnoreCase(strParameterUsage)) {

				/*
				 * If check Item expression evaluates to false
				 * then policy fails
				 */
				if(((LogicalExpression)diameterParamDetail.getParameterExpression())
					.evaluate(new DiameterAVPValueProvider(diameterPacket)) == false){
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Check expression NOT satisfied for Attribute-Id: " 
								+ diameterParamDetail.getAttr_id());
					}
					return false;
				}
			} 
			if (UniversalDiameterPluginConstant.REJECT_ITEM.equalsIgnoreCase(strParameterUsage)) {

				/*
				 * If reject Item expression evaluates to true
				 * then policy fails
				 */
				if(((LogicalExpression)diameterParamDetail.getParameterExpression())
					.evaluate(new DiameterAVPValueProvider(diameterPacket)) ) {
					
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Reject expression satisfied for Attribute-Id: " 
								+ diameterParamDetail.getAttr_id());
					}
					return false;
				}
			}			
		}
		/*
		 * All the Check Items and Reject Items succeed
		 * Policy Successfully Applied 
		 */
		return true;
	}

	
	 public enum UniversalDiameterActionConstant{
		 
		 	NONE(1,"NONE"),	
		 	STOP(2,"STOP");
			
			
			public final int value;
			public final String name;
			private static final Map<Integer,UniversalDiameterActionConstant> map;
			public static final UniversalDiameterActionConstant[] VALUES = values();
			static {
				map = new HashMap<Integer,UniversalDiameterActionConstant>();
				for (UniversalDiameterActionConstant type : VALUES) {
					map.put(type.value, type);
				}
			}	
			
			UniversalDiameterActionConstant(int value,String name){
				this.value = value;
				this.name = name;
			}	
			public int getValue(){
				return this.value;
			}
			public static boolean isValid(int value){
				return map.containsKey(value);	
			}
			
			public static UniversalDiameterActionConstant get(int key){
				return map.get(key);
			}
			public static String getName(int value){
				return map.get(value).name;
			}
		}

}
