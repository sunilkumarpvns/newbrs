package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "spi-group-list")
@ConfigurationProperties(moduleName ="SPI_KEY_CONFIGURABLE",synchronizeKey ="SPI_KEY", readWith = XMLReader.class)
@XMLProperties(configDirectories = {"conf"}, name = "spi-keys-conf", schemaDirectories = {"system","schema"})
public class SPIKeyConfigurable extends Configurable implements SPIKeyConfiguration {

	private List<SPIGroupDetail> spiGroupList;
	private Map<String ,Map<String,String>> spiGroupMap;

	public SPIKeyConfigurable(){
		spiGroupList = new ArrayList<SPIKeyConfigurable.SPIGroupDetail>();
		spiGroupMap =  new HashMap<String, Map<String,String>>();
	}

	@XmlElement(name = "spi-group")
	@Reloadable(type = SPIGroupDetail.class)
	public List<SPIGroupDetail> getSpiGroupList() {
		return spiGroupList;
	}

	public void setSpiGroupList(List<SPIGroupDetail> spiGroup) {
		this.spiGroupList = spiGroup;
	}

	@PostRead
	public void postReadProcessing() {
		if(this.spiGroupList!=null){
			this.spiGroupMap = new HashMap<String, Map<String,String>>();
			int numOFSpiGroup = spiGroupList.size();
			SPIGroupDetail spiGroup;
			for(int i=0;i<numOFSpiGroup;i++){
				spiGroup = spiGroupList.get(i);
				String [] haAddress = null;
				Map<String ,String> keyPairMap = new HashMap<String, String>();
				SPIKeyPairList spiKeyPairConf = spiGroup.getSpiKeyPairList();
				if(spiKeyPairConf!=null){
					List<SPIKeyPairDetails>  spiKeyPairList = spiKeyPairConf.getSpiKeyPairList();
					if(spiKeyPairList!=null){
						int numOFKeyPair = spiKeyPairList.size();
						for(int j=0;j<numOFKeyPair;j++){
							SPIKeyPairDetails spiKeyPair = spiKeyPairList.get(j);
							keyPairMap.put(spiKeyPair.getSpi(), spiKeyPair.getKey());
						}
					}
				}
				String haAddressStr = spiGroup.getHaAddress();
				if(haAddressStr!=null && haAddressStr.trim().length()>0){
					haAddress = spiGroup.getHaAddress().split("[,;]");	
					for(int m=0;m<haAddress.length;m++){
						this.spiGroupMap.put(haAddress[m],keyPairMap);
					}
				}
			}
		}
	}


	@PostWrite
	public void postWriteProcessing(){

	}

	@PostReload
	public void postReloadProcessing(){
		//as this class is totally reloadable so calling the method for read processing
		postReadProcessing();
	}

	@Override
	@XmlTransient
	public Map<String, Map<String, String>> getSPIgroupMap() {
		return spiGroupMap;
	}
	public void setSPIgroupMap(Map<String, Map<String, String>> spiGroupMap){
		this.spiGroupMap = spiGroupMap;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SPI Key Configuration: \n");
		builder.append("---------------------------\n");
		for(SPIGroupDetail spiGroupDetail : spiGroupList){
			builder.append("--SPI Group Detail--\n");
			builder.append(spiGroupDetail);
			builder.append("\n");
		}
		return builder.toString();
	}
	
	@XmlType(propOrder = {})
	@Reloadable(type = SPIGroupDetail.class)
	public static class SPIGroupDetail{

		private String haAddress;
		private SPIKeyPairList spiKeyPairList;

		public SPIGroupDetail(){
			//requied by Jaxb.
			spiKeyPairList = new SPIKeyPairList();
		}


		@XmlElement(name = "ha-ip-address-list",type = String.class)
		public String getHaAddress() {
			return haAddress;
		}
		public void setHaAddress(String haAddress) {
			this.haAddress = haAddress;
		}
		@XmlElement(name = "spi-key-pair-list")
		public SPIKeyPairList getSpiKeyPairList() {
			return spiKeyPairList;
		}
		public void setSpiKeyPairList(SPIKeyPairList spiKeyPairList) {
			this.spiKeyPairList = spiKeyPairList;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("HA IP Address: " + haAddress);
			builder.append("\n");
			builder.append(spiKeyPairList);
			return builder.toString();
		}
	}
	
	@XmlType(propOrder = {})
	public static class SPIKeyPairList{

		private List<SPIKeyPairDetails> spiKeyPairList;

		public SPIKeyPairList(){
			//required by Jaxb.
			spiKeyPairList = new ArrayList<SPIKeyPairDetails>();
		}

		@XmlElement(name = "spi-key-pair")
		public List<SPIKeyPairDetails> getSpiKeyPairList() {
			return spiKeyPairList;
		}

		public void setSpiKeyPairList(List<SPIKeyPairDetails> spiKeyPairList) {
			this.spiKeyPairList = spiKeyPairList;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SPI Key Pair List: \n");
			builder.append(spiKeyPairList);
			return builder.toString();
		}
	}

	@XmlType(propOrder = {})
	public static class SPIKeyPairDetails{

		private String spi;
		private String key;

		public SPIKeyPairDetails(){
			//required by Jaxb.
		}
		@XmlElement(name = "spi",type = String.class)
		public String getSpi() {
			return spi;
		}
		public void setSpi(String spi) {
			this.spi = spi;
		}
		@XmlElement(name = "key",type = String.class)
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		
		@Override
		public String toString() {
			return "SPI: " + spi + " Key: " + key + "\n";
		}
	}
}
