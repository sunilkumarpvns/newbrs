package com.elitecore.aaa.radius.plugins.quotamgmt.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.radius.service.acct.plugins.DS;
import com.elitecore.aaa.radius.service.acct.plugins.Pair;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import net.sf.json.JSONObject;

@XmlRootElement(name = "quota-management-plugin-data")
public class QuotaManagementData implements QuotaManagementPluginConf, Differentiable {

	private static final String MODULE = "QUOTA-MGMT-PLUGIN-DATA";
	public static final Predicate<QuotaManagementData> ENABLED = new Predicate<QuotaManagementData>() {

		@Override
		public boolean apply(QuotaManagementData input) {
			return input.getEnabled();
		}
	};
	
	private String name;
	private String ruleset;
	private String prepaidQuotaType;	
	private String furtherProcessing;
	private Integer action;
	private String packetType;      
	private String keyForVolume;
	private String keyForTime;      
	private String strAttributes;
	private Boolean enabled;

	private LogicalExpression expression;
	private Map<String, Object> paramMap = new HashMap<String, Object>();
	
	public QuotaManagementData() {
		// Do nothing
	}

	@XmlElement(name = "name", type = String.class)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	

	@XmlElement(name ="ruleset",type = String.class)
	public String getRuleset() {
		return ruleset;
	}

	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name ="prepaid-quota-type",type = String.class)
	public String getPrepaidQuotaType() {
		return prepaidQuotaType;
	}

	public void setPrepaidQuotaType(String prepaidQuotaType) {
		this.prepaidQuotaType = prepaidQuotaType;
	}

	@XmlElement(name ="continue-further-processing",type = String.class)
	public String getFurtherProcessing() {
		return furtherProcessing;
	}

	public void setFurtherProcessing(String furtherProcessing) {
		this.furtherProcessing = furtherProcessing;
	}

	@XmlElement(name ="action",type = Integer.class)
	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	@XmlElement(name ="type-of-packet",type = String.class)
	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	@XmlElement(name ="class-attribute-key-for-volume",type = String.class)
	public String getKeyForVolume() {
		return keyForVolume;
	}

	public void setKeyForVolume(String keyForVolume) {
		this.keyForVolume = keyForVolume;
	}

	@XmlElement(name ="class-attribute-key-for-time",type = String.class)
	public String getKeyForTime() {
		return keyForTime;
	}

	public void setKeyForTime(String keyForTime) {
		this.keyForTime = keyForTime;
	}

	@XmlElement(name ="list-of-attributes",type = String.class)
	public String getStrAttributes() {
		return strAttributes;
	}

	public void setStrAttributes(String strAttributes) {
		this.strAttributes = strAttributes;
	}

	@XmlElement(name = "enabled" , type = Boolean.class)
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public JSONObject toJson() {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Name", name);
		jsonObject.put("Ruleset", ruleset);
		jsonObject.put("Enabled", enabled);
		jsonObject.put("Prepaid Quota Type", prepaidQuotaType);
		jsonObject.put("Continue Further Processing", furtherProcessing);
		jsonObject.put("Action", action);
		jsonObject.put("Type Of Packet", packetType);
		jsonObject.put("Class Attribute Key For Volume", keyForVolume);
		jsonObject.put("Class Attribute Key For Time", keyForTime);
		jsonObject.put("List of Attributes", strAttributes);

		return jsonObject;
	}

	@PostRead
	public void postRead() {
		parseConfigurations();
	}

	private void parseConfigurations() {
		ArrayList<DS> attributesList = new ArrayList<DS>();
		if(getPrepaidQuotaType() !=null)
			paramMap.put(QuotaManagementPluginConf.QUOTA_TYPE,getPrepaidQuotaType().toLowerCase());
		if(getFurtherProcessing() !=null)
			paramMap.put(QuotaManagementPluginConf.CONTINUE_FURTHER_PROCESSING, getFurtherProcessing().toLowerCase());

		paramMap.put(QuotaManagementPluginConf.ACTION, getIntActionValue(getAction()));

		if((RadiusConstants.COA_REQUEST_MESSAGE == Integer.parseInt(getPacketType()))) {
			paramMap.put(QuotaManagementPluginConf.TYPE_OF_PACKET,new Integer(RadiusConstants.COA_REQUEST_MESSAGE));
		} else if((RadiusConstants.DISCONNECTION_REQUEST_MESSAGE == Integer.parseInt(getPacketType()))) {
			paramMap.put(QuotaManagementPluginConf.TYPE_OF_PACKET,new Integer(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE));
		} else {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Invalid packet type: " + getPacketType() + " configured for Quota Management Plugin.");
			}
		}
		if(getKeyForVolume() !=null)
			paramMap.put(QuotaManagementPluginConf.CLASS_ATTRIBUTE_KEY_FOR_VOLUME,getKeyForVolume().toLowerCase());
		if(getKeyForTime() !=null)
			paramMap.put(QuotaManagementPluginConf.CLASS_ATTRIBUTE_KEY_FOR_TIME,getKeyForTime().toLowerCase());

		try{		
			String attributes = getStrAttributes();
			if(attributes !=null && !attributes.equals("")){
				String[] delimiterSeparatedString = ParserUtility.splitString(attributes, ',', ';');
				if(delimiterSeparatedString != null) {
					for(int i=0 ; i < delimiterSeparatedString.length ; i++) {
						String content = delimiterSeparatedString[i].trim();
						StringTokenizer stk2 = new StringTokenizer(content,"=");
						if(stk2.countTokens() == 1){
							parseAttributeIdToken(attributesList, content, stk2);  
						}else if(stk2.countTokens() == 2){
							parseAttributePairToken(attributesList, content, stk2);
						} else{
							throw new InvalidInputException("Invalid input: " + attributes + " is configured in attribute configuration");
						}						
					}
				}
			}
			paramMap.put(QuotaManagementPluginConf.LIST_OF_ATTRIBUTES,attributesList);
		} catch(InvalidInputException iie) {
			LogManager.getLogger().warn(MODULE,"Invalid Input for list of attributes tag in interim-volume-meter-configurations.xml, Reason: " + iie.getMessage());
		} catch(Exception e) {
			LogManager.getLogger().warn(MODULE,"Error occurred while reading list of attributes from xml file." + e.getMessage());
		}
	}

	private void parseAttributePairToken(ArrayList<DS> attributesList, String content, StringTokenizer stk2) {
		try {
			DS data = new DS();
			data.setCurr(parse(stk2.nextToken()));
			String next = stk2.nextToken();
			if (!(next.startsWith("\"") && next.endsWith("\""))) {
				if (next.contains(":")) {
					data.setFixedValue(null);
					data.setDyn(parse(next));
				}
			} else {
				next = next.substring(1, next.length() - 1);
				data.setFixedValue(next);
				data.setDyn(null);
			}
			attributesList.add(data);
		} catch (Exception iie) {
			LogManager.getLogger().trace(iie);
			LogManager.getLogger().warn(MODULE, "Invalid input: " + content + " is configured in attribute configuration");
		}
	}

	private void parseAttributeIdToken(ArrayList<DS> attributesList, String content, StringTokenizer stk2) {
		try {
			DS data = new DS();
			data.setCurr(parse(stk2.nextToken()));
			data.setFixedValue(null);
			data.setDyn(null);
			attributesList.add(data);
		} catch (Exception iie) {
			LogManager.getLogger().trace(iie);
			LogManager.getLogger().warn(MODULE, "Invalid input: " + content + " is configured in attribute configuration");
		}
	}

	private Pair parse(String str) throws InvalidInputException {
		Pair data = new Pair();
		StringTokenizer stk = new StringTokenizer(str,":");
		if (stk.countTokens() == 2) {
			data.setVendorID(Long.parseLong(stk.nextToken()));
			data.setAttributeID(Integer.parseInt(stk.nextToken()));
		} else {
			throw new InvalidInputException("Invalid input: " + str + " is configured in attribute configuration");
		}
		return data;
	}

	class InvalidInputException extends Exception {

		private static final long serialVersionUID = 1L;

		public InvalidInputException(String message) {
			super(message);
		}
	}

	private Integer getIntActionValue(Integer action) {
		int iAction = QuotaManagementPluginConf.ACCEPT;

		if(action == QuotaManagementPluginConf.ACCEPT)
			iAction = QuotaManagementPluginConf.ACCEPT;
		else if(action == QuotaManagementPluginConf.DROP)
			iAction = QuotaManagementPluginConf.DROP;
		return iAction;
	}

	@Override
	public Object getValue(String key) {
		return this.paramMap.get(key);
	}

	@Override
	public Integer getAction(String key) {
		return (Integer)paramMap.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<DS> getListOfAttributes() {
		return (ArrayList<DS>)paramMap.get(LIST_OF_ATTRIBUTES);
	}

	@XmlTransient
	public LogicalExpression getExpression() {
		return expression;
	}

	public void setExpression(LogicalExpression expression) {
		this.expression = expression;
	}
}