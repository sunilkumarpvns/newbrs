package com.elitecore.netvertexsm.web.servermgr.service.form;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class AddNetServerServiceInstanceForm extends BaseWebForm{
	private static final long serialVersionUID = 5255963621584039541L;
        private Long netFormServerId;
	private String netFormServerTypeId;

	private String netFormServiceTypeId;
	private String action;
	private String status;
	private int itemIndex;
	private List<INetServiceInstanceData> listInstanceServices = new ArrayList<INetServiceInstanceData>();
    private List<INetServiceTypeData> lstServiceType = null;
    private List<INetServiceTypeData> netServiceInstanceList;
	private String selectedServices[];
	
	public List<INetServiceTypeData> getNetServiceInstanceList() {
		return netServiceInstanceList;
	}
	public void setNetServiceInstanceList(
			List<INetServiceTypeData> netServiceInstanceList) {
		this.netServiceInstanceList = netServiceInstanceList;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<INetServiceInstanceData> getListInstanceServices() {
		return listInstanceServices;
	}
	public void setListInstanceServices(List<INetServiceInstanceData> listInstanceServices) {
		this.listInstanceServices = listInstanceServices;
	}
	public String getAlias(int index){
		return ((NetServiceInstanceData)listInstanceServices.get(index)).getDisplayName();
	}
	public void setAlias(int index,String alias){
		while(listInstanceServices.size() - 1 < index)			
			listInstanceServices.add(new NetServiceInstanceData());
		((NetServiceInstanceData)listInstanceServices.get(index)).setDisplayName(alias);
	}
	public String getName(int index){
		return ((NetServiceInstanceData)listInstanceServices.get(index)).getName();
	}
	public void setName(int index,String name){
		while(listInstanceServices.size() -1 < index)
			listInstanceServices.add(new NetServiceInstanceData());
		((NetServiceInstanceData)listInstanceServices.get(index)).setName(name);
	}
	public String getDescription(int index){
		return ((NetServiceInstanceData)listInstanceServices.get(index)).getDescription();
	}
	public void setDescription(int index,String description){
		while(listInstanceServices.size() -1 < index)
			listInstanceServices.add(new NetServiceInstanceData());
		
		((NetServiceInstanceData)listInstanceServices.get(index)).setDescription(description);
	}
	public Long getNetServerId(int index){
		return ((NetServiceInstanceData)listInstanceServices.get(index)).getNetServerId();
	}
	public void setNetServerId(int index,Long netServerId){
		while(listInstanceServices.size() -1 < index)
			listInstanceServices.add(new NetServiceInstanceData());

		((NetServiceInstanceData)listInstanceServices.get(index)).setNetServerId(netServerId);
	}
	public String getNetServiceTypeId(int index){
		return ((NetServiceInstanceData)listInstanceServices.get(index)).getNetServiceTypeId();
	}
	public void setNetServiceTypeId(int index,String netServiceTypeId){
		while(listInstanceServices.size() -1 < index)
			listInstanceServices.add(new NetServiceInstanceData());

		((NetServiceInstanceData)listInstanceServices.get(index)).setNetServiceTypeId(netServiceTypeId);
	}
	public String getCommonStatusId(int index){
		 return ((NetServiceInstanceData)listInstanceServices.get(index)).getCommonStatusId();
	}
	public void setCommonStatusId(int index,String commonStatusId){
		while(listInstanceServices.size() -1 < index)
			listInstanceServices.add(new NetServiceInstanceData());
		
		((NetServiceInstanceData)listInstanceServices.get(index)).setCommonStatusId(commonStatusId);
	}
	
	public String[] getSelectedServices() {
		return selectedServices;
	}
	public void setSelectedServices(String[] selectedServices) {
		this.selectedServices = selectedServices;
	}
	
	public Long getNetFormServerId() {
		return netFormServerId;
	}
	public List<INetServiceTypeData> getLstServiceType() {
		return lstServiceType;
	}
	public void setLstServiceType(List<INetServiceTypeData> lstServiceType) {
		this.lstServiceType = lstServiceType;
	}
	public void setNetFormServerId(Long netFormServerId) {
		this.netFormServerId = netFormServerId;
	}
	public String getNetFormServerTypeId() {
		return netFormServerTypeId;
	}
	public void setNetFormServerTypeId(String netFormServerTypeId) {
		this.netFormServerTypeId = netFormServerTypeId;
	}
	public String getNetFormServiceTypeId() {
		return netFormServiceTypeId;
	}
	public void setNetFormServiceTypeId(String netFormServiceTypeId) {
		this.netFormServiceTypeId = netFormServiceTypeId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
}
