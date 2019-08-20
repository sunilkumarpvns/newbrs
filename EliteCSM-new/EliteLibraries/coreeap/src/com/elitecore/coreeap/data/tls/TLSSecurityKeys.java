package com.elitecore.coreeap.data.tls;

public class TLSSecurityKeys implements Cloneable{
	private byte[] clientRandom;
	private byte[] serverRandom;
	private byte[] masterSecret;
	private byte[] pMS;
	private byte[] macRead;
	private byte[] macWrite;
	private byte[] encryptionRead;
	private byte[] encryptionWrite;
	private byte[] clientIV;
	private byte[] serverIV;
	private long readSeq = 0 ;
	private long writeSeq = 0;
	
	public TLSSecurityKeys(){

	}
	
	public byte[] getClientRandom() {
		return clientRandom;
	}
	public void setClientRandom(byte[] clientRandom) {
		if(clientRandom != null){
			this.clientRandom = new byte[clientRandom.length];
			System.arraycopy(clientRandom, 0, this.clientRandom, 0, clientRandom.length);
		}		
	}
	public byte[] getServerRandom() {
		return serverRandom;
	}
	public void setServerRandom(byte[] serverRandom) {
		if(serverRandom != null){
			this.serverRandom = new byte[serverRandom.length];
			System.arraycopy(serverRandom, 0, this.serverRandom, 0, serverRandom.length);
		}	
	}
	public byte[] getMasterSecret() {
		return masterSecret;
	}
	public void setMasterSecret(byte[] masterSecret) {
		if(masterSecret != null){
			this.masterSecret = new byte[masterSecret.length];
			System.arraycopy(masterSecret, 0, this.masterSecret, 0, masterSecret.length);
		}
	}
	public byte[] getMacRead() {
		return macRead;
	}
	public void setMacRead(byte[] macRead) {
		if(macRead != null){
			this.macRead = new byte[macRead.length];
			System.arraycopy(macRead, 0, this.macRead, 0, macRead.length);
		}
	}
	public byte[] getMacWrite() {
		return macWrite;
	}
	public void setMacWrite(byte[] macWrite) {
		if(macWrite != null){
			this.macWrite = new byte[macWrite.length];
			System.arraycopy(macWrite, 0, this.macWrite, 0, macWrite.length);
		}
	}
	public byte[] getEncryptionRead() {
		return encryptionRead;
	}
	public void setEncryptionRead(byte[] encryptionRead) {
		if(encryptionRead != null){
			this.encryptionRead = new byte[encryptionRead.length];
			System.arraycopy(encryptionRead, 0, this.encryptionRead, 0, encryptionRead.length);
		}
	}
	public byte[] getEncryptionWrite() {
		return encryptionWrite;
	}
	public void setEncryptionWrite(byte[] encryptionWrite) {
		if(encryptionWrite != null){
			this.encryptionWrite = new byte[encryptionWrite.length];
			System.arraycopy(encryptionWrite, 0, this.encryptionWrite, 0, encryptionWrite.length);
		}
	}
	public byte[] getClientIV() {
		return clientIV;
	}
	public void setClientIV(byte[] clientIV) {
		if(clientIV != null){
			this.clientIV = new byte[clientIV.length];
			System.arraycopy(clientIV, 0, this.clientIV, 0, clientIV.length);
		}
	}
	public byte[] getServerIV() {
		return serverIV;
	}
	public void setServerIV(byte[] serverIV) {
		if(serverIV != null){
			this.serverIV = new byte[serverIV.length];
			System.arraycopy(serverIV, 0, this.serverIV, 0, serverIV.length);
		}
	}
	public byte[] getReadSeq() {
		byte[] seqNum = new byte[8];		
		seqNum[0] = (byte) ((this.readSeq >>> 56) & 0xFF) ;
		seqNum[1] = (byte) ((this.readSeq >>> 48) & 0xFF) ;
		seqNum[2] = (byte) ((this.readSeq >>> 40) & 0xFF) ;
		seqNum[3] = (byte) ((this.readSeq >>> 32) & 0xFF) ;
		seqNum[4] = (byte) ((this.readSeq >>> 24) & 0xFF) ;
		seqNum[5] = (byte) ((this.readSeq >>> 16) & 0xFF) ;
		seqNum[6] = (byte) ((this.readSeq >>> 8) & 0xFF) ;
		seqNum[7] = (byte) ((this.readSeq) & 0xFF) ;
		return seqNum;
	}
	public void incrementReadSeq() {
		this.readSeq++;
	}
	public void decrementReadSeq(){
		this.readSeq--;
	}
	public void resetReadSeq(){
		this.readSeq = 0;
	}
	public byte[] getWriteSeq() {
		byte[] seqNum = new byte[8];		
		seqNum[0] = (byte) ((this.writeSeq >>> 56) & 0xFF) ;
		seqNum[1] = (byte) ((this.writeSeq >>> 48) & 0xFF) ;
		seqNum[2] = (byte) ((this.writeSeq >>> 40) & 0xFF) ;
		seqNum[3] = (byte) ((this.writeSeq >>> 32) & 0xFF) ;
		seqNum[4] = (byte) ((this.writeSeq >>> 24) & 0xFF) ;
		seqNum[5] = (byte) ((this.writeSeq >>> 16) & 0xFF) ;
		seqNum[6] = (byte) ((this.writeSeq >>> 8) & 0xFF) ;
		seqNum[7] = (byte) ((this.writeSeq) & 0xFF) ;
		return seqNum;
	}
	public void incrementWriteSeq() {
		this.writeSeq++;
	}
	public void decrementWriteSeq(){
		this.writeSeq--;
	}
	public void resetWriteSeq(){
		this.writeSeq = 0;
	}
	public Object clone() throws CloneNotSupportedException {
		TLSSecurityKeys securityKeys = (TLSSecurityKeys)super.clone();
		if(this.clientRandom != null){
			securityKeys.clientRandom = new byte[this.clientRandom.length];
			System.arraycopy(this.clientRandom, 0, securityKeys.clientRandom, 0, securityKeys.clientRandom.length);
		}
		if(this.serverRandom != null){
			securityKeys.serverRandom = new byte[this.serverRandom.length];
			System.arraycopy(this.serverRandom, 0, securityKeys.serverRandom, 0, securityKeys.serverRandom.length);
		}
		if(this.masterSecret != null){
			securityKeys.masterSecret = new byte[this.masterSecret.length];
			System.arraycopy(this.masterSecret, 0, securityKeys.masterSecret, 0, securityKeys.masterSecret.length);
		}
		if(this.macRead != null){
			securityKeys.macRead = new byte[this.macRead.length];
			System.arraycopy(this.macRead, 0, securityKeys.macRead, 0, securityKeys.macRead.length);
		}
		if(this.macWrite != null){
			securityKeys.macWrite = new byte[this.macWrite.length];
			System.arraycopy(this.macWrite, 0, securityKeys.macWrite, 0, securityKeys.macWrite.length);
		}
		if(this.encryptionRead != null){
			securityKeys.encryptionRead = new byte[this.encryptionRead.length];
			System.arraycopy(this.encryptionRead, 0, securityKeys.encryptionRead, 0, securityKeys.encryptionRead.length);
		}
		if(this.encryptionWrite != null){
			securityKeys.encryptionWrite = new byte[this.encryptionWrite.length];
			System.arraycopy(this.encryptionWrite, 0, securityKeys.encryptionWrite, 0, securityKeys.encryptionWrite.length);
		}
		if(this.clientIV != null){
			securityKeys.clientIV = new byte[this.clientIV.length];
			System.arraycopy(this.clientIV, 0, securityKeys.clientIV, 0, securityKeys.clientIV.length);
		}
		if(this.serverIV != null){
			securityKeys.serverIV = new byte[this.serverIV.length];
			System.arraycopy(this.serverIV, 0, securityKeys.serverIV, 0, securityKeys.serverIV.length);
		}
		securityKeys.readSeq = this.readSeq;
		securityKeys.writeSeq = this.writeSeq;

		return securityKeys;
	}

	public byte[] getPMS() {
		return pMS;
	}

	public void setPMS(byte[] pms) {
		pMS = pms;
	}
	
	public void reset(){
		pMS=null;
		serverRandom=null;
		macRead=null;
		macWrite=null;
		encryptionRead=null;
		encryptionWrite=null;
		clientIV=null;
		serverIV=null;
		readSeq = 0 ;
		writeSeq = 0;
	}

}
