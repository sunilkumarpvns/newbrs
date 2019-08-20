package com.elitecore.netvertex.core.locationmanagement.data;


import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.*;
import java.util.stream.Collectors;

public class LacConfiguration implements ToStringable{
	
	private String lacInfoId;
	private long lacCode;
    private HashSet<LocationInformationConfiguration> locationInformationConfigurationSet;
	private Map<Long,CGIConfiguration> cgiMap;
	private Map<Long,RAIConfiguration> raiMap;
	private Map<Long,SAIConfiguration> saiMap;
	private Set<String> regionSet;
	private Set<String> citySet;
	private Set<String> areaSet;

    public LacConfiguration(String lacInfoId, Long lacCode) {
		this.lacInfoId = lacInfoId;
		this.lacCode = lacCode;
		cgiMap= new HashMap<>();
		raiMap= new HashMap<>();
		saiMap= new HashMap<>();
		locationInformationConfigurationSet = new HashSet<>();
		regionSet= new HashSet<>();
		citySet= new HashSet<>();
		areaSet= new HashSet<>();
		
	}
	
	
    public String getLacInfoId() {
		return lacInfoId;
	}
	/**
	 * @return the lacCode
	 */
	public long getLacCode() {
		return lacCode;
	}
	
	public void addCgi(CGIConfiguration cgiConfiguration){
		
		cgiMap.put(cgiConfiguration.getCi(), cgiConfiguration);
		
	}
				
	public void addRai(RAIConfiguration raiConfiguration){
		raiMap.put(raiConfiguration.getRacCode(), raiConfiguration);
		
	}
	
	public void addSai(SAIConfiguration saiConfiguration){
		saiMap.put(saiConfiguration.getSacCode(), saiConfiguration);
		
	}


	public Set<LocationInformationConfiguration> getLocationInformationConfigurationSet() {
		return locationInformationConfigurationSet;
	}


	public void addLocationInformation(LocationInformationConfiguration locationInformationConfiguration){
		locationInformationConfigurationSet.add(locationInformationConfiguration);
		regionSet.add(locationInformationConfiguration.getRegion());
		citySet.add(locationInformationConfiguration.getCity());
		areaSet.add(locationInformationConfiguration.getArea());
	}
	
	
	
	public CGIConfiguration getCgi(Long ci){
	   return cgiMap.get(ci);
		
	}
	
	public RAIConfiguration getRai(Long racCode ){
		
		return raiMap.get(racCode);
	}
	
	public SAIConfiguration getSai(Long sacCode){

		return saiMap.get(sacCode);
	}
	
   public Set<Long> getCgiKeySet(){
	   return cgiMap.keySet();
	   
   }

   public Set<Long> getRaiKeySet(){
	   return raiMap.keySet();
	   
   }
   public Set<Long> getSaiKeySet(){
	   return saiMap.keySet();
	   
   }

  
	public Set<String> getRegionSet() {
		return regionSet;
	}

	public Set<String> getCitySet() {
		return citySet;
	}


	public Set<String> getLocationSet() {
		return areaSet;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- LAC Information-- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("LAC Info ID", lacInfoId)
				.append("LAC Code", lacCode)
				.appendChildObject("RAI", raiMap.values())
				.appendChildObject("SAI", saiMap.values())
				.appendChildObject("CGI", cgiMap.values())
		.appendChild("Location Ids", locationInformationConfigurationSet.stream().map(LocationInformationConfiguration::getLocationId).collect(Collectors.toList()))
		.appendChild("Region", regionSet);
	}
}
