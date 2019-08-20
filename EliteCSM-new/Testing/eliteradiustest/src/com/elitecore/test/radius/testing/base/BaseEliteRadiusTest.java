package com.elitecore.test.radius.testing.base;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.elitecore.test.radius.testcase.EliteRadiusTestCase;
import com.elitecore.test.radius.testcase.data.AttributeData;
import com.elitecore.test.radius.testcase.data.TestCaseRequestData;
import com.elitecore.test.radius.testcase.data.TestCaseResponseData;
import com.elitecore.test.radius.util.DOMHelper;

public abstract class BaseEliteRadiusTest extends TestCase {
	
	//private ArrayList<EliteRadiusTestCase> testCaseList;
	
	private DatagramSocket datagramSocket;
	
	public BaseEliteRadiusTest(String name) {
		super(name);
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			
		}
	} 

	public void logInfo(String strMessage){
		System.out.println(strMessage);
	}

	protected final ArrayList<EliteRadiusTestCase> parseRadiusTestCase(File testCaseXMLFile) {
		
		ArrayList<EliteRadiusTestCase> testCaseList = new ArrayList<EliteRadiusTestCase>();
		try{
			Document document = DOMHelper.getDocument(testCaseXMLFile);
		
			NodeList testCaseNodeList = document.getElementsByTagName("test-case");
			for ( int s = 0; s < testCaseNodeList.getLength(); s++ ) {
				Node testCaseNode = testCaseNodeList.item(s);
				if(testCaseNode instanceof Element) {
					testCaseList.add(readAAATestCase((Element) testCaseNode));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return testCaseList;
	}

	protected final byte[] doTest(byte[] requestBytes) throws IOException {
		
        DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, InetAddress.getByName("127.0.0.1"), getServicePort());
        datagramSocket.send(requestPacket);        
        DatagramPacket responsePacket = new DatagramPacket(new byte[4096], 4096);
        datagramSocket.setSoTimeout(10000);
        datagramSocket.receive(responsePacket);
        return responsePacket.getData();
        
	}
	
	
	private EliteRadiusTestCase readAAATestCase(Element  testCaseNode) {
		
		Node radiusRequestPacketNode = DOMHelper.getChildElementByTagName((Element) testCaseNode,"request-packet");
		Node radiusResponsePacketNode = DOMHelper.getChildElementByTagName((Element) testCaseNode,"expected-response-packet");

		String id = DOMHelper.getChildElementValueByTagName((Element) testCaseNode, "id");
		String description = DOMHelper.getChildElementValueByTagName((Element) testCaseNode, "description");
		
		EliteRadiusTestCase eliteAAATestCase = new EliteRadiusTestCase();
		eliteAAATestCase.setTestCaseId(id);
		eliteAAATestCase.setDescription(description);
		
		eliteAAATestCase.setTestCaseRequestData(readTestCaseRequestData((Element)radiusRequestPacketNode));
		eliteAAATestCase.setTestCaseResponseData(readTestCaseResponseData((Element)radiusResponsePacketNode));
		
		//byte[] radius_Request_Packet =  readRadiusRequestPackate((Element) radiusRequestPacketNode);
		//byte[] radius_Response_Packet = readRadiusResponsePacketElement((Element) radiusResponsePacketNode);
		
	   	return 	eliteAAATestCase;
	}

	private TestCaseRequestData readTestCaseRequestData(Element radiusPackateNode) {
		
		TestCaseRequestData requestData = new TestCaseRequestData();
		
		requestData.setCode(parseInt(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"code")));
		requestData.setIdentifier( parseInt(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"identifier")));
		requestData.setRequestAuthenticator(HexToBytes(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"authenticator")));
		Node attributesNode = DOMHelper.getChildElementByTagName((Element) radiusPackateNode,"attributes");

		requestData.setAttributes(readRadiusAttibutes(attributesNode));
		
		return requestData;
	}
	
	private TestCaseResponseData readTestCaseResponseData(Element radiusPackateNode) {
		
		TestCaseResponseData responseData = new TestCaseResponseData();
		
		responseData.setCode(parseInt(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"code")));
		responseData.setIdentifier(parseInt(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"identifier")));
		
		Node attributesNode = DOMHelper.getChildElementByTagName((Element) radiusPackateNode,"attributes");

		responseData.setAttributes(readRadiusAttibutes(attributesNode));
		
		return responseData;
	}
	
	
	private List<AttributeData> readRadiusAttibutes(Node atrributes) {
		
		List<AttributeData> resultAttributeList = new ArrayList<AttributeData>();
		
		NodeList attributeList = (NodeList) atrributes.getChildNodes();
		for(int s =0; s<attributeList.getLength(); s++){
			Node attribute = attributeList.item(s);
			if(attribute instanceof Element){
				 resultAttributeList.add(readRadiusAttibute((Element) attribute));
			}
		}
		return resultAttributeList;
	}

	private AttributeData readRadiusAttibute(Element atrribute) {
		
		byte[] attributeByteArray ;
		int counter = 2;
		int attributeLength = 0;
		
		String attributeId = DOMHelper.getChildElementValueByTagName((Element)atrribute,"id");
		String attributeType = DOMHelper.getChildElementValueByTagName((Element)atrribute,"type");
		String attributeValue = DOMHelper.getChildElementValueByTagName((Element)atrribute,"value");
		
		if(attributeType.equals("ipaddr")){
			attributeLength = 2 + 4;
			attributeByteArray = new byte[attributeLength];
			attributeByteArray[0] = (byte)parseInt(attributeId);
			attributeByteArray[1] = (byte)attributeLength;
			
			StringTokenizer stringTokenizer = new StringTokenizer(attributeValue,".");
			while(stringTokenizer.hasMoreTokens()){
				int intValue = parseInt(stringTokenizer.nextToken());
				attributeByteArray[counter] = (byte)intValue;
				counter++;
			}
			
		}else if(attributeType.equals("integer")){
			attributeLength = 2 + 4;
			int intValue = parseInt(attributeValue);
			attributeByteArray = new byte[attributeLength];
			
			attributeByteArray[0] = (byte)parseInt(attributeId);
			attributeByteArray[1] = (byte)attributeLength;
			
			attributeByteArray[counter] = (byte)(intValue >>> 24);
			counter++;
			attributeByteArray[counter] = (byte)(intValue >>> 16);
			counter++;
			attributeByteArray[counter] = (byte)(intValue >>> 8);
			counter++;
			attributeByteArray[counter] = (byte)intValue;
			counter++;
		}else if(attributeType.equals("byte")){
			byte temp[] = HexToBytes(attributeValue.toString().trim());
			int headerlength = 2;
			attributeLength = headerlength + temp.length;
			
			attributeByteArray = new byte[attributeLength];
			attributeByteArray[0] = (byte)parseInt(attributeId);
			attributeByteArray[1] = (byte)attributeLength;
			
			System.arraycopy(temp, 0, attributeByteArray, counter, temp.length);
		}else{			
			byte temp[] = attributeValue.toString().trim().getBytes();
			int headerlength = 2;
			attributeLength = headerlength + temp.length;
			
			attributeByteArray = new byte[attributeLength];
			attributeByteArray[0] = (byte)parseInt(attributeId);
			attributeByteArray[1] = (byte)attributeLength;
			
			System.arraycopy(temp, 0, attributeByteArray, counter, temp.length);
			
		}
		AttributeData attributeData = new AttributeData(attributeByteArray[0],attributeByteArray);
		return attributeData;
	}
	
	//private - protected methods
	
	protected final int parseInt(String value) {
		int parsedValue = 0;
		try {
			parsedValue = Integer.parseInt(value);
		}catch(NumberFormatException n) {
		}
		return parsedValue;
	}

	protected final String bytesToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        buf.append("0x");
        for (int i = 0; i < data.length; i++) {
        	buf.append(byteToHex(data[i]));
        }
        return (buf.toString());
    }

	protected final String byteToHex(byte data) {
		StringBuilder buf = new StringBuilder();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}
	
	protected final char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }
	
	protected final byte[] HexToBytes(String data){
		if(data == null)
			return null;
		if(data.charAt(1) == 'x')
			data = data.substring(2);		
		int len = data.length();
		if(len % 2 != 0)
			len ++ ;		
		byte[] returnBytes = new byte[len/2];
		for(int i=0 ; i<len-1; ){
			returnBytes[i/2] = (byte) (HexToByte(data.substring(i, i+2)) & 0xFF);			
			i +=2;			
		}
		return returnBytes;
	}
	protected final int HexToByte(String data){
		int byteVal = toByte(data.charAt(0)) & 0xFF;
		byteVal = byteVal << 4;
		byteVal = byteVal | toByte(data.charAt(1));
		return byteVal;
	}
	protected final int toByte(char ch){
		if((ch >= '0') && (ch <= '9')){
			return ch - 48;
		}else if((ch >= 'A') && (ch <= 'F')){
			return ch - 65 + 10;
		}else if((ch >= 'a') && (ch <= 'f')){
			return ch - 97 + 10;
		}else {
			return 0;
		}
	}
	public abstract int getServicePort();
}





