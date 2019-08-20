package com.elitecore.nvsmx.policydesigner.model.pkg.syquota;

import java.io.Serializable;

import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;

public class SyQuotaProfileWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;
	
	public static class SyQuotaProfileWrapperBuilder{
		public SyQuotaProfileWrapper syQuotaProfileWrapper;
		
		public SyQuotaProfileWrapperBuilder(){
			this.syQuotaProfileWrapper = new SyQuotaProfileWrapper();
		}
		
		public SyQuotaProfileWrapper build(){
			return syQuotaProfileWrapper;
		}
		
		public SyQuotaProfileWrapperBuilder withQuotaProfileDetails(SyQuotaProfileData syQuotaProfileData) {
			syQuotaProfileWrapper.id = syQuotaProfileData.getId();
			syQuotaProfileWrapper.name = syQuotaProfileData.getName();
			syQuotaProfileWrapper.description = syQuotaProfileData.getDescription();
			return this;
		}
		
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
