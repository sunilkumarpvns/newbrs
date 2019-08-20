package com.elitecore.coreeap.data;

import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;

public class AAAEapRespData {
	private EAPPacket eapRespPacket = null;
	private EAPPacket eapReqPacket = null;	
	private int fragmentedSize = 0;	
	private byte[] aaaEapKeyData = null;
	private String oui = null;
	             
	public AAAEapRespData() {}
	
	public AAAEapRespData(byte[] eapPacketBytes) throws EAPException {
		EAPPacket eapPacket;
		try {
			eapPacket = new EAPPacket(eapPacketBytes);
		} catch (InvalidEAPPacketException e) {		
			throw new EAPException("Problem while creating EAP Packet. Reason: " + e.getMessage(), e);
		}
		setEapRespPacket(eapPacket);		
	}
	
	public String getOUI() {
		return oui;
	}
	public void setOUI(String oui) {
		this.oui = oui;
	}
	

	public AAAEapRespData(EAPPacket eapPacket){
		setEapRespPacket(eapPacket);		
	}
	
	public void setEapRespPacket(byte[] eapPacketBytes) throws EAPException{
		EAPPacket eapPacket;
		try {
			eapPacket = new EAPPacket(eapPacketBytes);
		} catch (InvalidEAPPacketException e) {		
			throw new EAPException("Problem while creating EAP Packet. Reason: " + e.getMessage(), e);
		}
		setEapRespPacket(eapPacket);		
	}

	private void setEapRespPacket(EAPPacket eapPacket){
		this.eapRespPacket = eapPacket;
	}
	public EAPPacket getEapRespPacket(){
		return this.eapRespPacket;
	}
	public byte[] getEapRespPacketBytes(){
		if(this.eapRespPacket != null)
			return this.eapRespPacket.getBytes();
		return null;
	}
	public EAPPacket getEapReqPacket() {
		return eapReqPacket;
	}
	public byte[] getEapReqPacketBytes(){
		if(this.eapReqPacket != null)
			return this.eapReqPacket.getBytes();
		return null;
	}	
	public void setEapReqPacket(EAPPacket eapReqPacket) {
		this.eapReqPacket = eapReqPacket;
	}
	public void setEapReqPacket(byte[] eapPacketBytes) throws EAPException {
		EAPPacket eapPacket;
		try {
			eapPacket = new EAPPacket(eapPacketBytes);
		} catch (InvalidEAPPacketException e) {		
			throw new EAPException("Problem while creating EAP Packet. Reason: " + e.getMessage(), e);
		}		
		setEapReqPacket(eapPacket);
	}

	public byte[] getAAAEapKeyData(){
		return this.aaaEapKeyData;
	}
	public void setAAAEapKeyData(byte[] keyData){
		if(keyData != null){
			this.aaaEapKeyData = new byte[keyData.length];
			System.arraycopy(keyData, 0, this.aaaEapKeyData, 0, keyData.length);
		}
	}
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(" AAA EAP Data = ");
		strBuilder.append("\nEAP Response Packet = \n");
		strBuilder.append((this.eapRespPacket != null ? this.eapRespPacket : " null"));
		strBuilder.append("\nEAP Request Packet = \n");
		strBuilder.append((this.eapReqPacket != null ? this.eapReqPacket : " null"));		
		return strBuilder.toString();
	}
	
	public int getFragmentedSize() {
		return fragmentedSize;
	}
	
	public void setFragmentedSize(int fragmentedSize) {
		this.fragmentedSize = fragmentedSize;
	}
}
