<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="com.elitecore.netvertexsm.blmanager.customizedmenu.ContainerMenu"%>
<%@page import="com.elitecore.netvertexsm.blmanager.customizedmenu.NonContainerMenu"%>
<%@page import="com.elitecore.netvertexsm.blmanager.customizedmenu.MenuType"%>
<%@page import="com.elitecore.netvertexsm.blmanager.customizedmenu.MenuItem"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.customizedmenu.CustomizedMenuBLManager" %>
<%@ page import="java.util.List" %>

<%
    String basePath1 = request.getContextPath();
	String pdSSOURlVal = (String) request.getServletContext().getAttribute("pdSSOURL");
	CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager(); 
	List<MenuItem> customizedMenuItemsList = customizedMenuBLManager.getCustomizedMenuItems();	
%>

<script type="text/javascript" src="<%=basePath1%>/js/menu/stmenu.js"></script>

<html>
<body>
<div id="Layer1" style="margin-top: 0px;vertical-align: top;text-align: left;">
	<script type="text/javascript">
		stm_bm(["esmmenu",800,"","",1,"stgcl()","stgct()+110",0,0,25,0,250,0,0,0,"","",0,0,1,1,"default","hand","file:///E|/Sothink2009"],this);
	 	stm_bp("p0",[1,4,0,0,0,4,0,7,100,"",-2,"",-2,31,0,0,"#FFFFFF","transparent","",3,0,0,"#000000"]);


		stm_aix("p0i1","",[0," PCRF Service Policy","","",-1,-1,0,"<%=basePath1%>/initSearchPCRFService.do?isMenuCall=yes","mainFrame","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);

		 	stm_aix("p0i2","",[0," Server Configuration","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"]);
	  		stm_bpx("p2","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
		        stm_aix("p2i7","",[0,"  Server Group","","",-1,-1,0,"<%=basePath1%>/serverGroupManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i1","",[0,"  Session Configuration ","","",-1,-1,0,"<%=basePath1%>/viewSessionConf.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i2","",[0,"  SP Interface Configuration","","",-1,-1,0,"<%=basePath1%>/initSearchSPInterface.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i3","",[0,"  Subscriber Profile Repository ","","",-1,-1,0,"<%=basePath1%>/sprData.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i7","",[0,"  DDF Table ","","",-1,-1,0,"<%=basePath1%>/ddfTableData.do?method=view&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i4","",[0,"  Driver Management ","","",-1,-1,0,"<%=basePath1%>/initSearchDriverInstance.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i5","",[0,"  Alert Configuration ","","",-1,-1,0,"<%=basePath1%>/initSearchAlertListener.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p2i6","",[0,"  Certificate","","",-1,-1,0,"<%=basePath1%>/searchAllCertificate.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
		        
		        

			stm_ep();

			// SSO URL
			<% if(Strings.isNullOrBlank(pdSSOURlVal)==false){ %>
				stm_aix("p0i17","",[0," Policy Designer","","",-1,-1,0,"${applicationScope.pdSSOURL}","_blank","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #007CC3 #007CC3","#007CC3 #005197 #007CC3 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],198);
			<% } %>

			
	  		stm_aix("p0i3","",[0," Gateway Management","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			stm_bpx("p3","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
				stm_aix("p3i0","",[0,"  Profile ","","",-1,-1,0,"<%=basePath1%>/initSearchProfile.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
		   		stm_aix("p3i1","",[0,"  Gateway ","","",-1,-1,0,"<%=basePath1%>/initSearchGateway.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			  	stm_aix("p3i2","",[0,"  Packet Mapping ","","",-1,-1,0,"<%=basePath1%>/searchMapping.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	stm_aix("p3i3","",[0,"  PCC Rule Mapping ","","",-1,-1,0,"<%=basePath1%>/pccRuleManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			 	
			stm_ep();
			
			stm_aix("p0i6","",[0," Roaming","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			stm_bpx("p6","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
				stm_aix("p6i0","",[0,"  MCC-MNC Group ","","",-1,-1,0,"<%=basePath1%>/mccmncGroupManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
		   		stm_aix("p6i1","",[0,"  Routing Table ","","",-1,-1,0,"<%=basePath1%>/routingTableManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			  	stm_aix("p6i2","",[0,"  Network Management ","","",-1,-1,0,"<%=basePath1%>/networkManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			
			stm_ep();
			
			stm_aix("p0i7","",[0," Location Configuration","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			stm_bpx("p7","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
				stm_aix("p7i0","",[0,"  Region ","","",-1,-1,0,"<%=basePath1%>/regionManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
		   		stm_aix("p7i1","",[0,"  City ","","",-1,-1,0,"<%=basePath1%>/cityManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			  	stm_aix("p7i2","",[0,"  Area ","","",-1,-1,0,"<%=basePath1%>/areaManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			
			stm_ep();
			
			stm_aix("p0i9","",[0," Device Management","","",-1,-1,0,"<%=basePath1%>/deviceMgmt.do?method=initSearch&isMenuCall=yes","mainFrame","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  		
			stm_aix("p0i10","",[0," External System","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			stm_bpx("p10","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
				stm_aix("p10i0","",[0,"  LDAP Datasource ","","",-1,-1,0,"<%=basePath1%>/initSearchLDAPDS.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
		   		stm_aix("p10i1","",[0,"  Database Datasource ","","",-1,-1,0,"<%=basePath1%>/initSearchDatabaseDS.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			
			
		   		
			//Customized Menu
			<%if(customizedMenuItemsList!=null && !customizedMenuItemsList.isEmpty()){
					String customizedMenuName = (String)request.getSession().getAttribute("customizedMenuName");
					if(customizedMenuName==null || customizedMenuName.trim().isEmpty()){
						customizedMenuName="Customized Menu";
					}
				%>
				stm_ep();
				stm_aix("p0i11","",[," <%=customizedMenuName%>","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				stm_bpx("p11","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
				<% 
					String script = null;
					for(MenuItem menuItem :customizedMenuItemsList){
							script = menuItem.getMenuScript();
					%>
							<%=script%>;
					<%}
			}%>

			stm_ep();
			//Business Inteligence Module

			stm_aix("p0i12","",[0," BI / CEA","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			stm_bpx("p12","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
					stm_aix("p10i0","",[0,"  Template ","","",-1,-1,0,"<%=basePath1%>/searchBITemplate.do?isMenuCall=yes'","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			stm_ep();
			
			//System
			stm_aix("p0i15","",[0," System","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			stm_bpx("p15","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
					stm_aix("p15i0","",[0,"  System Parameter ","","",-1,-1,0,"<%=basePath1%>/viewSystemParameter.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
					stm_aix("p15i1","",[0,"  Manage Customized Menu ","","",-1,-1,0,"<%=basePath1%>/customizedMenumgmt.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
			
				stm_aix("p15i2","",[0," Staff","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
				stm_bpx("p15","",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"]);
					stm_aix("p15i20","",[0,"  Staff Management ","","",-1,-1,0,"<%=basePath1%>/searchStaff.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
					stm_aix("p15i21","",[0,"  Role Management ","","",-1,-1,0,"<%=basePath1%>/listAccessGroup.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
					stm_aix("p15i22","",[0,"  Staff Audit ","","",-1,-1,0,"<%=basePath1%>/initStaffAudit.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
					stm_aix("p15i23","",[0,"  Change Password ","","",-1,-1,0,"<%=basePath1%>/initChangePassword.do?isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
					stm_aix("p15i23","",[0,"  Group Management ","","",-1,-1,0,"<%=basePath1%>/groupManagement.do?method=initSearch&isMenuCall=yes","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],190);
					stm_ep();
			stm_ep();
			
			
			
			//Logout
			stm_aix("p0i16","",[0," Logout","","",-1,-1,0,"<%=basePath1%>/logout.do?isMenuCall=yes&skipPasswordCheck=1","mainFrame","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #007CC3 #007CC3","#007CC3 #005197 #007CC3 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			
			stm_ep();
			stm_ep();
			stm_cf([2,0,-55,"mainFrame","leftFrame",1]);
			stm_em();
	</script>
</div>
</body>
</html>