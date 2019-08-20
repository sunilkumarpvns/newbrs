package com.elitecore.aclgenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AclSqlGenerator {

	private String modelPrefixChars = "BM";
	private String modulePrefixChars = "BMO";
	private String subModulePrefixChars = "SBM";
	private String actionPrefixChars="ACN";
	
	private int length = 8;

	private int currentModelId=1;
	private int currentModuleId=1;
	private int currentSubModuleId=1;
	private int currentActionId=1;
	
	private static final int MODEL=1;
	private static final int MODULE=2;
	private static final int SUBMODULE=3;
	private static final int ACTION=4;
	
	private StringBuffer modelBuffer = new StringBuffer("-----MODEL----\n");
	private StringBuffer moduleBuffer = new StringBuffer("-----MODULE----\n");
	private StringBuffer subModuelBuffer = new StringBuffer("-----SUBMODULE----\n");
	private StringBuffer actionBuffer = new StringBuffer("-----ACTION----\n");
	private StringBuffer modelModuleRelBuffer = new StringBuffer("-----MODEL MODULE RELATION----\n");
	private StringBuffer moduleSubmoduleRelBuffer = new StringBuffer("-----MODULE SUBMODULE RELATION----\n");
	private StringBuffer submoduleActionRelBuffer = new StringBuffer("-----SUBMODULE ACTION RELATION----\n");
	private StringBuffer adminActionRelBuffer = new StringBuffer("-----ADMIN ACTION RELATION----\n");
	private StringBuffer supportActionRelBuffer = new StringBuffer("-----SUPPORT ACTION RELATION----\n");
	private StringBuffer operatorActionRelBuffer = new StringBuffer("-----OPERATOR ACTION RELATION----\n");
	
	Map<Node, String> idMap = new HashMap<Node, String>();
	
	public String generateNextId(int type){
		int currentId=0;
		String prefixChars="";
		if(type==MODEL){
			currentId= ++currentModelId;
			prefixChars=modelPrefixChars;
		}else if(type==MODULE){
			currentId= ++currentModuleId;
			prefixChars=modulePrefixChars;
		}else if(type==SUBMODULE){
			currentId= ++currentSubModuleId;
			prefixChars=subModulePrefixChars;
		}else if(type==ACTION){
			currentId= ++currentActionId;
			prefixChars=actionPrefixChars;
		}

		String fillChars = "";

		int idLength = Integer.toString(currentId).length();
		int fillLength = this.length - (idLength + prefixChars.length());
		for (int i = 0; i < fillLength; ++i)
			fillChars = fillChars + "0";

		String nextId = prefixChars + fillChars + currentId;
		return nextId; 

	}

	public String getParentId(Node parentNode) {
		String id = (String)this.idMap.get(parentNode);
		if (id == null) {
			id = "NULL";
		}
		else
			id = "'" + id + "'";

		return id; }

	public File generateDbc(File file, int lastModelId, int lastModuleId,int lastSubmoduleId, int lastActionId) {
	    this.currentModelId=lastModelId;
	    this.currentModuleId=lastModuleId;
	    this.currentSubModuleId=lastSubmoduleId;
	    this.currentActionId=lastActionId;
	    
		File dbcFile = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(file);

			String dbcName = "acl.sql";

			dbcFile = new File(dbcName);
			
			dbcFile.createNewFile();
			
			FileWriter writer = new FileWriter(dbcFile);
			
			NodeList list = document.getChildNodes();
			for (int i = 0; i < list.getLength(); ++i) {
				Node node = list.item(i);
				if (node.getNodeType() == 1)
				{
					Node rootNode = node;

					if(rootNode.getNodeName().equalsIgnoreCase("model")||
							rootNode.getNodeName().equalsIgnoreCase("module")||
							rootNode.getNodeName().equalsIgnoreCase("submodule")||
							rootNode.getNodeName().equalsIgnoreCase("action")
					){
					String insertQurey = generateInsertQueryForNode(rootNode,null);
					}


					processChildNodes(rootNode);
					break;
				}
			}
			
			writer.write(modelBuffer.toString());
			writer.write(moduleBuffer.toString());
			writer.write(subModuelBuffer.toString());
			writer.write(modelModuleRelBuffer.toString());
			writer.write(moduleSubmoduleRelBuffer.toString());
			writer.write(actionBuffer.toString());
			writer.write(submoduleActionRelBuffer.toString());
			writer.write(adminActionRelBuffer.toString());
			writer.write(supportActionRelBuffer.toString());
			writer.write(operatorActionRelBuffer.toString());
			writer.write("\nCommit;");
			writer.close();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dbcFile; }

//	private String generateConfigurationInsert(String fileName, String configID, String configurationKey, String serialNo) {
//		String configurationName = getDisplayName(fileName);
//
//		String values = "'" + configID + "', " + serialNo + " ,'" + configurationName + "' , '" + configurationName + "' , '" + fileName + "' ,\n\t'" + configurationKey + "' , '1.0'";
//
//		String insertQuery = "\nINSERT INTO TBLMNETCONFIGURATION\n\t(NETCONFIGID, SERIALNO, NAME, DISPLAYNAME, FILENAME,\n\tALIAS, CONFIGVERSION)\nValues\n\t(" + 
//		values + ");\n\nCommit;\n";
//
//		return insertQuery;
//	}

	public String generateInsertQueryForNode(Node node, Node parentNode)
	{
		String id ="";
		String parentId = getParentId(parentNode);
		String nameValue = node.getTextContent();
		int type=0;
		
		String insertQuery ="";
		//int serialNo = 1;

		
		
		if(node.getNodeName().equalsIgnoreCase("MODEL")){
			
			id = generateNextId(MODEL);
			nameValue = getNameValue(node);
			type=MODEL;
			//Insert into TBLMBISMODEL (BUSINESSMODELID,ALIAS,NAME,SYSTEMGENERATED,STATUS,DESCRIPTION,FREEZEPROFILE) values ('BM000001','RADIUS','Radius','N','E','Radius','E');
			
			insertQuery = "INSERT INTO TBLMBISMODEL \n\t(BUSINESSMODELID,ALIAS,NAME,SYSTEMGENERATED,STATUS,DESCRIPTION,FREEZEPROFILE) \nVALUES\n\t"+
						  "('"+id+"','"+nameValue+"','"+getDisplayName(nameValue)+"','N','E','"+getDisplayName(nameValue)+"','E');\n\n";
			modelBuffer.append(insertQuery);
		}else if(node.getNodeName().equalsIgnoreCase("MODULE")){
			
			id = generateNextId(MODULE);
			nameValue = getNameValue(node);
			type=MODULE;
			String insertQuery1 = "INSERT INTO TBLMBISMODULE \n\t(BUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,BISMODULETYPEID,DESCRIPTION,FREEZEPROFILE)\n VALUES\n\t"+
			  "('"+id+"','"+nameValue+"','"+getDisplayName(nameValue)+"','N','E','BTY00001','"+getDisplayName(nameValue)+"','E');\n\n";
			String insertQuery2 = "INSERT INTO TBLTBISMODELMODULEREL (BUSINESSMODELID,BUSINESSMODULEID,STATUS) VALUES ("+parentId+",'"+id+"','E');\n\n";
			insertQuery = insertQuery1+"\n\n"+insertQuery2;
			
			moduleBuffer.append(insertQuery1);
			modelModuleRelBuffer.append(insertQuery2);
			
		}else if(node.getNodeName().equalsIgnoreCase("SUBMODULE")){
			
			id = generateNextId(SUBMODULE);
			nameValue = getNameValue(node);
			type=SUBMODULE;

			String insertQuery1 = "INSERT INTO TBLMSUBBISMODULE \n\t(SUBBUSINESSMODULEID,ALIAS,NAME,SYSTEMGENERATED,STATUS,SUBBISMODULETYPEID,DESCRIPTION,FREEZEPROFILE) \n VALUES\n\t"+
			  "('"+id+"','"+nameValue+"','"+getDisplayName(nameValue)+"','N','E','STY00001','"+getDisplayName(nameValue)+"','E');\n\n";
			String insertQuery2 = "Insert into TBLTBISMODULESUBBISMODULEREL (BUSINESSMODULEID,SUBBUSINESSMODULEID,STATUS) values ("+parentId+",'"+id+"','E');\n\n";
			insertQuery = insertQuery1+"\n\n"+insertQuery2;
			
			subModuelBuffer.append(insertQuery1);
			moduleSubmoduleRelBuffer.append(insertQuery2);
			
		}else if(node.getNodeName().equalsIgnoreCase("ACTION")){
			id = generateNextId(ACTION);
			type=ACTION;//System.out.println("ACTION:"+id);
			String insertQuery1 = "Insert into TBLMACTION (ACTIONID,NAME,ALIAS,DESCRIPTION,ACTIONTYPEID,PARENTACTIONID,ACTIONLEVEL,SYSTEMGENERATED,SCREENID,STATUS,FREEZEPROFILE) \n"+
								  "values ('"+id+"','"+getDisplayName(nameValue)+"','"+nameValue+"','"+getDisplayName(nameValue)+"','ACT00','ACN00001',1,'N','SCR001','E','E');\n\n";
			String insertQuery2 = "Insert into TBLTSUBBISMODULEACTIONREL (ACTIONID,SUBBUSINESSMODULEID,STATUS) values ('"+id+"',"+parentId+",'E');\n\n";
			String insertQuery3 = "Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('"+id+"',1);\n\n";
			
			
			
			insertQuery = insertQuery1+"\n\n"+insertQuery2+"\n\n"+insertQuery3;
			
			if(nameValue.toLowerCase().startsWith("view") || nameValue.toLowerCase().startsWith("search") || nameValue.toLowerCase().startsWith("list")){
				String insertQuery4 ="Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('"+id+"',2);\n\n";	
				insertQuery = insertQuery+"\n\n"+insertQuery4;
				String insertQuery5 ="Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('"+id+"',3);\n\n";	
				supportActionRelBuffer.append(insertQuery4);
				operatorActionRelBuffer.append(insertQuery5);
			}
			if(nameValue.toLowerCase().startsWith("change") || nameValue.toLowerCase().startsWith("manage") || nameValue.toLowerCase().startsWith("update")){
				String insertQuery5 ="Insert into TBLMGROUPACTIONREL (ACTIONID,GROUPID) values ('"+id+"',3);\n\n";	
				operatorActionRelBuffer.append(insertQuery5);
			}
			
			actionBuffer.append(insertQuery1);
			submoduleActionRelBuffer.append(insertQuery2);
			adminActionRelBuffer.append(insertQuery3);
			
		}
		this.idMap.put(node, id);		
		

		return insertQuery;
		
	}

	private String getNameValue(Node node){
		String nameValue=null;
		NamedNodeMap namedNodeMap = node.getAttributes();
		for (int j = 0; j < namedNodeMap.getLength(); ++j) {
			Node attributeNode = namedNodeMap.item(j);
			//System.out.println("\t\t Node Attribute  : " + attributeNode.getNodeName());
			if(attributeNode.getNodeName().equals("name")){
				nameValue  = attributeNode.getNodeValue();
			}
		}	
		return nameValue;
	}
	
	public void processChildNodes(Node parentNode) throws IOException {


		NodeList nodeList = parentNode.getChildNodes();


		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == 1)
			{
				generateInsertQueryForNode(node, node.getParentNode());


				processChildNodes(node);
			}
		}


	}

	private String getDisplayName(Node node)
	{
		String nodeName = node.getNodeName();
		return getDisplayName(nodeName);
	}

