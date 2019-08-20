package com.elitecore.test.aaa.anttask.diameter;

import com.elitecore.test.aaa.anttask.core.BaseAttributeTask;

public class DiameterAvpTask extends BaseAttributeTask{
	private int flag = 0;
	public byte[] getBytes(){
		
		byte[] attributeByteArray ;
		int attributeLength = 0;		
		int headerlength = 8;
		if(getValue() != null && getValue().trim().length() > 0){
			
			if(getType().equalsIgnoreCase("integer")){
				attributeLength = 12;
				attributeByteArray = new byte[attributeLength];
				setHeader(attributeByteArray, attributeLength);
				/* AVP Value */
				int val = parseInt(getValue());
				attributeByteArray[8] =  (byte) (val >>> 24);
				attributeByteArray[9] =  (byte) (val >>> 16);
				attributeByteArray[10] = (byte) (val >>> 8);
				attributeByteArray[11] = (byte) val;
			}else if(getType().equalsIgnoreCase("byte")){
				byte temp[] = HexToBytes(getValue().trim());
				attributeLength = headerlength + temp.length;
				
				int	padding = 0;
				if((attributeLength% 4) != 0)
					padding= 4 - (attributeLength% 4) ;
				
				attributeByteArray = new byte[attributeLength + padding];
				setHeader(attributeByteArray, attributeLength);
				/* AVP Value */				
				System.arraycopy(temp, 0, attributeByteArray, headerlength, temp.length);
				for(int i=0;i<padding;i++)
					attributeByteArray[attributeLength+i] = 0;
			}else {//(getType().equals("string")){
				byte temp[] = getValue().trim().getBytes();
				attributeLength = headerlength + temp.length;
				
				int	padding = 0;
				if((attributeLength% 4) != 0)
					padding= 4 - (attributeLength% 4) ;
				
				attributeByteArray = new byte[attributeLength + padding];
				setHeader(attributeByteArray, attributeLength);
				/* AVP Value */				
				System.arraycopy(temp, 0, attributeByteArray, headerlength, temp.length);
				for(int i=0;i<padding;i++)
					attributeByteArray[attributeLength+i] = 0;
			}
		}else{			
			attributeLength = 8;
			attributeByteArray = new byte[attributeLength];
			setHeader(attributeByteArray, attributeLength);			
		}
		return attributeByteArray;
	}
	
	private void setHeader(byte[] attributeByteArray,int length){
		/* AvpId */
		attributeByteArray[0] = (byte) (getId() >>> 24);
		attributeByteArray[1] = (byte) (getId() >>> 16);
		attributeByteArray[2] = (byte) (getId() >>> 8);
		attributeByteArray[3] = (byte)  getId();
		
		/* V M P r r r r r */
		attributeByteArray[4] = (byte)getFlag();

		/* AVP Length */
		attributeByteArray[5] = (byte) (length >>> 16);
		attributeByteArray[6] = (byte) (length >>> 8);
		attributeByteArray[7] = (byte) length;
		
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}
	
}



/*if(getType().equals("ipaddr")){
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

}else if(getType().equals("byte")){
	byte temp[] = HexToBytes(getValue().trim());
	int headerlength = 8;
	attributeLength = headerlength + temp.length+(temp.length%4);
	
	attributeByteArray = new byte[attributeLength];
	attributeByteArray[0] = (byte)getId();
	attributeByteArray[1] = (byte)attributeLength;
	
	System.arraycopy(temp, 0, attributeByteArray, counter, temp.length);
*/