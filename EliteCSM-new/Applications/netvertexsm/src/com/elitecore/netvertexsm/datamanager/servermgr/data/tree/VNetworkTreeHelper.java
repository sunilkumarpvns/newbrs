package com.elitecore.netvertexsm.datamanager.servermgr.data.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.corenetvertex.data.GatewayInfo;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData;


/**
 * 
 * @author Nayan Faldu
 * Functionality Description :
 * 					This class use to generate TreeView in Jsp page.
 * Start Date : 14/05/2009
 */

public class VNetworkTreeHelper
{
	public  static final String MODULE = "[VNETWORKTREEHELPER]";
	
	
	public static synchronized TreeMap<String, TreeBean> getTreeViewMap(HttpServletRequest req,String lLocationid)
	{
//		
		TreeBean infoData = null;
		String strNodename="CISCO SCE";
		String strNodeMapid="strNodeMapid";
		String strDevicetypename="strDevicetypename";
		String strIpaddress="10.10.10.1";
		StringBuffer strSetCode=null;	
		StringBuffer txt = null;
//		
		TreeMap<String, TreeBean> mpTreeMap = new TreeMap<String, TreeBean>();
		try{
			
			 List<GatewayInfo> gatewayInfoList =null;
			if(req.getAttribute("gatewayInfoList")!=null)
			{
				gatewayInfoList = (List<GatewayInfo> )req.getAttribute("gatewayInfoList");	
			}
				
			 
			strSetCode = new StringBuffer();
			strSetCode.append("Gateways  ");
			infoData = new TreeBean();	
			infoData.setCode(strSetCode.toString());
			mpTreeMap.put("0-0",infoData);
			
			TreeMap<String,TreeMap<String, List<Object>>> map = new TreeMap<String, TreeMap<String,List<Object>>>();
			GatewayBLManager gatewayBLManager = new GatewayBLManager();
			
			List<GatewayLocationData> locationList = gatewayBLManager.getLocationList();
			List<String> addedLocatiion = new ArrayList<String>();
			for(int j=0;j<locationList.size();j++)
			{
				addedLocatiion = new ArrayList<String>();
				for(int i=0;i<gatewayInfoList.size();i++)
				{
					infoData = new TreeBean();
					txt = new StringBuffer();
					GatewayInfo  gatewayInfo = gatewayInfoList.get(i);
					if(gatewayInfo==null)
					{
						continue;
					}
					if(gatewayInfo.getLocation()!=null && !gatewayInfo.getLocation().equals(""))
					{	
						 
						if(!addedLocatiion.contains(gatewayInfo.getLocation()) && gatewayInfo.getLocation().equals(locationList.get(j).getLocationName())) // && gatewayInfo.getLocation().equals(locationList.get(j).getLocationName()) 
						{
							infoData = new TreeBean();
							txt = new StringBuffer();
							strSetCode = new StringBuffer();
							strSetCode.append(
									gatewayInfo.getLocation()+"</td>" +
								"</tr></table>"); 
							infoData.setCode(strSetCode.toString());
							infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
							infoData.setOpen(false);
							infoData.setShowAsFolder(false);
							addedLocatiion.add(gatewayInfo.getLocation());
							mpTreeMap.put("0-1",infoData);
							mpTreeMap.get("0-0").addChild(infoData);
						
							List<String> addedGatewayType = new ArrayList<String>();	
							for(int l=0;l<gatewayInfoList.size();l++)
							{
									GatewayInfo gl= gatewayInfoList.get(l);
									if(gl.getLocation().equals(gatewayInfo.getLocation())&& !addedGatewayType.contains(gl.getGatewayType()))
									{	
											infoData = new TreeBean();
											txt = new StringBuffer();
											strSetCode = new StringBuffer();
											strSetCode.append(
													gl.getGatewayType()+"</td>" +
												"</tr></table>"); 
											infoData.setCode(strSetCode.toString());
											infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
											infoData.setOpen(false);
											infoData.setShowAsFolder(false);
											addedGatewayType.add(gl.getGatewayType());
											mpTreeMap.put("1-0",infoData);
											mpTreeMap.get("0-1").addChild(infoData);

											for(int k=0;k<gatewayInfoList.size();k++)
											{
												GatewayInfo gk = gatewayInfoList.get(k);
												if(gk.getGatewayType().equals(gl.getGatewayType()) && gk.getLocation().equals(gatewayInfo.getLocation()))
												{	
													String connectionURL  = gk.getConnectionURL();
													if(connectionURL!=null && !connectionURL.equals(""))
													{
														infoData = new TreeBean();
														txt = new StringBuffer();
														txt.append("<table width=\"100%\" style=\"table-layout:fixed;\"><tr>");
														
														
														txt.append("<td width=\"35%\" align=\"center\" ><font class=\"note\">" +
															"<font class=\"capacity\">" + connectionURL+ "</font>" );
												
														txt.append(
															"</td></table>");
										
														
														strSetCode = new StringBuffer();
														if(gk.getGatewayType().equals("Cisco_SCE"))
														{
															strSetCode.append( 
																	"<img src=\""+req.getContextPath()+"/images/tree/sce.gif"+"\" height=\"18\" width=\"23\" title=\"Cisco_SCE\" />&nbsp;&nbsp;"+
																	((gk.getAreaName()!=null && !gk.getAreaName().equals(""))?gk.getAreaName():gk.getLocation()));
															
															strSetCode.append("</td>" +
																	"<td class=\"tabletext\" align=\"center\"  width=35%>" +
																	"<span id="+"\""+strNodeMapid+"\">"+
																	txt.toString()+
																	"</span>");
																if(gk.getStatus()!=null && gk.getStatus().equals("Active"))
																	strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gk.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gk.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
																else
																	strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>"+gk.getActiveSessions()  +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
																
					
														}
														else if(gk.getGatewayType().equals("Diameter"))
														{
															
															strSetCode.append( 
																	"<img src=\""+req.getContextPath()+"/images/tree/asngw.JPG"+"\" height=\"18\" width=\"23\" title=\"Diameter\" />&nbsp;&nbsp;"+
																	((gk.getAreaName()!=null && !gk.getAreaName().equals(""))?gk.getAreaName():gk.getLocation()));
															
															strSetCode.append("</td>" +
																	"<td class=\"tabletext\" align=\"center\"  width=35%>" +
																	"<span id="+"\""+strNodeMapid+"\">"+
																	txt.toString()+
																	"</span>");
																if(gk.getStatus()!=null && gk.getStatus().equals("Active"))
																	strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gk.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gk.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
																else
																	strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gk.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
																	
														}
														else if(gk.getGatewayType().equals("Radius"))
														{
															strSetCode.append( 
																	"<img src=\""+req.getContextPath()+"/images/tree/Bras.gif"+"\" height=\"18\" width=\"23\" title=\"Radius\" />&nbsp;&nbsp;"+
																	((gk.getAreaName()!=null && !gk.getAreaName().equals(""))?gk.getAreaName():gk.getLocation()));
															
															strSetCode.append("</td>" +
																	"<td class=\"tabletext\" align=\"center\"  width=35%>" +
																	"<span id="+"\""+strNodeMapid+"\">"+
																	txt.toString()+
																	"</span>");
																if(gk.getStatus()!=null && !gk.getStatus().equals("InActive"))
																	strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gk.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gk.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
																else
																	strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gk.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
																	
														}
															
														
														strSetCode.append("</td></tr></table>");
														infoData.setCode(strSetCode.toString());
														mpTreeMap.put("1-1",infoData);
														mpTreeMap.get("1-0").addChild(infoData);
														
													}
												}	
											}	
									}
							}
							}
										
										
						}
					
					}
			}
//					String gatewayType = gatewayInfo.getGatewayType();
//					if(addedLocatiion.size()>0)
//					{
//						if(gatewayType!=null && !gatewayType.equals("") && !addedProtocol.contains(gatewayType))
//						{	
//								infoData = new TreeBean();
//								txt = new StringBuffer();
//								strSetCode = new StringBuffer();
//								strSetCode.append(
//										gatewayType+"</td>" +
//									"</tr></table>"); 
//								infoData.setCode(strSetCode.toString());
//								infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
//								infoData.setOpen(false);
//								infoData.setShowAsFolder(false);
//								addedProtocol.add(gatewayType);
//								mpTreeMap.put("1-0",infoData);
//								mpTreeMap.get("0-1").addChild(infoData);
//								
//								
//								
//								String connectionURL  = gatewayInfo.getConnectionURL();
//								if(connectionURL!=null && !connectionURL.equals(""))
//								{
//									infoData = new TreeBean();
//									txt = new StringBuffer();
//									txt.append("<table width=\"100%\" style=\"table-layout:fixed;\"><tr>");
//									
//									
//									txt.append("<td width=\"35%\" align=\"center\" ><font class=\"note\">" +
//										"<font class=\"capacity\">" + connectionURL+ "</font>" );
//							
//									txt.append(
//										"</td></table>");
//					
//									
//									strSetCode = new StringBuffer();
//									if(gatewayType.equals("Cisco_SCE"))
//									{
//										strSetCode.append( 
//												"<img src=\""+req.getContextPath()+"/images/tree/sce.gif"+"\" height=\"18\" width=\"23\" title=\"Cisco_SCE\" />&nbsp;&nbsp;"+
//												((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//										
//										strSetCode.append("</td>" +
//												"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//												"<span id="+"\""+strNodeMapid+"\">"+
//												txt.toString()+
//												"</span>");
//											if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//												strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//											else
//												strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//											
//
//									}
//									else if(gatewayType.equals("Diameter"))
//									{
//										
//										strSetCode.append( 
//												"<img src=\""+req.getContextPath()+"/images/tree/asngw.JPG"+"\" height=\"18\" width=\"23\" title=\"Diameter\" />&nbsp;&nbsp;"+
//												((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//										
//										strSetCode.append("</td>" +
//												"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//												"<span id="+"\""+strNodeMapid+"\">"+
//												txt.toString()+
//												"</span>");
//											if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//												strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//											else
//												strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//												
//									}
//									else if(gatewayType.equals("Radius"))
//									{
//										strSetCode.append( 
//												"<img src=\""+req.getContextPath()+"/images/tree/Bras.gif"+"\" height=\"18\" width=\"23\" title=\"Radius\" />&nbsp;&nbsp;"+
//												((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//										
//										strSetCode.append("</td>" +
//												"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//												"<span id="+"\""+strNodeMapid+"\">"+
//												txt.toString()+
//												"</span>");
//											if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//												strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//											else
//												strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//												
//									}
//										
//									
//									strSetCode.append("</td></tr></table>");
//									infoData.setCode(strSetCode.toString());
//									mpTreeMap.put("1-1",infoData);
//									mpTreeMap.get("1-0").addChild(infoData);
//									
//								}
//						}
//					}
//				}
//			}
//						
//							String gatewayType = gatewayInfo.getGatewayType();
//								if(gatewayType!=null && !gatewayType.equals("") )
//								{	
//										infoData = new TreeBean();
//										txt = new StringBuffer();
//										strSetCode = new StringBuffer();
//										strSetCode.append(
//												gatewayType+"</td>" +
//											"</tr></table>"); 
//										infoData.setCode(strSetCode.toString());
//										infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
//										infoData.setOpen(false);
//										infoData.setShowAsFolder(false);
//										mpTreeMap.put("1-0",infoData);
//										mpTreeMap.get("0-1").addChild(infoData);
//										
//										String connectionURL  = gatewayInfo.getConnectionURL();
//											if(connectionURL!=null && !connectionURL.equals(""))
//											{
//												infoData = new TreeBean();
//												txt = new StringBuffer();
//												txt.append("<table width=\"100%\" style=\"table-layout:fixed;\"><tr>");
//												
//												
//												txt.append("<td width=\"35%\" align=\"center\" ><font class=\"note\">" +
//													"<font class=\"capacity\">" + connectionURL+ "</font>" );
//										
//												txt.append(
//													"</td></table>");
//								
//												
//												strSetCode = new StringBuffer();
//												if(gatewayType.equals("Cisco_SCE"))
//												{
//													strSetCode.append( 
//															"<img src=\""+req.getContextPath()+"/images/tree/sce.gif"+"\" height=\"18\" width=\"23\" title=\"Cisco_SCE\" />&nbsp;&nbsp;"+
//															((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//													
//													strSetCode.append("</td>" +
//															"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//															"<span id="+"\""+strNodeMapid+"\">"+
//															txt.toString()+
//															"</span>");
//														if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//															strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//														else
//															strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//														
//
//												}
//												else if(gatewayType.equals("Diameter"))
//												{
//													
//													strSetCode.append( 
//															"<img src=\""+req.getContextPath()+"/images/tree/asngw.JPG"+"\" height=\"18\" width=\"23\" title=\"Diameter\" />&nbsp;&nbsp;"+
//															((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//													
//													strSetCode.append("</td>" +
//															"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//															"<span id="+"\""+strNodeMapid+"\">"+
//															txt.toString()+
//															"</span>");
//														if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//															strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//														else
//															strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//															
//												}
//												else if(gatewayType.equals("Radius"))
//												{
//													strSetCode.append( 
//															"<img src=\""+req.getContextPath()+"/images/tree/Bras.gif"+"\" height=\"18\" width=\"23\" title=\"Radius\" />&nbsp;&nbsp;"+
//															((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//													
//													strSetCode.append("</td>" +
//															"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//															"<span id="+"\""+strNodeMapid+"\">"+
//															txt.toString()+
//															"</span>");
//														if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//															strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//														else
//															strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//															
//												}
//													
//												
//												strSetCode.append("</td></tr></table>");
//												infoData.setCode(strSetCode.toString());
//												mpTreeMap.put("1-1",infoData);
//												mpTreeMap.get("1-0").addChild(infoData);
//												
//											}
//
//										}
//										
//								}
//							}
//			}
//			for(int i=0;i<gatewayInfoList.size();i++)
//			{
//				GatewayInfo gatewayInfo = gatewayInfoList.get(i);
//				set.add(gatewayInfo.getLocation());
//				List<Object> detail = new ArrayList<Object>();
//				if(gatewayInfo ==null)
//					continue;
//				if(gatewayInfo.getAreaName()!=null)
//					detail.add(gatewayInfo.getAreaName());
//				if(gatewayInfo.getConnectionURL()!=null)
//					detail.add(gatewayInfo.getConnectionURL());
//				if(gatewayInfo.getStatus()!=null)
//					detail.add(gatewayInfo.getStatus());
//				else
//					detail.add("In Active");
//				detail.add(gatewayInfo.getActiveSessions());
//				
//				TreeMap<String, List<Object>> protocol = new TreeMap<String, List<Object>>();
//				if(gatewayInfo.getGatewayType()!=null )
//					protocol.put(gatewayInfo.getGatewayType(), detail);
//				
//				
//				if(gatewayInfo.getLocation()!=null )
//				{
//						map.put(gatewayInfo.getLocation(), protocol);
//				}
				
//			}
			
//			for(int i=0;i<gatewayInfoList.size();i++)
//			{
//				infoData = new TreeBean();
//				txt = new StringBuffer();
//				if(gatewayInfoList.get(i)==null)
//				{
//					continue;
//				}
//				if(gatewayInfoList.get(i)!=null && gatewayInfoList.get(i).getLocation()!=null && !gatewayInfoList.get(i).getLocation().equals(""))
//				{	
//					GatewayInfo gatewayInfo = gatewayInfoList.get(i); 
//					if(gatewayInfo ==null)
//						continue;
//					
//						infoData = new TreeBean();
//						txt = new StringBuffer();
//						strSetCode = new StringBuffer();
//						strSetCode.append(
//								gatewayInfo.getLocation()+"</td>" +
//							"</tr></table>"); 
//						infoData.setCode(strSetCode.toString());
//						infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
//						infoData.setOpen(false);
//						infoData.setShowAsFolder(false);
//						
//						mpTreeMap.put("0-1",infoData);
//						mpTreeMap.get("0-0").addChild(infoData);
//						
//						String gatewayType = gatewayInfo.getGatewayType();
//							if(gatewayType!=null && !gatewayType.equals("") )
//							{	
//									infoData = new TreeBean();
//									txt = new StringBuffer();
//									strSetCode = new StringBuffer();
//									strSetCode.append(
//											gatewayType+"</td>" +
//										"</tr></table>"); 
//									infoData.setCode(strSetCode.toString());
//									infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
//									infoData.setOpen(false);
//									infoData.setShowAsFolder(false);
//									mpTreeMap.put("1-0",infoData);
//									mpTreeMap.get("0-1").addChild(infoData);
//									
//									String connectionURL  = gatewayInfo.getConnectionURL();
//										if(connectionURL!=null && !connectionURL.equals(""))
//										{
//											infoData = new TreeBean();
//											txt = new StringBuffer();
//											txt.append("<table width=\"100%\" style=\"table-layout:fixed;\"><tr>");
//											
//											
//											txt.append("<td width=\"35%\" align=\"center\" ><font class=\"note\">" +
//												"<font class=\"capacity\">" + connectionURL+ "</font>" );
//									
//											txt.append(
//												"</td></table>");
//							
//											
//											strSetCode = new StringBuffer();
//											if(gatewayType.equals("Cisco_SCE"))
//											{
//												strSetCode.append( 
//														"<img src=\""+req.getContextPath()+"/images/tree/sce.gif"+"\" height=\"18\" width=\"23\" title=\"Cisco_SCE\" />&nbsp;&nbsp;"+
//														((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//												
//												strSetCode.append("</td>" +
//														"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//														"<span id="+"\""+strNodeMapid+"\">"+
//														txt.toString()+
//														"</span>");
//													if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//														strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//													else
//														strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//													
//
//											}
//											else if(gatewayType.equals("Diameter"))
//											{
//												
//												strSetCode.append( 
//														"<img src=\""+req.getContextPath()+"/images/tree/asngw.JPG"+"\" height=\"18\" width=\"23\" title=\"Diameter\" />&nbsp;&nbsp;"+
//														((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//												
//												strSetCode.append("</td>" +
//														"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//														"<span id="+"\""+strNodeMapid+"\">"+
//														txt.toString()+
//														"</span>");
//													if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//														strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//													else
//														strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//														
//											}
//											else if(gatewayType.equals("Radius"))
//											{
//												strSetCode.append( 
//														"<img src=\""+req.getContextPath()+"/images/tree/Bras.gif"+"\" height=\"18\" width=\"23\" title=\"Radius\" />&nbsp;&nbsp;"+
//														((gatewayInfo.getAreaName()!=null && !gatewayInfo.getAreaName().equals(""))?gatewayInfo.getAreaName():gatewayInfo.getLocation()));
//												
//												strSetCode.append("</td>" +
//														"<td class=\"tabletext\" align=\"center\"  width=35%>" +
//														"<span id="+"\""+strNodeMapid+"\">"+
//														txt.toString()+
//														"</span>");
//													if(gatewayInfo.getStatus()!=null && gatewayInfo.getStatus().equals("Active"))
//														strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"note\"> "+ gatewayInfo.getStatus()+"</font></td><td class=\"tabletext\"  align=\"center\"  width=35%><img src=\""+req.getContextPath()+"/images/tree/bodconncurrentuser.gif"+"\" height=\"10\" width=\"8\" title=noOfCustomer/>&nbsp;&nbsp;" + gatewayInfo.getActiveSessions() +"</td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//													else
//														strSetCode.append("</td><td class=\"tabletext\" align=\"center\"  width=35% > <font class=\"errorfont\"> In Active </font></td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp; </td><td class=\"tabletext\" align=\"center\"  width=35%>&nbsp;&nbsp;&nbsp;</td>");
//														
//											}
//												
//											
//											strSetCode.append("</td></tr></table>");
//											infoData.setCode(strSetCode.toString());
//											mpTreeMap.put("1-1",infoData);
//											mpTreeMap.get("1-0").addChild(infoData);
//											
//										}
//
//									}
//									
//							}
//						}
//				}
				

//			}
//				while(i<4)
//				{
//				
//					 if(i==0){
//						strSetCode = new StringBuffer();
//						strSetCode.append(
//								"mehsana"+"</td>" +
//							"</tr></table>"); 
//						infoData.setCode(strSetCode.toString());
//						infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
//						infoData.setOpen(true);
//						infoData.setShowAsFolder(false);
//						
//						mpTreeMap.put("0-1",infoData);
//						mpTreeMap.get("0-0").addChild(infoData);
//					}else if(i==1){
//						
//						strSetCode = new StringBuffer();
//						strSetCode.append(
//								"CISCO SCE"+"</td>" +
//							"</tr></table>"); 
//						infoData.setCode(strSetCode.toString());
//						infoData.setPicture(req.getContextPath()+"/images/tree/location_leaf.gif");
//						infoData.setOpen(true);
//						infoData.setShowAsFolder(false);
//						mpTreeMap.put("1-0",infoData);
//						mpTreeMap.get("0-1").addChild(infoData);
//					}		
//					else if(i==2)
//					{
//
//						strIpaddress = "10.10.10.3";
//						strNodename = "SCE @ Modhera";
//						
//						txt.append("<table width=\"100%\" style=\"table-layout:fixed;\"><tr><td width=\"3%\" >");
//						
//						
//						txt.append("</td><td colsspan=\"5\" width=\"35%\" align=\"left\" ><font class=\"note\">" +
//							"<font class=\"capacity\"><center>" + strIpaddress + "</center></font>" );
//				
//						txt.append(
//							"</table>");
//
//						
//						strSetCode = new StringBuffer();
//						strSetCode.append( 
//								"<img src=\""+req.getContextPath()+"/images/tree/sce.gif"+"\" height=\"18\" width=\"23\" title=\""+strDevicetypename+"\" />&nbsp;&nbsp;"+
//							strNodename);
//						strSetCode.append("</td>" +
//							"<td class=\"tabletext\" width=35%>" +
//							"<span id="+"\""+strNodeMapid+"\">"+
//							txt.toString()+
//							"</span>"+
//							"</td><td class=\"tabletext\" width=35%>&nbsp;&nbsp;&nbsp;</td>");
//							
//								infoData.setCode(strSetCode.toString());
//						mpTreeMap.put("1-1",infoData);
//						mpTreeMap.get("1-0").addChild(infoData);
//						
//					}
//					else if(i==3)
//					{
//
//						strIpaddress = "10.10.10.3";
//						strNodename = "SCE @ Modhera";
//						
//						txt.append("<table width=\"100%\" style=\"table-layout:fixed;\"><tr><td width=\"3%\" >");
//						
//						
//						txt.append("</td><td colsspan=\"5\" width=\"35%\" align=\"left\" ><font class=\"note\">" +
//							"<font class=\"capacity\"><center>" + strIpaddress + "</center></font>" );
//				
//						txt.append(
//							"</table>");
//
//						
//						strSetCode = new StringBuffer();
//						strSetCode.append( 
//								"<img src=\""+req.getContextPath()+"/images/tree/sce.gif"+"\" height=\"18\" width=\"23\" title=\""+strDevicetypename+"\" />&nbsp;&nbsp;"+
//							strNodename);
//						strSetCode.append("</td>" +
//							"<td class=\"tabletext\" width=35%>" +
//							"<span id="+"\""+strNodeMapid+"\">"+
//							txt.toString()+
//							"</span>"+
//							"</td><td class=\"tabletext\" width=35%>&nbsp;&nbsp;&nbsp;</td>");
//							
//								infoData.setCode(strSetCode.toString());
//						mpTreeMap.put("1-2",infoData);
//						mpTreeMap.get("1-0").addChild(infoData);
//						
//					}	
//								i++;
//				}	
				
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
		}
		return mpTreeMap;
	}
}
