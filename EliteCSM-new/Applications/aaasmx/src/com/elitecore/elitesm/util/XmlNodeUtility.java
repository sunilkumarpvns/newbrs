package com.elitecore.elitesm.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * 
 * @author chirag i. prajapati
 * <p> <b> XmlNodeUtility </b> is a simple,utility to find Element Value from xml.
 */

public class XmlNodeUtility {
    /**
     * <p><b>This is used to generate Document object from XML string. </b> </p>
     * @param Xmlcontnet String of XML 
     * @return  return Document object 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
	public static Document parseXML(String Xmlcontnet)throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(Xmlcontnet)));
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	/**
	 *<p><b> This method get the text(value) of the given attribute name from the XML string.</b> </p>
	 *<pre>for Example
	 *{@code
	 *	
	 *<xml>
	 *	<root>
	 *		<child-tag>demo1</child-tag>
	 *		<child-tag>demo2</child-tag>
	 *	</root>	
	 *</xml>
	 *}</pre>
	 *<p><b>if you enter tag name as "child-tag" than output is :</b>
	 *<b>["demo1","demo2"]</b></p>
	 *
	 * <p><b>Note :  tag-name is case-sensitive if given tag-name does not exist in XML it will return empty set.</b> </p>
	 * @param xmlData XML data
	 * @param tagName Tag name or Element name to be search
	 * @return All the text values(unique) the given attribute name.
	 */
	public static Set<String> getElementValueByTagName(String xmlData,final String tagName) {
		List<String> lStrings = new ArrayList<String>();
		lStrings.add(tagName);
		return getElementValueByTagName(xmlData, lStrings);
	}
	
	/**
	 *<p><b> This method returns the text(values) for the given attribute names from XML string.</b> </p>
	 *<pre>for Example
	 *{@code
	 *	
	 *<xml>
	 *	<root>
	 *		<child-tag-1>demo1</child-tag-1>
	 *		<child-tag-2>demo2</child-tag-2>
	 *	</root>	
	 *</xml>
	 *}</pre>
	 *<p><b>if the tag name list contains values "child-tag-1,child-tag-2" than output is :</b>
	 *<b>["demo1","demo2"]</b></p>
	 *
	 * <p><b>Note :  tag-name is case-sensitive if given tag-name does not exist in XML it will return empty set.</b> </p>
	 * @param xmlData XML data string
	 * @param tagNames List<String> that contains Tag names or Element names to be search
	 * @return All the text values(unique) the given attribute name.
	 */
	public static Set<String> getElementValueByTagName(String xmlData,List<String> tagNames) {
		Document doc = null;
		Set<String> values=new HashSet<String>();
		if( xmlData != null && xmlData.isEmpty() == false ){
			try {
				doc = parseXML(xmlData);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (doc != null) {
				for(String tagName : tagNames){
					NodeList nList = doc.getElementsByTagName(tagName);

					int nListSize = nList.getLength();
					for (int i = 0; i < nListSize; i++) {
						Node nNode = nList.item(i);
						Element eElement = (Element) nNode;
						String policyPluginName = eElement.getTextContent();
						values.add(policyPluginName);
					}
				}
			}
		}
		return values;
	}
}
