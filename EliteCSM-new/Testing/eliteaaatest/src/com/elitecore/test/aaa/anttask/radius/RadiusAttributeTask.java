package com.elitecore.test.aaa.anttask.radius;

import java.util.StringTokenizer;

import com.elitecore.test.aaa.anttask.core.BaseAttributeTask;

public class RadiusAttributeTask extends BaseAttributeTask {
	
	@Override
	public byte[] getBytes(){
		byte[] attributeByteArray ;
		int counter = 2;
		int attributeLength = 0;		
		if(getValue() != null && getValue().trim().length() > 0){
			if(getType().equals("ipaddr")){
				attributeLength = 2 + 4;
				attributeByteArray = new byte[attributeLength];
				attributeByteArray[0] = (byte)getId();
				attributeByteArray[1] = (byte)attributeLength;
				
				StringTokenizer stringTokenizer = new StringTokenizer(getValue(),".");
				while(stringTokenizer.hasMoreTokens()){
					int intValue = parseInt(stringTokenizer.nextToken());
					attributeByteArray[counter] = (byte)intValue;
					counter++;
				}
				
			}else if(getType().equals("integer")){
				attributeLength = 2 + 4;
				int intValue = parseInt(getValue());
				attributeByteArray = new byte[attributeLength];
				
				attributeByteArray[0] = (byte)getId();
				attributeByteArray[1] = (byte)attributeLength;
				
				attributeByteArray[counter] = (byte)(intValue >>> 24);
				counter++;
				attributeByteArray[counter] = (byte)(intValue >>> 16);
				counter++;
				attributeByteArray[counter] = (byte)(intValue >>> 8);
				counter++;
				attributeByteArray[counter] = (byte)intValue;
				counter++;
			}else if(getType().equals("byte")){
				byte temp[] = HexToBytes(getValue().trim());
				int headerlength = 2;
				attributeLength = headerlength + temp.length;
				
				attributeByteArray = new byte[attributeLength];
				attributeByteArray[0] = (byte)getId();
				attributeByteArray[1] = (byte)attributeLength;
				
				System.arraycopy(temp, 0, attributeByteArray, counter, temp.length);
			}else{					
				byte temp[] = getValue().trim().getBytes();
				int headerlength = 2;
				attributeLength = headerlength + temp.length;
				
				attributeByteArray = new byte[attributeLength];
				attributeByteArray[0] = (byte)getId();
				attributeByteArray[1] = (byte)attributeLength;
				
				System.arraycopy(temp, 0, attributeByteArray, counter, temp.length);
				
			}
		}else{			
			attributeLength = 2;
			
			attributeByteArray = new byte[attributeLength];
			attributeByteArray[0] = (byte)getId();
			attributeByteArray[1] = (byte)attributeLength;
			
		}
		return attributeByteArray;
	}

}
