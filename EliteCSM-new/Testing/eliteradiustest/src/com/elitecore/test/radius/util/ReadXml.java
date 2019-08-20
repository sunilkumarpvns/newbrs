package com.elitecore.test.radius.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class ReadXml implements ErrorHandler 
{
	private static final String ENTITY_NAME = "ReadXml";
	
	private static List<Map<String, String>> readTestCases(String fileName)
	{
		Document docDictionaryXMLParse = null;

	
		try{
			docDictionaryXMLParse = DOMHelper.getDocument(fileName);
			NodeList testCaseNodeList = docDictionaryXMLParse.getElementsByTagName("test-case");

			int index = 0;
			for ( int s = 0; s < testCaseNodeList.getLength(); s++ ) {
				Node testCaseNode = testCaseNodeList.item(s);
				
				StringBuffer tempTestPackate = new StringBuffer();
				StringBuffer tempTestResult = new StringBuffer();

				if(testCaseNode instanceof Element){
					System.out.println("====================== Test Case + " + s + "====================");
					System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element) testCaseNode, "id")));
					System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element) testCaseNode, "description")==null?null:DOMHelper.getChildElementValueByTagName((Element) testCaseNode, "description").trim()));
					Node requestpacketNode = DOMHelper.getChildElementByTagName((Element) testCaseNode,"request-packet");
					readRadiusPakateNode((Element) requestpacketNode);
					System.out.println("==========================================");
				}
				index++;
			}
        }catch(FileNotFoundException fe){

		}catch(ParserConfigurationException pce){

		}catch(SAXException saxe){			

		}catch(IOException ioe){			

		}
		return null;
	}

	public static void readRadiusPakateNode(Element radiusPackateNode)
	{
		System.out.println("====================request-packet====================");

		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"code")));
		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"identifier")));
		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"length")));
		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)radiusPackateNode,"authenticator")));
		Node attributesNode = DOMHelper.getChildElementByTagName((Element) radiusPackateNode,"attributes");
		readRadiusAttibutes(attributesNode);

	}

	public static void readRadiusAttibutes(Node atrributes)
	{
		NodeList attributeList = (NodeList) atrributes.getChildNodes();
		for(int s =0;s<attributeList.getLength();s++){
			Node attribute = attributeList.item(s);
			if(attribute instanceof Element){
			System.out.println("===========attributes======");
				readRadiusAttibute((Element) attribute);
			}
		}

	}

	public static void readRadiusAttibute(Element atrribute){
		System.out.println("===========attribute======");

		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)atrribute,"id")));
		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)atrribute,"length")));
		System.out.println(DOMHelper.trim(DOMHelper.getChildElementValueByTagName((Element)atrribute,"value")));
	}

	public void error(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void fatalError(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void warning(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub

	}







}
