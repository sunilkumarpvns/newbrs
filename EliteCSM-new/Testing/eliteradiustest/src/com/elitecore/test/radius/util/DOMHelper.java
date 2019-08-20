/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DOMHelper.java                            
 * ModualName com.elitecore.ssssportal.core.util                                      
 * Created on Feb 1, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.test.radius.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DOMHelper {
    
	public static Document getDocument( String fileName ) throws ParserConfigurationException, SAXException, IOException {
		File f = new File(fileName);
		return getDocument(f);
	}

	public static Document getDocument( File file ) throws ParserConfigurationException, SAXException, IOException {
		Document doc;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		doc = docBuilder.parse(file);
		doc.getDocumentElement().normalize();
		return doc;
	}
    
    
    public static boolean nodeNameEquals( Node node , String desiredName ) {
        return (desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName()));
    }
    
    public static String getChildElementValueByTagName( Element ele , String childEleName ) {
        Element child = getChildElementByTagName(ele, childEleName);
        return (child != null ? getTextValue(child) : null);
    }
    
    public static Element getChildElementByTagName( Element ele , String childEleName ) {
        NodeList nl = ele.getChildNodes();
        for ( int i = 0; i < nl.getLength(); i++ ) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameEquals(node, childEleName)) {
                return (Element) node;
            }
        }
        return null;
    }
    
    public static String getTextValue( Element valueEle ) {
        StringBuffer value = new StringBuffer();
        NodeList nl = valueEle.getChildNodes();
        for ( int i = 0; i < nl.getLength(); i++ ) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                value.append(item.getNodeValue());
            }
        }
        return value.toString();
    }
    
    public static String trim( String valueEle ) {
     return valueEle ==null?null:valueEle.trim();
    }
    
}
