package com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice;

import java.io.Serializable;

import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;

/**
 * Customized the IMSPkgServiceData
 * @author Dhyani.Raval
 *
 */
public class IMSPkgServiceWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String mediaTypeName;
	private String appFunctionId;
	private String action;

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
	public String getMediaTypeName() {
		return mediaTypeName;
	}
	public void setMediaTypeName(String mediaTypeName) {
		this.mediaTypeName = mediaTypeName;
	}
	public String getAppFunctionId() {
		return appFunctionId;
	}
	public void setAppFunctionId(String appFunctionId) {
		this.appFunctionId = appFunctionId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public static class IMSPkgServiceWrapperBuilder{
		private IMSPkgServiceWrapper imsPkgServiceWrapper;

		public IMSPkgServiceWrapperBuilder(){
			imsPkgServiceWrapper = new IMSPkgServiceWrapper();
		}

		public IMSPkgServiceWrapper build(){
			return imsPkgServiceWrapper;
		}

		public IMSPkgServiceWrapperBuilder withIMSPkgServiceDatas(IMSPkgServiceData imsPkgServiceData){
			imsPkgServiceWrapper.id = imsPkgServiceData.getId();
			imsPkgServiceWrapper.name = imsPkgServiceData.getName();
			imsPkgServiceWrapper.mediaTypeName = imsPkgServiceData.getMediaTypeData().getName();
			imsPkgServiceWrapper.action = imsPkgServiceData.getAction().getName();
			imsPkgServiceWrapper.appFunctionId = imsPkgServiceData.getAfApplicationId();
			return this;
		}
	}
}
