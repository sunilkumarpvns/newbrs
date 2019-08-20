package com.elitecore.elitesm.util.ippool;

import java.util.Arrays;

import org.apache.commons.lang.math.NumberUtils;

import com.elitecore.elitesm.util.validation.CustomValidator;

public class IPPoolUtil {
	
	public static long ipToLong(String ipAddress) {
	    long result = 0;
	    String[] atoms = ipAddress.split("\\.");
	    	
	    for (int i = 3; i >= 0; i--) {
	        result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
	    }

	    return result & 0xFFFFFFFF;
	}
	
	public static String longToIp(long ip) {
	    StringBuilder sb = new StringBuilder(15);

	    for (int i = 0; i < 4; i++) {
	        sb.insert(0, Long.toString(ip & 0xff));

	        if (i < 3) {
	            sb.insert(0, '.');
	        }

	        ip >>= 8;
	    }

	    return sb.toString();
	  }
	
	public static  String getNextIPV4Address(String ip) {
	    String[] nums = ip.split("\\.");
	    int i = (Integer.parseInt(nums[0]) << 24 | Integer.parseInt(nums[2]) << 8
	          |  Integer.parseInt(nums[1]) << 16 | Integer.parseInt(nums[3])) + 1;
	    // skip over .255 addresses.
	   // if ((byte) i == -1) i++;

	    return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF, i >> 16 & 0xFF,
	                                        i >>   8 & 0xFF, i >>  0 & 0xFF);
	}
	
	
	public static void main(String[] args) {
		long[][] ipAddressValue;
		String[] ipAddressRangeArray = {"10.106.1.100-50","10.106.1.90-10.106.1.95","10.106.1.90-30","10.106.1.40-20","10.106.1.160-20","10.106.1.120-10.106.1.130","10.106.1.90-10.106.1.130","10.106.1.140-10.106.1.160"};
		ipAddressValue = new long[ipAddressRangeArray.length][2];
		for(int i=0; i<ipAddressRangeArray.length ;i++){
			String[] ipAddresses =  ipAddressRangeArray[i].split("-");
			if(ipAddresses.length > 1 && CustomValidator.isValidIP4Address(ipAddresses[0])){
				if(NumberUtils.isNumber(ipAddresses[1])){
					long ipLongValue = IPPoolUtil.ipToLong(ipAddresses[0]);
					ipAddressValue[i][0] = ipLongValue;
					ipAddressValue[i][1] = ipLongValue+Long.parseLong(ipAddresses[1]);
				}else if(CustomValidator.isValidIP4Address(ipAddresses[1])){
					ipAddressValue[i][0] = IPPoolUtil.ipToLong(ipAddresses[0]);;
					ipAddressValue[i][1] = IPPoolUtil.ipToLong(ipAddresses[1]);
				}else if(CustomValidator.isValidIP4Address(ipAddressRangeArray[i])){
					ipAddressValue[i][0] = IPPoolUtil.ipToLong(ipAddressRangeArray[i]);;
					ipAddressValue[i][1] = IPPoolUtil.ipToLong(ipAddressRangeArray[i]);
				}
			}
		}
		System.out.println("Array Info:"+Arrays.deepToString(ipAddressValue));
		for(int i =0 ; i<ipAddressValue.length; i++){
			System.out.println("Checking for Range "+ipAddressRangeArray[i]);
			for(int j=i+1; j<ipAddressValue.length; j++){
				if((ipAddressValue[j][0] >= ipAddressValue[i][0] && ipAddressValue[j][0] <= ipAddressValue[i][1]) ||
				   (ipAddressValue[j][1] >= ipAddressValue[i][0] && ipAddressValue[j][1] <= ipAddressValue[i][1])) {
					System.out.println("Range "+ipAddressRangeArray[j]+" overlaps  Range "+ipAddressRangeArray[i]);
					break;
				}
			}
		}
		
	}
	
}
