package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.core.translator.policy.data.impl.MappingDataImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslationDetailImpl;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorPolicyDataImpl;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;

@XmlType(propOrder = {})
@XmlRootElement(name = "translation-mapping") 
@ConfigurationProperties(moduleName ="TRANSLATION-MAPPING-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "translation-mapping-conf", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db"})
public class TranslationMappingConfigurable extends Configurable{

	private List<TranslationMappingConfigurationData> translationMappingList;
	private final static String MODULE = "TRANSLATION-MAPPING-CONFIGURABLE";	
	private static final String REQUEST_PARAMETERS="TMI0001";
	private static final String RESPONSE_PARAMETERS="TMI0002";
	
	public TranslationMappingConfigurable(){
		translationMappingList = new ArrayList<TranslationMappingConfigurationData>();
	}

	@DBRead
	@DBReload
	public void readTranslationMappingConfiguration() throws Exception {
		this.translationMappingList = loadTranslationMappingConfiguration();
	}

	@PostRead
	public void postReadProcessing() {

	}

	@XmlElement(name = "trasnaltion-mapping")
	public List<TranslationMappingConfigurationData> getTranslationMappingList(){
		return translationMappingList;
	}
	public void setTranslationMappingList(List<TranslationMappingConfigurationData> translationMappingList){
		this.translationMappingList = translationMappingList;
	}

