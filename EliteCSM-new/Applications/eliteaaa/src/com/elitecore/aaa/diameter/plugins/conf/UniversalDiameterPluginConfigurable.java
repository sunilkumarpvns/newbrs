package com.elitecore.aaa.diameter.plugins.conf;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.plugins.conf.BasePluginConfigurable;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.plugins.DiameterParamDetail;
import com.elitecore.diameterapi.plugins.DiameterPlugin;
import com.elitecore.diameterapi.plugins.DiameterUniversalPluginPolicyDetail;
import com.elitecore.diameterapi.plugins.universal.UniversalDiameterPlugin;
import com.elitecore.diameterapi.plugins.universal.UniversalDiameterPlugin.UniversalDiameterActionConstant;
import com.elitecore.diameterapi.plugins.universal.conf.DiameterUniversalPluginDetails;
import com.elitecore.diameterapi.plugins.universal.constant.UniversalDiameterPluginConstant;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

@XmlType(propOrder = {})
@XmlRootElement(name = "diameter-universal-plugins")
@ConfigurationProperties(moduleName ="UNIVERSAL_DIA_PLUGIN_CONFIGURABLE",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","plugin","diameter"},name = "universal-diameter-plugin")
public class UniversalDiameterPluginConfigurable extends BasePluginConfigurable<DiameterPlugin> {

	public static final String MODULE = "UNIVERSAL-DIAMETER-PLUGIN";
	
	private List<DiameterUniversalPluginDetails> data = new ArrayList<DiameterUniversalPluginDetails>();
	
	private List<DiameterUniversalPluginPolicyDetail> inPluginList = new ArrayList<DiameterUniversalPluginPolicyDetail>();
	private List<DiameterUniversalPluginPolicyDetail> outPluginList = new ArrayList<DiameterUniversalPluginPolicyDetail>();
	private PluginContext pluginContext;
	
	public UniversalDiameterPluginConfigurable() {
		this.inPluginList = new ArrayList<DiameterUniversalPluginPolicyDetail>();
		this.outPluginList = new ArrayList<DiameterUniversalPluginPolicyDetail>();
		this.pluginContext = new PluginContext() {
			
			@Override
			public ServerContext getServerContext() {
				return ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
			}
			
			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		};
	}
		
	@DBRead
	public void readFromDB() throws JAXBException, SQLException {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		JAXBContext context = JAXBContext.newInstance(DiameterUniversalPluginDetails.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			
			Set<PluginInfo> authTypePlugin = ((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).getPluginDetail().getTypeSpecific(PluginType.UNIVERSAL_DIAMETER_PLUGIN);
			
			List<DiameterUniversalPluginDetails> tmpData = new ArrayList<DiameterUniversalPluginDetails>();
			
			for (PluginInfo pluginInfo : authTypePlugin) {
				pstmt = connection.prepareStatement(getQueryForConfReading());
				pstmt.setString(1, pluginInfo.getId());
				resultSet = pstmt.executeQuery();
				while (resultSet.next()) {
					byte[] universalPluginBytes = resultSet.getBytes("PLUGINDATA");
					InputStream xmlDataAsAStream = new ByteArrayInputStream(universalPluginBytes);
					DiameterUniversalPluginDetails configData = (DiameterUniversalPluginDetails) unMarshaller.unmarshal(xmlDataAsAStream);
					tmpData.add(configData);
				}
			}
			
			this.data = tmpData;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(connection);
		}
	}
	
	private String getQueryForConfReading() {
		return "SELECT * FROM TBLMUNIVERSALPLUGIN WHERE PLUGININSTANCEID = ?";
	}
	
	@DBReload
	public void reloadFromDB() {
		
	}
	
	@PostRead
	public void postReadProcessing() {

		for (DiameterUniversalPluginDetails configData : data) {
			
			configData.postRead();
			
			if(Collectionz.isNullOrEmpty(configData.getInPluginList()) == false){
				this.inPluginList = getParsedPolicies(configData.getInPluginList());
			}
			if(Collectionz.isNullOrEmpty(configData.getOutPluginList()) == false){
				this.outPluginList = getParsedPolicies(configData.getOutPluginList());
			}
		}
	}

	@XmlElement(name = "diameter-universal-plugin")
	public List<DiameterUniversalPluginDetails> getData() {
		return data;
	}
	
	public void setData(List<DiameterUniversalPluginDetails> data) {
		this.data = data;
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}

	public List<DiameterUniversalPluginPolicyDetail> getParsedPolicies(List<DiameterUniversalPluginPolicyDetail> policyList){
		List<DiameterUniversalPluginPolicyDetail> tempList = new ArrayList<DiameterUniversalPluginPolicyDetail>();
		for(DiameterUniversalPluginPolicyDetail pluginPolicyDetail: policyList) {
			if(pluginPolicyDetail.getParameterDetailsForPlugin() !=null &&  pluginPolicyDetail.getParameterDetailsForPlugin().size()>0){
				pluginPolicyDetail.setPolicyAction(Numbers.parseInt(pluginPolicyDetail.getAction(),UniversalDiameterActionConstant.NONE.value));
				try {
					for(DiameterParamDetail diameterParamDetail : pluginPolicyDetail.getParameterDetailsForPlugin()){
						diameterParamDetail.setIsActive(Strings.toBoolean(diameterParamDetail.getActive()));
						compile(diameterParamDetail);
					}
				} catch (InvalidExpressionException e) {
					
					LogManager.getLogger().warn(MODULE, "Skipping Policy: " + pluginPolicyDetail.getName() 
							+ ", Reason: Error while parsing Parameter Details, "  + e.getMessage());
					continue;
				}
				tempList.add(pluginPolicyDetail);
			}
		}
		return tempList;
	}

	private void compile(DiameterParamDetail diameterParamDetail) throws InvalidExpressionException {
		
		if (Strings.isNullOrBlank(diameterParamDetail.getAttr_id()) ) {
			throw new InvalidExpressionException("Attribute-Id not configured");
		}
		
		if (Strings.isNullOrBlank(diameterParamDetail.getAttribute_value()) ) {
			throw new InvalidExpressionException("Attribute-Value not configured");
		}
		
		diameterParamDetail.setAttr_id(diameterParamDetail.getAttr_id().trim());
		diameterParamDetail.setAttribute_value(diameterParamDetail.getAttribute_value().trim());
		
		String expression = "";
		// for check and reject items -> expression will be a comparison expression
		if (UniversalDiameterPluginConstant.CHECK_ITEM.equalsIgnoreCase(diameterParamDetail.getParameter_usage())
				|| UniversalDiameterPluginConstant.REJECT_ITEM.equalsIgnoreCase(diameterParamDetail.getParameter_usage())){

			/* expression will be of form 
			 * 0:1 = 0:89  --> in this case attrId is 0:1 attrValue is 0:89, 
			 * this code will concat `attrId`, `=` and `attrValue` to generate Expression
			 * 0:1 IN (0:31,0:89,0:30) --> in this case attrId is 0:1 attrValie is IN (0:31,0:89,0:30)
			 * below code will concat `attrId` and `attrValue` to generate Expression
			 */
			expression = diameterParamDetail.getAttr_id() + " ";
			if (diameterParamDetail.getAttribute_value().startsWith("IN") == false
					&& diameterParamDetail.getAttribute_value().startsWith("<") == false
					&& diameterParamDetail.getAttribute_value().startsWith(">") == false) {
				expression += "= ";
			} 
		}
		// when parameter usage is other than check and reject item,
		// expression will be any expression
		expression += diameterParamDetail.getAttribute_value();
		diameterParamDetail.setParameterExpression(
				Compiler.getDefaultCompiler().parseExpression(expression));
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println("\n ----Universal Diameter Plugin Configuration---- ");
		out.println();
        out.println("----Diameter In Policy PlugIn Details----");
        out.println("	--Diameter In Policy--");
        if(inPluginList != null && !inPluginList.isEmpty()){
        	for (int i=0; i< inPluginList.size(); i++){
                out.println(inPluginList.get(i));
        	}
        }else { 
        	out.println("No Diameter In plugin-policy configured");
			}

        out.println("----Diameter Out Policy plugIn Details----");
       
        if(outPluginList != null && !outPluginList.isEmpty()){
        	for (int i=0; i< outPluginList.size(); i++){
        		out.println("	--Diameter Out Policy--");
                out.println(outPluginList.get(i) );
				}
        }else { 
        	out.println("No Diameter Out plugin-policy configured");
			}
        out.close();

        return stringBuffer.toString();
	}

	@Override
	public void createPlugin(Map<String, DiameterPlugin> nameToPlugin) {
		for (DiameterUniversalPluginDetails pluginDetail : this.data) {
			UniversalDiameterPlugin plugin = new UniversalDiameterPlugin(pluginContext, pluginDetail);
			try {
				plugin.init();
				LogManager.getLogger().info(MODULE, "Successfully initailized Universal Diameter Plugin: " + pluginDetail.getName());
				nameToPlugin.put(pluginDetail.getName(), plugin);
			} catch (InitializationFailedException e) {
				LogManager.getLogger().error(MODULE, "Failed to initialize universal diameter plugin, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
		}
	}
}