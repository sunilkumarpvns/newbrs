package com.elitecore.netvertexsm.blmanager.customizedmenu;

import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.util.EliteUtility;

public class NonContainerMenu implements MenuItem {
	private Long id;
	private Long order;
	private String title;
	private String url;
	private String urlParameters;
	private OpenMethod openMethod;
	@Override
	public String getTitle() {
		return title;
	}
	
	@Override
	public MenuType getType() {
		return MenuType.NONCONTAINER;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlParameters() {
		return urlParameters;
	}
	public void setUrlParameters(String urlParameters) {
		this.urlParameters = urlParameters;
	}
	public OpenMethod getOpenMethod() {
		return openMethod;
	}
	public void setOpenMethod(OpenMethod openMethod) {
		this.openMethod = openMethod;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "NonContainerMenu [title=" + title + ", url=" + url
				+ ", urlParameters=" + urlParameters + ", openMethod="
				+ openMethod + "]";
	}
	
	@Override
	public String getMenuScript(){
		if(Strings.isNullOrBlank(url)==false && url.startsWith("/")){	
			return "stm_aix(\""+id+"\",\"\",[0,\"  " +title+ "\",\"\",\"\",-1,-1,0,\""+ EliteUtility.getContextPath()+url +"?"+urlParameters +"\",\"_"+openMethod.methodType+"\",\"\",\"\",\"\",\"\",0,0,0,\"\",\"\",7,7,0,0,1,\"#005197\",0,\"rgb(3, 44, 77)\",0,\"\",\"\",3,3,1,1,\"#007CC3 #005197 #005197 #007CC3\",\"#007CC3 #005197 #005197 #007CC3\",\"#FFFFFF\",\"#FFFFFF\",\"9pt Arial\",\"9pt Arial\"],190);\n";
		} 
		 return "stm_aix(\""+id+"\",\"\",[0,\"  " +title+ "\",\"\",\"\",-1,-1,0,\""+ url +"?"+urlParameters +"\",\"_"+openMethod.methodType+"\",\"\",\"\",\"\",\"\",0,0,0,\"\",\"\",7,7,0,0,1,\"#005197\",0,\"rgb(3, 44, 77)\",0,\"\",\"\",3,3,1,1,\"#007CC3 #005197 #005197 #007CC3\",\"#007CC3 #005197 #005197 #007CC3\",\"#FFFFFF\",\"#FFFFFF\",\"9pt Arial\",\"9pt Arial\"],190);\n";
	}

	
}
