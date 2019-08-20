package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.util.constants.DiameterRoutingTableConstant;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable.DiameterPeerNameToIdAdapter;

import net.sf.json.JSONObject;

@XmlRootElement(name = "peer-data")
@XmlType(propOrder = { "peerUUID", "loadFector" })
public class DiameterPeerGroupRelData extends BaseData implements Serializable,Differentiable{

	public DiameterPeerGroupRelData() {
		loadFector = DiameterRoutingTableConstant.LOAD_FECTOR;
	}
	
	private static final long serialVersionUID = 1L;
	
	private String peerUUID;
	private String peerGroupId;
	private Long loadFector;
	private DiameterPeerData diameterPeerData;
	private String peerName;
	private Long orderNumber;
	
	@XmlTransient
	public DiameterPeerData getDiameterPeerData() {
		return diameterPeerData;
	}

	public void setDiameterPeerData(DiameterPeerData diameterPeerData) {
		this.diameterPeerData = diameterPeerData;
	}

	@XmlElement(name = "peer")
	@XmlJavaTypeAdapter(DiameterPeerNameToIdAdapter.class)
	public String getPeerUUID() {
		return peerUUID;
	}
	
	public void setPeerUUID(String peerUUID) {
		this.peerUUID = peerUUID;
	}
	
	@XmlTransient
	public String getPeerGroupId() {
		return peerGroupId;
	}
	
	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}
	
	@XmlElement(name = "load-factor")
	public Long getLoadFector() {
		return loadFector;
	}
	
	public void setLoadFector(Long loadFector) {
		this.loadFector = loadFector;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Peer", EliteSMReferencialDAO.fetchDiameterPeerData(peerUUID));
		object.put("Load Factor", loadFector);
		return object;
	}

	@XmlTransient
	public String getPeerName() {
		return peerName;
	}

	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}

	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
}