/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */
package com.elitecore.elitesm.datamanager.diameter.diameterpeer.data;

import java.sql.Timestamp;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.elitecore.aaa.util.constants.DiameterPeersConstant;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.diameterpeers.PeerProfileNameFromIdAdapter;
import com.elitecore.elitesm.ws.rest.adapter.diameterpeers.RequestTimeoutAdapter;
import com.elitecore.elitesm.ws.rest.adapter.diameterpeers.RetransmissionCountAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "diameter-peers")
@ValidObject
@XmlType(propOrder = { "name", "hostIdentity", "realmName", "remoteAddress", "localAddress", "diameterURIFormat", "requestTimeout", "retransmissionCount", "secondaryPeerName", "peerProfileId" })
public class DiameterPeerData extends BaseData implements Differentiable, Validator {
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Peer name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 60, message = "Length of Name must not more than 60.")
	private String name;

	private String peerUUID;

	@Expose
	@SerializedName("Peer ID")
	private Long peerId;
	
	@Expose
	@SerializedName("Host Identity")
	@Length(max = 256, message = "Length of Host Identity must not more than 256.")
	private String hostIdentity;

	@Expose
	@SerializedName("Realm")
	@NotEmpty(message = "Realm Name must be specified. ")
	@Length(max = 256, message = "Length of Realm Name must not more than 256.")
	private String realmName;

	@Expose
	@SerializedName("Remote Address")
	@Length(max = 100, message = "Length of Remote Address must not more than 100.")
	private String remoteAddress;

	@Expose
	@SerializedName("Local Address")
	@Length(max = 100, message = "Length of Local Address must not more than 100.")
	private String localAddress;

	@Expose
	@SerializedName("Diameter URI Format")
	@Length(max = 256, message = "Length of Diameter URI Format must not more than 256.")
	private String diameterURIFormat;

	@Expose
	@SerializedName("Request Timeout")
	@Range(min = 1000, max = 10000, message = "Request Time-out value should be between 1000 to 10000.")
	private Long requestTimeout;

	@Expose
	@SerializedName("Retransmission Count")
	@Range(min = 0, max = 3, message = "Invalid value in Retransmission Count...!, Possible values are : 0 or 0-3.")
	private Long retransmissionCount;

	@Expose
	@SerializedName("Profile Name")
	@NotNull(message="Peer Profile Name must be specified.")
	private String peerProfileId;

	@Expose
	@SerializedName("Secondary Peer")
	private String secondaryPeerName;

	private String createdByStaffId;
	private Timestamp createDate;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private DiameterPeerProfileData diameterPeerProfileData;
	private String auditUId;

	public DiameterPeerData() {
		retransmissionCount = DiameterPeersConstant.RETRANSMISSION_COUNT;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public Long getPeerId() {
		return peerId;
	}

	public void setPeerId(Long peerId) {
		this.peerId = peerId;
	}

	@XmlElement(name = "host-identity")
	public String getHostIdentity() {
		return hostIdentity;
	}

	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}

	@XmlElement(name = "realm")
	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	@XmlElement(name = "remote-address")
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	@XmlElement(name = "profile-name")
	@XmlJavaTypeAdapter(PeerProfileNameFromIdAdapter.class)
	public String getPeerProfileId() {
		return peerProfileId;
	}

	public void setPeerProfileId(String peerProfileId) {
		this.peerProfileId = peerProfileId;
	}

	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}

	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}

	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}

	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@XmlTransient
	public DiameterPeerProfileData getDiameterPeerProfileData() {
		return diameterPeerProfileData;
	}

	public void setDiameterPeerProfileData(DiameterPeerProfileData diameterPeerProfileData) {
		this.diameterPeerProfileData = diameterPeerProfileData;
	}

	@XmlElement(name = "local-address")
	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	@XmlElement(name = "diameter-uri-format")
	public String getDiameterURIFormat() {
		return diameterURIFormat;
	}

	public void setDiameterURIFormat(String diameterURIFormat) {
		this.diameterURIFormat = diameterURIFormat;
	}

	@XmlElement(name = "request-timeout")
	@XmlJavaTypeAdapter(RequestTimeoutAdapter.class)
	public Long getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(Long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	@XmlElement(name = "retransmission-count")
	@XmlJavaTypeAdapter(RetransmissionCountAdapter.class)
	public Long getRetransmissionCount() {
		return retransmissionCount;
	}

	public void setRetransmissionCount(Long retransmissionCount) {
		this.retransmissionCount = retransmissionCount;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElement(name = "secondary-peer")
	public String getSecondaryPeerName() {
		return secondaryPeerName;
	}

	public void setSecondaryPeerName(String secondaryPeerName) {
		this.secondaryPeerName = secondaryPeerName;
	}

	@XmlTransient
	public String getPeerUUID() {
		return peerUUID;
	}

	public void setPeerUUID(String peerUUID) {
		this.peerUUID = peerUUID;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Host Identity", hostIdentity);
		object.put("Realm", realmName);
		object.put("Remote Address", remoteAddress);
		object.put("Local Address", localAddress);
		object.put("Diameter URI Format", diameterURIFormat);
		object.put("Request Timeout", requestTimeout);
		object.put("Retransmission Count", retransmissionCount);
		object.put("Secondary Peer", secondaryPeerName);
		object.put("Profile Name", EliteSMReferencialDAO.fetchDiameterPeerProfileData(peerProfileId));
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		Boolean isValid = true;
		
		try {
			if (Strings.isNullOrBlank(hostIdentity) && Strings.isNullOrBlank(remoteAddress)) {
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Either Host Identity or Remote Address Must be Specified.");
			}
			
			if (Strings.isNullOrBlank(remoteAddress) == false) {
				if (validateIPAddress(remoteAddress) == false) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of Remote Address.");
				} else if (Strings.isNullOrBlank(hostIdentity) == false) {
					if (remoteAddress.indexOf("-") != -1) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Host Identity is not allowed with IP Range.");
					} else if (remoteAddress.indexOf("/") != -1) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Host Identity is not allowed with Subnetmask.");
					}
				}
			}
			
			if ( Strings.isNullOrBlank(localAddress) == false) {
				if (validateIPAddress(localAddress) == false) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of Local Address.");
				}
			}
			
			if (Strings.isNullOrBlank(peerProfileId) == false) {
				if ("-1L".equals(peerProfileId)) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Diameter Peer Profile Name must be specified.");
				} else if ("-2L".equals(peerProfileId)) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Diameter Peer Profile not found.");
				}
			}
			
			if (Strings.isNullOrBlank(secondaryPeerName) == false) {
				try {
					new DiameterPeerBLManager().getDiameterPeerByName(secondaryPeerName);
				} catch (DataManagerException e) {
					if (e.getMessage().contains("Peer not found")) {
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Secondary Diameter Peer not found.");
					}
				}
			}
			
			if (retransmissionCount == null) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Retransmission Count must be Numeric.");
			}
			
			if (requestTimeout == null) {
				isValid = false;
				RestUtitlity.setValidationMessage(context,"Request Timeout must be specify and It must be Numeric.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isValid;
	}

	private boolean validateIPAddress(String localAddress) {
		if (validateIP(localAddress) == false && validateIPAdressForPort(localAddress) == false && validateIPAdressForSubNetMask(localAddress) == false && validateIPAdressForRange(localAddress) == false) {
			return false;
		} else {
			return true;
		}
	}

	private boolean validateIPAdressForPort(String iPAddressForPort) {
		boolean validIP = false;
		boolean validPort = false;
		boolean validIPForPort = false;
		
		int noOfIPPortion = iPAddressForPort.split(":").length - 1;
		
		if (noOfIPPortion < 1) {
			validIPForPort = false;
		} else if (noOfIPPortion == 1) {
			String[] iPAddressAndPort = iPAddressForPort.split(":");
			validIP = validateIP(iPAddressAndPort[0]);
			validPort = validatePort(iPAddressAndPort[1]);
		} else {
			String[] firstPortionOfIP = iPAddressForPort.split("\\[");
			if (firstPortionOfIP.length > 1) {
				String[] secondPortionOfIP = firstPortionOfIP[1].split("\\]:");
				if (secondPortionOfIP.length > 1) {
					String IP = secondPortionOfIP[0];
					String Port = secondPortionOfIP[1];
					validIP = validateIP(IP);
					validPort = validatePort(Port);
				}
			}
		}
		
		if (validIP == true && validPort == true) {
			validIPForPort = true;
		}
		
		return validIPForPort;
	}
	
	private boolean validateIPAdressForSubNetMask(String iPAddressForSubNetMask) {
		boolean validIPForPort = false;
		
		if (iPAddressForSubNetMask.split("/").length == 2) {
			String[] iPAddressAndSubNetMask = iPAddressForSubNetMask.split("/");
			String iPAddress = iPAddressAndSubNetMask[0];
			String subNetMask = iPAddressAndSubNetMask[1];
			if (validateIPv4(iPAddress)) {
				if (validateSubNetMaskForIPv4(subNetMask)) {
					validIPForPort = true;
				}
			} else if (validateIPv6(iPAddress)) {
				if (validateSubNetMaskForIPv6(subNetMask)) {
					validIPForPort = true;
				}
			}
		}
		
		return validIPForPort;
	}
	
	private boolean validateIPAdressForRange(String iPAddressForRange) {
		boolean validIPForPort = false;
		
		if (iPAddressForRange.split("-").length == 2) {
			String[] iPAddresses = iPAddressForRange.split("-");
			boolean validFirstIPRange = validateIP(iPAddresses[0]);
			boolean validSecondIPRange = validateIP(iPAddresses[1]);
			if (validFirstIPRange == true && validSecondIPRange == true) {
				validIPForPort = true;
			}
		}
		
		return validIPForPort;
	}

	private boolean validateIPv4(String iPAddress) {
		String ipv4Regex = "(^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)";
		java.util.regex.Pattern ipPortPattern = java.util.regex.Pattern.compile(ipv4Regex);
		return ipPortPattern.matcher(iPAddress).matches();
	}
	
	private boolean validateIPv6(String iPAddress) {
		String ipv6Regex = "((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))|(^\\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\\s*$)";
		java.util.regex.Pattern ipPortPattern = java.util.regex.Pattern.compile(ipv6Regex);
		return ipPortPattern.matcher(iPAddress).matches();
	}

	private boolean validateIP(String iPAdress) {
		java.util.regex.Pattern ipPortPattern = java.util.regex.Pattern.compile(RestValidationMessages.IPV4_IPV6_REGEX);
		return ipPortPattern.matcher(iPAdress).matches();
	}
	
	private boolean validatePort(String portStr) {
		int port = Integer.parseInt(portStr);
		if (port >= 0 && port <= 65535) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean validateSubNetMaskForIPv4(String subNetMaskStr) {
		int subNetMask = Integer.parseInt(subNetMaskStr);
		if (subNetMask >= 0 && subNetMask <= 32) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean validateSubNetMaskForIPv6(String subNetMaskStr) {
		int subNetMask = Integer.parseInt(subNetMaskStr);
		if (subNetMask >= 0 && subNetMask <= 128) {
			return true;
		} else {
			return false;
		}
	}

}
