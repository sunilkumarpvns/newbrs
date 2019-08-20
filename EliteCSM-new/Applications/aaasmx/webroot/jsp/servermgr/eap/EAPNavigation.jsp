

<%@page import="com.elitecore.elitesm.util.constants.EAPConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData"%><table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr> 
<td colspan="2" valign="top"> 







<%
	String navigationBasePath = request.getContextPath();
	EAPConfigData eapConfigData = (EAPConfigData)request.getAttribute("eapConfigData");
	
	String updateBasicDetails      = navigationBasePath+"/updateEAPBasicDetails.do?eapId="+eapConfigData.getEapId();
	String updateEapTtls 		   = navigationBasePath+"/updateEAPTLSDetails.do?typeVal=ttls&eapId="+eapConfigData.getEapId();
	String updateEapTls            = navigationBasePath+"/updateEAPTLSDetails.do?typeVal=tls&eapId="+eapConfigData.getEapId();
	String updateEapPeap 		   = navigationBasePath+"/updateEAPTLSDetails.do?typeVal=peap&eapId="+eapConfigData.getEapId();
	String updateSupportedMethods  = navigationBasePath+"/updateSupportedMethods.do?eapId="+eapConfigData.getEapId();
	String updateEapGsm			   = navigationBasePath+"/updateEAPGsmConfig.do?eapId="+eapConfigData.getEapId();
	
	String viewBasicDetails		   = navigationBasePath+"/viewEAPConfig.do?eapId="+eapConfigData.getEapId(); 
	String viewEapTtls    		   = navigationBasePath+"/viewEAPTLSConfig.do?type=ttls&eapId="+eapConfigData.getEapId();
	String viewEapTls    		   = navigationBasePath+"/viewEAPTLSConfig.do?type=tls&eapId="+eapConfigData.getEapId();
	String viewEapPeap    		   = navigationBasePath+"/viewEAPTLSConfig.do?type=peap&eapId="+eapConfigData.getEapId();
	String viewEapGsm			   = navigationBasePath+"/viewEAPGsmConfig.do?eapId="+eapConfigData.getEapId();
	String viewDriverHistory 	   = navigationBasePath+"/viewEAPConfigHistory.do?eapId="+eapConfigData.getEapId()+"&auditUid="+eapConfigData.getAuditUId()+"&name="+eapConfigData.getName();;	

	String enabledAuthMethods=eapConfigData.getEnabledAuthMethods();
	System.out.println("Enabled Auth Methods:"+enabledAuthMethods);
	String[] enabledAuthArray=enabledAuthMethods.split(",");
    
	%>

	<script type="text/javascript">

	
	function isTLSConfigExit(){
		             
			 <% 
			 boolean isExistMethod=false;
			 for(int i=0;i<enabledAuthArray.length;i++){
				 if(EAPConfigConstant.TLS_STR.equals(enabledAuthArray[i])){
					 isExistMethod= true;
				 }
			 }
			 if(!isExistMethod){
			 %>
              
			 alert("Please Enable TLS Auth Method.");
		     return false;
		     
			 <%}%>
		 
	}


	function isTTLSConfigExit(){
	
		<% 
		  isExistMethod=false;
		 for(int i=0;i<enabledAuthArray.length;i++){
			 if(EAPConfigConstant.TTLS_STR.equals(enabledAuthArray[i])){
				 isExistMethod= true;
			 }
		 }
		 if(!isExistMethod){
		 %>
         
		 alert("Please Enable TTLS Auth Method.");
	     return false;
	     
		 <%}%>
  		 
	}
	function isPEAPConfigExit(){
		
		<% 
		  isExistMethod=false;
		 for(int i=0;i<enabledAuthArray.length;i++){
			 if(EAPConfigConstant.PEAP_STR.equals(enabledAuthArray[i])){
				 isExistMethod= true;
			 }
		 }
		 if(!isExistMethod){
		 %>
         
		 alert("Please Enable PEAP Auth Method.");
	     return false;
	     
		 <%}%>
       
	}
	function isGsmConfigExit(){
		
		<% 
		  isExistMethod=false;
		 for(int i=0;i<enabledAuthArray.length;i++){
			 if(EAPConfigConstant.SIM_STR.equals(enabledAuthArray[i]) || EAPConfigConstant.AKA_STR.equals(enabledAuthArray[i]) || EAPConfigConstant.AKA_PRIME_STR.equals(enabledAuthArray[i])){
				 isExistMethod= true;
			 }
		 }
		 if(!isExistMethod){
		 %>
         
		 alert("Please Enable SIM Or AKA Or AKA' Auth Method.");
	     return false;
	     
		 <%}%>
       
	}
	function isAKAConfigExit(){
	
		<% 
		  isExistMethod=false;
		 for(int i=0;i<enabledAuthArray.length;i++){
			 if(EAPConfigConstant.AKA_STR.equals(enabledAuthArray[i])){
				 isExistMethod= true;
			 }
		 }
		 if(!isExistMethod){
		 %>
	     
		 alert("Please Enable AKA Auth Method.");
	     return false;
	     
		 <%}%>
   
	}
	
	</script>





			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id="header">
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								
								<tr>
									<td class="subLinks">
										<a href="<%=updateBasicDetails%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.updatebasicdetails" /></a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=updateSupportedMethods%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.updatesupportedmethods" /></a>
									</td>
								</tr>
								<%//if((eapConfigData.getEnabledAuthMethods().contains("TLS")) && (eapConfigData.getEnabledAuthMethods().contains("TTLS"))){%>		
								<%-- 
								<tr>
									<td class="subLinks">
										<a href="<%=updateEapTtls%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.updateeaptlsttlsdetails" /></a>
									</td>
								</tr>
								--%>
                                
								<tr>
									<td class="subLinks">
										<a href="<%=updateEapTls%>" onclick="return isTLSConfigExit();"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.updateeaptlsdetails" /></a>
									</td>
								</tr>	
                                
								<tr>
									<td class="subLinks">
										<a href="<%=updateEapTtls%>" onclick="return isTTLSConfigExit();"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.updateeapttlsdetails" /></a>
									</td>
								</tr>	
								<tr>
									<td class="subLinks">
										<a href="<%=updateEapPeap%>" onclick="return isPEAPConfigExit();"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.updateeappeapdetails" /></a>
									</td>
								</tr>	
								
								<tr>
									<td class="subLinks">
										<a href="<%=updateEapGsm%>" onclick="return isGsmConfigExit();"> <bean:message bundle="servermgrResources" key="servermgr.eapconfig.updateeapgsmdetails"/> </a>
									</td>
								</tr>		
							</table>
						</div>
					</td>
				</tr>
				<tr id="header">
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewBasicDetails%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewbasicdetails" /></a>
									</td>
								</tr>

                                
								<tr>
									<td class="subLinks">
										<a href="<%=viewEapTls%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vieweaptlsdetails" /></a>
									</td>
								</tr>	
								<tr>
									<td class="subLinks">
										<a href="<%=viewEapTtls%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vieweapttlsdetails" /></a>
									</td>
								</tr>	
								<tr>
									<td class="subLinks">
										<a href="<%=viewEapPeap%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vieweappeapdetails" /></a>
									</td>
								</tr
								 
								
								<tr>
									<td class="subLinks">
										<a href="<%=viewEapGsm%>"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.vieweapgsmdetails" /></a>
									</td>
								</tr>
								
								<tr>
									<td class="subLinks">
										<a href="<%=viewDriverHistory%>">
											<bean:message bundle="datasourceResources" key="database.datasource.viewDatabaseDatasourceHistory" />
										</a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
  </tr>

</table>
