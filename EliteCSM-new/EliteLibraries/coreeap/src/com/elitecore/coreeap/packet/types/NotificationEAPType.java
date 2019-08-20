/*
 *	EAP Project
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Nov 7, 2008
 *	Created By Devang Adeshara
 */
package com.elitecore.coreeap.packet.types;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

public class NotificationEAPType extends EAPType{

	private byte[] notification;
	
	public NotificationEAPType(){
		super(EapTypeConstants.NOTIFICATION.typeId);
		//this.setData(super.getData());
	}

	public NotificationEAPType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.NOTIFICATION.typeId){
			this.setType(EapTypeConstants.NOTIFICATION.typeId);
			//this.setData(super.getData());
		}else{
			throw new IllegalArgumentException("EAP Type  : not NOTIFICATION");
		}
	}
	
	public byte[] getNotification() {
		return this.notification;
	}

	public void setNotification(byte[] notification) throws IllegalArgumentException{
		if(notification != null){
			this.notification = new byte[notification.length];
			if(notification.length >= 1 && notification.length <= 1015){
				System.arraycopy(notification, 0, this.notification, 0, notification.length);
			}else{
				throw new IllegalArgumentException("Invalid number of bytes "+notification.length);
			}
		}else{
			throw new IllegalArgumentException("Data bytes : null");
		}
	}

	@Override
	public byte[] getData() {
		return this.getNotification();
	}

	@Override
	public void setData(byte[] data) {
		try{
			this.setNotification(data);
		}catch(IllegalArgumentException ille){
			throw ille;
		}
	}

	public byte[] toBytes(){
		byte[] returnBytes = new byte[this.notification.length + 1];
		returnBytes[0] = (byte)this.getType();
		System.arraycopy(this.notification, 0, returnBytes, 1, this.notification.length);
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("  Type=");
		strBuilder.append(this.getType());
		strBuilder.append("  Notification=");
		try{
			strBuilder.append(new String(this.getNotification(),CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			strBuilder.append(new String(this.getNotification()));
		}
		return strBuilder.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		NotificationEAPType newObject = (NotificationEAPType)super.clone();
		if(this.notification != null){
			newObject.notification = new byte[this.notification.length];
			System.arraycopy(this.notification, 0, newObject.notification, 0, this.notification.length);
		}
		return newObject;
	}
	
	
}
