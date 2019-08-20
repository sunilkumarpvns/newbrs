<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAuthDriverRelationData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData"%>
<%@ page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>

<% 
	EAPPolicyData eapData = (EAPPolicyData)request.getAttribute("eapPolicyData");
%>

<script>
	$(document).ready(function(){
		 /* bind toggle on click image */
		$("#authenticationToggleImageElement").click(function(){
			toggleData(this,"authenticationDetail");  
		  });
		$("#authorizationToggleImageElement").click(function(){
			toggleData(this,"authorizationDetails");  
		  });
		$("#rfcToggleImageElement").click(function(){
			toggleData(this,"rfcDetails");  
		  });
		$("#profiledriverToggleImageElement").click(function(){
			toggleData(this,"profileDriverDetail");  
		  });
		
		$("#responseAttributeToggleImageElement").click(function(){
			toggleData(this,"responseAttributesDetails");  
		});
		
		
		$("#authorizationDetails").hide();
		$('#rfcDetails').hide();
		$("#profileDriverDetail").hide();
		$('#responseAttributesDetails').hide();
	});

	function toggleData(image,divisionId){
		if ($("#"+divisionId).is(':hidden')) {
			image.src="<%=basePath%>/images/top-level.jpg";
       } else {
    	   image.src="<%=basePath%>/images/bottom-level.jpg";
       }
		$("#"+divisionId).slideToggle("normal");
	}
	
</script>

