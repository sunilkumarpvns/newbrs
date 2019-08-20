<%@ page import="com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%
    String basePath1 = request.getContextPath();
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<script type="text/javascript" src="<%=basePath1%>/js/menu/stmenu.js"></script>
</head>
<body>
<div id="Layer1" style="margin-top: 0px;vertical-align: top;text-align: left;">
 <script type="text/javascript">
	 stm_bm(["esmmenu",800,"","",1,"stgcl()","stgct()+110",0,0,50,0,250,0,0,0,"","",0,0,1,1,"default","hand","file:///E|/Sothink2009"],this);
	 stm_bp("p0",[1,4,0,0,0,4,0,7,100,"",-2,"",-2,31,0,0,"#FFFFFF","transparent","",3,0,0,"#000000"]);
		  
	 <%if(ProfileManager.getModelStatus(request , ConfigConstant.AAA_SERVICE_POLICY)) { %>
		  
  		  	stm_aix("p0i1","p0i0",[0," Service Policy","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
  		 	stm_bpx("p1","p0",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			 
  		  	/******* RADIUS POLICY *******/
	  		<%if(ProfileManager.getModuleStatus(request, ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.RADIUS)){%>          
			  	stm_aix("p1i0","p0i1",[0,"   Radius ","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  	stm_bpx("p1","p0",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			  
				 <%if(ProfileManager.getSubModuleStatus(request , ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.RADIUS,ConfigConstant.SEARCH_RADIUS_SERVICE_POLICY)) {	%>                                                                                                                                                              
				 	stm_aix("p1i0","p1i0",[0,"Radius Service Policy","","",-1,-1,0,"<%=basePath1%>/searchRadiusServicePolicy.do"],175,20);
			 	 <%}%>
				 
				 <%if(ProfileManager.getSubModuleStatus(request , ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.RADIUS,ConfigConstant.SEARCH_DYNAUTH_POLICY)) {	%>                                                                                                                                                              
			  		stm_aix("p1i1","p1i0",[0,"DynAuth Policy","","",-1,-1,0,"<%=basePath1%>/searchDynAuthServicePolicy.do"],175,20);
			  	<%}%>
			  
			  stm_ep();
			 <%}%>
		  
			 /******* DIAMETER POLICY *******/
		   <%if(ProfileManager.getModuleStatus(request, ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.DIAMETER)){%>
			  stm_aix("p1i2","p1i0",[0,"   Diameter ","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  stm_bpx("p2","p1",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			 
				<%if(ProfileManager.getSubModuleStatus(request , ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.DIAMETER,ConfigConstant.SEARCH_NAS_SERVICE_POLICY)) {	%>                                                                                                                                                              
			  	  stm_aix("p2i0","p1i0",[0,"  3GPP AAA Policy ","","",-1,-1,0,"<%=basePath1%>/searchTGPPAAAPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				<%}%>
			  
				<%if(ProfileManager.getSubModuleStatus(request , ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.DIAMETER,ConfigConstant.SEARCH_NAS_SERVICE_POLICY)) {	%>                                                                                                                                                              
			  	  stm_aix("p2i1","p1i0",[0,"  NAS Policy ","","",-1,-1,0,"<%=basePath1%>/searchNASServicePolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				<%}%>
				
				<%if(ProfileManager.getSubModuleStatus(request , ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.DIAMETER,ConfigConstant.SEARCH_CREDIT_CONTROL_SERVICE_POLICY)) {	%>                                                                                                                                                              
			  	  stm_aix("p2i2","p1i0",[0,"  Credit Control Policy ","","",-1,-1,0,"<%=basePath1%>/searchCcpolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				<%}%>
				
				<%if(ProfileManager.getSubModuleStatus(request , ConfigConstant.AAA_SERVICE_POLICY,ConfigConstant.DIAMETER,ConfigConstant.SEARCH_DIAMETER_EAP_POLICY)) {	%>                                                                                                                                                              
			 	  stm_aix("p2i3","p1i0",[0,"  EAP Policy ","","",-1,-1,0,"<%=basePath1%>/searchEAPPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				<%}%>
				stm_ep();
		   <%}%>
		  
		    /******* RM POLICY *******/
		  
		    <%if(true){%>  
		   	 	stm_aix("p1i3","p1i1",[0,"   RM ","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			    stm_bpx("p3","p2",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			    <%if(true) {%> 
					 stm_aix("p3i0","p1i0",[0," Charging Policy ","","",-1,-1,0,"<%=basePath1%>/searchCGPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				     stm_ep();
			    <%}%>
			    stm_ep();
		    <%}%>
		  <%}%>
		  
		  /******* Servers / Configurations *******/
		  <%if(ProfileManager.getModelStatus(request , ConfigConstant.SERVER_MODEL)) { %>
		  		stm_aix("p0i2","p0i0",[0," Servers / Configurations","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  		stm_bpx("p4","p3",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
				  	
				  	<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.SERVER)){%>        
				  		stm_aix("p4i0","p0i0",[0,"  Server Instance ","","",-1,-1,0,"<%=basePath1%>/listNetServerInstance.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  	<%}%>
				  	
				  	<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.SESSION_MANAGER)){%>     
				   		stm_aix("p4i1","p0i0",[0,"  Session Manager ","","",-1,-1,0,"<%=basePath1%>/searchSessionManager.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.SERVICE_DRIVERS)){%>                                                                                                                                                                   
					  	stm_aix("p4i2","p0i0",[0,"  Service Drivers ","","",-1,-1,0,"<%=basePath1%>/searchDriverInstance.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.DIGEST_CONFIGURATION)){%>
					 	stm_aix("p4i3","p0i0",[0,"  Digest Configuration ","","",-1,-1,0,"<%=basePath1%>/searchDigestConf.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.ALERT_CONFIGURATION)){%>
					 	stm_aix("p4i4","p0i0",[0,"  Alert Configuration ","","",-1,-1,0,"<%=basePath1%>/searchAlertListener.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.EAP_CONFIGURATION)){%>
					  	stm_aix("p4i5","p0i0",[0,"  EAP Configuration ","","",-1,-1,0,"<%=basePath1%>/searchEAPConfig.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.ACCESS_POLICY)){%>
			 			stm_aix("p4i6","p0i0",[0,"  Access Policy ","","",-1,-1,0,"<%=basePath1%>/searchAccessPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.GRACE_POLICY)){%>
					 	 stm_aix("p4i7","p0i0",[0,"  Grace Policy ","","",-1,-1,0,"<%=basePath1%>/initGracepolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request,ConfigConstant.SERVER_MODEL,ConfigConstant.TRANSLATION_MAPPING_CONFIG)) {%>
					 	stm_aix("p4i8","p0i0",[0,"  Translation Mapping Config","","",-1,-1,0,"<%=basePath1%>/searchTranslationMappingConfig.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					
					 <%if(ProfileManager.getModuleStatus(request,ConfigConstant.SERVER_MODEL,ConfigConstant.COPY_PACKET_TRANSLATION_MAPPING_CONFIG)) {%> 
						stm_aix("p4i9","p0i0",[0,"  Copy Packet Config","","",-1,-1,0,"<%=basePath1%>/searchCopyPacketMappingconf.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>
					
					<%if(ProfileManager.getModuleStatus(request,ConfigConstant.SERVER_MODEL,ConfigConstant.CERTIFICATE)){%> 
						 stm_aix("p4i10","p0i0",[0,"  SSL Certificates ","","",-1,-1,0,"<%=basePath1%>/serverAllCertificates.do?method=initSearch","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					<%}%>	
					
					<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.PLUGIN)){%>        
	  					stm_aix("p4i11","p0i0",[0,"  Plugins ","","",-1,-1,0,"<%=basePath1%>/searchPlugin.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
	  				<%}%>
	  				
	  				<%if(ProfileManager.getModuleStatus(request, ConfigConstant.SERVER_MODEL,ConfigConstant.SCRIPT)){%>        
  						stm_aix("p4i12","p0i0",[0,"  Script ","","",-1,-1,0,"<%=basePath1%>/script.do?method=search","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
  					<%}%>
					
		 	 stm_ep(); 
		  <%}%>
		  
		  /******* RADIUS *******/
		  <%if(ProfileManager.getModelStatus(request , ConfigConstant.RADIUS)) {%>
			  stm_aix("p0i3","p0i0",[0," RADIUS ","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  stm_bpx("p5","p4",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			  
			  <%if(ProfileManager.getModuleStatus(request, ConfigConstant.RADIUS,ConfigConstant.RADIUS_POLICY)) {	%>
	 				stm_aix("p5i0","p0i0",[0,"  Radius Policy ","","",-1,-1,0,"<%=basePath1%>/searchRadiusPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
	  	 	  <%}%>
	  	 	  
	  	 	  <%if(ProfileManager.getModuleStatus(request, ConfigConstant.RADIUS,ConfigConstant.RADIUS_POLICY_GROUP)) {	%>
 			 		stm_aix("p5i1","p0i0",[0,"  Radius Policy Group ","","",-1,-1,0,"<%=basePath1%>/searchRadiusPolicyGroup.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
  	 	 	  <%}%>
			  
			  <%if(ProfileManager.getModuleStatus(request, ConfigConstant.RADIUS,ConfigConstant.DICTIONARY_MANAGEMENT)) {	%>
		 			 stm_aix("p5i2","p0i0",[0,"  Dictionary Management ","","",-1,-1,0,"<%=basePath1%>/listDictionary.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  	  <%}%>
		  	  
		  	  <% if(ProfileManager.getModuleStatus(request,ConfigConstant.RADIUS,ConfigConstant.RADIUS_TEST)) { %>
					 stm_aix("p5i3","p0i0",[0,"  RADIUS Test ","","",-1,-1,0,"<%=basePath1%>/initListRadiusPacket.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  	  <%}%>
		  	  
			  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.RADIUS,ConfigConstant.BLACKLIST_CANDIDATES)) {%>
			 		 stm_aix("p5i4","p0i0",[0,"  Blacklist Candidates ","","",-1,-1,0,"<%=basePath1%>/searchBWList.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  <%}%>
		   
			  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.RADIUS,ConfigConstant.TRUSTED_CLIENT_PROFILE)) {%>
				   	 stm_aix("p5i5","p0i0",[0,"  Trusted Client Profile ","","",-1,-1,0,"<%=basePath1%>/searchClientProfile.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  	  <%}%>
		  	  
		  	
	 		  
		  	  <%if(true) {%>
				  stm_aix("p5i6","p0i0",[0,"  Utilities ","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  stm_bpx("p7","p6",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
					  <%if(true){%>
						 stm_aix("p7i0","p1i0",[0,"  Password Verification ","","",-1,-1,0,"<%=basePath1%>/initVerifyPassword.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				 	 <%}%>
				 	stm_ep();
		    <%}%>
		    
		    <%if(ProfileManager.getModuleStatus(request,ConfigConstant.RADIUS,ConfigConstant.RADIUS_ESI_GROUP)) {%>
 				stm_aix("p5i7","p0i0",[0,"  Radius ESI Group ","","",-1,-1,0,"<%=basePath1%>/searchRadiusESIGroup.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
	 		<%}%>

            <%if(ProfileManager.getModuleStatus(request,ConfigConstant.RADIUS,ConfigConstant.CORRELATED_RADIUS)) {%>
                stm_aix("p5i8","p0i0",[0,"  Correlated Radius ","","",-1,-1,0,"<%=basePath1%>/searchCorrelatedRadius.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
            <%}%>
	 		
			stm_ep();
		   
		  <%}%>
		    
		    /******* DIAMETER *******/
		   <%if(ProfileManager.getModelStatus(request , ConfigConstant.DIAMETER)){%>
		 		  stm_aix("p0i4","p0i0",[0," Diameter","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],185);
				  stm_bpx("p8","p7",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
				 
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.AUTHORIZATION_POLICY)) {%>
				 	 stm_aix("p8i0","p0i0",[0,"  Authorization Policies ","","",-1,-1,0,"<%=basePath1%>/searchDiameterPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.DIAMETER_POLICY_GROUP)) {%>
				 	 stm_aix("p8i1","p0i0",[0,"  Diameter Policy Group ","","",-1,-1,0,"<%=basePath1%>/searchDiameterPolicyGroup.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.DIAMETER_DICTIONARY_MANAGEMENT)) {%>
				 	 stm_aix("p8i2","p0i0",[0,"  Dictionary Management ","","",-1,-1,0,"<%=basePath1%>/listDiameterdic.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.DIAMETER_PEER_PROFILE)) {%>
				 	 stm_aix("p8i3","p0i0",[0,"  Diameter Peer Profiles ","","",-1,-1,0,"<%=basePath1%>/initSearchDiameterPeerProfiles.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.DIAMETER_PEER)) {%>
					 stm_aix("p8i4","p0i0",[0,"  Diameter Peers ","","",-1,-1,0,"<%=basePath1%>/initSearchDiameterPeer.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  
				   stm_aix("p8i5","p0i0",[0,"  Diameter Peer Group","","",-1,-1,0,"<%=basePath1%>/searchDiameterPeerGroup.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  
				  <%if(true){%>
					  stm_aix("p8i6","p0i0",[0,"  Diameter Routing Table ","","",-1,-1,0,"<%=basePath1%>/initSearchDiameterRoutingConfig.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
			      <%if(true){%>
				 	 stm_aix("p8i7","p0i0",[0,"  Subscriber Routing Table","","",-1,-1,0,"<%=basePath1%>/searchSubscriberRoutingTable.do?method=initSearch","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
			      <%}%>
				  <%if(true){%>
				 	  stm_aix("p8i8","p0i0",[0,"  Priority Table ","","",-1,-1,0,"<%=basePath1%>/updatePriorityTableConfig.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.DIAMETER_SESSION_MANAGER)) {%>
			   		  stm_aix("p8i9","p0i0",[0,"  Diameter Session Manager","","",-1,-1,0,"<%=basePath1%>/searchDiameterSessionManager.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
				  <%}%>
				  <%if(ProfileManager.getModuleStatus(request,ConfigConstant.DIAMETER,ConfigConstant.DIAMETER_CONCURRENCY)) {%>
		   		  	stm_aix("p8i10","p0i0",[0,"  Diameter Concurrency","","",-1,-1,0,"<%=basePath1%>/searchDiameterConcurrency.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],188);
			 	  <%}%>
			 		
				stm_ep();
		    <%}%> 
		  
		    /******* RESOURCE MANAGER *******/
		    <%if(ProfileManager.getModelStatus(request , ConfigConstant.RESOURCE_MANAGER)){%>
			  	stm_aix("p0i5","p0i0",[0," Resource Manager","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			 	stm_bpx("p9","p8",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
				 
			 		<% if(ProfileManager.getModuleStatus(request , ConfigConstant.RESOURCE_MANAGER,ConfigConstant.IP_POOL)){ %>
				 	 	stm_aix("p9i0","p0i0",[0,"  IP Pool ","","",-1,-1,0,"<%=basePath1%>/searchIPPool.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  	<%}%>
				  	
				  	<%	if(ProfileManager.getModuleStatus(request ,ConfigConstant.RESOURCE_MANAGER,ConfigConstant.CONCURRENT_POLICY)) {  %>
			  			stm_aix("p9i1","p0i0",[0,"  Concurrent Login Policy ","","",-1,-1,0,"<%=basePath1%>/searchConcurrentLoginPolicy.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  		<%}%>
			  		
			  	stm_ep();
			 <%}%>   
			 
			 /******* EXTERNAL SYSTEM *******/
			 <% if(ProfileManager.getModelStatus(request , ConfigConstant.EXTERNAL_SYSTEM)){%>
				  stm_aix("p0i5","p0i0",[0," External System","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  stm_bpx("p10","p9",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
		  
				  <% if(ProfileManager.getModuleStatus(request ,ConfigConstant.EXTERNAL_SYSTEM,ConfigConstant.EXTENDED_RADIUS)){%>       
					  stm_aix("p10i0","p0i0",[0,"  Extended RADIUS ","","",-1,-1,0,"<%=basePath1%>/searchESIInstance.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  		  <%}%>
			  		
			  	  <% if(ProfileManager.getModuleStatus(request ,ConfigConstant.EXTERNAL_SYSTEM,ConfigConstant.LDAP_DATASOURCE)){%>                                                                                                                                                              
					 <%if(ProfileManager.getSubModuleStatus(request ,ConfigConstant.EXTERNAL_SYSTEM,ConfigConstant.LDAP_DATASOURCE,ConfigConstant.CREATE_LDAP_DATASOURCE)){%>
					 	 stm_aix("p10i1","p0i0",[0,"  LDAP Datasource ","","",-1,-1,0,"<%=basePath1%>/initSearchLDAPDS.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		 			  <%}%>
		 		  <%}%>
		 		  <% if(ProfileManager.getModuleStatus(request ,ConfigConstant.EXTERNAL_SYSTEM,ConfigConstant.DATABASE_DATASOURCE)){%> 
					  stm_aix("p10i2","p0i0",[0,"  Database Datasource ","","",-1,-1,0,"<%=basePath1%>/searchDatabaseDS.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  		  <%}%>
		  		 stm_ep();	
		  	<%}%>
		  	
		  	 /******* In-Memory Data Grid *******/
		  	<% if(ProfileManager.getModelStatus(request , ConfigConstant.INMEMORYDATAGRID)){%>
				  stm_aix("p0i6","p0i0",[0," In-Memory Data Grid","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  stm_bpx("p10","p9",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
		  
				  <% if(ProfileManager.getModuleStatus(request ,ConfigConstant.INMEMORYDATAGRID,ConfigConstant.CONFIGURE)){%>       
					  stm_aix("p10i0","p0i0",[0,"  Configure ","","",-1,-1,0,"<%=basePath1%>/inMemoryDataGrid.do?method=search","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		  		  <%}%>
		  		 stm_ep();	
		  	 <%}%>
		  
		  	 /******* SYSTEM *******/
		  	<%if(ProfileManager.getModelStatus(request ,ConfigConstant.SYSTEM)){%>
			 	 stm_aix("p0i7","p0i0",[0," System","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			 	 stm_bpx("p11","p10",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			  
				  <%if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.SYSTEM_PARAMETER)) {%>
					  stm_aix("p11i0","p0i0",[0,"  System Parameter ","","",-1,-1,0,"<%=basePath1%>/viewSystemParameter.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.SYSTEM_PARAMETER)) {%>
					  stm_aix("p11i1","p0i0",[0,"  Dashboard Configuration ","","",-1,-1,0,"<%=basePath1%>/dashboardConfiguration.do?method=viewDashboardConfiguration","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.SYSTEM_PARAMETER)){%>
					  stm_aix("p11i2","p0i0",[0,"  System Information ","","",-1,-1,0,"<%=basePath1%>/systemInformation.do?method=getSystemConfiguration","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				  
				  <%if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.LICENSE)){%>
				  stm_aix("p11i3","p0i1",[0,"  License ","","",-1,-1,0,"<%=basePath1%>/viewLicenceAction.do?method=getLicenceData","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  	  <%}%>
				  
				  <%if(ProfileManager.getSubModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.STAFF,ConfigConstant.STAFF_AUDIT_ACTION)){%>		
				 	 stm_aix("p11i4","p0i0",[0,"  Audit Log ","","",-1,-1,0,"<%=basePath1%>/initStaffAudit.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>

				  <%if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.DATABASEPROPERTIES)){%>
				 	 stm_aix("p11i5","p0i0",[0,"  Database Properties ","","",-1,-1,0,"<%=basePath1%>/databaseProperties.do?method=getDatabasePropertiesData","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				  stm_ep();	
			 <%}%>
		  
			 /******* REPORTS *******/
			 <%if(true){%>
			 	 stm_aix("p0i8","p0i0",[0," Reports","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			 	 stm_bpx("p12","p11",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
			 
				 	<%if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.SYSTEM_PARAMETER)) {%>
				 		 stm_aix("p12i0","p0i0",[0,"  Connection Requests ","","",-1,-1,0,"<%=basePath1%>/initSearchUserStatistics.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				 	<%}%>
			 		
				 	stm_ep();	
			  <%}%>
			
			  /******* SYSTEM *******/
			  <%if(ProfileManager.getModelStatus(request ,ConfigConstant.SYSTEM)) {%>
				<%	if(ProfileManager.getModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.STAFF)){%>
				  stm_aix("p0i9","p0i0",[0," Staff","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  stm_bpx("p13","p12",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
				  <%if(true) { %>	
				 	 stm_aix("p12i0","p0i0",[0," Access Group Management","","",-1,-1,0,"<%=basePath1%>/listAccessGroup.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				 
				  <%if(ProfileManager.getSubModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.STAFF,ConfigConstant.STAFF_AUDIT_ACTION)){%>		
				 	 stm_aix("p12i1","p0i0",[0," Change Password ","","",-1,-1,0,"<%=basePath1%>/initChangePassword.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				  
				  <%if(ProfileManager.getSubModuleStatus(request ,ConfigConstant.SYSTEM,ConfigConstant.STAFF,ConfigConstant.SEARCH_STAFF)) {	%>
				 	 stm_aix("p12i2","p0i0",[0," Staff ","","",-1,-1,0,"<%=basePath1%>/searchStaff.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
				  <%}%>
				  stm_ep();	
			  	<%}%>
			 <%}%>
			   
			  /******* WEB SERVICE *******/
			 <% if(ProfileManager.getModelStatus(request , ConfigConstant.WEB_SERVICE)){%>
			  stm_aix("p0i10","p0i0",[0," Web Service","","",-1,-1,0,"","_self","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
			  stm_bpx("p14","p13",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
				 
			  	<% if(ProfileManager.getModuleStatus(request ,ConfigConstant.WEB_SERVICE,ConfigConstant.SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG)){%>      
			 		  stm_aix("p14i0","p0i0",[0," Subscriber","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					  stm_bpx("p15","p14",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
					  
					  <% if(ProfileManager.getSubModuleStatus(request ,ConfigConstant.WEB_SERVICE,ConfigConstant.SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG,ConfigConstant.UPDATE_SUBSCRIBER_PROFILE_WSCONFIG)) {%>
					 	stm_aix("p15i0","p1i0",[0,"  Database Configuation ","","",-1,-1,0,"<%=basePath1%>/initSubscriberWSDatabaseConfig.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					  <%}%>
					  stm_ep();
				 <%}%>
				 
				 <%if(ProfileManager.getModuleStatus(request ,ConfigConstant.WEB_SERVICE,ConfigConstant.SESSION_MANAGEMENT_WEB_SERVICE_CONFIG)){%>
					  stm_aix("p14i1","p0i0",[0," Session Management","","",-1,-1,0,"","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					  stm_bpx("p16","p15",[1,2,0,0,0,4,0,0,100,"progid:DXImageTransform.Microsoft.Wipe(GradientSize=1.0,wipeStyle=0,motion=forward,enabled=0,Duration=0)",-2,"",-2,85,0,0,"#7F7F7F","#FFFFFF"],175);
					 
					 <% if(ProfileManager.getSubModuleStatus(request ,ConfigConstant.WEB_SERVICE,ConfigConstant.SESSION_MANAGEMENT_WEB_SERVICE_CONFIG,ConfigConstant.UPDATE_SESSION_MANAGEMENT_WSCONFIG)) {%>
						  stm_aix("p16i0","p1i0",[0," Database Configuation ","","",-1,-1,0,"<%=basePath1%>/initSessionMgmtWSDatabaseConfig.do","","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #005197 #007CC3","#007CC3 #005197 #005197 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
					 <%}%>
					  stm_ep();
				 <%}%>
			      stm_ep();
			   <%}%>
			
			
				 	  
		  	 /******* LOGOUT *******/
		  	  <% if(true){%>
		 		 stm_aix("p0i11","p0i0",[0," Logout","","",-1,-1,0,"<%=basePath1%>/logout.do?skipPasswordCheck=yes","mainFrame","","","","",0,0,0,"","",7,7,0,0,1,"#005197",0,"rgb(3, 44, 77)",0,"","",3,3,1,1,"#007CC3 #005197 #007CC3 #007CC3","#007CC3 #005197 #007CC3 #007CC3","#FFFFFF","#FFFFFF","9pt Arial","9pt Arial"],175);
		 	  <%}%>
		
		 	  stm_ep();
	  	 	  stm_ep();

		  stm_cf([2,0,-55,"mainFrame","leftFrame",1]);
		  stm_em();
 </script>
</div>
</body>
</html>