//	private String getDisplayName(String name) {
//		StringTokenizer tokenizer = new StringTokenizer(name, "-");
//		String strDisplayName = "";
//		while (tokenizer.hasMoreTokens()) {
//			String temp = tokenizer.nextToken();
//			if (temp.length() > 1) {
//				String camleCase = temp.substring(0, 1);
//				camleCase = camleCase.toUpperCase();
//				camleCase = camleCase + temp.substring(1) + " ";
//				strDisplayName = strDisplayName + camleCase;
//			}
//		}
//		int indexOfDot = strDisplayName.indexOf(46);
//		if (indexOfDot > 0)
//			strDisplayName = strDisplayName.substring(0, indexOfDot);
//
//		return strDisplayName.trim();
//	}
	private String getDisplayName(String name) {
		StringTokenizer tokenizer = new StringTokenizer(name, "_");
		String strDisplayName = "";
		while (tokenizer.hasMoreTokens()) {
			String temp = tokenizer.nextToken();
			if (temp.length() > 1) {
				String camleCase = temp.substring(0, 1);
				camleCase = camleCase.toUpperCase();
				camleCase = camleCase + temp.substring(1).toLowerCase() + " ";
				strDisplayName = strDisplayName + camleCase;
			}
		}
		int indexOfDot = strDisplayName.indexOf(46);
		if (indexOfDot > 0)
			strDisplayName = strDisplayName.substring(0, indexOfDot);

		return strDisplayName.trim();
	}
	private String getFileNameWithOutExt(File file){
		String name = file.getName();
		int indexOfDot = name.indexOf(46);
		if (indexOfDot > 0)
			name = name.substring(0, indexOfDot);

		return name;
	}

	private boolean isNodeLeaf(Node node){
		boolean flag = true;
		NodeList nodeList = node.getChildNodes();
		System.out.println("checking node is leaf or not. :" + node.getNodeName());
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node nodeItem = nodeList.item(i);
			if ((nodeItem.getNodeType() == 1) || (nodeItem.getNodeType() == 2)) {
				System.out.println("child nodes[" + nodeItem.getNodeType() + "]: " + nodeList.item(i).getNodeName());
				return false;
			}
		}
		return flag; 
	}

//	private String getDBCNodeType(Node node) {
//		String nodeType = "L";
//
//		if (!(isNodeLeaf(node))) {
//			nodeType = "P";
//		}
//
//		if (node.getNodeType() == 2) {
//			nodeType = "A";
//		}
//
//		return nodeType;
//	}
	public static void main(String args[]) throws Exception{
		AclSqlGenerator generator = new AclSqlGenerator();
		File file = new File("acl.xml");
		System.out.println("File : "+file.getCanonicalPath());
		int lastModelId = 1;
		int lastModuleId = 1;
		int lastSubmoduleId =1;
		int lastActionId=1;
		generator.generateDbc(file, lastModelId, lastModuleId, lastSubmoduleId, lastActionId);
	}

}