	private List<TranslationMappingConfigurationData> loadTranslationMappingConfiguration() throws Exception{

		LogManager.getLogger().info(MODULE, "Loading Detail of Translation Mapping configuration from database");
		TranslatorPolicyDataImpl translatorPolicyDataImpl;
		TranslationDetailImpl translationDetailImpl; 
		MappingDataImpl mappingDataImpl;

		// Added 
		List<TranslationMappingConfigurationData> transMappingConfigurationList = new  ArrayList<TranslationMappingConfigurationData>();
		List <TranslationDetailImpl> translationDetailList;
		List<MappingDataImpl>requestMappingDataList ;
		List<MappingDataImpl>responseMappingDataList;

		Connection connection = null;

		PreparedStatement pstmtTranslatorPolicyData=null;
		PreparedStatement pstmtTranslationDetail=null;
		PreparedStatement pstmtMppingData=null;
		PreparedStatement pstmtDummyResponceData=null;
		PreparedStatement pstmtMappingInstDetailRelData=null;

		ResultSet rsTranslatorPolicyData = null;
		ResultSet rsTranslationDetail = null;
		ResultSet rsMappingData = null;
		ResultSet rsDummyResponceData = null;
		ResultSet rsMappingInstDetailRelData = null;


		try{
			connection=EliteAAADBConnectionManager.getInstance().getConnection();
			pstmtTranslatorPolicyData=connection.prepareStatement(getTranslatorPolicyDataQuery());
			pstmtTranslationDetail=connection.prepareStatement(getTranslationDetailQuery());
			pstmtMppingData=connection.prepareStatement(getMappingDetailQuery());
			pstmtDummyResponceData=connection.prepareStatement(getDummyResponseDataQuery());
			pstmtMappingInstDetailRelData=connection.prepareStatement(getMappingInstDetailRelQuery());

			if(pstmtTranslatorPolicyData == null || pstmtTranslationDetail == null || pstmtMppingData== null ||
					pstmtDummyResponceData==null ||pstmtMappingInstDetailRelData==null){

				LogManager.getLogger().debug(MODULE,"PreparedStatement is null.");
				throw new SQLException("Prepared statement is null.");
			}

			rsTranslatorPolicyData=pstmtTranslatorPolicyData.executeQuery();
			while(rsTranslatorPolicyData.next()){
				TranslationMappingConfigurationData translationMappingConfigurationData = new TranslationMappingConfigurationData();
				translatorPolicyDataImpl=new TranslatorPolicyDataImpl();

				translatorPolicyDataImpl.setTransMapConfId(rsTranslatorPolicyData.getString("transmapconfid"));
				translatorPolicyDataImpl.setName(rsTranslatorPolicyData.getString("name"));
				translatorPolicyDataImpl.setFromTranslatorId(rsTranslatorPolicyData.getString("fromtype"));
				translatorPolicyDataImpl.setToTranslatorId(rsTranslatorPolicyData.getString("totype"));
				translatorPolicyDataImpl.setBaseTranslationMappingId(rsTranslatorPolicyData.getString("BASETRANSMAPCONFID"));
				translatorPolicyDataImpl.setIsDummyResponse(ConfigurationUtil.stringToBoolean(rsTranslatorPolicyData.getString("dummyresponse"), translatorPolicyDataImpl.getIsDummyResponse()));
				translatorPolicyDataImpl.setScript(rsTranslatorPolicyData.getString("script"));
				pstmtDummyResponceData.setString(1, translatorPolicyDataImpl.getTransMapConfId());
				rsDummyResponceData=pstmtDummyResponceData.executeQuery();
				List<DummyResponseDetail> dummyResponseList=new ArrayList<DummyResponseDetail>();
				while(rsDummyResponceData.next()){ 
					DummyResponseDetail dummyResponseConf = new DummyResponseDetail();
					dummyResponseConf.setOutfield(rsDummyResponceData.getString("outfield"));
					dummyResponseConf.setValue(rsDummyResponceData.getString("value"));
					dummyResponseList.add(dummyResponseConf);
				}       
				translatorPolicyDataImpl.setDummyResposeList(dummyResponseList);
				
				pstmtTranslationDetail.setString(1, translatorPolicyDataImpl.getTransMapConfId());     
				rsTranslationDetail=pstmtTranslationDetail.executeQuery();      
				translationDetailList= new ArrayList<TranslationDetailImpl>();      
				while(rsTranslationDetail.next()){        
					boolean dummyResponse = false;
					translationDetailImpl=new TranslationDetailImpl();      
					
					translationDetailImpl.setMappingName(rsTranslationDetail.getString("mappingname"));
					translationDetailImpl.setInRequestType(rsTranslationDetail.getString("inmessage"));      
					translationDetailImpl.setOutRequestType(rsTranslationDetail.getString("outmessage"));
					dummyResponse = ConfigurationUtil.stringToBoolean(rsTranslationDetail.getString("DUMMYRESPONSE"), dummyResponse);
					translationDetailImpl.setIsDummyResponse(dummyResponse);
					pstmtMappingInstDetailRelData.setString(1, rsTranslationDetail.getString("mappinginstanceid"));    
					rsMappingInstDetailRelData=pstmtMappingInstDetailRelData.executeQuery();        
					requestMappingDataList=new ArrayList<MappingDataImpl>();     
					responseMappingDataList=new ArrayList<MappingDataImpl>();     
					while(rsMappingInstDetailRelData.next()){    
						pstmtMppingData.setString(1, rsMappingInstDetailRelData.getString("detailid"));     
						rsMappingData=pstmtMppingData.executeQuery();      
						while(rsMappingData.next()){        
							mappingDataImpl=new MappingDataImpl(); 
							String str = rsMappingData.getString("checkexpression");
							if(str == null || str.length() < 1){
								continue;
							}
							mappingDataImpl.setCheckExpression(str);     
							str = rsMappingData.getString("mappingexpression");
							if(str == null || str.length() < 1){
								continue;
							}
							mappingDataImpl.setMappingExpression(str);
							mappingDataImpl.setDefaultValue(rsMappingData.getString("defaultvalue"));
							mappingDataImpl.setValueMapping(rsMappingData.getString("valuemapping"));

							if(rsMappingData.getString("mappingtypeid").equals(REQUEST_PARAMETERS)){
								requestMappingDataList.add(mappingDataImpl); 

							}else if(rsMappingData.getString("mappingtypeid").equals(RESPONSE_PARAMETERS)){
								responseMappingDataList.add(mappingDataImpl);
							}
						}
					}
					translationDetailImpl.setRequestMappingDataList(requestMappingDataList);
					translationDetailImpl.setResponseMappingDataList(responseMappingDataList);
					translationDetailList.add(translationDetailImpl);
				}
				translatorPolicyDataImpl.setTranslationDetailList(translationDetailList);
				translationMappingConfigurationData.setTransMappingConfId(translatorPolicyDataImpl.getTransMapConfId());
				translationMappingConfigurationData.setTranslationPolicyDataObj(translatorPolicyDataImpl);
				// Added For TrnaslationMapping 
				transMappingConfigurationList.add(translationMappingConfigurationData);
			}
		}finally{
			DBUtility.closeQuietly(rsTranslatorPolicyData);
			DBUtility.closeQuietly(rsTranslationDetail);
			DBUtility.closeQuietly(rsMappingData);
			DBUtility.closeQuietly(rsDummyResponceData);
			DBUtility.closeQuietly(rsMappingInstDetailRelData);
			DBUtility.closeQuietly(pstmtTranslatorPolicyData);
			DBUtility.closeQuietly(pstmtTranslationDetail);
			DBUtility.closeQuietly(pstmtMppingData);
			DBUtility.closeQuietly(pstmtDummyResponceData);
			DBUtility.closeQuietly(pstmtMappingInstDetailRelData);
			DBUtility.closeQuietly(connection);
		}	
		return transMappingConfigurationList;		
	}

	private String getTranslatorPolicyDataQuery(){
		return "select * from tblmtranslationmappingconf";
	}

	private String getDummyResponseDataQuery(){
		return "select * from tblmdummyresponseparams where  transmapconfid= ? order by ordernumber";
	}

	private String getTranslationDetailQuery(){
		return "select * from tblmtransmappinginstance  where transmapconfid= ? order by ordernumber";
	}

	private String getMappingInstDetailRelQuery(){
		return "select * from tblmmappinginstdetailrel where mappinginstanceid = ? order by ordernumber";
	}

	private String getMappingDetailQuery(){
		return "select * from tblmtransmappinginstdetail where detailid = ? order by ordernumber";

	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	
	}

