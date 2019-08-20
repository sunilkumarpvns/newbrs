package com.elitecore.exprlib.parser.expression.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.net.AddressResolver;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 * Class check whether the given IP falls  between the Give IP-Range or NetMask
 */

public class FunctionNetMatch extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	private transient AddressResolver addressResolver;
	
	public FunctionNetMatch() {
		this(AddressResolver.defaultAddressResolver());
	}
	
	@VisibleForTesting
	FunctionNetMatch(AddressResolver fakeAddressResolver) {
		this.addressResolver = fakeAddressResolver;
	}

	public int getFunctionType() {	
		return 0;
	}

	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=2)
			throw new IllegalArgumentException("Number of parameter mismatch ,NetMatch function must have TWO arguements 1)IP Address   2)IP-Range OR NETMASK");
		
		try{
			String ipAddress=argumentList.get(0).getStringValue(valueProvider);
			String ipRange=argumentList.get(1).getStringValue(valueProvider);
			InetAddress ipAdd = addressResolver.byName(ipAddress);
			Long longIPAdd = bytesToLong(ipAdd.getAddress());
			
			if(ipRange.contains("-")){
				return validateInIPRange(longIPAdd,ipRange);
			}else if(ipRange.contains("/")){
				return validateInNetworkMask(longIPAdd, ipRange);
			}else if(ipAddress.equals(ipRange))
				return "true";
			else
				return "false";
		}catch(UnknownHostException ex){
			throw new IllegalArgumentException("Invalid host configured in Function Reason :"+ex.getMessage());
		}
	}
	
	/** 
	 * @param longIPAdd
	 * @param ipRange
	 * @return
	 * @throws UnknownHostException
	 * Validate whether the given IP falls between the IP-Range
	 */
	
	private String validateInIPRange(Long longIPAdd, String ipRange)throws UnknownHostException{
		String[] ips = ipRange.split("-");
		if(ips.length != 2)
			return "false";
		
		InetAddress ip1 = addressResolver.byName(ips[0].trim());
		InetAddress ip2 = addressResolver.byName(ips[1].trim());
		
		Long startAddress = bytesToLong(ip1.getAddress());
		Long endAddress = bytesToLong(ip2.getAddress());
		
		if(startAddress == null || endAddress == null || endAddress < startAddress){
			return "false";
		}
		
		if(startAddress<=longIPAdd && longIPAdd<=endAddress)
			return "true";
		else
			return "false";
	}
	
	/**
	 * @param longIPAdd
	 * @param ipRange
	 * @return
	 * @throws UnknownHostException
	 * @throws InvalidTypeCastException
	 * Validate whether the NetMask of IP & the give NetMask is same or not
	 */
	private String validateInNetworkMask(Long longIPAdd, String ipRange)throws UnknownHostException,InvalidTypeCastException{
		String[] ipAndMask = ipRange.split("/");
		if(ipAndMask.length != 2)
			return "false";

		try{
			int subNet = Integer.parseInt(ipAndMask[1].trim());
			if(subNet < 1 || subNet > 32)
				return "false"; 

			InetAddress address = addressResolver.byName(ipAndMask[0].trim());
			Long longAddress = bytesToLong(address.getAddress());

			if(longAddress == null)
				return "false";

			int clientPart = 32-subNet;
			Long lMask = (long) ((1<<clientPart) -1);
			lMask = (~lMask) & 0x00000000FFFFFFFFL;
			Long startAddress = longAddress & lMask;
			lMask = (long) ((1<<clientPart) -1);
			Long endAddress = startAddress | lMask;

			if(startAddress<=longIPAdd && longIPAdd<=endAddress)
				return "true";
			else
				return "false";

		}catch (NumberFormatException e) {
			throw new InvalidTypeCastException("NetMask Bits :"+ipAndMask[1].trim()+" must be numeric ");
		}
	}
	
	private static Long bytesToLong(byte[] bytes){
		if(bytes == null)
			return null;
		
		Long val = 0L;
		for(byte b : bytes){
			val <<= 8;
			val |= b & 0xFF;
		}
		return val;
	}

	public String getName() {
		return "netmatch";
	}
	
}