<%@ include file="ViewEAPPolicy.jsp" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="eapPolicyInstBean" name="eapPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData" />
    <bean:define id="diameterConcurrencyBean" name="diameterConcurrencyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData" />
    <bean:define id="additionalDiameterConcurrencyData" name="additionalDiameterConcurrencyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData" />
  
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0"  >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	View <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.authenticationdetails"/>
            </td>
            <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="authenticationToggleImageElement" src="<%=basePath%>/images/top-level.jpg"/>
           </td>
          </tr>
         </table>  
         
         <div id="authenticationDetail" width="100%" border="0" cellspacing="0" cellpadding="0">
         <table width="100%" cellpadding="0" cellspacing="0" border="0"> 
          <tr> 
            <td class="tblfirstcol" width="30%" ><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.uidcasesensitivity" /></td>
            <td class="tblcol" width="70%" >
            <logic:equal value="1" name="eapPolicyData" property="caseSensitiveUserIdentity">
            	No Change
            </logic:equal>
            <logic:equal value="2" name="eapPolicyData" property="caseSensitiveUserIdentity">
            	Lower Case
            </logic:equal>
            <logic:equal value="3" name="eapPolicyData" property="caseSensitiveUserIdentity">
            	Upper Case
            </logic:equal>
            &nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.eapconfig" /></td>
            <td class="tblcol" >
            	<logic:notEmpty name="eapConfigData" property="name">
            		<span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="eapConfigData" property="eapId"/>','<bean:write name="eapConfigData" property="name"/>','<%=EliteViewCommonConstant.EAP_CONFIGURATION%>');">
            			<bean:write name="eapConfigData" property="name"/>
            		</span>
            	</logic:notEmpty>
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.multipleuid" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="multipleUserIdentity"/>&nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.stripuid" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="stripUserIdentity"/>&nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.realmpattern" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="realmPattern"/>&nbsp; </td>
          </tr>
           <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.realmseparator" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="realmSeparator"/>&nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.trimuid" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="trimUserIdentity"/>&nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties"  key="servicepolicy.eappolicy.trimpassword" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="trimPassword"/>&nbsp; </td>
          </tr>
          <tr> 
            <td class="tblfirstcol"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.anonymousidentity" /></td>
            <td class="tblcol" ><bean:write name="eapPolicyData" property="anonymousProfileIdentity"/>&nbsp; </td>
          </tr>
          </table>  
          </div>
          
          <table width="100%" border="0" cellspacing="0" cellpadding="0"> 
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	View <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.authorizationdetails"/>
            </td>
             <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="authorizationToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
           </td>
          </tr> 
          </table>
          
          <div id="authorizationDetails">
          <table width="100%" cellpadding="0" cellspacing="0" border="0">
	         <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.wimax" /></td>
	         	<td class="tblcol" width="70%">
	         		<logic:equal value="true" name="eapPolicyInstBean" property="wimax">
	         			Enabled
	         		</logic:equal>
	         		<logic:equal value="false" name="eapPolicyInstBean" property="wimax">
	         			Disabled
	         		</logic:equal>
	         		&nbsp;
	         	</td>	
	         </tr>
	          <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rejectoncheckitemnotfound" /></td>
	         	<td class="tblcol" width="70%"><bean:write name="eapPolicyInstBean" property="rejectOnCheckItemNotFound"/>&nbsp;</td>	
	         </tr>
	         <tr>	
	         	<td class="tblfirstcol" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rejectonrejectitemnotfound" /></td>
	         	<td class="tblcol"><bean:write name="eapPolicyInstBean" property="rejectOnRejectItemNotFound"/>&nbsp;</td>
	         </tr>
	         <tr>	
	         	<td class="tblfirstcol" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.acceptonpolicynotfound" /></td>
	         	<td class="tblcol">
	         		<logic:equal value="1" name="eapPolicyInstBean" property="actionOnPolicyNotFound">true</logic:equal>
		        	<logic:equal value="2" name="eapPolicyInstBean" property="actionOnPolicyNotFound">false</logic:equal>
	         	</td>
	         </tr>
	         <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.gracepolicy" /></td>
	         	<td class="tblcol" width="70%"><bean:write name="eapPolicyInstBean" property="gracePolicy"/>&nbsp;</td>	
	         </tr>
	          <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.diameterconcurrency" /></td>
	         	<td class="tblcol" width="70%"><bean:write name="diameterConcurrencyBean" property="name"/>&nbsp;</td>	
	         </tr>
	          <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.additionaldiameterconcurrency" /></td>
	         	<td class="tblcol" width="70%"><bean:write name="additionalDiameterConcurrencyData" property="name"/>&nbsp;</td>	
	         </tr>
	         <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultsessiontimeout" /></td>
	         	<td class="tblcol" width="70%"><bean:write name="eapPolicyInstBean" property="defaultSessionTimeout"/>&nbsp;</td>	
	         </tr>
         </table>
         </div>
         
         <!-- RFC 4372 -->
          <table width="100%" border="0" cellspacing="0" cellpadding="0"> 
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	View <bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rfc4372cui"/>
            </td>
             <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="rfcToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
           </td>
          </tr> 
          </table>
         <div id="rfcDetails">
          <table width="100%" cellpadding="0" cellspacing="0" border="0">
	         <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.cui" /> </td>
	         	<td class="tblcol" width="70%"><bean:write name="eapPolicyInstBean" property="cui"/>&nbsp;</td>	
	         </tr>
	         <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.advancedcuiexpression" /> </td>
	         	<td class="tblcol" width="70%"><bean:write name="eapPolicyInstBean" property="advancedCuiExpression"/>&nbsp;</td>	
	         </tr>
	         <tr>
	         	<td class="tblfirstcol" width="30%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.cuiresattrs" />  </td>
	         	<td class="tblcol" width="70%"><bean:write name="eapPolicyInstBean" property="cuiResponseAttributes"/>&nbsp;</td>	
	         </tr>
         </table>
         </div>
         
         
         <table width="100%" cellpadding="0" cellspacing="0" border="0"> 
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	View <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.driverdetails"/>
            </td>
             <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="profiledriverToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
           </td>
          </tr> 
          </table>
          
          <div id="profileDriverDetail">
          <table width="100%" cellpadding="0" cellspacing="0">
          <tr> 
            <td class="tblfirstcol" width="30%" >Drivers</td>
            <td class="tblcol" width="70%" >
            
            	<%												
					List<EAPPolicyAuthDriverRelationData> driverList = eapData.getDriverList();												
					if(driverList != null){
						for(int i = 0;i<driverList.size();i++){																									
							EAPPolicyAuthDriverRelationData data = driverList.get(i);
							String nm = " ";
								nm = data.getDriverData().getName();										
				%>																												
							<%=i+1%>.
							<% if(nm != null){%>
								<span class="view-details-css" onclick="openViewDetails(this,'<%= data.getDriverData().getDriverInstanceId()%>','<%=nm%>','DRIVERS');">
								   	 <%=nm%>
								</span><br/>
							<%}%>
										    
				<%}
			}else{%>
            	No Drivers Configured
            	<%} %>
          </tr> 
           <tr> 
            <td class="tblfirstcol" width="30%" > Additional Drivers</td>
            <td class="tblcol" width="70%" >
            	<%int additionalDriverIndex = 0; %>
            	<logic:iterate id="obj" name="eapPolicyData" property="eapAdditionalDriverRelDataList" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData">
            		 <%=++additionalDriverIndex%>.
            		 <logic:notEmpty name="obj" property="driverInstanceData.name">
						 <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="obj" property="driverInstanceData.driverInstanceId"/>','<bean:write name="obj" property="driverInstanceData.name"/>','DRIVERS');">
						  	<bean:write name="obj" property="driverInstanceData.name"/>
						 </span>
					</logic:notEmpty>
            		 
            		<BR/>
            	</logic:iterate>
            	<%if(additionalDriverIndex==0) {%>
            		No Additional Drivers Configured
            	<%} %>
            
            </td>
          </tr>
          <tr>	
         	<td class="tblfirstcol" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.script" /></td>
         	<td class="tblcol"><bean:write name="eapPolicyData" property="script"/>&nbsp;</td>
          </tr>
          <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="4">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /> 
			</td>
		  </tr>
          <tr>
				<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
					<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
						<tr>
							<td class="captiontext" valign="top">
								<table cellspacing="0" cellpadding="0" border="0" width="100%"
									id="pre-plugin-mapping-table" class="pre-plugin-mapping-table">
									<tr>
										<td class="tbl-header-bold tbl-header-bg" width="50%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginname" /> 
												<ec:elitehelp header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname"></ec:elitehelp>
										</td>
										<td class="tbl-header-bold tbl-header-bg" width="50%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginagr" /> 
												<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg"></ec:elitehelp>
										</td>
									</tr>

									<logic:iterate id="obj1" name="eapPolicyData" property="eapPolicyPluginConfigList">
										<logic:equal property="pluginType" name="obj1" value="<%=PolicyPluginConstants.IN_PLUGIN%>">
											<tr>
												<td class="tblfirstcol" width="50%">
													<input type="text" name="prePluginName" class="noborder" style="width: 100%;" value='<bean:write name="obj1" property="pluginName"/>' />
												</td>
												<td class="tblrows" width="50%">
													<textarea name="prePluginArgument" rows="1" cols="1" style="width: 100%; height: 19px;" class="noborder"><bean:write name="obj1" property="pluginArgument" /></textarea>
												</td>
											</tr>
										</logic:equal>
									</logic:iterate>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="left" class="tblheader-bold" valign="top" colspan="4">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" />
				</td>
			</tr>
			<tr>
				<td colspan="3" class="captiontext" style="padding: 10px;"
					align="left">
					<table id="postPluginTbl" class="postPluginTbl" cellspacing="0"
						cellpadding="0" width="70%">
						<tr>
							<td class="captiontext" valign="top">
								<table cellspacing="0" cellpadding="0" border="0" width="100%" id="post-plugin-mapping-table" class="post-plugin-mapping-table">
									<tr>
										<td class="tbl-header-bold tbl-header-bg" width="50%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginname" /> 
												<ec:elitehelp header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname"></ec:elitehelp>
										</td>
										<td class="tbl-header-bold tbl-header-bg" width="50%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginagr" /> 
												<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg"></ec:elitehelp>
										</td>
									</tr>
									<logic:iterate id="obj" name="eapPolicyData" property="eapPolicyPluginConfigList">
										<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.OUT_PLUGIN%>">
											<tr>
												<td class="tblfirstcol" width="50%">
													<input type="text" name="postPluginName" class="noborder" style="width: 100%;" value='<bean:write name="obj" property="pluginName"/>' />
												</td>
												<td class="tblrows" width="50%">
													<textarea name="postPluginArgument" rows="1" cols="1" style="width: 100%; height: 19px;" class="noborder"><bean:write name="obj" property="pluginArgument" /></textarea>
												</td>
											</tr>
										</logic:equal>
									</logic:iterate>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
         </table>
         </div>
         
         
          <table width="100%" cellpadding="0" cellspacing="0" border="0"> 
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%">
            	View Response Attributes Details
            </td>
             <td class="tblheader-bold"  align="right" width="15px">
           		<img alt="bottom" id="responseAttributeToggleImageElement" src="<%=basePath%>/images/bottom-level.jpg"/>
           </td>
          </tr> 
          </table>
          <div id="responseAttributesDetails">
	          <table width="70%" cellpadding="0" cellspacing="0" border="0" align="left" style="padding: 20px;">
		        <tr>
	         		<td class="tblheader-bold">
	         			Command Codes
	         		</td>
	         		<td class="tblheader-bold">
	         			Response Attributes
	         		</td>
	         	</tr>
	         	<%Set<EAPResponseAttributes> eapResponseAttributes = eapData.getEapResponseAttributesSet();
	         		if(eapResponseAttributes != null) {
	         			for(EAPResponseAttributes eapAttributes : eapResponseAttributes){%>
		         		 <tr>
			         		<td class="tblfirstcol">
			         			<%=(eapAttributes.getCommandCodes() == null)?"":eapAttributes.getCommandCodes() %>&nbsp;
			         		</td>
			         		<td class="tblcol border-right-css">
			         			<%=(eapAttributes.getResponseAttributes() == null)?"":eapAttributes.getResponseAttributes()%>&nbsp;
			         		</td>
			         	</tr>	
		         	<%	}
	         		}
	         	%>
	         </table>
         </div>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>

</table>

